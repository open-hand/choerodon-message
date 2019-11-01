import React, { useContext } from 'react';
import { Table, Form, Output, Spin } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Tabs } from 'choerodon-ui';
import { Action, axios, Content, StatusTag, PageTab, PageWrap } from '@choerodon/boot';
import Store from '../Store';

import './MessageTypeDetail.less';

const cssPrefix = 'c7n-notify-messageTypeDetail';

const { Column } = Table;
const { TabPane } = Tabs;

const MessageTypeDetail = observer(() => {
  const { messageTypeDetailDataSet, templateDataSet } = useContext(Store);
  const { current } = messageTypeDetailDataSet;

  const sentTypeRenderer = ({ record }) => {
    const ret = [];
    if (record.get('emailEnabledFlag')) {
      ret.push('邮件');
    }
    if (record.get('pmEnabledFlag')) {
      ret.push('站内信');
    }
    if (record.get('smsEnabledFlag')) {
      ret.push('短信');
    }
    if (record.get('webhookEnabledFlag')) {
      ret.push('webhook');
    }
    return ret.join('、');
  };

  const yesOrNoRenderer = ({ value }) => (value ? '是' : '否');

  const TemplateForm = ({ record, showTheme }) => (record ? (
    <React.Fragment>
      {showTheme && (
        <div>
          <span>发送主题</span>
          <span>{record.get('theme')}</span>
        </div>
      )}
      <p>预览</p>
      <div className={`${cssPrefix}-htmlContainer`} dangerouslySetInnerHTML={{ __html: record.get('content') }} style={{ marginBottom: 0 }} />
    </React.Fragment>
  ) : null);

  return current ? (
    <React.Fragment>
      <header className={`${cssPrefix}-header`}>
        <span
          className={`${cssPrefix}-header-circle`}
          style={{ backgroundColor: current.get('enabled') ? '#00BFA5' : 'rgba(0,0,0,0.20)' }}
        />
        <span className={`${cssPrefix}-header-name`}>{current.get('name')}</span>
      </header>
      <Form header="发送设置" className={`${cssPrefix}-form`} dataSet={messageTypeDetailDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={225}>
        <Output label="发送方式" renderer={sentTypeRenderer} />
        <Output name="allowConfig" renderer={yesOrNoRenderer} />
        <Output name="isSendInstantly" renderer={yesOrNoRenderer} />
        <Output name="retryCount" />
        <Output name="isManualRetry" renderer={yesOrNoRenderer} />
        <Output name="backlogFlag" renderer={yesOrNoRenderer} />
      </Form>
      <Tabs defaultActiveKey="1">
        <TabPane tab="邮件模版" key="1">
          <TemplateForm record={templateDataSet.find((item) => item.get('sendingType') === 'email')} showTheme />
        </TabPane>
        <TabPane tab="站内信模版" key="2">
          <TemplateForm record={templateDataSet.find((item) => item.get('sendingType') === 'pm')} showTheme />
        </TabPane>
        <TabPane tab="短信模版" key="3">
          <TemplateForm record={templateDataSet.find((item) => item.get('sendingType') === 'sms')} />
        </TabPane>
        <TabPane tab="webhook模版" key="4">
          <TemplateForm record={templateDataSet.find((item) => item.get('sendingType') === 'webhook')} />
        </TabPane>
      </Tabs>
    </React.Fragment>
  ) : <Spin />;
});

export default MessageTypeDetail;
