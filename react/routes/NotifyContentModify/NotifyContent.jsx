import React, { Component, useContext, useState } from 'react/index';
import { observer, useComputed } from 'mobx-react-lite';
import { DataSet, Table, Modal, Button, Tabs, Tooltip } from 'choerodon-ui/pro';
import { axios, Content, Header, Page, Permission, Breadcrumb } from '@choerodon/master';
import { injectIntl, FormattedMessage } from 'react-intl';
import SendSetting from './SendSetting';
import TemplateSelect from './TemplateSelect';

import Store from './Store';
import CreateTemplate from './CreateTemplate';
import ModifySetting from './ModifySetting';

const { TabPane } = Tabs;

export default (props) => {
  // const [TabKey, setTabKey] = useState('mail');
  const context = useContext(Store);
  // console.log('NotifyContent', context);
  async function handleSave() {
    try {
      if ((await context.createTemplateDataSet.submit())) {
        setTimeout(() => { window.location.reload(true); }, 1000);
        return true;
      } else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }
  async function handleSaveConfig() {
    try {
      if ((await context.sendSettingDataSet.submit())) {
        // setTimeout(() => { window.location.reload(true); }, 1000);
        return true;
      } else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }
  const modifyEdit = () => {
    Modal.open({
      title: '修改配置',
      drawer: true,
      style: {
        width: 380,
      },
      children: (
        <ModifySetting context={context} />
      ),
      onOk: handleSaveConfig,
      // onCancel: resetFunc,
      // beforeClose: (a, b, c) => { debugger;window.console.log('after close'); },
    });
  };

  const createTemplate = () => {
    Modal.open({
      title: '创建模版',
      drawer: true,
      style: {
        width: 380,
      },
      children: (
        <CreateTemplate context={context} />
      ),
      onOk: handleSave,
      // onCancel: resetFunc,
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
          onClick={modifyEdit}
          icon="mode_edit"
        >
          {'修改配置'}
        </Button>
        <Button
          color="blue"
          onClick={createTemplate}
          icon="playlist_add"
        >
          {'创建模版'}
        </Button>
      </Header>
      <Breadcrumb title="设置邮件内容" />
      <Content>
        <SendSetting />
        <TemplateSelect />
      </Content>
    </Page>
  );
};
