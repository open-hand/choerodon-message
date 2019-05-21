package io.choerodon.notify.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.notify.api.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("v1/notices/files")
@Api("文件上传接口")
public class FileController {

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "上传文件")
    @PostMapping
    public ResponseEntity<List<String>> uploadFile(HttpServletRequest request) {
        return new ResponseEntity<>(fileService.uploadFile(request), HttpStatus.OK);
    }

}
