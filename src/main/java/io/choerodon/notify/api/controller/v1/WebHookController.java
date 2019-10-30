package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.notify.api.service.WebHookService;
import io.choerodon.notify.api.vo.WebHookVO;
import io.choerodon.notify.infra.dto.WebHookDTO;
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
@RequestMapping(value = "/v1/project/{project_id}/webhooks")
public class WebHookController {

    private WebHookService webHookService;

    public WebHookController(WebHookService webHookService) {
        this.webHookService = webHookService;
    }

    @GetMapping
    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "分页查询webhook信息（项目层）")
    @CustomPageRequest
    public ResponseEntity<PageInfo<WebHookDTO>> pagingByMessage(@ApiIgnore
                                                                @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @RequestParam(required = false) String name,
                                                                @RequestParam(required = false) String type,
                                                                @RequestParam(required = false) Boolean enableFlag,
                                                                @RequestParam(required = false) String params) {
        return new ResponseEntity<>(webHookService.pagingWebHook(pageable, projectId, name, type, enableFlag, params), HttpStatus.OK);
    }

    @GetMapping("/check")
    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "校验webhook名称是否已经存在（项目层）")
    public void check(@RequestParam("name") String name) {
        webHookService.check(name);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "添加webhook")
    @PostMapping
    public ResponseEntity<WebHookDTO> save(@PathVariable(name = "project_id") Long projectId,@RequestBody @Validated WebHookVO webHookVO) {
        return new ResponseEntity<>(webHookService.createWebHook(projectId,webHookVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "更新webhook")
    @PutMapping
    public ResponseEntity<WebHookDTO> update(@PathVariable("project_id") Long projectId, @RequestBody @Validated WebHookDTO webHookDTO) {
        return new ResponseEntity<>(webHookService.updateWebHook(projectId, webHookDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "删除webhook")
    @DeleteMapping("/{id}")
    public ResponseEntity<WebHookDTO> delete(@PathVariable("id") Long id) {
        return new ResponseEntity<>(webHookService.deleteWebHook(id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "禁用webhook")
    @PutMapping("/{id}/disable")
    public ResponseEntity<WebHookDTO> disable(@PathVariable("id") Long id) {
        return new ResponseEntity<>(webHookService.disableWebHook(id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "启用webhook")
    @PutMapping("/{id}/enable")
    public ResponseEntity<WebHookDTO> enable(@PathVariable("id") Long id) {
        return new ResponseEntity<>(webHookService.enableWebHook(id), HttpStatus.OK);
    }

}
