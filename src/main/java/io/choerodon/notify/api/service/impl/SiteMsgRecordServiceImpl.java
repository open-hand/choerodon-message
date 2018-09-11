package io.choerodon.notify.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.pojo.SiteMsgRecordQueryParam;
import io.choerodon.notify.api.service.SiteMsgRecordService;
import io.choerodon.notify.domain.SiteMsgRecord;
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public Page<SiteMsgRecordDTO> pagingQuery(final SiteMsgRecordQueryParam query) {
        return PageHelper.doPageAndSort(query.getPageRequest(), () ->
                siteMsgRecordMapper.fulltextSearch(query));
    }

    @Override
    public List<SiteMsgRecordDTO> listByReadAndId(Long userId, Boolean isRead) {
        SiteMsgRecordQueryParam queryParam = new SiteMsgRecordQueryParam();
        queryParam.setUserId(userId);
        queryParam.setRead(isRead);
        queryParam.setDeleted(false);
        return siteMsgRecordMapper.fulltextSearch(queryParam);
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
