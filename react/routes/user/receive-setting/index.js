import React from 'react';
import { asyncRouter } from '@choerodon/boot';
import { StoreProvider } from './stores';
import ReceiveSetting from './ReceiveSetting';

const Index = (props) => (
  <StoreProvider {...props}>
    <ReceiveSetting />
  </StoreProvider>
);

export default Index;
