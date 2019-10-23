import React, { Component, useContext } from 'react/index';
import classnames from 'classnames';
import { Table, Button } from 'choerodon-ui/pro';
import { Action, axios, Page, Breadcrumb, Content, Header } from '@choerodon/boot';
import Store from './Store';

import './TableMessage.less';

const { Column } = Table;
// 设置邮件，设置短信，设置站内信

const StatusCard = ({ enabled }) => (
  <div
    className={classnames('c7n-notify-status', {
      'c7n-notify-enable': enabled,
      'c7n-notify-disable': !enabled,
    })}
  >
    {enabled ? '启用' : '停用'}
  </div>
);

export default function Tab() {
  const { sendSettingDataSet, history, match } = useContext(Store);
  function deleteLink(mes) {
    const id = sendSettingDataSet.current.get('id');
    const businessType = sendSettingDataSet.current.get('code');
    const { search } = history.location;
    if (mes === 'email') {
      history.push(`${match.path}/send-setting/${id}/${businessType}/email${search}`);
    } else if (mes === 'sms') {
      history.push(`${match.path}/send-setting/${id}/${businessType}/sms${search}`);
    } else if (mes === 'pm') {
      history.push(`${match.path}/send-setting/${id}/${businessType}/pm${search}`);
    }
  }
  // 启用状态改变切换
  async function changeMake() {
    const status = sendSettingDataSet.current.get('enabled');
    const id = sendSettingDataSet.current.get('id');
    const url = `/notify/v1/notices/send_settings/${id}/${status ? 'disabled' : 'enabled'}`;
    const res = await axios.put(url);
    sendSettingDataSet.query();
  }
  // 允许配置接收
  async function changeReceive() {
    const config = sendSettingDataSet.current.get('allowConfig');
    const id = sendSettingDataSet.current.get('id');
    const url = `/notify/v1/notices/send_settings/${id}/${config ? 'forbidden_configuration' : 'allow_configuration'}`;
    const res = await axios.put(url);
    sendSettingDataSet.query();
  }
  // 渲染消息类型
  function getNameMethod({ value, record }) {
    const messageType = record.get('messageType');
    const id = record.get('id');
    const actionDatas = [{
      service: [],
      text: '设置邮件内容',
      action: () => deleteLink('email'),
    },
    {
      service: [],
      text: '设置站内信内容',
      action: () => deleteLink('pm'),
    },
    {
      service: [],
      text: '设置短信内容',
      action: () => deleteLink('sms'),
    },
    {
      service: [],
      text: record.get('enabled') ? '停用' : '启用',
      action: () => changeMake(),
    },
    {
      service: [],
      text: record.get('allowConfig') ? '不允许配置接收' : '允许配置接收',
      action: () => changeReceive(),
    }];
    return (
      <React.Fragment>
        <span className="c7n-tableMessage-name">{value}</span>
        <Action className="action-icon" data={actionDatas} />
      </React.Fragment>
    );
  }
  // 渲染启用状态
  const getEnabled = ({ record }) => <StatusCard enabled={record.get('enabled')} />;

  // 接收配置渲染
  function getAllowConfig({ record }) {
    if (record.get('allowConfig')) {
      return (
        <div className="font">
          允许
        </div>
      );
    } else {
      return (
        <div className="font">
          禁止
        </div>
      );
    }
  }
  // 平台渲染
  function getLevel({ record }) {
    if (record.get('level') === 'site') {
      return (
        <div>平台</div>
      );
    } else if (record.get('level') === 'organization') {
      return (
        <div>组织</div>
      );
    } else if (record.get('level') === 'project') {
      return (
        <div>项目</div>
      );
    }
  }
  return (
    <Page>
      {/* <Header>
        <div className="title">消息服务</div>
      </Header> */}
      <Breadcrumb />

      <Content className="">
        <Table className="message-service" dataSet={sendSettingDataSet}>
          <Column align="left" className="column1" name="messageType" renderer={getNameMethod} />
          <Column name="introduce" />
          <Column name="level" width={80} renderer={getLevel} />
          <Column width={80} name="enabled" renderer={getEnabled} align="left" />
          <Column width={147} className="column5" name="allowConfig" renderer={getAllowConfig} />
        </Table>
      </Content>
    </Page>
  );
}
