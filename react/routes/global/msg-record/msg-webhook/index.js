import React from 'react';
import { asyncRouter } from '@choerodon/boot';
import { StoreProvider } from '../stores';

const MsgWebhook = asyncRouter(() => (import('./MsgWebhook')));

const Index = (props) => (
  <StoreProvider {...props}>
    <MsgWebhook />
  </StoreProvider>
);

export default Index;
