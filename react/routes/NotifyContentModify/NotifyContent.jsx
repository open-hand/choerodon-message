import React, { Component, useContext, useState, useEffect } from 'react/index';
import { observer, useComputed } from 'mobx-react-lite';
import { DataSet, Table, Modal, Button, Tabs, Tooltip } from 'choerodon-ui/pro';
import { axios, Content, Header, Page, Permission, Breadcrumb } from '@choerodon/master';
import { injectIntl, FormattedMessage } from 'react-intl';
import SendSetting from './SendSetting';
import TemplateSelect from './TemplateSelect';

import Store from './Store';
import CreateTemplate from './CreateTemplate';
import ModifySetting from './ModifySetting';
import './NotifyContent.less';

const { TabPane } = Tabs;

export default (props) => {
  // const [TabKey, setTabKey] = useState('mail');
  // const [title, settitle] = useState(undefined);
  const context = useContext(Store);
  const { settingType, prefixCls } = context;

  async function handleSaveConfig() {
    if (!context.sendSettingDataSet.isModified()) {
      return true;
    }
    try {
      if ((await context.sendSettingDataSet.submit())) {
        context.sendSettingDataSet.query();
        context.templateDataSet.query();
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
      className: prefixCls,
      children: (
        <ModifySetting context={context} />
      ),
      onOk: handleSaveConfig,
      okText: '保存',
      cancelText: '取消',
    });
  };

  const createTemplate = () => {
    Modal.open({
      title: '创建模版',
      drawer: true,
      style: {
        width: 740,
      },
      className: prefixCls,
      children: (
        <CreateTemplate context={context} />
      ),
      okText: '保存',
      cancelText: '取消',
    });
  };
  function chooseBreadcrumb() {
    if (settingType === 'email') {
      return '设置邮件内容';
    } else if (settingType === 'sms') {
      return '设置短信内容';
    } else if (settingType === 'pm') {
      return '设置站内信内容';
    }
  }
  function isHasEditConfig() {
    if (settingType === 'email') {
      return true;
    } else if (settingType === 'sms') {
      return false;
    } else if (settingType === 'pm') {
      return true;
    }
  }

  return (
    <Page className={`${prefixCls}`}>
      <Header
        title="通知配置"
        className={`${prefixCls}-header`}
      >{isHasEditConfig() ? (
        <Button
          color="blue"
          onClick={modifyEdit}
          icon="mode_edit"
        >
          {'修改配置'}
        </Button>
      ) : null}
        <Button
          color="blue"
          onClick={createTemplate}
          icon="playlist_add"
        >
          {'创建模版'}
        </Button>
      </Header>
      <Breadcrumb title={chooseBreadcrumb()} />
      <Content>
        {settingType !== 'sms' ? <SendSetting /> : null}
        <TemplateSelect />
      </Content>
    </Page>
  );
};
