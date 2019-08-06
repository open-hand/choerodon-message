import React from 'react/index';
import { StoreProvider } from './Store';
import NotifyContent from './NotifyContent';

export default props => (
  <StoreProvider {...props}>
    <NotifyContent />
  </StoreProvider>
);
