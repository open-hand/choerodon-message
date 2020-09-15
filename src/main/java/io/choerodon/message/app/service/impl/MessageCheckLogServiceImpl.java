package io.choerodon.message.app.service.impl;

import com.zaxxer.hikari.util.UtilityElf;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.entity.TemplateArg;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.infra.mapper.MessageTemplateMapper;
import org.hzero.message.infra.mapper.TemplateArgMapper;
import org.hzero.message.infra.mapper.TemplateServerLineMapper;
import org.hzero.message.infra.mapper.TemplateServerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.message.app.service.MessageCheckLogService;
import io.choerodon.message.infra.dto.NotifyMessageSettingConfigDTO;
import io.choerodon.message.infra.mapper.NotifyMessageSettingConfigMapper;

/**
 * Created by wangxiang on 2020/9/15
 */
@Service
public class MessageCheckLogServiceImpl implements MessageCheckLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCheckLogServiceImpl.class);

    private static final ExecutorService executorService = new ThreadPoolExecutor(0, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new UtilityElf.DefaultThreadFactory("message-upgrade", false));

    @Autowired
    private MessageTemplateMapper messageTemplateMapper;

    @Autowired
    private TemplateArgMapper templateArgMapper;

    @Autowired
    private TemplateServerMapper templateServerMapper;

    @Autowired
    private TemplateServerLineMapper templateServerLineMapper;

    @Autowired
    private NotifyMessageSettingConfigMapper notifyMessageSettingConfigMapper;


    @Override
    public void checkLog(String version) {
        LOGGER.info("start upgrade task");
        executorService.execute(new UpgradeTask(version));

    }

    class UpgradeTask implements Runnable {
        private String version;

        UpgradeTask(String version) {
            this.version = version;
        }

        @Override
        public void run() {
            try {
                if (StringUtils.equalsIgnoreCase("0.24.0", version.trim())) {
                    clearTemplate();
                }
            } catch (Exception ex) {
                LOGGER.warn("Exception occurred when applying data migration. The ex is: {}", ex.getMessage());
            }
        }

    }

    private void clearTemplate() {
        List<String> templateCodeList = Arrays.asList("INSTANCE_FAILURE.WEB", "INSTANCE_FAILURE.EMAIL");
        for (String code : templateCodeList) {
            MessageTemplate messageTemplate = new MessageTemplate();
            messageTemplate.setTemplateCode(code);
            MessageTemplate template = messageTemplateMapper.selectByCode(0L, code);
            if (template == null) {
                continue;
            }
            TemplateArg templateArg = new TemplateArg();
            templateArg.setTemplateId(template.getTemplateId());
            templateArgMapper.delete(templateArg);
            messageTemplateMapper.delete(messageTemplate);
        }
        TemplateServer templateServer = new TemplateServer();
        templateServer.setMessageCode("INSTANCEFAILURE");
        TemplateServer server = templateServerMapper.selectOne(templateServer);
        if (server == null) {
            return;
        }
        TemplateServerLine templateServerLine = new TemplateServerLine();
        templateServerLine.setTempServerId(server.getTempServerId());
        templateServerLineMapper.delete(templateServerLine);

        templateServerMapper.deleteByPrimaryKey(server.getTempServerId());

        NotifyMessageSettingConfigDTO notifyMessageSettingConfigDTO = new NotifyMessageSettingConfigDTO();
        notifyMessageSettingConfigDTO.setMessageCode("INSTANCEFAILURE");
        notifyMessageSettingConfigMapper.delete(notifyMessageSettingConfigDTO);

    }
}
