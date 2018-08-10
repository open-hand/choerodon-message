package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.EmailConfigDTO;

public interface ConfigService {

    EmailConfigDTO create(EmailConfigDTO configDTO);

    EmailConfigDTO update(EmailConfigDTO configDTO);

    EmailConfigDTO selectEmail();

}
