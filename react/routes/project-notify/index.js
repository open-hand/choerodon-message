import React from 'react';
import { PageTab, PageWrap } from '@choerodon/boot';
import { StoreProvider } from './stores';
import WebhookContent from '../WebhooksSetting';
import AgileContent from './agile';
import DevopsContent from './devops';
import ResourceContent from './resource';
import Tips from '../../components/tips';

import './index.less';

export default props => (
  <StoreProvider {...props}>
    <PageWrap noHeader={['choerodon.code.project.setting-notify-agile', 'choerodon.code.project.setting-notify-devops', 'choerodon.code.project.setting-notify-resource']}>
      <PageTab title="敏捷消息" tabKey="choerodon.code.project.setting-notify-agile" component={AgileContent} />
      <PageTab title="Devops消息" tabKey="choerodon.code.project.setting-notify-devops" component={DevopsContent} />
      <PageTab
        title={<Tips title="资源删除验证" helpText="资源删除验证用于为项目下所有环境中的资源删除操作配置二次确认的通知信息，配置成功后，当用户在执行相应的删除操作时，就需要输入验证码进行二次确认" placement="topLeft" />}
        tabKey="choerodon.code.project.setting-notify-resource"
        component={ResourceContent}
      />
      <PageTab title="Webhook配置" tabKey="choerodon.code.project.setting-notify-webhook" component={WebhookContent} />
    </PageWrap>
  </StoreProvider>
);
