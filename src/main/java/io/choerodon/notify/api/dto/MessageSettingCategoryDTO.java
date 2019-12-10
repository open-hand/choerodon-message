package io.choerodon.notify.api.dto;

import io.choerodon.notify.infra.dto.MessageSettingDTO;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2019/12/10
 */

public class MessageSettingCategoryDTO {
    private Long id;
    private String name;
    private List<MessageSettingDTO> messageSettingDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MessageSettingDTO> getMessageSettingDTO() {
        return messageSettingDTO;
    }

    public void setMessageSettingDTO(List<MessageSettingDTO> messageSettingDTO) {
        this.messageSettingDTO = messageSettingDTO;
    }
}
