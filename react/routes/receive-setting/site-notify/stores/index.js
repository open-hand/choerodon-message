import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import TableDataSet from './TableDataSet';

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

  const tableDs = useMemo(() => new DataSet(TableDataSet({ formatMessage, intlPrefix })), []);
  const value = {
    ...props,
    intlPrefix,
    prefixCls: 'receive-setting-site',
    permissions: [],
    tableDs,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
