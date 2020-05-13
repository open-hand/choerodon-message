package io.choerodon.message.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.message.api.vo.OrganizationProjectVO;
import io.choerodon.message.api.vo.ProjectVO;
import io.choerodon.message.api.vo.UserVO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.feign.IamFeignClient;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2019/12/11 19:04
 */
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
    public ResponseEntity<TenantDTO> queryTenantById(Long tenantId) {
        throw new CommonException("error.tenant.get");
    }

}
