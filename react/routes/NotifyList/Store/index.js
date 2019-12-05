import React, { createContext, useMemo, useState } from 'react';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import TemplateDataSet from './TemplateDataSet';
import MessageTypeTableDataSet from './MessageTypeTableDataSet';
import MessageTypeDetailDataSet from './MessageTypeDetailDataSet';
import QueryTreeDataSet from './QueryTreeDataSet';
import LevelDataSet from './LevelDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children } = props;
    const [currentPageType, setCurrentPageType] = useState({
      currentSelectedType: 'table',
      icon: 'folder_open2',
      title: '全部',
      id: null,
    });
    const levelDataSet = useMemo(() => new DataSet(LevelDataSet()), []);
    const messageTypeTableDataSet = useMemo(() => new DataSet(MessageTypeTableDataSet(levelDataSet)), []);
    const templateDataSet = useMemo(() => new DataSet(TemplateDataSet()), []);
    const messageTypeDetailDataSet = useMemo(() => new DataSet(MessageTypeDetailDataSet(templateDataSet)), []);
    const queryTreeDataSet = useMemo(() => new DataSet(QueryTreeDataSet({ setCurrentPageType })), []);
    const value = {
      ...props,
      messageTypeTableDataSet,
      templateDataSet,
      messageTypeDetailDataSet,
      queryTreeDataSet,
      currentPageType,
      setCurrentPageType,
      intl,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
