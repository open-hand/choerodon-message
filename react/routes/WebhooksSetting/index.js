import React from 'react';
import { PageWrap, PageTab } from '@choerodon/boot';
import { StoreProvider } from './Store';
import WebhooksSetting from './WebhooksSetting';

export default (props) => (
  <StoreProvider {...props}>
    <WebhooksSetting />
  </StoreProvider>
);
