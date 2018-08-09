package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.service.BusinessTypeService;
import io.choerodon.notify.domain.BusinessType;
import io.choerodon.notify.infra.mapper.BusinessTypeMapper;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BusinessTypeServiceImpl implements BusinessTypeService {

    private BusinessTypeMapper businessTypeMapper;

    public BusinessTypeServiceImpl(BusinessTypeMapper businessTypeMapper) {
        this.businessTypeMapper = businessTypeMapper;
    }

    @Override
    public Set<String> listNames() {
        return businessTypeMapper.selectAll().stream()
                .map(BusinessType::getName).collect(Collectors.toSet());
    }
}
