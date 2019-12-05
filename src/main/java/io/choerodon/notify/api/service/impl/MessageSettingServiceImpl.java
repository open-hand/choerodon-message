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
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public Long[] checkTargetUser(Long projectId, String ids, String code) {
        if (StringUtils.isEmpty(ids)) {
            return new Long[0];
        }
        //校验接收对象是否正确
        Set<Long> idsList = new HashSet<>();
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setCode(code);
        messageSettingDTO.setProjectId(projectId);
        MessageSettingDTO messageSettingDTO1 = messageSettingMapper.selectOne(messageSettingDTO);
        if (Objects.isNull(messageSettingDTO1)) {
            return new Long[0];
        }
        TargetUserDTO targetUserDTO = new TargetUserDTO();
        targetUserDTO.setMessageSettingId(messageSettingDTO1.getId());
        List<TargetUserDTO> targetUserDTOS = targetUserMapper.select(targetUserDTO);
        if (targetUserDTOS == null || targetUserDTOS.size() == 0) {
            return new Long[0];
        }
        String idsStr = targetUserDTOS.stream()
                .filter(e -> !Objects.isNull(e.getUserId()))
                .map(e -> e.getUserId())
                .collect(Collectors.joining(","));
        Set<Long> targetIds = Stream.of(idsStr.split(",")).map(e -> Long.valueOf(e)).collect(Collectors.toSet());
        Set<Long> originalIds = new HashSet<>();
        if (ids.contains(",")) {
            Set<Long> longSet = Arrays.stream(ids.trim().split(",")).map(e -> Long.valueOf(e)).collect(Collectors.toSet());
            originalIds.addAll(longSet);
        } else {
            originalIds.add(Long.valueOf(ids));
        }
        Set<Long> allowTargetIds = new HashSet<>();
        allowTargetIds.addAll(targetIds);
        allowTargetIds.retainAll(originalIds);
        return allowTargetIds.toArray(new Long[allowTargetIds.size()]);
    }
}
