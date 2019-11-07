import React, { useContext } from 'react';
import { Table, Form, Output, Spin, Icon, Tooltip } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Tabs } from 'choerodon-ui';
import { Action, axios, Content, StatusTag, PageTab, PageWrap } from '@choerodon/boot';
import Store from '../Store';
import ShadowView from './ShadowView';

import './MessageTypeDetail.less';

const cssPrefix = 'c7n-notify-messageTypeDetail';

const { Column } = Table;
const { TabPane } = Tabs;

const MessageTypeDetail = observer(() => {
  const { messageTypeDetailDataSet, templateDataSet } = useContext(Store);
  const { current } = messageTypeDetailDataSet;

  const sentTypeRenderer = ({ record }) => {
    const ret = [];
    if (record.getPristineValue('emailEnabledFlag')) {
      ret.push('邮件');
    }
    if (record.getPristineValue('pmEnabledFlag')) {
      ret.push('站内信');
    }
    if (record.getPristineValue('smsEnabledFlag')) {
      ret.push('短信');
    }
    if (record.getPristineValue('webhookEnabledFlag')) {
      ret.push('webhook');
    }
    return ret.length > 0 ? ret.join('、') : '无';
  };

  const yesOrNoRenderer = ({ value }) => (value ? '是' : '否');
  const TemplateForm = ({ record, showTheme }) => (record ? (
    <React.Fragment>
      {showTheme && (
        <div style={{ fontSize: '0.14rem' }}>
          <span style={{ marginRight: '1.7rem', color: 'rgba(0,0,0,0.65)' }}>发送主题</span>
          <span>{record.getPristineValue('title')}</span>
        </div>
      )}
      <p style={{ marginTop: '0.16rem', marginBottom: '0.08rem', fontSize: '0.14rem', color: 'rgba(0,0,0,0.65)' }}>预览</p>
      <div className={`${cssPrefix}-htmlContainer`}>
        <ShadowView>
          {/* eslint-disable-next-line react/no-danger */}
          <div dangerouslySetInnerHTML={{ __html: record.getPristineValue('content') }} style={{ marginBottom: '.2rem' }} />
        </ShadowView>
      </div>
    </React.Fragment>
  ) : <div>无</div>);
  const getIcon = (type) => {
    if (!messageTypeDetailDataSet.current.get(`${type}EnabledFlag`)) {
      return (            
        <Tooltip title="该模板未启用，请在发送设置中选择">
          <Icon type="error" style={{ color: '#FFB100' }} />
        </Tooltip>
      );
    }
    return null;
  };
  return current ? (
    <React.Fragment>
      <header className={`${cssPrefix}-header`}>
        <span
          className={`${cssPrefix}-header-circle`}
          style={{ backgroundColor: current.getPristineValue('enabled') ? '#00BFA5' : 'rgba(0,0,0,0.20)' }}
        />
        <span className={`${cssPrefix}-header-name`}>{current.getPristineValue('name')}</span>
      </header>
      <Form pristine header="发送设置" className={`${cssPrefix}-form`} dataSet={messageTypeDetailDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={225}>
        <Output label="发送方式" renderer={sentTypeRenderer} />
        <Output name="allowConfig" renderer={yesOrNoRenderer} />
        <Output name="isSendInstantly" renderer={yesOrNoRenderer} />
        <Output name="retryCount" />
        <Output name="isManualRetry" renderer={yesOrNoRenderer} />
        <Output name="backlogFlag" renderer={yesOrNoRenderer} />
      </Form>
      <Tabs defaultActiveKey="1">
        <TabPane tab={(<span>邮件模板 {getIcon('email')}</span>)} key="1">
          <TemplateForm record={templateDataSet.find((item) => item.getPristineValue('sendingType') === 'email')} showTheme />
        </TabPane>
        <TabPane tab={(<span>站内信模板 {getIcon('pm')}</span>)} key="2">
          <TemplateForm record={templateDataSet.find((item) => item.getPristineValue('sendingType') === 'pm')} showTheme />
        </TabPane>
        <TabPane tab={(<span>webhook模板 {getIcon('webhook')}</span>)} key="3">
          <TemplateForm record={templateDataSet.find((item) => item.getPristineValue('sendingType') === 'webhook')} />
        </TabPane>
        <TabPane tab={(<span>短信模板 {getIcon('sms')}</span>)} key="4">
          <TemplateForm record={templateDataSet.find((item) => item.getPristineValue('sendingType') === 'sms')} />
        </TabPane>
       
      </Tabs>
    </React.Fragment>
  ) : <Spin />;
});

export default MessageTypeDetail;
