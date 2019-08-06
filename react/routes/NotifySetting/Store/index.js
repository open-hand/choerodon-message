import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import MailSettingDataSet from './MailSettingDataSet';
import SmsSettingDataSet from './SmsSettingDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children } = props;
    const intlPrefix = 'global.notifySetting';
    const mailSettingDataSet = useMemo(() => new DataSet(MailSettingDataSet(intl, `${intlPrefix}.mailsetting`)));
    const smsSettingDataSet = useMemo(() => new DataSet(SmsSettingDataSet(intl, `${intlPrefix}.smssetting`)));
    const value = {
      ...props,
      intlPrefix,
      mailSettingDataSet,
      smsSettingDataSet,
      getCurrentDataSet: code => (code === 'mail' ? mailSettingDataSet : smsSettingDataSet),
      singleSendApiMap: new Map([
        ['batch', '批量调用'],
        ['single', '单体调用'],
        ['async', '异步调用'],
      ]),
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
