import React from 'react';
import { observer } from 'mobx-react-lite';
import { Table } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import { Content, TabPage, Breadcrumb, StatusTag } from '@choerodon/boot';

import { useStore } from '../stores';


const { Column } = Table;
function MsgWebhook() {
  const { AppState, msgWebhookDataSet, ENABLED_GREEN, DISABLED_GRAY } = useStore();

  const StatusCard = ({ value }) => (<StatusTag name={<FormattedMessage id={value.toLowerCase()} />} color={value !== 'FAILED' ? ENABLED_GREEN : DISABLED_GRAY} />);

  return (
    <TabPage
      service={['notify-service.webhook-record.pagingByMessage']}
    >
      <Breadcrumb />
      <Content
        values={{ name: AppState.getSiteInfo.systemName || 'Choerodon' }}
        style={{ paddingTop: 0 }}
      >
        <Table dataSet={msgWebhookDataSet}>
          <Column align="left" name="sendTime" />
          <Column align="left" width={100} name="status" renderer={StatusCard} />
          <Column name="failedReason" />
          <Column name="projectName" />
          <Column name="webhookPath" />
        </Table>
      </Content>
    </TabPage>
  );
}

export default observer(MsgWebhook);
