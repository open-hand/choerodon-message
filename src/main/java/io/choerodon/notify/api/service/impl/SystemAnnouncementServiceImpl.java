package io.choerodon.notify.api.service.impl;

import java.util.Date;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.ReceiveSettingDTO;
import io.choerodon.notify.api.dto.SystemAnnouncementDTO;
import io.choerodon.notify.api.service.SystemAnnouncementService;
import io.choerodon.notify.domain.SystemAnnouncement;
import io.choerodon.notify.infra.mapper.SystemAnnouncementMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


/**
 * @author dengyouquan
 **/
@Service
public class SystemAnnouncementServiceImpl implements SystemAnnouncementService {
    private final ModelMapper modelMapper = new ModelMapper();

    private SystemAnnouncementMapper announcementMapper;

    public SystemAnnouncementServiceImpl(SystemAnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
        modelMapper.addMappings(ReceiveSettingDTO.entity2Dto());
        modelMapper.addMappings(ReceiveSettingDTO.dto2Entity());
        modelMapper.validate();
    }

    @Override
    public SystemAnnouncementDTO create(SystemAnnouncementDTO dto) {
        dto.setSendDate(new Date());
        SystemAnnouncement systemAnnouncement = modelMapper.map(dto, SystemAnnouncement.class);
        if (announcementMapper.insert(systemAnnouncement) != 1) {
            throw new CommonException("error.systemAnnouncement.create");
        }
        return modelMapper.map(systemAnnouncement, SystemAnnouncementDTO.class);
    }

    @Override
    public Page<SystemAnnouncementDTO> pagingQuery(PageRequest pageRequest, String title, String content, String param) {
        return PageHelper.doPageAndSort(pageRequest, () ->
                announcementMapper.fulltextSearch(title, content, param));
    }
}
