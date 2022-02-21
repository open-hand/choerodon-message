package org.hzero.starter.sms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.JsonUtils;
import org.hzero.starter.sms.configuration.SmsConfigProperties;
import org.hzero.starter.sms.constant.HuaweiSmsConstant;
import org.hzero.starter.sms.constant.SmsConstant;
import org.hzero.starter.sms.entity.SmsConfig;
import org.hzero.starter.sms.entity.SmsMessage;
import org.hzero.starter.sms.entity.SmsReceiver;
import org.hzero.starter.sms.support.HuaweiSmsSupporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * 华为云短信发送
 *
 * @author dehui.ren@hand-china.com 2021/12/17 11:15
 */
@Component
public class HuaweiSmsServiceImpl extends SmsService {

    private final SmsConfigProperties configProperties;

    @Autowired
    public HuaweiSmsServiceImpl(SmsConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public String serverType() {
        return "HUAWEI";
    }

    @Override
    public void smsSend(List<SmsReceiver> receiverAddressList, SmsConfig smsConfig, SmsMessage message, Map<String, String> args) {
        // 获取接收人信息
        List<String> telephoneList = new ArrayList<>();
        if (configProperties.getSms().isFakeAction() && StringUtils.isNotBlank(configProperties.getSms().getFakeAccount())) {
            telephoneList = Collections.singletonList(configProperties.getSms().getFakeAccount());
        } else if (configProperties.getSms().isFakeAction()) {
            return;
        } else {
            for (SmsReceiver item : receiverAddressList) {
                String idd = StringUtils.isNotBlank(item.getIdd()) ? item.getIdd() : SmsConstant.DEFAULT_IDD;
                String phone = item.getPhone();
                telephoneList.add(idd + phone);
            }
        }
        // 必填,接收人信息，多个号码之间用英文逗号分隔
        String receiver = StringUtils.join(telephoneList, BaseConstants.Symbol.COMMA);

        // 获取拓展参数
        Map<String, String> param = JsonUtils.fromJsonOrDefault(smsConfig.getExtParam(), new TypeReference<Map<String, String>>() {
        }, new HashMap<>(1));
        // 必填,国内短信签名通道号或国际/港澳台短信通道号
        String sender = param.get(HuaweiSmsConstant.HuaweiParams.SENDER);
        // 必填
        String appKey = smsConfig.getAccessKey();
        // 必填
        String appSecret = smsConfig.getAccessKeySecret();
        // 必填,APP接入地址
        String url = smsConfig.getEndPoint();
        // 条件必填,国内短信关注,当templateId指定的模板类型为通用模板时生效且必填,必须是已审核通过的,与模板类型一致的签名名称
        // 国际/港澳台短信不用关注该参数
        String signature = smsConfig.getSignName();
        // 必填,模板ID
        String templateId = message.getExternalCode();
        // 获取模板参数
        String[] params;
        if (args.size() > 0) {
            params = new String[args.size()];
            List<String> argsList = getTemplateArgs();
            for (int i = 0; i < argsList.size(); i++) {
                String arg = argsList.get(i);
                if (args.containsKey(arg)) {
                    params[i] = "\"" + args.get(arg) + "\"";
                } else {
                    throw new CommonException("No arg found for template " + message.getTemplateCode() + " where arg named " + arg);
                }
            }
        } else {
            params = new String[]{""};
        }
        // 短信模板参数,华为云要求模板参数须满足"[\"3\",\"人民公园正门\"]"的格式
        String templateParas = String.join(BaseConstants.Symbol.COMMA, params);
        if (StringUtils.isNotBlank(templateParas)) {
            templateParas = "[" + templateParas + "]";
        }


        try {
            HuaweiSmsSupporter.sendSms(sender, receiver, templateId, templateParas, null, signature, appKey, appSecret, url);
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }
}
