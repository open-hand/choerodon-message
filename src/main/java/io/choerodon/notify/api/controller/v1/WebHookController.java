package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.service.WebHookService;
import io.choerodon.notify.api.vo.WebHookVO;
import io.choerodon.notify.infra.dto.WebHookDTO;
import io.choerodon.notify.infra.enums.WebHookTypeEnum;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author lrc
 * @since 2019/10/28
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/web_hooks")
public class WebHookController {

    private WebHookService webHookService;

    public WebHookController(WebHookService webHookService) {
        this.webHookService = webHookService;
    }

    @GetMapping
    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "分页查询WebHook信息")
    @CustomPageRequest
    public ResponseEntity<PageInfo<WebHookDTO>> pagingByMessage(@ApiIgnore
                                                                @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @RequestParam(required = false) String name,
                                                                @RequestParam(required = false) String type,
                                                                @RequestParam(required = false) Boolean enableFlag,
                                                                @RequestParam(required = false) String params) {
        WebHookDTO filterDTO = new WebHookDTO().setEnableFlag(enableFlag).setName(name).setType(type);
        return new ResponseEntity<>(webHookService.pagingWebHook(pageable, projectId, filterDTO, params), HttpStatus.OK);
    }

    @GetMapping("/check_path")
    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "校验WebHook地址是否已经存在")
    public ResponseEntity<Boolean> check(@RequestParam(value = "id", required = false) Long id,
                                         @RequestParam("path") String path) {
        return new ResponseEntity<>(webHookService.checkPath(id, path), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "查询WebHook详情")
    @GetMapping("/{id}")
    public ResponseEntity<WebHookVO> getOne(@PathVariable(name = "project_id") Long projectId,
                                            @PathVariable("id") Long id) {
        return new ResponseEntity<>(webHookService.getById(projectId, id), HttpStatus.OK);
    }


    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "新增WebHook")
    @PostMapping
    public ResponseEntity<WebHookVO> create(@PathVariable(name = "project_id") Long projectId,
                                            @RequestBody @Validated WebHookVO webHookVO) {
        webHookVO.setProjectId(projectId);
        //校验type
        if (!WebHookTypeEnum.isInclude(webHookVO.getType())) {
            throw new CommonException("error.web.hook.type.invalid");
        }
        return new ResponseEntity<>(webHookService.create(projectId, webHookVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "更新WebHook")
    @PutMapping("/{id}")
    public ResponseEntity<WebHookVO> update(@PathVariable("project_id") Long projectId,
                                            @PathVariable("id") Long id,
                                            @RequestBody @Validated WebHookVO webHookVO) {
        webHookVO.setProjectId(projectId);
        //校验type
        if (!WebHookTypeEnum.isInclude(webHookVO.getType())) {
            throw new CommonException("error.web.hook.type.invalid");
        }
        return new ResponseEntity<>(webHookService.update(projectId, webHookVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "删除WebHook")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        webHookService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "禁用WebHook")
    @PutMapping("/{id}/disabled")
    public ResponseEntity<WebHookDTO> disabled(@PathVariable("id") Long id) {
        return new ResponseEntity<>(webHookService.disabled(id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "启用WebHook")
    @PutMapping("/{id}/enabled")
    public ResponseEntity<WebHookDTO> enabled(@PathVariable("id") Long id) {
        return new ResponseEntity<>(webHookService.enabled(id), HttpStatus.OK);
    }

}
