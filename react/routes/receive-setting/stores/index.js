import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import { Choerodon } from '@choerodon/boot';

const Store = createContext();

export function useReceiveSettingStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    intl: { formatMessage },
  } = props;

  const value = {
    ...props,
    intlPrefix: 'user.receive.setting',
    prefixCls: 'user-receive-setting',
    promptMsg: formatMessage({ id: 'global.menusetting.prompt.inform.title' }) + Choerodon.STRING_DEVIDER + formatMessage({ id: 'global.menusetting.prompt.inform.message' }),
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
