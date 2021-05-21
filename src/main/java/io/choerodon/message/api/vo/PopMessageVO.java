package io.choerodon.message.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wangxiang on 2021/5/18
 */
public class PopMessageVO {
    @ApiModelProperty("弹窗的内容")
    private String content;


    @ApiModelProperty("消息主题")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
