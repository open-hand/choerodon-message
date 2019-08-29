import React from 'react';
import { asyncRouter } from '@choerodon/master';
import { StoreProvider } from '../stores';

const MsgEmail = asyncRouter(() => (import('./MsgEmail')));
// const detail = asyncRouter(() => import('./APIDetail'));

const Index = (props) => (
  <StoreProvider {...props}>
    <MsgEmail />
  </StoreProvider>
);

export default Index;
