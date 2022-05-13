package io.choerodon.message.infra.feign;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.swagger.annotations.ApiParam;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.OrganizationProjectVO;
import io.choerodon.message.api.vo.ProjectVO;
import io.choerodon.message.api.vo.UserVO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.feign.fallback.IamFeignClientFallback;

/**
 * 〈功能简述〉
 * 〈base-service Fegin接口〉
 *
 * @author wanghao
 * @Date 2019/12/11 19:00
 */
@FeignClient(value = HZeroService.Iam.NAME, fallback = IamFeignClientFallback.class)
public interface IamFeignClient {

    @PostMapping(value = "/choerodon/v1/users/ids")
    ResponseEntity<List<UserVO>> listUsersByIds(@RequestBody Long[] ids, @RequestParam(value = "only_enabled") Boolean onlyEnabled);


    @GetMapping("/choerodon/v1/users/ids")
    ResponseEntity<Long[]> getUserIds();

    @PostMapping("/choerodon/v1/organizations/ids")
    ResponseEntity<List<TenantDTO>> listOrganizationsByIds(@RequestBody Set<Long> ids);

    @PostMapping("/choerodon/v1/projects/ids")
    ResponseEntity<List<ProjectDTO>> listProjectsByIds(@RequestBody Set<Long> ids);

    @PostMapping("/choerodon/v1/users/emails")
    ResponseEntity<List<UserVO>> listUsersByEmails(@RequestBody String[] emails);

    @GetMapping("/choerodon/v1/users/{id}/organization_project")
    ResponseEntity<OrganizationProjectVO> queryByUserIdOrganizationProject(@PathVariable("id") Long id);

    @GetMapping("/choerodon/v1/projects/list/by_name")
    ResponseEntity<List<Long>> getProListByName(@RequestParam("name") String name);

    @GetMapping("/choerodon/v1/projects/{project_id}")
    ResponseEntity<ProjectDTO> queryProjectById(@PathVariable("project_id") Long projectId);

    @GetMapping("/choerodon/v1/projects/{project_id}")
    ResponseEntity<ProjectDTO> queryProjectByIdWithoutExtraInfo(@PathVariable("project_id") Long projectId,
                                                                @RequestParam(value = "with_category_info") Boolean withCategoryInfo,
                                                                @RequestParam(value = "with_user_info") Boolean withUserInfo,
                                                                @RequestParam(value = "with_agile_info") Boolean withAgileInfo);

    @GetMapping("/v1/{organizationId}/tenants")
    ResponseEntity<TenantDTO> queryTenantById(@PathVariable("organizationId") Long organizationId);

    @GetMapping("/v1/projects/{tenant_id}")
    ResponseEntity<List<ProjectDTO>> listProjectsByTenantId(@PathVariable("tenant_id") Long tenantId);

    @GetMapping("/choerodon/v1/projects/{project_id}/users/search")
    ResponseEntity<Page<UserVO>> pagingQueryUsersWithRolesOnProjectLevel(@PathVariable("project_id") Long projectId,
                                                                         @RequestParam("page") int page,
                                                                         @RequestParam("size") int size,
                                                                         @ApiParam(value = "登录名")
                                                                         @RequestParam(required = false, name = "loginName") String loginName);

    @GetMapping("/choerodon/v1/inner/projects/all")
    ResponseEntity<List<ProjectVO>> listAllProjects(@RequestParam Boolean enabled);

    @PostMapping("/choerodon/v1/users/query_open_user_ids")
    ResponseEntity<Map<Long,String>> getOpenUserIdsByUserIds(@RequestBody List<Long> userIdList, @RequestParam("open_app_code") String openAppCode);

    @GetMapping("/choerodon/v1/organizations/{organization_id}/open_app/is_message_enabled")
    Boolean isMessageEnabled(@RequestParam("organization_id") Long organizationId, @RequestParam("type") String type);

    @GetMapping("/choerodon/v1/users/query_org_id")
    List<UserVO> queryOrgId(List<Long> userIdList);
}
