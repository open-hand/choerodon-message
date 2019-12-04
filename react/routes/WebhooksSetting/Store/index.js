import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import TriggerEventsSettingDataSet from './TriggerEventsSettingDataSet';
import WebhooksDataSet from './WebhooksDataSet';
import WebhooksFormDataSet from './WebhooksFormDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children } = props;
    const webhooksDataSet = useMemo(() => new DataSet(WebhooksDataSet(id)), []);
    const createTriggerEventsSettingDataSet = useMemo(() => new DataSet(TriggerEventsSettingDataSet('create', id)), []);
    const editTriggerEventsSettingDataSet = useMemo(() => new DataSet(TriggerEventsSettingDataSet('edit', id)), []);
    const editWebhooksFormDataSet = useMemo(() => new DataSet(WebhooksFormDataSet('edit', id, editTriggerEventsSettingDataSet)), []);
    const createWebhooksFormDataSet = useMemo(() => new DataSet(WebhooksFormDataSet('create', id)), []);
    const value = {
      projectId: id,
      webhooksDataSet,
      editWebhooksFormDataSet,
      editTriggerEventsSettingDataSet,
      createWebhooksFormDataSet,
      createTriggerEventsSettingDataSet,
      webhooksTypeMap: {
        WeChat: '企业微信',
        DingTalk: '钉钉',
        Json: 'JSON',
      },
      ENABLED_GREEN: 'rgba(0, 191, 165, 1)',
      DISABLED_GRAY: 'rgba(0, 0, 0, 0.2)',
      prefixCls: 'webhook-setting',
      ...props,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
