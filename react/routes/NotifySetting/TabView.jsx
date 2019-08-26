import React, { Component, useContext, useState } from 'react';
import { observer, useComputed } from 'mobx-react-lite';
import { DataSet, Table, Modal, Button, Tabs, Tooltip } from 'choerodon-ui/pro';
import { axios, Content, Header, Page, Permission, Breadcrumb } from '@choerodon/master';
import { injectIntl, FormattedMessage } from 'react-intl';
import MailSettingOutput from './mail-setting/MailSettingOutput';
import SmsSettingOutput from './sms-setting/SmsSettingOutput';
import MailSettingForm from './mail-setting/MailSettingForm';
import SmsSettingForm from './sms-setting/SmsSettingForm';

import Store from './Store';

const { TabPane } = Tabs;


export default (props) => {
  const context = useContext(Store);
  const [TabKey, setTabKey] = useState('mail');

  const toggleTabKey = currentKey => setTabKey(currentKey);

  const refreshByTabKey = () => context.refresh(TabKey);

  const testConnection = () => {
    axios.post(TabKey === 'mail' ? 'notify/v1/notices/configs/email/test' : '', context.getCurrentDataSet(TabKey).current.toData()).then((data) => {
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
    context.getCurrentDataSet(TabKey).validate().then((validateStatus) => {
      if (validateStatus) {
        context.getCurrentDataSet(TabKey).submit().then((res) => {
          context.getCurrentDataSet(TabKey).query();
          resolve();
        });
      } else {
        reject(new Error('校验未通过'));
      }
    });
  });

  const resetFunc = () => context.getCurrentDataSet(TabKey).reset();

  const openSideBar = () => {
    Modal.open({
      title: `修改${TabKey === 'mail' ? '邮件' : '短信'}配置`,
      drawer: true,
      style: {
        width: 380,
      },
      children: (
        TabKey === 'mail' ? <MailSettingForm context={context} /> : <SmsSettingForm context={context} />
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
        {
          TabKey === 'mail'
            && (
            <Button
              onClick={testConnection}
              color="blue"
              icon="low_priority"
            >
              {/* <FormattedMessage id={`${intlPrefix}.test.contact`} /> */}
              <span>连接测试</span>
            </Button>
            )
        }
        <Button
          color="blue"
          onClick={() => openSideBar()}
          icon="mode_edit"
        >
          {`修改${TabKey === 'mail' ? '邮箱' : '短信'}配置`}
        </Button>
      </Header>
      <Breadcrumb title="通知配置" />
      <Content
        values={{ name: context.AppState.getSiteInfo.systemName || 'Choerodon' }}
      >
        <Tabs activeKey={TabKey} onChange={toggleTabKey}>
          <TabPane tab="邮箱配置" key="mail">
            <MailSettingOutput />
          </TabPane>
          <TabPane tab="短信配置" key="sms">
            <SmsSettingOutput />
          </TabPane>
        </Tabs>
      </Content>
    </Page>
  );
};
