package io.choerodon.notify.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.OrganizationDTO;
import io.choerodon.notify.api.dto.ProjectDTO;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.api.service.SiteMsgRecordService;
import io.choerodon.notify.domain.SiteMsgRecord;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.enums.SenderType;
import io.choerodon.notify.infra.feign.UserFeignClient;
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;
import io.choerodon.websocket.helper.WebSocketHelper;
import io.choerodon.websocket.send.SendMessagePayload;
import org.apache.commons.collections.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.choerodon.notify.api.service.impl.WebSocketWsSendServiceImpl.MSG_TYPE_PM;

/**
 * @author dengyouquan
 **/
@Service
public class SiteMsgRecordServiceImpl implements SiteMsgRecordService {
    private static final Logger logger = LoggerFactory.getLogger(SiteMsgRecordServiceImpl.class);

    private final SiteMsgRecordMapper siteMsgRecordMapper;

    private final WebSocketHelper webSocketHelper;

    private final UserFeignClient userFeignClient;

    public SiteMsgRecordServiceImpl(SiteMsgRecordMapper siteMsgRecordMapper,
                                    WebSocketHelper webSocketHelper,
                                    UserFeignClient userFeignClient) {
        this.siteMsgRecordMapper = siteMsgRecordMapper;
        this.webSocketHelper = webSocketHelper;
        this.userFeignClient = userFeignClient;
    }

    @Override
    public PageInfo<SiteMsgRecordDTO> pagingQueryByUserId(Long userId, Boolean isRead, String type, int page, int size) {
        PageInfo<SiteMsgRecordDTO> result = PageHelper.startPage(page, size).doSelectPageInfo(() -> siteMsgRecordMapper.selectByUserIdAndReadAndDeleted(userId, isRead, type));
        List<SiteMsgRecordDTO> records = result.getList();
        Map<String, Set<Long>> senderMap = getSenderMap(records);
        processSendBy(records, senderMap);
        return result;
    }

    private void processSendBy(List<SiteMsgRecordDTO> records, Map<String, Set<Long>> senderMap) {
        List<OrganizationDTO> organizations = userFeignClient.listOrganizationsByIds(senderMap.get(SenderType.ORGANIZATION.value())).getBody();
        List<ProjectDTO> projects = userFeignClient.listProjectsByIds(senderMap.get(SenderType.PROJECT.value())).getBody();
        Set<Long> userIdSet = senderMap.get(SenderType.USER.value());
        List<UserDTO> users = userFeignClient.listUsersByIds(userIdSet.toArray(new Long[userIdSet.size()])).getBody();
        MultiKeyMap multiKeyMap = new MultiKeyMap();
        organizations.forEach(org -> multiKeyMap.put(SenderType.ORGANIZATION.value(), org.getId(), org));
        projects.forEach(pro -> multiKeyMap.put(SenderType.PROJECT.value(), pro.getId(), pro));
        users.forEach(user -> multiKeyMap.put(SenderType.USER.value(), user.getId(), user));
        records.stream().parallel().forEach(record -> {
            Object sender = multiKeyMap.get(record.getSenderType(), record.getSendBy());
            if (sender == null) {
                fullInDefaultSender(record);
            } else {
                setSendBy(record, sender);
            }
        });
    }

    private void setSendBy(SiteMsgRecordDTO record, Object sender) {
        if (SenderType.ORGANIZATION.value().equals(record.getSenderType())) {
            record.setSendByOrganization((OrganizationDTO) sender);
        }
        if (SenderType.PROJECT.value().equals(record.getSenderType())) {
            record.setSendByProject((ProjectDTO) sender);
        }
        if (SenderType.USER.value().equals(record.getSenderType())) {
            record.setSendByUser((UserDTO) sender);
        }
    }

    private Map<String, Set<Long>> getSenderMap(List<SiteMsgRecordDTO> records) {
        Map<String, Set<Long>> senderMap = new HashMap<>();
        senderMap.put(SenderType.ORGANIZATION.value(), new HashSet<>());
        senderMap.put(SenderType.PROJECT.value(), new HashSet<>());
        senderMap.put(SenderType.USER.value(), new HashSet<>());
        for (SiteMsgRecordDTO record : records) {
            String senderType = record.getSenderType();
            Long sendBy = record.getSendBy();
            if (sendBy == null) {
                fullInDefaultSender(record);
            } else if (sendBy.equals(0L)) {
                logger.warn("illegal id of sender because of id = 0, so set site as default sender, siteMsgRecord id = {}", record.getId());
                fullInDefaultSender(record);
            } else {
                Set<Long> ids = senderMap.get(senderType);
                if (ids != null) {
                    ids.add(sendBy);
                }
            }
        }
        return senderMap;
    }

    private void fullInDefaultSender(SiteMsgRecordDTO record) {
        record.setSendBy(0L);
        record.setSenderType(SenderType.SITE.value());
    }

    @Override
    @Transactional
    public void batchUpdateSiteMsgRecordIsRead(Long userId, Long[] ids) {
        if (ids.length == 0) return;
        for (Long id : ids) {
            if (id == null) continue;
            SiteMsgRecord siteMsgRecord = siteMsgRecordMapper.selectByPrimaryKey(id);
            if (siteMsgRecord != null && siteMsgRecord.getUserId().equals(userId) && !siteMsgRecord.getRead()) {
                siteMsgRecord.setRead(true);
                siteMsgRecordMapper.updateByPrimaryKeySelective(siteMsgRecord);
            }
        }
        String key = "choerodon:msg:site-msg:" + userId;
        webSocketHelper.sendMessageByKey(key, new SendMessagePayload<>(MSG_TYPE_PM, key, siteMsgRecordMapper.selectCountOfUnRead(userId)));
    }

    @Override
    @Transactional
    public void batchUpdateSiteMsgRecordIsDeleted(Long userId, Long[] ids) {
        if (ids.length == 0) return;
        for (Long id : ids) {
            if (id == null) continue;
            SiteMsgRecord siteMsgRecord = siteMsgRecordMapper.selectByPrimaryKey(id);
            if (siteMsgRecord != null && siteMsgRecord.getUserId().equals(userId) && !siteMsgRecord.getDeleted()) {
                siteMsgRecord.setDeleted(true);
                siteMsgRecordMapper.updateByPrimaryKeySelective(siteMsgRecord);
            }
        }
        String key = "choerodon:msg:site-msg:" + userId;
        webSocketHelper.sendMessageByKey(key, new SendMessagePayload<>(MSG_TYPE_PM, key, siteMsgRecordMapper.selectCountOfUnRead(userId)));
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void insertRecord(Template template, String pmContent, Long[] ids) {
        AtomicInteger count = new AtomicInteger();
        List<SiteMsgRecord> records = new LinkedList<>();
        for (Long id : ids) {
            SiteMsgRecord record = new SiteMsgRecord(id, template.getPmTitle(), pmContent);
            records.add(record);
            if (records.size() >= 999) {
                siteMsgRecordMapper.batchInsert(records);
                records.clear();
            }
            count.incrementAndGet();
        }
        siteMsgRecordMapper.batchInsert(records);
        records.clear();
        logger.debug("PmSendTask insert database count:{}", count);
    }
}
