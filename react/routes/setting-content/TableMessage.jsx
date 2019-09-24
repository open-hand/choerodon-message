import React, { Component, useContext } from 'react';
import { Table, Button } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';
import { Action, axios, Content, Header, Page } from '@choerodon/master';
import Store from './Store/index';

import './TableMessage.less';

const { Column } = Table;
// 设置邮件，设置短信，设置站内信
export default function Tab() {
  const { sendSettingDataSet, history, match } = useContext(Store);
  function deleteLink(mes) {
    const id = sendSettingDataSet.current.get('id');
    if (mes === 'email') {
      history.push(`/notify/template-setting/${id}/email`);
    } else if (mes === 'sms') {
      history.push(`/notify/template-setting/${id}/sms`);
    } else if (mes === 'inmail') {
      history.push(`/notify/template-setting/${id}/inmail`);
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
  function getNameMethod({ record }) {
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
      action: () => deleteLink('inmail'),
    },
    {
      service: [],
      text: '设置短信内容',
      action: () => deleteLink('sms'),
    },
    {
      service: [],
      text: record.get('enabled') ? '禁用' : '启用',
      action: () => changeMake(),
    },
    {
      service: [],
      text: record.get('allowConfig') ? '不允许配置接收' : '允许配置接收',
      action: () => changeReceive(),
    }];
    return (
      <div className="option1">
        <span className="name">{messageType}</span>
        <Action className="action-icon" data={actionDatas} />
      </div>
    );
  }
  // 渲染启用状态
  function getEnabled({ record }) {
    if (record.get('enabled')) {
      return (
        <div>
          <Tag className="start">启用</Tag>
        </div>
      );
    } else {
      return (
        <div>
          <Tag className="forbidden">禁用</Tag>
        </div>
      );
    }
  }
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
    <page className="message-service">
      <header><div className="title">消息服务</div></header>
      <Content>
        <Table className="messageService" dataSet={sendSettingDataSet}>
          <Column className="column1" name="messageType" renderer={getNameMethod} />
          <Column name="introduce" />
          <Column name="level" renderer={getLevel} />
          <Column width={130} name="enabled" renderer={getEnabled} />
          <Column width={147} className="column5" name="allowConfig" renderer={getAllowConfig} />
        </Table>
      </Content>
    </page>
  );
}
