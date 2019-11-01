import React, { useContext } from 'react';
import { Table, Form, Output } from 'choerodon-ui/pro';
import { Tabs } from 'choerodon-ui';
import { Action, axios, Content, StatusTag, PageTab, PageWrap } from '@choerodon/boot';
import Store from '../Store';

const { Column } = Table;

const MessageTypeTable = () => {
  const cssPrefix = 'c7n-notify-MessageTypeTable';
  const { messageTypeTableDataSet, messageTypeDetailDataSet, setCurrentPageType } = useContext(Store);

  // 启用状态改变切换
  async function changeMake() {
    const status = messageTypeTableDataSet.current.get('enabled');
    const id = messageTypeTableDataSet.current.get('id');
    const url = `/notify/v1/notices/send_settings/${id}/${status ? 'disabled' : 'enabled'}`;
    const res = await axios.put(url);
    messageTypeTableDataSet.query();
  }

  // 允许配置接收
  async function changeReceive() {
    const config = messageTypeTableDataSet.current.get('allowConfig');
    const id = messageTypeTableDataSet.current.get('id');
    const url = `/notify/v1/notices/send_settings/${id}/${config ? 'forbidden_configuration' : 'allow_configuration'}`;
    const res = await axios.put(url);
    messageTypeTableDataSet.query();
  }

  const ActionRenderer = ({ value, record }) => {
    const actionArr = [{
      service: [],
      text: record.get('enabled') ? '停用' : '启用',
      action: () => changeMake(),
    }, {
      service: [],
      text: record.get('allowConfig') ? '不允许配置接收' : '允许配置接收',
      action: () => changeReceive(),
    }];
    return <Action className="action-icon" data={actionArr} />;
  };

  const getEnabled = ({ record }) => (
    <StatusTag
      name={record.get('enabled') ? '启用' : '停用'}
      color={record.get('enabled') ? '#00bfa5' : '#00000033'}
    />
  );

  const getAllowConfig = ({ record }) => (record.get('allowConfig') ? '允许' : '禁止');

  return (
    <Table className="message-service" dataSet={messageTypeTableDataSet} header="问题管理">
      <Column
        name="messageType"
        className={`${cssPrefix}-nameContainer`}
        onCell={({ record }) => ({
          onClick: () => {
            messageTypeDetailDataSet.setQueryParameter('code', record.get('code'));
            messageTypeDetailDataSet.query();
            setCurrentPageType({
              currentSelectedType: 'form',
            });
          },
        })}
      />
      <Column renderer={ActionRenderer} width={40} />
      <Column name="introduce" />
      <Column width={80} name="enabled" renderer={getEnabled} align="left" />
      <Column width={147} name="allowConfig" renderer={getAllowConfig} />
    </Table>
  );
};

export default MessageTypeTable;
