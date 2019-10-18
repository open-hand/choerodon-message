import React, { Component, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Button, Table as OldTable, Tooltip, IconSelect, Menu, Dropdown } from 'choerodon-ui';
import { Table } from 'choerodon-ui/pro';
import classnames from 'classnames';
import { injectIntl, FormattedMessage } from 'react-intl';
import { withRouter } from 'react-router-dom';
import { axios, Content, Header, TabPage, Permission, Breadcrumb, Action, Choerodon } from '@choerodon/boot';
import './MsgEmail.less';
import MouseOverWrapper from '../../../../components/mouseOverWrapper';
import StatusTag from '../../../../components/statusTag';
import { handleFiltersParams } from '../../../../common/util';
import { useStore } from '../stores';


const { Column } = Table;
function MsgEmail(props) {
  const context = useStore();
  const { AppState, intl, permissions, MsgRecordStore, msgRecordDataSet } = context;

  function getPermission() {
    const { type } = AppState.currentMenuType;
    let retryService = ['notify-service.send-setting-site.update'];
    if (type === 'organization') {
      retryService = ['notify-service.send-setting-org.update'];
    }
    return retryService;
  }


  // 重发
  function retry(record) {
    const { type, id: orgId } = AppState.currentMenuType;
    // const getTypePath = () => (type === 'site' ? '' : `/organizations/${orgId}`);
    axios({
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
  const renderDropDown = ({ text, action, disabled }) => {
    const menu = (
      <Menu onClick={action}>
        <Menu.Item key="1">
          {text}
        </Menu.Item>
      </Menu>
    );
    return (
      disabled ? (
        <Permission
          service={getPermission()}
        >
          <Dropdown overlay={menu} trigger={['click']}>
            <Button size="small" shape="circle" style={{ color: '#000' }} icon="more_vert" />
          </Dropdown>
        </Permission>

      ) : null
    );
  };
  const StatusCard = ({ value }) => (
    <div
      className={classnames('c7n-msgrecord-status',
        value === 'FAILED' ? 'c7n-msgrecord-status-failed'
          : 'c7n-msgrecord-status-completed')}
    >
      <FormattedMessage id={value.toLowerCase()} />
      {/* 失败 */}
    </div>
  );

  const renderAction = ({ value, record }) => {
    const action = {
      text: <FormattedMessage id="msgrecord.resend" />,
      action: retry.bind(this, record),
      disabled: record.get('status') === 'FAILED' && record.get('isManualRetry'),
    };
    // const ac=Action()
    return renderDropDown(action);
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
        className="c7n-msgrecord"
        service={permissions}
      >
        <Breadcrumb title="" />
        <Content
          values={{ name: AppState.getSiteInfo.systemName || 'Choerodon' }}
        >
          <Table dataSet={msgRecordDataSet}>
            <Column align="left" name="email" renderer={renderEmail} />
            <Column name="action" width={30} renderer={renderAction} />
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
