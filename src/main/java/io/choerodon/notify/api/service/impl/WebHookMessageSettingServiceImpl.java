package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.notify.api.service.WebHookMessageSettingService;
import io.choerodon.notify.infra.dto.WebHookMessageSettingDTO;
import io.choerodon.notify.infra.mapper.WebHookMessageSettingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class WebHookMessageSettingServiceImpl implements WebHookMessageSettingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookMessageSettingServiceImpl.class);


    private WebHookMessageSettingMapper webHookMessageSettingMapper;

    public WebHookMessageSettingServiceImpl(WebHookMessageSettingMapper webHookMessageSettingMapper) {
        this.webHookMessageSettingMapper = webHookMessageSettingMapper;
    }

    @Override
    public void deleteByWebHookId(Long webHookId) {
        webHookMessageSettingMapper.delete(new WebHookMessageSettingDTO().setWebhookId(webHookId));
    }

    @Override
    public List<WebHookMessageSettingDTO> getByWebHookId(Long webHookId) {
        return webHookMessageSettingMapper.select(new WebHookMessageSettingDTO().setWebhookId(webHookId));
    }

    @Override
    public List<WebHookMessageSettingDTO> update(Long webHookId, Set<Long> sendSettingIds) {
        //1.删除全部关联
        deleteByWebHookId(webHookId);
        //2.新增全部关联
        if (CollectionUtils.isEmpty(sendSettingIds)) {
            return new ArrayList<>();
        }
        sendSettingIds.forEach(sendSettingId -> {
            if (webHookMessageSettingMapper.insertSelective(new WebHookMessageSettingDTO().setWebhookId(webHookId).setSendSettingId(sendSettingId)) != 1) {
                throw new InsertException("error.web.hook.message.setting.insert");
            }
        });
        return getByWebHookId(webHookId);
    }
}
