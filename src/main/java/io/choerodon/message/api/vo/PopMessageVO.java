package io.choerodon.message.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wangxiang on 2021/5/18
 */
public class PopMessageVO {
    @ApiModelProperty("弹窗的内容")
    private String content;

    @ApiModelProperty("状态：S 成功")
    private String status;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
