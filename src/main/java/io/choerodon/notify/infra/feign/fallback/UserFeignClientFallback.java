package io.choerodon.notify.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.OrganizationDTO;
import io.choerodon.notify.api.dto.OrganizationProjectDTO;
import io.choerodon.notify.api.dto.ProjectDTO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.infra.feign.UserFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author dengyouquan
 **/
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public ResponseEntity<Long[]> getUserIds() {
        throw new CommonException("error.iam.getUserId");
    }

    @Override
    public ResponseEntity<List<UserDTO>> listUsersByIds(Long[] ids) {
        throw new CommonException("error.iam.listUsersByIds");
    }

    @Override
    public ResponseEntity<List<OrganizationDTO>> listOrganizationsByIds(Set<Long> ids) {
        throw new CommonException("error.iam.listOrganizationsByIds");
    }

    @Override
    public ResponseEntity<List<ProjectDTO>> listProjectsByIds(Set<Long> ids) {
        throw new CommonException("error.iam.listProjectsByIds");
    }

    @Override
    public ResponseEntity<List<UserDTO>> listUsersByEmails(String[] emails) {
        throw new CommonException("error.iam.listUsersByEmails");
    }

    @Override
    public ResponseEntity<OrganizationProjectDTO> queryByUserIdOrganizationProject(Long id) {
        throw new CommonException("error.iam.queryByUserIdOrganizationProject");
    }
}
