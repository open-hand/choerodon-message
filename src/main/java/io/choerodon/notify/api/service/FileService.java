package io.choerodon.notify.api.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

     String uploadFile(MultipartFile file);

}
