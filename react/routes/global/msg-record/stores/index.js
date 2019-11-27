import React, { createContext, useMemo, useContext } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import MsgRecordDataSet from './MsgRecordDataSet';
import MsgWebhookDataSet from './MsgWebhookDataSet';

const Store = createContext();
export function useStore() {
  return useContext(Store);
}
export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children } = props;
    const intlPrefix = 'msgrecord';
    const msgRecordDataSet = useMemo(() => new DataSet(MsgRecordDataSet(organizationId, type, intl, intlPrefix)), []);
    const msgWebhookDataSet = useMemo(() => new DataSet(MsgWebhookDataSet()), []);
    const value = {
      ...props,
      intlPrefix,
      permissions: [
        'notify-service.message-record-site.pageEmail',
        'notify-service.message-record-org.pageEmail',
        'notify-service.webhook-record.pagingByMessage',
      ],
      ENABLED_GREEN: 'rgba(0, 191, 165, 1)',
      DISABLED_GRAY: 'rgba(0, 0, 0, 0.2)',
      msgRecordDataSet,
      msgWebhookDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
