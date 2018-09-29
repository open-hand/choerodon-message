package io.choerodon.notify.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.service.SiteMsgRecordService;
import io.choerodon.notify.domain.SiteMsgRecord;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;
import io.choerodon.notify.websocket.send.MessageSender;
import io.choerodon.notify.websocket.send.WebSocketSendPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

import static io.choerodon.notify.api.service.impl.WebSocketWsSendServiceImpl.MSG_TYPE_PM;

/**
 * @author dengyouquan
 **/
@Service
public class SiteMsgRecordServiceImpl implements SiteMsgRecordService {
    private static final Logger logger = LoggerFactory.getLogger(SiteMsgRecordServiceImpl.class);

    private final SiteMsgRecordMapper siteMsgRecordMapper;

    private final MessageSender messageSender;

    public SiteMsgRecordServiceImpl(SiteMsgRecordMapper siteMsgRecordMapper, MessageSender messageSender) {
        this.siteMsgRecordMapper = siteMsgRecordMapper;
        this.messageSender = messageSender;
    }

    @Override
    public Page<SiteMsgRecordDTO> pagingQueryByUserId(Long userId, Boolean isRead, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () ->
                siteMsgRecordMapper.selectByUserIdAndReadAndDeleted(userId, isRead));
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
        messageSender.sendByKey(key, new WebSocketSendPayload<>(MSG_TYPE_PM, key, siteMsgRecordMapper.selectCountOfUnRead(userId)));
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
        messageSender.sendByKey(key, new WebSocketSendPayload<>(MSG_TYPE_PM, key, siteMsgRecordMapper.selectCountOfUnRead(userId)));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void insertRecord(Template template, String pmContent, Long[] ids) {
        AtomicInteger count = new AtomicInteger();
        for (Long id : ids) {
            SiteMsgRecord record = new SiteMsgRecord(id, template.getPmTitle(), pmContent);
            //oracle 不支持 insertList
            siteMsgRecordMapper.insert(record);
            count.incrementAndGet();
        }
        logger.info("PmSendTask insert database count:{}", count);
    }
}
