import React from 'react/index';
import { StoreProvider } from './Store';
import Tab from './TableMessage';

export default props => (
  <StoreProvider {...props}>
    <Tab />
  </StoreProvider>
);
