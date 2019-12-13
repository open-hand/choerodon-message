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
    <PageWrap noHeader={['choerodon.code.project.setting-notify-agile', 'choerodon.code.project.setting-notify-devops', 'choerodon.code.project.setting-notify-resource']}>
      <PageTab title="敏捷消息" tabKey="choerodon.code.project.setting-notify-agile" component={AgileContent} />
      <PageTab title="Devops消息" tabKey="choerodon.code.project.setting-notify-devops" component={DevopsContent} />
      <PageTab title="资源删除验证" tabKey="choerodon.code.project.setting-notify-resource" component={ResourceContent} />
      <PageTab title="Webhook配置" tabKey="choerodon.code.project.setting-notify-webhook" component={WebhookContent} />
    </PageWrap>
  </StoreProvider>
);
