import React from 'react';
import { observer } from 'mobx-react-lite';
import { Table } from 'choerodon-ui/pro';
import classnames from 'classnames';
import { FormattedMessage } from 'react-intl';
import { Content, Page, Breadcrumb } from '@choerodon/boot';

import { useStore } from '../stores';


const { Column } = Table;
function MsgWebhook() {
  const context = useStore();
  const { AppState, permissions, msgWebhookDataSet } = context;
  const StatusCard = ({ value }) => (
    <div
      className={classnames('c7n-msgrecord-status',
        value === 'FAILED' ? 'c7n-msgrecord-status-failed'
          : 'c7n-msgrecord-status-completed')}
    >
      <FormattedMessage id={value.toLowerCase()} />
    </div>
  );
  return (
    <Page
      title="消息日志"
      service={permissions}
    >
      <Breadcrumb />
      <Content 
        values={{ name: context.AppState.getSiteInfo.systemName || 'Choerodon' }}
      >
        <Table dataSet={msgWebhookDataSet}>
          <Column align="left" name="sendTime" />
          <Column align="left" width={100} name="status" renderer={StatusCard} />
          <Column name="failedReason" />
          <Column name="projectName" />
          <Column name="webhookPath" />
        </Table>
      </Content>
    </Page>
  );
}

export default observer(MsgWebhook);
