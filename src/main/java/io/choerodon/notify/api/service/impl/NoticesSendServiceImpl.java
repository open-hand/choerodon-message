package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.dto.ScheduleTaskDTO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.api.service.*;
import io.choerodon.notify.infra.dto.NotifyScheduleRecordDTO;
import io.choerodon.notify.infra.dto.ReceiveSettingDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.enums.SenderType;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.feign.AsgardFeignClient;
import io.choerodon.notify.infra.feign.UserFeignClient;
import io.choerodon.notify.infra.mapper.NotifyScheduleRecordMapper;
import io.choerodon.notify.infra.mapper.ReceiveSettingMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoticesSendServiceImpl implements NoticesSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticesSendServiceImpl.class);
    private static final String SITE_SCHEDULE_NOTYFICATION_CODE = "scheduleNotice";
    private EmailSendService emailSendService;

    private WebSocketSendService webSocketSendService;
    private WebHookService webHookService;
    private ReceiveSettingMapper receiveSettingMapper;
    private SendSettingMapper sendSettingMapper;
    private UserFeignClient userFeignClient;
    private AsgardFeignClient asgardFeignClient;
    private SmsService smsService;
    private NotifyScheduleRecordMapper notifyScheduleRecordMapper;
    private MessageSettingService messageSettingService;

    public NoticesSendServiceImpl(EmailSendService emailSendService,
                                  @Qualifier("pmWsSendService") WebSocketSendService webSocketSendService,
                                  WebHookService webHookService, ReceiveSettingMapper receiveSettingMapper,
                                  SendSettingMapper sendSettingMapper,
                                  UserFeignClient userFeignClient,
                                  AsgardFeignClient asgardFeignClient,
                                  SmsService smsService,
                                  NotifyScheduleRecordMapper notifyScheduleRecordMapper,
                                  MessageSettingService messageSettingService) {
        this.emailSendService = emailSendService;
        this.webSocketSendService = webSocketSendService;
        this.webHookService = webHookService;
        this.receiveSettingMapper = receiveSettingMapper;
        this.sendSettingMapper = sendSettingMapper;
        this.userFeignClient = userFeignClient;
        this.smsService = smsService;
        this.asgardFeignClient = asgardFeignClient;
        this.notifyScheduleRecordMapper = notifyScheduleRecordMapper;
        this.messageSettingService = messageSettingService;
    }

    //单元测试
    public void setUserFeignClient(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public void testEmailConnect(EmailConfigDTO config) {
        emailSendService.testEmailConnect(config);
    }

    /**
     * 删除定时任务
     *
     * @param notifyScheduleRecordDTO
     */
    @Override
    public void deleteScheduleNotice(NotifyScheduleRecordDTO notifyScheduleRecordDTO) {
        NotifyScheduleRecordDTO notifyDTO = new NotifyScheduleRecordDTO();
        notifyDTO.setScheduleNoticeCode(notifyScheduleRecordDTO.getScheduleNoticeCode());
        NotifyScheduleRecordDTO result = notifyScheduleRecordMapper.selectOne(notifyDTO);
        if (result == null) {
            throw new CommonException("error.delete.notice.not.match");
        }
        asgardFeignClient.deleteSiteTaskByTaskId(result.getTaskId());
        if (notifyScheduleRecordMapper.deleteByPrimaryKey(result.getId()) != 1) {
            throw new CommonException("error.notice.delete");
        }
    }

    /**
     * 更新定时任务
     *
     * @param scheduleNoticeCode
     * @param noticeSendDTO
     * @param date
     * @param isNewNotice
     */
    @Override
    public void updateScheduleNotice(String scheduleNoticeCode, Date date, NoticeSendDTO noticeSendDTO, Boolean isNewNotice) {
        NotifyScheduleRecordDTO notifyScheduleRecordDTO = new NotifyScheduleRecordDTO();
        notifyScheduleRecordDTO.setScheduleNoticeCode(scheduleNoticeCode);
        NotifyScheduleRecordDTO result = notifyScheduleRecordMapper.selectOne(notifyScheduleRecordDTO);
        if (result == null) {
            throw new CommonException("error.update.notify.not.exist");
        }
        asgardFeignClient.deleteSiteTaskByTaskId(result.getTaskId());
        if (!isNewNotice) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                noticeSendDTO = objectMapper.readValue(result.getNoticeContent(), NoticeSendDTO.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        notifyScheduleRecordMapper.deleteByPrimaryKey(result.getId());
        sendScheduleNotice(noticeSendDTO, date, scheduleNoticeCode);
    }

    @Override
    public void sendNotice(NoticeSendDTO noticeSendDTO) {
        LOGGER.info(">>>START_SENDING_MESSAGE>>>");
        // 0 发送短信
        if (!ObjectUtils.isEmpty(noticeSendDTO) && !ObjectUtils.isEmpty(noticeSendDTO.isSendingSMS()) && noticeSendDTO.isSendingSMS()) {
            smsService.send(noticeSendDTO);
        }

        // 0.1 校验SendSetting是否存在 : 不存在 则 取消发送
        SendSettingDTO sendSettingDTO = sendSettingMapper.selectOne(new SendSettingDTO().setCode(noticeSendDTO.getCode()));
        if (ObjectUtils.isEmpty(sendSettingDTO)) {
            LOGGER.warn(">>>CANCEL_SENDING>>> The send setting code does not exist.[INFO:send_setting_code:'{}']", noticeSendDTO.getCode());
            return;
        }
        // 0.2 校验SendSetting启用状态 / 发送方式启用状态 : 如停用 或 发送方式皆不启用 则 取消发送
        if (!sendSettingDTO.getEnabled() ||
                !(sendSettingDTO.getEmailEnabledFlag() || sendSettingDTO.getPmEnabledFlag() ||
                        sendSettingDTO.getSmsEnabledFlag() || sendSettingDTO.getWebhookEnabledFlag())) {
            LOGGER.warn(">>>CANCEL_SENDING>>> The send setting has been disabled OR all sending types for this send setting have been disabled.[INFO:send_setting_code:'{}']", noticeSendDTO.getCode());
            return;
        }
        // 0.3 校验发送对象不为空 : 如发送对象为空，则取消此次发送
        if (ObjectUtils.isEmpty(noticeSendDTO.getTargetUsers())) {
            LOGGER.warn(">>>CANCEL_SENDING>>> No sending receiver is specified");
            return;
        }
        // 1.获取发送对象
        Set<UserDTO> users = getNeedSendUsers(noticeSendDTO);

        //1.5 项目层校验发送对象
//        messageSettingService.checkTargetUser(users,noticeSendDTO.getCode())

        // 2.获取是否启用自定义发送类型
        boolean customizedSendingTypesFlag = !CollectionUtils.isEmpty(noticeSendDTO.getCustomizedSendingTypes());
        LOGGER.info(">>>WHETHER_TO_CUSTOMIZE_THE_CONFIGURATION>>>{}>>>email:{}>>>pm:{}>>>sms:{}>>>wb:{}", customizedSendingTypesFlag, noticeSendDTO.isSendingEmail(), noticeSendDTO.isSendingSiteMessage(), noticeSendDTO.isSendingSMS(), noticeSendDTO.isSendingWebHook());
        // 3.1.发送邮件
        if (((customizedSendingTypesFlag && noticeSendDTO.isSendingEmail()) || !customizedSendingTypesFlag) && sendSettingDTO.getEmailEnabledFlag()) {
            trySendEmail(noticeSendDTO, sendSettingDTO, users);
        }
        // 3.2.发送站内信
        if (((customizedSendingTypesFlag && noticeSendDTO.isSendingSiteMessage()) || !customizedSendingTypesFlag) && sendSettingDTO.getPmEnabledFlag()) {
            trySendSiteMessage(noticeSendDTO, sendSettingDTO, users);
        }
        // 3.3.发送WebHook
        if (((customizedSendingTypesFlag && noticeSendDTO.isSendingWebHook()) || !customizedSendingTypesFlag) && sendSettingDTO.getWebhookEnabledFlag()) {
            trySendWebHook(noticeSendDTO, sendSettingDTO, users);
        }

    }

    @Override
    public Long sendScheduleNotice(NoticeSendDTO dto, Date date, String scheduleNoticeCode) {
        Long methodId = asgardFeignClient.getMethodIdByCode(SITE_SCHEDULE_NOTYFICATION_CODE).getBody();
        Long[] assignUserIds = new Long[1];
        assignUserIds[0] = DetailsHelper.getUserDetails().getUserId();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = new String();
        try {
            jsonStr = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            LOGGER.error("json translation failed!", e);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("noticeSendDTO", jsonStr);
        ScheduleTaskDTO createTskDTO = new ScheduleTaskDTO(
                methodId, params, "通知消息", "消息信息", date, assignUserIds);
        Long taskId = asgardFeignClient.createSiteScheduleTask(createTskDTO).getBody().getId();
        //存储定时任务和消息的映射关系
        NotifyScheduleRecordDTO notifyScheduleRecordDTO = new NotifyScheduleRecordDTO();
        notifyScheduleRecordDTO.setTaskId(taskId);
        notifyScheduleRecordDTO.setScheduleNoticeCode(scheduleNoticeCode);
        notifyScheduleRecordDTO.setNoticeContent(jsonStr);
        if (notifyScheduleRecordMapper.insertSelective(notifyScheduleRecordDTO) != 1) {
            throw new InsertException("error.insert.scheduleTaskRecord");
        }
        return taskId;
    }

    /**
     * 通知消息 JobTask
     *
     * @param map 参数map
     */
    @JobTask(maxRetryCount = 0, code = "scheduleNotice", params = {
            @JobParam(name = "noticeSendDTO", description = "参数")
    }, description = "发送通知消息")
    public void scheduleNotice(Map<String, Object> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        NoticeSendDTO dto = null;
        try {
            dto = objectMapper.readValue((Optional.ofNullable((String) map.get("noticeSendDTO"))).orElseThrow(() -> new CommonException("error.systemNotification.id.empty")), NoticeSendDTO.class);
        } catch (IOException e) {
            LOGGER.error("send email failed!", e);
        }
        sendNotice(dto);
    }

    /**
     * 发送邮件
     * 需要捕获异常并LOG
     *
     * @param noticeSendDTO  发送信息
     * @param sendSettingDTO 发送设置信息
     * @param users          用户
     */
    private void trySendEmail(NoticeSendDTO noticeSendDTO, SendSettingDTO sendSettingDTO, final Set<UserDTO> users) {
        try {
            //1.获取邮件接收用户
            Set<UserDTO> mailRecipient = getNeedReceiveNoticeTargetUsers(noticeSendDTO, sendSettingDTO, users, SendingTypeEnum.EMAIL);
            //2.发送邮件
            emailSendService.sendEmail(noticeSendDTO.getParams(), mailRecipient, sendSettingDTO);
        } catch (Exception e) {
            LOGGER.warn(">>>SENDING_EMAIL_ERROR>>> An error occurred while sending the message.", e);
        }
    }

    /**
     * 发送WebHook
     * 需要捕获异常并LOG
     *
     * @param noticeSendDTO  发送信息
     * @param sendSettingDTO 发送设置信息
     * @param users          用户
     */
    private void trySendWebHook(NoticeSendDTO noticeSendDTO, SendSettingDTO sendSettingDTO, final Set<UserDTO> users) {
        try {
            //1.获取邮件接收用户
            Set<String> mobiles = users.stream().map(UserDTO::getPhone).collect(Collectors.toSet());
            //2.发送邮件
            webHookService.trySendWebHook(noticeSendDTO, sendSettingDTO, mobiles);
        } catch (Exception e) {
            LOGGER.warn(">>>SENDING_WEBHOOL_ERROR>>> An error occurred while sending the message.", e);
        }
    }

    /**
     * 发送站内信
     * 需要捕获异常并LOG
     *
     * @param noticeSendDTO  发送信息
     * @param sendSettingDTO 发送设置信息
     * @param users          用户
     */
    private void trySendSiteMessage(NoticeSendDTO noticeSendDTO, SendSettingDTO sendSettingDTO, final Set<UserDTO> users) {
        try {
            //1.获取站内信接收用户
            Set<UserDTO> needSendPmUsers = getNeedReceiveNoticeTargetUsers(noticeSendDTO, sendSettingDTO, users, SendingTypeEnum.PM);

            //2.获取发送方信息
            Map<String, Long> sender = new HashMap<>(5);
            String senderType = getSenderDetail(noticeSendDTO, sender, sendSettingDTO);

            //3.发送站内信
            webSocketSendService.sendSiteMessage(noticeSendDTO.getCode(), noticeSendDTO.getParams(), needSendPmUsers,
                    sender.get(senderType), senderType, sendSettingDTO);
        } catch (Exception e) {
            LOGGER.warn(">>>SENDING_SITE_MESSAGE_ERROR>>> An error occurred while sending the message.", e);
        }
    }

    /**
     * sender type is user/project/organization/site
     *
     * @param dto NoticeSendDTO
     * @return sender type
     */
    private String getSenderDetail(NoticeSendDTO dto, Map<String, Long> map, SendSettingDTO sendSetting) {
        NoticeSendDTO.User user = dto.getFromUser();
        //设置默认发送者为平台
        String senderType = SenderType.SITE.value();
        map.put(senderType, 0L);
        if (user == null) {
            String sendSettingLevel = sendSetting.getLevel();
            if (SenderType.ORGANIZATION.value().equals(sendSettingLevel)) {
                senderType = sendSettingLevel;
                Long organizationId = dto.getSourceId();
                map.put(senderType, organizationId);
            } else if (SenderType.PROJECT.value().equals(sendSettingLevel)) {
                senderType = sendSettingLevel;
                Long projectId = dto.getSourceId();
                map.put(senderType, projectId);
            }
        } else {
            senderType = SenderType.USER.value();
            map.put(senderType, user.getId());
        }
        return senderType;
    }

    /**
     * 取得需要发送通知的用户
     */
    private Set<UserDTO> getNeedSendUsers(final NoticeSendDTO dto) {
        Set<UserDTO> users = new HashSet<>();
        // 取得User中id不为空的id，将发起feign调用
        Set<Long> needQueryUserIds = dto.getTargetUsers().stream().filter(user -> user.getId() != null).map(NoticeSendDTO.User::getId).collect(Collectors.toSet());
        if (!needQueryUserIds.isEmpty()) {
            Long[] userIds = needQueryUserIds.toArray(new Long[0]);
            //TODO 如果用户太多，需要多次查询
            users.addAll(userFeignClient.listUsersByIds(userIds).getBody());
        }
        // 取得User中id为空且email不为空的email，将发起feign调用
        Set<String> needQueryEmails = dto.getTargetUsers().stream().filter(user -> user.getId() == null && user.getEmail() != null).map(NoticeSendDTO.User::getEmail).collect(Collectors.toSet());
        if (!needQueryEmails.isEmpty()) {
            String[] emails = needQueryEmails.toArray(new String[0]);
            // 注册组织时没有真正的用户，只有email,因此伪造一个UserDTO
            //TODO 如果用户太多，需要多次查询
            List<UserDTO> emailUsers = userFeignClient.listUsersByEmails(emails).getBody();
            Set<String> queryEmails = emailUsers.stream().map(UserDTO::getEmail).collect(Collectors.toSet());
            List<UserDTO> onlyEmailUsers = needQueryEmails.stream().filter(s -> !queryEmails.contains(s)).map(email -> {
                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(email);
                return userDTO;
            }).collect(Collectors.toList());
            users.addAll(emailUsers);
            users.addAll(onlyEmailUsers);
        }
        return users;
    }


    /**
     * 如果发送设置允许配置通知
     * 则得到没有禁用接收通知的用户
     * 否则得到全部用户
     */
    private Set<UserDTO> getNeedReceiveNoticeTargetUsers(final NoticeSendDTO noticeSendDTO, final SendSettingDTO sendSettingDTO, final Set<UserDTO> users, final SendingTypeEnum type) {
        //1.是否允许用户配置拒绝接收
        if (!sendSettingDTO.getAllowConfig()) {
            return users;
        }
        //2.过滤去除拒绝接收的用户
        return users.stream().filter(user -> {
            new ReceiveSettingDTO().setUserId(user.getId());
            ReceiveSettingDTO setting = new ReceiveSettingDTO(sendSettingDTO.getId(), type.getValue(), noticeSendDTO.getSourceId(), sendSettingDTO.getLevel(), user.getId());
            return receiveSettingMapper.selectCount(setting) == 0;
        }).collect(Collectors.toSet());
    }
}
