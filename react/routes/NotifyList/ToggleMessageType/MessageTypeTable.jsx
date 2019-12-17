import React, { useContext } from 'react';
import { Table, Icon, Output, Modal } from 'choerodon-ui/pro';
import { Action, axios, Content, StatusTag, PageTab, PageWrap } from '@choerodon/boot';
import Store from '../Store';
import './MessageTypeTable.less';

const { Column } = Table;
const modalKey = Modal.key();

const MessageTypeTable = () => {
  const cssPrefix = 'c7n-notify-MessageTypeTable';
  const { messageTypeTableDataSet, messageTypeDetailDataSet, setCurrentPageType, currentPageType, intl: { formatMessage } } = useContext(Store);
  let disableModal;

  // 启用状态改变切换
  async function changeMake() {
    const status = messageTypeTableDataSet.current.get('enabled');
    const id = messageTypeTableDataSet.current.get('id');
    const code = messageTypeTableDataSet.current.get('code');
    if (status) {
      disableModal.close();
    }
    const url = `/notify/v1/notices/send_settings/${status ? 'disabled' : 'enabled'}?code=${code}`;
    const res = await axios.put(url);
    messageTypeTableDataSet.query();
  }

  function openDisableModal(record) {
    disableModal = Modal.open({
      key: modalKey,
      title: formatMessage({ id: 'disable' }),
      children: formatMessage({ id: 'notify-lists.disable.message' }),
      okText: formatMessage({ id: 'disable' }),
      onOk: () => changeMake(),
    });
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
    const enabled = record.get('enabled');
    const actionArr = [{
      service: [],
      text: enabled ? formatMessage({ id: 'disable' }) : formatMessage({ id: 'enable' }),
      action: () => (enabled ? openDisableModal() : changeMake()),
    }, {
      service: [],
      text: record.get('allowConfig') ? '不允许配置接收' : '允许配置接收',
      action: () => changeReceive(),
    }];
    return <Action className="action-icon" data={actionArr} />;
  };

  const getEnabled = ({ record }) => (
    <StatusTag
      name={record.get('enabled') ? formatMessage({ id: 'disable' }) : formatMessage({ id: 'enable' })}
      color={record.get('enabled') ? '#00bfa5' : '#00000033'}
    />
  );

  const getAllowConfig = ({ record }) => (record.get('allowConfig') ? '允许' : '禁止');

  return (
    <div>
      <div className="c7n-notify-messageTypeDetail-title">
        <Icon type={currentPageType.icon} />{currentPageType.title}
      </div>
      <Table className="message-service" dataSet={messageTypeTableDataSet}>
        <Column
          name="messageType"
          className={`${cssPrefix}-nameContainer link`}
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
        <Column renderer={ActionRenderer} width={50} />
        <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="introduce" />
        <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} width={80} name="enabled" renderer={getEnabled} align="left" />
        <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} width={147} name="allowConfig" renderer={getAllowConfig} />
      </Table>
    </div>
  );
};

export default MessageTypeTable;
