package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.PmSendDTO;

public interface PmSendService {

    void send(PmSendDTO dto);

}
