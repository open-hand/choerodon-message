package io.choerodon.notify.api.service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FileService {

    List<String> uploadFile(HttpServletRequest request);

}
