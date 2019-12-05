package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.api.service.MessageSettingService;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import io.choerodon.notify.infra.dto.TargetUserDTO;
import io.choerodon.notify.infra.mapper.MessageSettingMapper;
import io.choerodon.notify.infra.mapper.TargetUserMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
@Service
public class MessageSettingServiceImpl implements MessageSettingService {
    @Autowired
    private MessageSettingMapper messageSettingMapper;

    @Autowired
    private TargetUserMapper targetUserMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<MessageSettingVO> listMessageSetting(Long projectId, MessageSettingVO messageSettingVO) {
        return modelMapper.map(messageSettingMapper.listMessageSettingByCondition(modelMapper.map(messageSettingVO,
                MessageSettingDTO.class)), new TypeToken<List<MessageSettingVO>>() {
        }.getType());
    }

    @Override
    @Transactional
    public void updateMessageSetting(List<MessageSettingVO> messageSettingVOS) {
        if (messageSettingVOS == null || messageSettingVOS.size() == 0) {
            return;
        }
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setNotifyType(messageSettingVOS.get(0).getNotifyType());
        List<MessageSettingDTO> messageSettingDTOS = messageSettingMapper.listMessageSettingByCondition(messageSettingDTO);
        TargetUserDTO targetUserDTO = new TargetUserDTO();
        messageSettingDTOS.stream().forEach(e -> {
            targetUserDTO.setMessageSettingId(e.getId());
            targetUserMapper.delete(targetUserDTO);
            MessageSettingDTO messageSettingDTO1 = new MessageSettingDTO();
            messageSettingDTO1.setId(e.getId());
            messageSettingMapper.delete(messageSettingDTO1);
        });

        messageSettingVOS.stream().forEach(e -> {
            messageSettingMapper.insertSelective(modelMapper.map(e, MessageSettingDTO.class));
            List<TargetUserDTO> targetUserDTOS = e.getTargetUserDTOS();
            targetUserDTOS.stream().forEach(v -> {
                targetUserDTO.setId(v.getId());
                targetUserDTO.setMessageSettingId(e.getId());
                targetUserDTO.setUserId(v.getUserId());
                targetUserDTO.setType(v.getType());
                targetUserMapper.insert(targetUserDTO);
            });
        });
    }

    @Override
    public Long[] checkTargetUser(Long[] ids, String code) {
        //校验接收对象是否正确
        ArrayList arrayList = new ArrayList();
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setCode(code);
        MessageSettingDTO messageSettingDTO1 = messageSettingMapper.selectOne(messageSettingDTO);
        if (Objects.isNull(messageSettingDTO1) || ids == null || ids.length == 0) {
            return new Long[0];
        }
        return new Long[0];
    }
}
