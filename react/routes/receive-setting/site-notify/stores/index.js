import React, { createContext, useContext, useEffect, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import TableDataSet from './TableDataSet';
import useStore from './useStore';

const Store = createContext();

export function useProjectNotifyStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    intl: { formatMessage },
  } = props;
  const intlPrefix = 'receive.setting.site';

  const receiveStore = useMemo(() => useStore(), []);
  const tableDs = useMemo(() => new DataSet(TableDataSet({ formatMessage, intlPrefix, receiveStore })), []);
  const value = {
    ...props,
    intlPrefix,
    prefixCls: 'receive-setting-site',
    permissions: [],
    tableDs,
  };

  async function loadInitData() {
    await receiveStore.loadReceiveData();
    tableDs.query();
  }

  useEffect(() => {
    loadInitData();
  }, []);

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
