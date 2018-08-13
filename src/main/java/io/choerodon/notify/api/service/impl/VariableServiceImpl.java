package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.service.VariableService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VariableServiceImpl implements VariableService {

    @Override
    public Map<String, Object> getVariables() {
        Map<String, Object> variables = new HashMap<>(1 << 4);
        return variables;
    }
}
