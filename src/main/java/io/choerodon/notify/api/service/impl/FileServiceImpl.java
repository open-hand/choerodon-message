package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.service.FileService;
import io.choerodon.notify.infra.feign.FileFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    private FileFeignClient fileFeignClient;

    public FileServiceImpl(FileFeignClient fileFeignClient) {
        this.fileFeignClient = fileFeignClient;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        return fileFeignClient.uploadFile("notify-service", file.getOriginalFilename(), file).getBody();
    }
}
