import React from 'react';
import { withRouter } from 'react-router-dom';
import { asyncRouter, PageWrap, PageTab } from '@choerodon/boot';

const MsgEmail = asyncRouter(() => import('./msg-email'));

function MsgRecord(props) {
  return (
    <PageWrap noHeader={['choerodon.code.site.message-log-email']}>
      <PageTab title="邮件日志" tabKey="choerodon.code.site.message-log-email" component={withRouter(MsgEmail)} />
      {/* <PageTab title="站内信日志" tabKey="tab2" component={withRouter(MsgEmail)} />    
      <PageTab title="短信日志" tabKey="tab3" component={withRouter(MsgEmail)} />     */}
    </PageWrap>
  );
}
export default MsgRecord;
