import React from 'react';
import { StoreProvider } from './Store';
import TabView from './TabView';

export default props => (
  <StoreProvider {...props}>
    <TabView />
  </StoreProvider>
);
