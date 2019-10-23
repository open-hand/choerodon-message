import React from 'react';
import { PageWrap, PageTab } from '@choerodon/boot';
import { StoreProvider } from './Store';
import MailSetting from './mail-setting';
import SmsSetting from './sms-setting';

export default (props) => (
  <StoreProvider {...props}>
    <PageWrap noHeader={['choerodon.code.site.organization-approve', 'choerodon.code.site.organization-category']}>
      <PageTab title="邮箱配置" tabKey="choerodon.code.site.message-config-email" component={MailSetting} />
      <PageTab title="短信配置" tabKey="choerodon.code.site.message-config-sms" component={SmsSetting} />
    </PageWrap>
  </StoreProvider>
);
