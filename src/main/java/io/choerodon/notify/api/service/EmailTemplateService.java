package io.choerodon.notify.api.service;

import io.choerodon.swagger.notify.NotifyTemplateScanData;

import java.util.Set;

public interface EmailTemplateService {

    void createByScan(Set<NotifyTemplateScanData> set);

}
