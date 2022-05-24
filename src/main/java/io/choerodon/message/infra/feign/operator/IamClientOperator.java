package io.choerodon.message.infra.feign.operator;

import java.util.List;
import java.util.Set;

import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.message.api.vo.UserVO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.feign.IamFeignClient;
import io.choerodon.message.infra.utils.OptionalBean;

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

    public UserVO getUser(Long projectId, String loginName) {
        ResponseEntity<Page<UserVO>> pageResponseEntity = iamFeignClient.pagingQueryUsersWithRolesOnProjectLevel(projectId, BaseConstants.PAGE_NUM, BaseConstants.PAGE_SIZE, loginName);
        if (!pageResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.get.user.by.loginName", loginName);
        }
        return pageResponseEntity.getBody().size() > 0 ? pageResponseEntity.getBody().get(0) : null;
    }

    public List<UserVO> listUsersByIds(Set<Long> ids, Boolean onlyEnabled) {
        ResponseEntity<List<UserVO>> listResponseEntity = iamFeignClient.listUsersByIds(ids.toArray(new Long[10]), onlyEnabled);
        if (!listResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.get.user.by.ids");
        }
        return OptionalBean.ofNullable(listResponseEntity.getBody()).get();
    }


    public List<UserVO> listUserByOpenIds(Long tenantId, String openAppType, Set<String> openIds) {
        ResponseEntity<List<UserVO>> listResponseEntity = iamFeignClient.listUserByOpenIds(tenantId, openAppType, openIds);
        if (!listResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CommonException("error.get.user.by.openIds");
        }
        return OptionalBean.ofNullable(listResponseEntity.getBody()).get();
    }
}
