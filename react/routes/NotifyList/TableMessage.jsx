import React, { Component, useContext, useState } from 'react/index';
import classnames from 'classnames';
import { Table, Button, Tree, Icon, TextField, Modal } from 'choerodon-ui/pro';
import { Header, axios, Page, Breadcrumb, Content, PageTab, Action } from '@choerodon/boot';
import { runInAction } from 'mobx';
import { observer } from 'mobx-react-lite';
import EditSendSettings from './Sider/EditSendSettings';
import EditTemplate from './Sider/EditTemplate';
import Store from './Store';

import ToggleMessageType from './ToggleMessageType';
import './TableMessage.less';

const { Column } = Table;
const cssPrefix = 'c7n-notify-contentList';
// 设置邮件，设置短信，设置站内信
export default observer(() => {
  const context = useContext(Store);
  const { queryTreeDataSet, messageTypeTableDataSet, messageTypeDetailDataSet, history, currentPageType, setCurrentPageType } = context;
  const [inputValue, setInputValue] = useState('');
  const [isEnable, setIsEnable] = useState(false);
  function getTitle(record) {
    const name = record.get('name').toLowerCase();
    const searchValue = inputValue.toLowerCase();
    const index = name.indexOf(searchValue);
    const beforeStr = name.substr(0, index).toLowerCase();
    const afterStr = name.substr(index + searchValue.length).toLowerCase();
    const title = index > -1 ? (
      <span>
        {beforeStr}
        <span style={{ color: '#f50' }}>{inputValue.toLowerCase()}</span>
        {afterStr}
      </span>
    ) : (
      <span>
        {name}
      </span>
    );
    return title;
  }
  async function handleToggleState(record) {
    const code = record.get('code');
    
    if (record.get('enabled')) {
      // 停用
      await axios.put(`/notify/v1/notices/send_settings/disabled?code=${code}`);
    } else {
      // 启用
      await axios.put(`/notify/v1/notices/send_settings/enabled?code=${code}`);
    }
    await queryTreeDataSet.query();
    await messageTypeDetailDataSet.query();
  }
  function getAction(record) {
    const { parent, children, level } = record;

    const actionDatas = [
      {
        text: record.get('enabled') ? '停用' : '启用',
        action: () => handleToggleState(record),
      },
    ];
    if (!children) {
      return <Action data={actionDatas} />;
    }
    return null;
  }

  const treeNodeRenderer = ({ record }) => {
    const treeIcon = () => {
      const { parent, children, level } = record;
      // 最上层节点, 展开
      if (!parent && record.get('expand')) {
        return <Icon type="folder_open2" />;
      }
      // 最上层节点，未展开
      if (!parent && !record.get('expand')) {
        return <Icon type="folder_open" />;
      }
      // 最底层节点
      if (level === 2) {
        return (
          <span
            className={`${cssPrefix}-circle`}
            style={{ backgroundColor: record.get('enabled') ? '#00BFA5' : 'rgba(0,0,0,0.20)' }}
          />
        );
      }
      return <Icon type="textsms" />;
    };
    const toggleContentRenderer = () => {
      const { parent, children, level } = record;
      if (!parent) {
        messageTypeTableDataSet.setQueryParameter('secondCode', undefined);
        messageTypeTableDataSet.setQueryParameter('firstCode', record.get('code'));
        messageTypeTableDataSet.query();
        setCurrentPageType({
          currentSelectedType: 'table',
          icon: 'folder_open2',
          title: record.get('name'),
        });
      } else if (level === 2) {
        messageTypeDetailDataSet.setQueryParameter('code', record.get('code'));
        messageTypeDetailDataSet.query();
        setCurrentPageType({
          currentSelectedType: 'form',
        });
      } else {
        messageTypeTableDataSet.setQueryParameter('firstCode', record.parent.get('code'));
        messageTypeTableDataSet.setQueryParameter('secondCode', record.get('code'));
        messageTypeTableDataSet.query();
        setCurrentPageType({
          currentSelectedType: 'table',
          icon: 'textsms',
          title: record.get('name'),
        });
      }
    };

    return (
      <div onClick={toggleContentRenderer}>
        <span className={`${cssPrefix}-icon`}>{treeIcon()}</span>
        {getTitle(record)}
        {getAction(record)}
      </div>
    );
  };

  function editSendSettings() {
    Modal.open({
      title: '修改发送设置',
      drawer: true,
      style: { width: '3.8rem' },
      children: <EditSendSettings context={context} />,
    });
  }

  function editTemplate(type, title) {
    Modal.open({
      title,
      drawer: true,
      style: { width: type === 'sms' ? '3.8rem' : '7.4rem' },
      children: <EditTemplate type={type} context={context} />,
    });
  }

  function getPageHeader() {
    return currentPageType.currentSelectedType === 'form' && (
      <Header>
        <Button icon="mode_edit" onClick={editSendSettings}>修改发送设置</Button>
        <Button icon="mode_edit" onClick={() => editTemplate('email', '修改邮件模板')}>修改邮件模板</Button>
        <Button icon="mode_edit" onClick={() => editTemplate('pm', '修改站内信模板')}>修改站内信模板</Button>
        <Button icon="mode_edit" onClick={() => editTemplate('webhook', '修改webhook模板')}>修改webhook模板</Button>
        <Button icon="mode_edit" onClick={() => editTemplate('sms', '修改短信模板')}>修改短信模板</Button>
      </Header>
    );
  }
  function handleSearch(value) {
    setInputValue(value);
  }
  function handleInput(e) {
    setInputValue(e.target.value);
  }

  function handleExpand(e) {
    runInAction(() => {
      queryTreeDataSet.forEach((record) => {
        record.set('expand', false);
      });
      queryTreeDataSet.forEach((record) => {
        if (record.get('name').toLowerCase().includes(inputValue.toLowerCase())) {
          while (record.parent) {
            record.parent.set('expand', true);
            record = record.parent;
          }
        }
      });
    });
  }

  return (
    <Page>
      {getPageHeader()}
      <Breadcrumb />
      <Content className={cssPrefix}>
        <div className={`${cssPrefix}-tree`}>
          <TextField
            name="id"
            className={`${cssPrefix}-tree-query`}
            prefix={<Icon type="search" style={{ color: 'rgba(0,0,0,0.65)' }} />}
            placeholder="请输入搜索条件"
            onInput={handleInput}
            onChange={handleSearch}
            value={inputValue}
            onEnterDown={handleExpand}
          />
          <Tree
            dataSet={queryTreeDataSet}
            renderer={treeNodeRenderer}
          />
        </div>
        <div className={`${cssPrefix}-rightContent`}>
          <ToggleMessageType />
        </div>
      </Content>
    </Page>
  );
});
