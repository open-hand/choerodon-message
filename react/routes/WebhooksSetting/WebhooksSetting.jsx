import React, { useContext } from 'react';
import { Table, Button, Modal } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import { axios, Breadcrumb, Header, Content, StatusTag, Action } from '@choerodon/boot';
import CreateAndEditWebhooksForm from './CreateAndEditWebhooksForm';
import Store from './Store';

import './index.less';

const ModalKey = Modal.key();
const { Column } = Table;

const WebhooksSetting = () => {
  const {
    projectId,
    webhooksDataSet,
    createWebhooksFormDataSet,
    createTriggerEventsSettingDataSet,
    editWebhooksFormDataSet,
    editTriggerEventsSettingDataSet,
    webhooksTypeMap,
    ENABLED_GREEN,
    DISABLED_GRAY,
    prefixCls,
  } = useContext(Store);

  const handleCreateWebhooks = () => {
    Modal.open({
      title: '添加Webhook',
      key: ModalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      children: (
        <CreateAndEditWebhooksForm dataSet={createWebhooksFormDataSet} triggerEventsSettingDataSet={createTriggerEventsSettingDataSet} />
      ),
      onOk: async () => {
        try {
          const res = await axios.post(`/notify/v1/projects/${projectId}/web_hooks`, {
            ...createWebhooksFormDataSet.toJSONData()[0],
            sendSettingIdList: createTriggerEventsSettingDataSet.toJSONData(true).filter((item) => !!item.categoryCode).map(item => item.id),
          });
          if (!res) {
            throw new Error();
          }
          if (res.failed) {
            message.error(res.message);
            throw new Error();
          }
          message.success('提交成功');
          webhooksDataSet.query();
          return true;
        } catch (e) {
          return false;
        }
      },
      afterClose: () => createWebhooksFormDataSet.reset(),
    });
  };

  const editWebhooks = (record) => {
    editWebhooksFormDataSet.queryUrl = `/notify/v1/projects/${projectId}/web_hooks/${record.get('id')}`;
    editWebhooksFormDataSet.query();
    Modal.open({
      title: '编辑Webhook',
      key: ModalKey,
      drawer: true,
      okText: '保存',
      style: {
        width: '51.39%',
      },
      onOk: async () => {
        try {
          const res = await axios.put(`/notify/v1/projects/${projectId}/web_hooks/${record.get('id')}`, {
            ...editWebhooksFormDataSet.toData()[0],
            sendSettingIdList: editTriggerEventsSettingDataSet.toJSONData(true).filter((item) => !!item.categoryCode).map(item => item.id),
            triggerEventSelection: undefined,
          });
          if (!res) {
            throw new Error();
          }
          if (res.failed) {
            message.error(res.message);
            throw new Error();
          }
          message.success('提交成功');
          webhooksDataSet.query();
          return true;
        } catch (e) {
          return false;
        }
      },
      children: (
        <CreateAndEditWebhooksForm dataSet={editWebhooksFormDataSet} triggerEventsSettingDataSet={editTriggerEventsSettingDataSet} />
      ),
      afterClose: () => editWebhooksFormDataSet.reset(),
    });
  };

  const deleteWebhooks = (record) => webhooksDataSet.delete(record).then((res) => {
    webhooksDataSet.query();
  });

  const toggleWebhooks = async (record) => {
    try {
      const res = await axios.put(`notify/v1/projects/${projectId}/web_hooks/${record.get('id')}/${record.get('enableFlag') ? 'disabled' : 'enabled'}`);
      if (res.failed) {
        message(res.message);
        throw Error();
      }
      if (!res) {
        throw Error(res);
      }
      webhooksDataSet.query();
    } catch (e) {
      return false;
    }
  };

  const ActionRenderer = ({ record }) => {
    const actionArr = [{
      service: [],
      text: '删除',
      action: () => deleteWebhooks(record),
    }, {
      service: [],
      text: record.get('enableFlag') ? '禁用' : '启用',
      action: () => toggleWebhooks(record),
    }];
    return <Action className="action-icon" data={actionArr} />;
  };

  const StatusRenderer = ({ value }) => <StatusTag name={value ? '启用' : '停用'} color={value ? ENABLED_GREEN : DISABLED_GRAY} />;

  const typeRenderer = ({ value }) => webhooksTypeMap[value];

  return (
    <React.Fragment>
      <Header>
        <Button icon="playlist_add" onClick={handleCreateWebhooks}>创建Webhooks</Button>
      </Header>
      <Breadcrumb />
      <Content className={`${prefixCls}-content`}>
        <Table dataSet={webhooksDataSet}>
          <Column
            name="name"
            onCell={({ record }) => ({
              onClick: () => editWebhooks(record),
            })}
          />
          <Column renderer={ActionRenderer} width={48} />
          <Column name="webhookPath" />
          <Column name="type" renderer={typeRenderer} />
          <Column name="enableFlag" renderer={StatusRenderer} />
        </Table>
      </Content>
    </React.Fragment>
  );
};

export default WebhooksSetting;
