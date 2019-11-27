import React, { Component, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Table, Tooltip } from 'choerodon-ui/pro';
import { injectIntl, FormattedMessage } from 'react-intl';
import { StatusTag, axios, Content, Header, TabPage, Breadcrumb, Action, Choerodon } from '@choerodon/boot';
import MouseOverWrapper from '../../../../components/mouseOverWrapper';
import { handleFiltersParams } from '../../../../common/util';
import { useStore } from '../stores';


const { Column } = Table;
function MsgEmail(props) {
  const context = useStore();
  const { AppState, intl, permissions, msgRecordDataSet, ENABLED_GREEN, DISABLED_GRAY } = context;

  // 重发
  function retry(record) {
    const { type, id: orgId } = AppState.currentMenuType;
    // const getTypePath = () => (type === 'site' ? '' : `/organizations/${orgId}`);
    return axios({
      // url: `/notify/v1/records/emails/${record.get('id')}/retry${getTypePath()}`,
      // method: type === 'site' ? 'post' : 'get',
      url: `/notify/v1/records/emails/${record.get('id')}/retry`,
      method: 'post',
    }).then((data) => {
      let msg = intl.formatMessage({ id: 'msgrecord.send.success' });
      if (data.failed) {
        msg = data.message;
      }
      Choerodon.prompt(msg);
      msgRecordDataSet.query();
    }).catch(() => {
      Choerodon.prompt(intl.formatMessage({ id: 'msgrecord.send.failed' }));
    });
  }
  const StatusCard = ({ value }) => (<StatusTag name={<FormattedMessage id={value.toLowerCase()} />} color={value !== 'FAILED' ? ENABLED_GREEN : DISABLED_GRAY} />);

  const actionRenderer = ({ value, record }) => {
    const actionArr = [{
      service: [],
      text: <FormattedMessage id="msgrecord.resend" />,
      action: () => retry(record),
    }];
    return record.get('status') === 'FAILED' && record.get('isManualRetry') && <Action className="action-icon" data={actionArr} />;
  };

  const renderMouseOver = ({ value }) => (
    <MouseOverWrapper text={value} width={0.2}>
      {value}
    </MouseOverWrapper>
  );
  const renderEmail = ({ value }) => (
    <Tooltip title={value} placement="topLeft">
      {value}
    </Tooltip>
  );

  function render() {
    return (
      <TabPage
        service={permissions}
      >
        <Breadcrumb />
        <Content
          values={{ name: AppState.getSiteInfo.systemName || 'Choerodon' }}
          style={{ paddingTop: 0 }}
        >
          <Table dataSet={msgRecordDataSet} style={{ paddingTop: 0 }}>
            <Column align="left" name="email" renderer={renderEmail} />
            <Column renderer={actionRenderer} width={48} />
            <Column align="left" width={100} name="status" renderer={StatusCard} />
            <Column name="templateType" renderer={renderMouseOver} />
            <Column name="failedReason" renderer={renderMouseOver} />
            <Column width={100} align="left" name="retryCount" />
            <Column name="creationDate" />
          </Table>
        </Content>
      </TabPage>
    );
  }
  return render();
}

export default observer(MsgEmail);
