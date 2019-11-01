import React from 'react';
import { PageWrap, PageTab } from '@choerodon/boot';
import { StoreProvider } from './Store';
import WebhooksSetting from './WebhooksSetting';

export default (props) => (
  <StoreProvider {...props}>
    <PageWrap noHeader={[]}>
      <PageTab title="Webhook配置" tabKey="choerodon.code.site.message-config-sms" component={WebhooksSetting} alwaysShow />
    </PageWrap>
  </StoreProvider>
);
