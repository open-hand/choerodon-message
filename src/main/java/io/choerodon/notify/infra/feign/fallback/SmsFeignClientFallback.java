package io.choerodon.notify.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.notify.NoticeSendDTO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.infra.feign.SmsFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author superlee
 * @since 2019-05-17
 **/
@Component
public class SmsFeignClientFallback implements SmsFeignClient {

    @Override
    public ResponseEntity<List<UserDTO>> send(NoticeSendDTO noticeSendDTO) {
        throw new CommonException("error.sms.send");
    }
}
