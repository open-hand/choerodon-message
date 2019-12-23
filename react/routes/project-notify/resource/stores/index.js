import React, { createContext, useContext, useEffect, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import TableDataSet from './TableDataSet';
import { useProjectNotifyStore } from '../../stores';
import useStore from './useStore';

const Store = createContext();

export function useResourceContentStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    intl: { formatMessage },
    AppState: { currentMenuType: { projectId } },
  } = props;
  const {
    userDs,
  } = useProjectNotifyStore();

  const intlPrefix = 'project.notify';

  const resourceStore = useStore();
  const tableDs = useMemo(() => new DataSet(TableDataSet({ formatMessage, intlPrefix, projectId, userDs })), [projectId]);
  const value = {
    ...props,
    intlPrefix,
    prefixCls: 'project-notify',
    permissions: [
      'notify-service.message-setting.listByType',
      'notify-service.message-setting.batchUpdateByType',
    ],
    tableDs,
    allSendRoleList: ['handler', 'projectOwner', 'specifier'],
    resourceStore,
  };

  async function loadData() {
    if (await resourceStore.checkEnabled()) {
      tableDs.query();
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
