import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import useStore from './useStore';

const Store = createContext();

export function useProjectNotifyStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
  } = props;

  const ProjectNotifyStore = useMemo(() => useStore({ defaultKey: 'agile' }), []);

  const value = {
    ...props,
    intlPrefix: 'project.notify',
    prefixCls: 'project-notify',
    permissions: [],
    tabs: ['agile', 'devops', 'resource', 'webhook'],
    ProjectNotifyStore,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
