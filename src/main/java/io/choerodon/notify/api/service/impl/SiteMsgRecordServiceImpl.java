package io.choerodon.notify.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.service.SiteMsgRecordService;
import io.choerodon.notify.domain.SiteMsgRecord;
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dengyouquan
 **/
@Service
public class SiteMsgRecordServiceImpl implements SiteMsgRecordService {

    private final SiteMsgRecordMapper siteMsgRecordMapper;

    public SiteMsgRecordServiceImpl(SiteMsgRecordMapper siteMsgRecordMapper) {
        this.siteMsgRecordMapper = siteMsgRecordMapper;
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
    }
}
