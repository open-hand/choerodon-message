import React, { createContext, useMemo } from 'react';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import SendSettingDataSet from './dataSet';
import LevelDataSet from './LevelDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children } = props;
    const levelDataSet = new DataSet(LevelDataSet());
    const sendSettingDataSet = new DataSet(SendSettingDataSet(levelDataSet));
    const value = {
      ...props,
      sendSettingDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
