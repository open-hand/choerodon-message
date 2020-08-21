package io.choerodon.message.api.vo;

import java.util.List;

import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.message.infra.dto.MessageSettingDTO;

/**
 * User: Mr.Wang
 * Date: 2019/12/10
 */

public class MessageSettingCategoryVO {
    @Encrypt
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
