import React, { createContext, useMemo, useContext } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import ReceiveSettingStoreObject from './ReceiveSettingStore';

const Store = createContext();
export function useStore() {
  return useContext(Store);
}
export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children } = props;
    const intlPrefix = 'user.userinfo';
    const ReceiveSettingStore = useMemo(() => new ReceiveSettingStoreObject(), []);
    const value = {
      ...props,
      prefixCls: 'user-info',
      intlPrefix,
      permissions: [
        'base-service.user.uploadPhoto',
      ],
      ReceiveSettingStore,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
