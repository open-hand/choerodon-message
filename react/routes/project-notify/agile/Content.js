import React from 'react';
import { TabPage, Content, Breadcrumb, Choerodon } from '@choerodon/boot';
import { Table, CheckBox, Dropdown, Icon } from 'choerodon-ui/pro';
import { Prompt } from 'react-router-dom';
import { observer } from 'mobx-react-lite';
import { useAgileContentStore } from './stores';
import NotifyObject from '../components/notify-object';
import MouserOverWrapper from '../../../components/mouseOverWrapper';
import FooterButtons from '../components/footer-buttons';
import { useProjectNotifyStore } from '../stores';

const { Column } = Table;

export default observer(props => {
  const {
    intlPrefix,
    prefixCls,
    intl: { formatMessage },
    tableDs,
    allSendRoleList,
    permissions,
  } = useAgileContentStore();
  const {
    promptMsg,
  } = useProjectNotifyStore();

  async function refresh() {
    tableDs.query();
  }

  async function saveSettings() {
    try {
      if (await tableDs.submit() !== false) {
        refresh();
      }
    } catch (e) {
      Choerodon.handleResponseError(e);
    }
  }

  function handlePmHeaderChange(value) {
    tableDs.forEach((record) => record.set('pmEnable', value));
  }
  
  function renderPmHeader(dataSet) {
    const isChecked = tableDs.totalCount && !tableDs.find((record) => !record.get('pmEnable'));
    const pmRecords = tableDs.find((record) => record.get('pmEnable'));
    return (
      <CheckBox
        checked={!!isChecked}
        indeterminate={!isChecked && !!pmRecords}
        onChange={handlePmHeaderChange}
      >
        {formatMessage({ id: `${intlPrefix}.pmEnable` })}
      </CheckBox>
    );
  }

  function renderPm({ record }) {
    return (
      <CheckBox
        record={record}
        name="pmEnable"
        checked={record.get('pmEnable')}
        onChange={(value) => record.set('pmEnable', value)}
      />
    );
  }

  function renderNotifyObject({ record }) {
    const data = [];
    const userList = record.get('userList');
    const sendRoleList = record.get('sendRoleList');
    sendRoleList.forEach((key) => {
      if (key !== 'specifier') {
        data.push(formatMessage({ id: `${intlPrefix}.object.${key}` }));
      } else if (userList && userList.length) {
        const names = userList.map(({ realName }) => realName);
        data.push(...names);
      }
    });

    return (
      <Dropdown
        overlay={<NotifyObject record={record} allSendRoleList={allSendRoleList} />}
        trigger={['click']}
        placement="bottomLeft"
      >
        <div className={`${prefixCls}-object-select`}>
          <MouserOverWrapper width={0.15} text={data.join()}>
            {data.join() || '-'}
          </MouserOverWrapper>
          <Icon type="arrow_drop_down" className={`${prefixCls}-object-select-icon`} />
        </div>
      </Dropdown>
    );
  }

  return (
    <TabPage service={permissions}>
      <Breadcrumb />
      <Prompt message={promptMsg} wrapper="c7n-iam-confirm-modal" when={tableDs.dirty} />
      <Content className={`${prefixCls}-page-content`}>
        <Table dataSet={tableDs}>
          <Column name="name" />
          <Column header={renderPmHeader} renderer={renderPm} align="left" />
          <Column renderer={renderNotifyObject} header={formatMessage({ id: `${intlPrefix}.noticeObject` })} />
        </Table>
        <FooterButtons onOk={saveSettings} onCancel={refresh} />
      </Content>
    </TabPage>
  );
});
