import React, { useContext } from 'react';
import { Table, Form, Output } from 'choerodon-ui/pro';
import { Tabs } from 'choerodon-ui';
import { Action, axios, Content, StatusTag, PageTab, PageWrap } from '@choerodon/boot';
import Store from '../Store';

import './MessageTypeDetail.less';
import MessageTypeTable from './MessageTypeTable';
import MessageTypeDetail from './MessageTypeDetail';

const { Column } = Table;
const { TabPane } = Tabs;

const ToggleMessageType = () => {
  const { currentPageType: { currentSelectedType } } = useContext(Store);
  return currentSelectedType === 'table' ? <MessageTypeTable /> : <MessageTypeDetail />;
};


export default ToggleMessageType;
