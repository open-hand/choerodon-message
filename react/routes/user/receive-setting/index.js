import React from 'react';
import { asyncRouter } from '@choerodon/master';
import { StoreProvider } from './stores';
import ReceiveSetting from './ReceiveSetting';

const Index = (props) => (
  <StoreProvider {...props}>
    <ReceiveSetting />
  </StoreProvider>
);

export default Index;
