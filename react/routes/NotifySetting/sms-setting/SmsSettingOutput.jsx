import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Output, Spin, Modal, NumberField, Password, EmailField, UrlField, DatePicker, Select, SelectBox, Switch, Lov, Button, TextArea } from 'choerodon-ui/pro';
import { axios, Content, Header, Page, Permission, Breadcrumb } from '@choerodon/master';
import store from '../Store';
import './SmsSetting.scss';
import SmsSettingForm from './SmsSettingForm';

const OutputEmptyValue = ({ value }) => (value ? <span>{value}</span> : <span>无</span>);

export default observer((props) => {
  const context = useContext(store);
  const { smsSettingDataSet, singleSendApiMap } = context;
  const sendType = smsSettingDataSet.current && smsSettingDataSet.current.getPristineValue('sendType');

  const submitFunc = () => new Promise((resolve, reject) => {
    smsSettingDataSet.validate().then((validateStatus) => {
      if (validateStatus) {
        smsSettingDataSet.submit().then((res) => {
          smsSettingDataSet.query();
          resolve();
        });
      } else {
        reject(new Error('校验未通过'));
      }
    });
  });

  const resetFunc = () => smsSettingDataSet.reset();

  const openSideBar = () => {
    Modal.open({
      title: '修改短信配置',
      drawer: true,
      className: 'msg-config-sider',
      style: {
        width: 380,
      },
      children: (
        <SmsSettingForm context={context} />
      ),
      onOk: submitFunc,
      onCancel: resetFunc,
      // beforeClose: (a, b, c) => { debugger;window.console.log('after close'); },
    });
  };
  return (
    <Page>
      <Header
        title="通知配置"
      >
        <Button
          color="blue"
          onClick={() => openSideBar()}
          icon="mode_edit"
        >
          {'修改'}
        </Button>
      </Header>
      <Breadcrumb />
      <Content
        values={{ name: context.AppState.getSiteInfo.systemName || 'Choerodon' }}
        className="msg-config"
      >
        <Spin dataSet={smsSettingDataSet}>
          <Form className="c7n-smssetting-form" pristine dataSet={smsSettingDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={120}>
            <Output name="signature" />
            <Output name="hostAddress" />
            <Output name="hostPort" renderer={OutputEmptyValue} />
            <Output
              name="sendType"
              renderer={({ value }) => (
                <span>{singleSendApiMap.get(value)}</span>
              )}
            />
            <Output name={`${sendType}SendApi`} renderer={OutputEmptyValue} />
            <Output renderer={() => '••••••'} name="secretKey" />
          </Form>
        </Spin>
      </Content>
    </Page>
  );
});
