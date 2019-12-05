import React from 'react';
import { PageTab, PageWrap } from '@choerodon/boot';
import { StoreProvider } from './stores';
import ProjectNotify from './project-notify';
import SiteNotify from './site-notify';

export default props => (
  <StoreProvider {...props}>
    <PageWrap noHeader={['project', 'site']} cache>
      <PageTab title="项目通知" tabKey="project" component={ProjectNotify} alwaysShow />
      <PageTab title="平台通知" tabKey="site" component={SiteNotify} alwaysShow />
    </PageWrap>
  </StoreProvider>
);
