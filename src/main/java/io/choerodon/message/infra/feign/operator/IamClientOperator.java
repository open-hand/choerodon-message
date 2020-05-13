package io.choerodon.message.infra.feign.operator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.feign.IamFeignClient;

/**
 * Created by Sheep on 2019/7/11.
 */

@Component
public class IamClientOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IamClientOperator.class);

    @Autowired
    private IamFeignClient iamFeignClient;

    public ProjectDTO queryProjectById(Long projectId) {
        ResponseEntity<ProjectDTO> projectDTOResponseEntity = iamFeignClient.queryProjectById(projectId);
        if (!projectDTOResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.project.query.by.id", projectId);
        }
        return projectDTOResponseEntity.getBody();
    }

    public TenantDTO queryTenantById(Long tenantId) {
        ResponseEntity<TenantDTO> projectDTOResponseEntity = iamFeignClient.queryTenantById(tenantId);
        if (!projectDTOResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.tenant.query.by.id", tenantId);
        }
        return projectDTOResponseEntity.getBody();
    }

    public List<ProjectDTO> listProjectsByTenantId(Long tenantId) {
        ResponseEntity<List<ProjectDTO>> projects = iamFeignClient.listProjectsByTenantId(tenantId);
        if (!projects.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.tenant.query.by.id", tenantId);
        }
        return projects.getBody();
    }
}
