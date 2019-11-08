import React from 'react';
import { withRouter } from 'react-router-dom';
import { PageWrap, PageTab } from '@choerodon/boot';
import { StoreProvider } from './stores';
import MsgEmail from './msg-email';
import MsgWebhook from './msg-webhook';

function MsgRecord(props) {
  return (
    <StoreProvider {...props}>
      <PageWrap noHeader={[]}>
        <PageTab title="邮件日志" tabKey="choerodon.code.msg-center.msg-log-email" component={MsgEmail} />
        <PageTab title="webhook日志" tabKey="choerodon.code.msg-center.msg-log-webhook" component={MsgWebhook} />
      </PageWrap>
    </StoreProvider>
    
  );
}
export default MsgRecord;
