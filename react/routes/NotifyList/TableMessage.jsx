import React, { Component, useContext, useState } from 'react/index';
import classnames from 'classnames';
import { Table, Button, Tree, Icon, TextField } from 'choerodon-ui/pro';
import { Action, axios, Page, Breadcrumb, Content, TabPage, PageTab } from '@choerodon/boot';
import Store from './Store';

import ToggleMessageType from './ToggleMessageType';
import './TableMessage.less';

const { Column } = Table;
const cssPrefix = 'c7n-notify-contentList';
// 设置邮件，设置短信，设置站内信

export default function Tab() {
  const { queryTreeDataSet, messageTypeTableDataSet, messageTypeDetailDataSet, history, match, setCurrentPageType } = useContext(Store);

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
        });
      }
    };

    return (
      <div onClick={toggleContentRenderer}>
        <span className={`${cssPrefix}-icon`}>{treeIcon()}</span>
        <span>{record.get('name')}</span>
      </div>
    );
  };

  return (
    <Page>
      {/* /!* <Header> */}
      {/*  <div className="title">消息服务</div> */}
      {/* </Header> *!/ */}
      <Breadcrumb />
      <Content className={cssPrefix}>
        <div className={`${cssPrefix}-tree`}>
          <TextField
            // dataSet={queryTreeDataSet.queryDataSet}
            name="id"
            className={`${cssPrefix}-tree-query`}
            prefix={<Icon type="search" style={{ color: 'rgba(0,0,0,0.65)' }} />}
            placeholder="请输入搜索条件"
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
}
