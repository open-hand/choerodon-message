import React, { Component, useContext } from 'react';
import { Table, Button } from 'choerodon-ui/pro';
import { Action, axios } from '@choerodon/boot';
import Store from './Store/index';
import './TableMessage.less';

const { Column } = Table;
// 设置邮件，设置短信，设置站内信
export default function Tab() {
  const { sendSettingDataSet, history, match } = useContext(Store);
  function deleteLink(id, mes) {
    if (mes === 'email') {
      history.push(`/notify/template-setting/${id}/email`);
    } else if (mes === 'sms') {
      history.push(`/notify/template-setting/${id}/sms`);
    } else if (mes === 'inmail') {
      history.push(`/notify/template-setting/${id}/inmail`);
    }
  }
  // 启用状态改变切换
  async function changeMake(record) {
    const status = record.get('enabled');
    const id = record.get('id');
    const url = status === 'true' ? `/notify/v1/notices/send_settings/${id}/disabled` : `/notify/v1/notices/send_settings/${id}/enabled`;
    const res = await axios.put(url);
    sendSettingDataSet.query();
  }
  // 允许配置接收
  async function changeReceive(record) {
    const config = record.get('allowConfig');
    const id = record.get('id');
    const url = config === 'true' ? `/notify/v1/notices/send_settings/${id}/forbidden_configuration` : `/notify/v1/notices/send_settings/${id}/allow_configuration`;
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
      action: () => deleteLink(id, 'email'),
    },
    {
      service: [],
      text: '设置站内信内容',
      action: () => deleteLink(id, 'inmail'),
    },
    {
      service: [],
      text: '设置短信内容',
      action: () => deleteLink(id, 'sms'),
    },
    {
      service: [],
      text: record.get('enabled') === 'true' ? '禁用' : '启用',
      action: () => changeMake(record),
    },
    {
      service: [],
      text: record.get('allowConfig') === 'true' ? '不允许配置接收' : '允许配置接收',
      action: () => changeReceive(record),
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
    if (record.data.enabled === 'true') {
      return (
        <div>
          <Button className="start">启用</Button>
        </div>
      );
    } else {
      return (
        <div>
          <Button className="forbidden">禁用</Button>
        </div>
      );
    }
  }
  // 接收配置渲染
  function getAllowConfig({ record }) {
    if (record.data.allowConfig === 'true') {
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
    if (record.data.level === 'site') {
      return (
        <div>平台</div>
      );
    } else if (record.data.level === 'organisition') {
      return (
        <div>平台1</div>
      );
    } else if (record.data.level === 'common') {
      return (
        <div>平台2</div>
      );
    }
  }
  return (
    <div>
      <header><div className="title">消息服务</div></header>
      <Table className="messageService" dataSet={sendSettingDataSet}>
        <Column className="column1" name="messageType" renderer={getNameMethod} />
        <Column name="introduce" />
        <Column name="level" renderer={getLevel} />
        <Column name="enabled" renderer={getEnabled} />
        <Column name="allowConfig" renderer={getAllowConfig} />
      </Table>
    </div>
  );
}
