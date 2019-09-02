import React from 'react';
import DetailTemplate from './DetailTemplate';
import { StoreProvider } from './Store';

export default (props) => (
  <StoreProvider {...props}>
    <DetailTemplate />
  </StoreProvider>
);
