import React from 'react';
import { Form, CheckBox, Radio, NumberField, message } from 'choerodon-ui/pro';
import './index.less';

export default function ({ context, modal }) {
  const { messageTypeDetailDataSet } = context;
  modal.handleOk(async () => {
    try {
      if (!await messageTypeDetailDataSet.submit()) {
        return false;
      }
    } catch (err) {
      return false;
    }
  });
  modal.handleCancel(() => {
    messageTypeDetailDataSet.reset();
  });
  return (
    <div className="c7n-notify-contentList-sider">
      <div className="c7n-notify-contentList-sider-label">发送方式</div>
      <Form columns={4} dataSet={messageTypeDetailDataSet}>
        <CheckBox name="emailEnabledFlag" />
        <CheckBox name="pmEnabledFlag" />
        <CheckBox name="webhookEnabledFlag" />
        <CheckBox name="smsEnabledFlag" />
      </Form>
      <div className="c7n-notify-contentList-sider-label">是否允许配置接收</div>
      <Form columns={4} dataSet={messageTypeDetailDataSet}>
        <Radio colSpan={1} name="allowConfig" value>是</Radio>
        <Radio colSpan={2} name="allowConfig" value={false}>否</Radio>
      </Form>
      <div className="c7n-notify-contentList-sider-label">是否即时发送</div>
      <Form columns={4} dataSet={messageTypeDetailDataSet}>
        <Radio colSpan={1} newLine name="isSendInstantly" value>是</Radio>
        <Radio colSpan={2} name="isSendInstantly" value={false}>否</Radio>
      </Form>
      <Form columns={4} dataSet={messageTypeDetailDataSet}>
        <NumberField colSpan={4} newLine name="retryCount" help="邮件发送失败后系统默认重发的次数" showHelp="tooltip" />
      </Form>
      <div className="c7n-notify-contentList-sider-label">是否允许手动重发邮件</div>
      <Form columns={4} dataSet={messageTypeDetailDataSet}>
        <Radio colSpan={1} newLine name="isManualRetry" value>是</Radio>
        <Radio colSpan={2} name="isManualRetry" value={false}>否</Radio>
      </Form>
      <div className="c7n-notify-contentList-sider-label">是否为待办提醒</div>
      <Form columns={4} dataSet={messageTypeDetailDataSet}>
        <Radio colSpan={1} newLine name="backlogFlag" value>是</Radio>
        <Radio colSpan={2} name="backlogFlag" value={false}>否</Radio>
      </Form>
    </div>
  );
}
