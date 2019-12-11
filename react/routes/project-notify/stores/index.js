import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import useStore from './useStore';
import UserOptionsDataSet from './UserOptionsDataSet';

const Store = createContext();

export function useProjectNotifyStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    AppState: { currentMenuType: { projectId } },
  } = props;

  const ProjectNotifyStore = useMemo(() => useStore({ defaultKey: 'agile' }), []);
  const userDs = useMemo(() => new DataSet(UserOptionsDataSet(projectId)), [projectId]);

  const value = {
    ...props,
    intlPrefix: 'project.notify',
    prefixCls: 'project-notify',
    permissions: [],
    tabs: ['agile', 'devops', 'resource', 'webhook'],
    ProjectNotifyStore,
    userDs,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
