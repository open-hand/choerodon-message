package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.EmailConfigDTO;

public interface ConfigService {

    EmailConfigDTO save(EmailConfigDTO configDTO);

    EmailConfigDTO selectEmail();

}
