package io.choerodon.message.infra.feign.fallback;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.message.api.vo.OrganizationProjectVO;
import io.choerodon.message.api.vo.ProjectVO;
import io.choerodon.message.api.vo.UserVO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.feign.IamFeignClient;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2019/12/11 19:04
 */
@Component
public class IamFeignClientFallback implements IamFeignClient {
    @Override
    public ResponseEntity<List<UserVO>> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
        return null;
    }

    @Override
    public ResponseEntity<Long[]> getUserIds() {
        return null;
    }

    @Override
    public ResponseEntity<List<TenantDTO>> listOrganizationsByIds(Set<Long> ids) {
        return null;
    }

    @Override
    public ResponseEntity<List<ProjectDTO>> listProjectsByIds(Set<Long> ids) {
        return null;
    }

    @Override
    public ResponseEntity<List<UserVO>> listUsersByEmails(String[] emails) {
        return null;
    }

    @Override
    public ResponseEntity<OrganizationProjectVO> queryByUserIdOrganizationProject(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<Long>> getProListByName(String name) {
        return null;
    }

    @Override
    public ResponseEntity<ProjectDTO> queryProjectById(Long projectId) {
        throw new CommonException("error.query.project");
    }

    @Override
    public ResponseEntity<ProjectDTO> queryProjectByIdWithoutExtraInfo(Long projectId, Boolean withCategoryInfo, Boolean withUserInfo, Boolean withAgileInfo) {
        throw new CommonException("error.query.project");
    }

    @Override
    public ResponseEntity<TenantDTO> queryTenantById(Long tenantId) {
        throw new CommonException("error.tenant.get");
    }

    @Override
    public ResponseEntity<List<ProjectDTO>> listProjectsByTenantId(Long tenantId) {
        throw new CommonException("error.projects.list");
    }

    @Override
    public ResponseEntity<Page<UserVO>> pagingQueryUsersWithRolesOnProjectLevel(Long projectId, int page, int size, String loginName) {
        throw new CommonException("error.get.user");
    }

    @Override
    public ResponseEntity<List<ProjectVO>> listAllProjects(Boolean enabled) {
        throw new CommonException("error.get.projects");
    }

    @Override
    public ResponseEntity<List<String>> getOpenUserIdsByUserIds(List<Long> userIdList, String openAppCode) {
        throw new CommonException("error.get.open.user.ids");
    }
}
