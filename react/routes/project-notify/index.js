import React from 'react';
import { PageTab, PageWrap } from '@choerodon/boot';
import { StoreProvider } from './stores';
import WebhookContent from '../WebhooksSetting';
import AgileContent from './agile';
import DevopsContent from './devops';
import ResourceContent from './resource';

import './index.less';

export default props => (
  <StoreProvider {...props}>
    <PageWrap noHeader={['agile', 'devops', 'resource']} cache>
      <PageTab title="敏捷消息" tabKey="agile" component={AgileContent} alwaysShow />
      <PageTab title="Devops消息" tabKey="devops" component={DevopsContent} alwaysShow />
      <PageTab title="资源删除验证" tabKey="resource" component={ResourceContent} alwaysShow />
      <PageTab title="Webhook配置" tabKey="webhook" component={WebhookContent} alwaysShow />
    </PageWrap>
  </StoreProvider>
);
