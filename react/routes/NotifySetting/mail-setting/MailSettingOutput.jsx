import React, { useContext } from 'react';
import { DataSet, Form, Output, Spin, Modal, NumberField, Password, EmailField, UrlField, DatePicker, Select, SelectBox, Switch, Lov, Button, TextArea } from 'choerodon-ui/pro';
import { axios, Content, Header, Page, Permission, Breadcrumb } from '@choerodon/master';
import store from '../Store';
import MailSettingForm from './MailSettingForm';
import './MailSetting.scss';

const OutputEmptyValue = ({ value }) => (value ? <span>{value}</span> : <span>无</span>);

export default (props) => {
  const context = useContext(store);
  const { mailSettingDataSet } = context;

  const testConnection = () => {
    axios.post('notify/v1/notices/configs/email/test', mailSettingDataSet.current && mailSettingDataSet.current.toData()).then((data) => {
      if (data.failed) {
        Choerodon.prompt(data.message);
      } else {
        Choerodon.prompt(context.intl.formatMessage({ id: `${context.intlPrefix}.connect.success` }));
      }
    }).catch((error) => {
      Choerodon.handleResponseError(error);
    });
  };

  const submitFunc = () => new Promise((resolve, reject) => {
    mailSettingDataSet.validate().then((validateStatus) => {
      if (validateStatus) {
        mailSettingDataSet.submit().then((res) => {
          mailSettingDataSet.query();
          resolve();
        });
      } else {
        reject(new Error('校验未通过'));
      }
    });
  });

  const resetFunc = () => mailSettingDataSet.reset();

  const openSideBar = () => {
    Modal.open({
      title: '修改邮件配置',
      drawer: true,
      className: 'msg-config-sider',
      style: {
        width: 380,
      },
      children: (
        <MailSettingForm context={context} />
      ),
      onOk: submitFunc,
      onCancel: resetFunc,
    });
  };

  return (
    <Page>
      <Header
        title="通知配置"
      >
        <Button
          onClick={testConnection}
          color="blue"
          icon="low_priority"
        >
          <span>连接测试</span>
        </Button>
        <Button
          color="blue"
          onClick={() => openSideBar()}
          icon="mode_edit"
        >
          {'修改邮箱配置'}
        </Button>
      </Header>
      <Breadcrumb />
      <Content
        values={{ name: context.AppState.getSiteInfo.systemName || 'Choerodon' }}
        className="msg-config"
      >
        <Spin dataSet={mailSettingDataSet}>
          <Form className="c7n-mailsetting-form" pristine dataSet={mailSettingDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={120}>
            <Output name="account" />
            <Output renderer={() => '••••••'} name="password" />
            <Output name="sendName" renderer={OutputEmptyValue} />
            <Output name="protocol" />
            <Output name="host" />
            <Output
              name="ssl"
              renderer={({ value }) => (
                <span>{value ? '是' : '否'}</span>
              )}
            />
            <Output name="port" />
          </Form>
        </Spin>
      </Content>
    </Page>
  );
};
