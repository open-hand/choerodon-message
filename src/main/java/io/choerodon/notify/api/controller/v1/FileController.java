package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.service.FileService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("v1/notices/files")
public class FileController {

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "上传文件")
    @PostMapping
    public ResponseEntity<String> uploadPhoto(@RequestPart MultipartFile file) {
        return new ResponseEntity<>(fileService.uploadFile(file), HttpStatus.OK);
    }

}
