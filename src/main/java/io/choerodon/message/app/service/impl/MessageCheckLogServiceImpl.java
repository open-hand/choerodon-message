package io.choerodon.message.app.service.impl;

import com.zaxxer.hikari.util.UtilityElf;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
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
import org.springframework.util.CollectionUtils;

import io.choerodon.core.enums.TargetUserType;
import io.choerodon.message.api.vo.UserVO;
import io.choerodon.message.app.service.MessageCheckLogService;
import io.choerodon.message.infra.dto.MessageSettingDTO;
import io.choerodon.message.infra.dto.NotifyMessageSettingConfigDTO;
import io.choerodon.message.infra.dto.TargetUserDTO;
import io.choerodon.message.infra.feign.IamFeignClient;
import io.choerodon.message.infra.feign.operator.IamClientOperator;
import io.choerodon.message.infra.mapper.MessageSettingC7nMapper;
import io.choerodon.message.infra.mapper.MessageSettingTargetUserC7nMapper;
import io.choerodon.message.infra.mapper.NotifyMessageSettingConfigMapper;
import io.choerodon.message.infra.utils.OptionalBean;

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

    @Autowired
    private MessageSettingC7nMapper messageSettingC7nMapper;

    @Autowired
    private MessageSettingTargetUserC7nMapper messageSettingTargetUserC7nMapper;

    @Autowired
    private IamClientOperator iamClientOperator;

    @Autowired
    private IamFeignClient iamFeignClient;


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
                if (StringUtils.equalsIgnoreCase("0.24.alpha", version.trim())) {
                    clearProjectMessageSetting();
                }
            } catch (Exception ex) {
                LOGGER.warn("Exception occurred when applying data migration. The ex is: {}", ex.getMessage());
            }
        }

    }

    private void clearProjectMessageSetting() {
        //查询项目下所有的自定义的通知设置，和指定用户
        //根据通知到的之指定用户，看看他们是否在项目下有角色
        //如果没有则清理
        // TODO: 2022/8/2 SourceLevel 
        List<MessageSettingDTO> messageSettingDTOS = messageSettingC7nMapper.selectAll().stream().filter(e -> e.getSourceId() != 0L).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(messageSettingDTOS)) {
            return;
        }
        messageSettingDTOS.forEach(settingDTO -> {

            TargetUserDTO targetUserDTO = new TargetUserDTO();
            targetUserDTO.setType(TargetUserType.SPECIFIER.getTypeName());
            targetUserDTO.setMessageSettingId(settingDTO.getId());

            List<TargetUserDTO> targetUserDTOS = messageSettingTargetUserC7nMapper.select(targetUserDTO);
            if (CollectionUtils.isEmpty(targetUserDTOS)) {
                return;
            }
            Set<Long> userIds = targetUserDTOS.stream().map(TargetUserDTO::getUserId).collect(Collectors.toSet());
            Map<Long, List<UserVO>> longListMap = iamClientOperator.listUsersByIds(userIds, Boolean.FALSE).stream().collect(Collectors.groupingBy(UserVO::getId));
            targetUserDTOS.forEach(targetUserDTO1 -> {
                //根据通知到的之指定用户，看看他们是否在项目下有角色
                UserVO userVO = longListMap.get(targetUserDTO1.getUserId()).get(0);
                // TODO: 2022/8/2 SourceId 
                boolean present = OptionalBean.ofNullable(iamClientOperator.getUser(settingDTO.getSourceId(), userVO.getLoginName())).getBean(UserVO::getRoles).isPresent();
                //user存在的时候返回
                if (present) {
                    return;
                }
                messageSettingTargetUserC7nMapper.delete(targetUserDTO1);
            });
        });
    }

    private void clearTemplate() {
        List<String> templateCodeList = Arrays.asList("INSTANCE_FAILURE.WEB", "INSTANCE_FAILURE.EMAIL");
        for (String code : templateCodeList) {
            MessageTemplate messageTemplate = new MessageTemplate();
            messageTemplate.setTemplateCode(code);
            messageTemplate.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
            MessageTemplate template = messageTemplateMapper.selectOne(messageTemplate);
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
