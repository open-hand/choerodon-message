import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import TableDataSet from './TableDataSet';
import { useProjectNotifyStore } from '../../stores';

const Store = createContext();

export function useAgileContentStore() {
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

  const tableDs = useMemo(() => new DataSet(TableDataSet({ formatMessage, intlPrefix, projectId, userDs })), [projectId]);
  const value = {
    ...props,
    intlPrefix,
    prefixCls: 'project-notify',
    permissions: [
      'notify-service.message-setting.listByType',
      'notify-service.message-setting.batchUpdateByType',
    ],
    allSendRoleList: ['reporter', 'assignee', 'projectOwner', 'specifier'],
    tableDs,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
