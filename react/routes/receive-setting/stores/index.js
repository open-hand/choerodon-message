import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import useStore from './useStore';

const Store = createContext();

export function useReceiveSettingStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
  } = props;

  const value = {
    ...props,
    intlPrefix: 'user.receive.setting',
    prefixCls: 'user-receive-setting',
    permissions: [],
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
