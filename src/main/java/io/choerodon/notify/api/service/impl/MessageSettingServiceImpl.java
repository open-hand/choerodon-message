package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.api.dto.TargetUserVO;
import io.choerodon.notify.api.service.MessageSettingService;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import io.choerodon.notify.infra.dto.TargetUserDTO;
import io.choerodon.notify.infra.feign.UserFeignClient;
import io.choerodon.notify.infra.mapper.MessageSettingMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TargetUserMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
@Service
public class MessageSettingServiceImpl implements MessageSettingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSettingServiceImpl.class);
    @Autowired
    private MessageSettingMapper messageSettingMapper;

    @Autowired
    private TargetUserMapper targetUserMapper;

    @Autowired
    private SendSettingMapper sendSettingMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<MessageSettingVO> listMessageSetting(Long projectId, MessageSettingVO messageSettingVO) {
        //如果根据project_id查不到数据，那么查默认的数据
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setProjectId(projectId);
        List<MessageSettingDTO> settingDTOS = messageSettingMapper.select(messageSettingDTO);
        if (Objects.isNull(settingDTOS) || settingDTOS.size() == 0) {
            return modelMapper.map(messageSettingMapper.listMessageSettingByCondition(null, modelMapper.map(messageSettingVO,
                    MessageSettingDTO.class)), new TypeToken<List<MessageSettingVO>>() {
            }.getType());
        } else {
            //如果project_id在后端有数据，那么有的照旧，没有的用默认的数据补上
            return modelMapper.map(messageSettingMapper.listMessageSettingByCondition(projectId, modelMapper.map(messageSettingVO,
                    MessageSettingDTO.class)), new TypeToken<List<MessageSettingVO>>() {
            }.getType());
        }
    }

    @Override
    @Transactional
    public void updateMessageSetting(Long projectId, List<MessageSettingVO> messageSettingVOS) {
        if (messageSettingVOS == null || messageSettingVOS.size() == 0 || projectId == null) {
            return;
        }
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setNotifyType(messageSettingVOS.get(0).getNotifyType());
        //判断这个是否首次修改
        TargetUserDTO targetUserDTO = new TargetUserDTO();
        messageSettingVOS.stream().forEach(e -> {
            messageSettingDTO.setProjectId(projectId);
            List<MessageSettingDTO> settingDTOS = messageSettingMapper.select(messageSettingDTO);
            //如果是首次修改则插入带ProjectId的数据
            if (Objects.isNull(settingDTOS) || settingDTOS.size() == 0) {
                e.setProjectId(projectId);
                messageSettingMapper.insertSelective(modelMapper.map(e, MessageSettingDTO.class));
                List<TargetUserDTO> targetUserDTOS = e.getTargetUserDTOS();
                targetUserDTOS.stream().forEach(v -> {
                    targetUserDTO.setId(v.getId());
                    targetUserDTO.setMessageSettingId(e.getId());
                    targetUserDTO.setUserId(v.getUserId());
                    targetUserDTO.setType(v.getType());
                    targetUserMapper.insert(targetUserDTO);
                });
            } else {
                //否则修改原来的数据
                List<MessageSettingDTO> settingDTOS1 = messageSettingMapper.listMessageSettingByCondition(projectId, messageSettingDTO);
                //先删除targetUser
                TargetUserDTO targetUserDTO1 = new TargetUserDTO();
                targetUserDTO1.setMessageSettingId(e.getId());
                int delete = targetUserMapper.delete(targetUserDTO1);
                //再删除MessageSetting
                MessageSettingDTO messageSettingDTO1 = new MessageSettingDTO();
                messageSettingDTO1.setId(e.getId());
                int delete1 = messageSettingMapper.delete(messageSettingDTO1);

                //然后插入更新后的数据
                e.setProjectId(projectId);
                int i = messageSettingMapper.insertSelective(modelMapper.map(e, MessageSettingDTO.class));
                List<TargetUserDTO> targetUserDTOS = e.getTargetUserDTOS();
                targetUserDTOS.stream().forEach(v -> {
                    targetUserDTO1.setId(v.getId());
                    targetUserDTO1.setMessageSettingId(e.getId());
                    targetUserDTO1.setUserId(v.getUserId());
                    targetUserDTO1.setType(v.getType());
                    int insert = targetUserMapper.insert(targetUserDTO1);
                });
            }
        });
    }

    @Override
    public List<TargetUserVO> getProjectLevelTargetUser(Long projectId, String code) {
        //校验接收对象是否正确,校验三个部分组成 平台层设置，项目层设置，个人接收设置
        //1.检验项目层发送设置是否存在，不存在就返回空集合
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setCode(code);
        messageSettingDTO.setProjectId(projectId);
        MessageSettingDTO messageSettingDTO1 = messageSettingMapper.selectOne(messageSettingDTO);
        if (Objects.isNull(messageSettingDTO1)) {
            LOGGER.warn(">>>CANCEL_SENDING>>> The message setting code does not exist.[INFO:message_setting_code:'{}']", code);
            return Collections.emptyList();
        }
        //2.发送设置存在返回该设置下的接收对象
        TargetUserDTO targetUserDTO = new TargetUserDTO();
        targetUserDTO.setMessageSettingId(messageSettingDTO1.getId());
        List<TargetUserDTO> targetUserDTOS = targetUserMapper.select(targetUserDTO);
        if (targetUserDTOS == null || targetUserDTOS.size() == 0) {
            LOGGER.warn(">>>CANCEL_SENDING>>> The message target user does not exist.[INFO:message_target_user_code:'{}']",
                    code);
            return Collections.emptyList();
        }
        return targetUserDTOS.stream().map(e -> modelMapper.map(e, TargetUserVO.class)).collect(Collectors.toList());
    }

    @Override
    public MessageSettingVO getMessageSetting(Long projectId, String code) {
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setProjectId(projectId);
        messageSettingDTO.setCode(code);
        List<MessageSettingDTO> settingDTOS = messageSettingMapper.queryByCodeOrProjectId(messageSettingDTO);
        if (settingDTOS != null && settingDTOS.size() > 0) {
            return modelMapper.map(settingDTOS.get(0), MessageSettingVO.class);
        }
        messageSettingDTO.setProjectId(null);
        List<MessageSettingDTO> messageSettingDTOS = messageSettingMapper.queryByCodeOrProjectId(messageSettingDTO);
        if (messageSettingDTOS == null || messageSettingDTOS.size() == 0) {
            LOGGER.warn(">>>CANCEL_SENDING>>> The message setting code does not exist.[INFO:message_setting_code:'{}']", code);
            return null;
        }
        return modelMapper.map(messageSettingDTOS.get(0), MessageSettingVO.class);
    }
}
