import React from 'react';
import { StoreProvider } from './Store/index';
import Tab from './TableMessage';

export default props => (
  <StoreProvider {...props}>
    <Tab />
  </StoreProvider>
);
