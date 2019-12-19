import React, { Fragment } from 'react';
import { TabPage, Content, Breadcrumb, Choerodon } from '@choerodon/boot';
import { Table, CheckBox, Icon, Dropdown, Spin } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Prompt } from 'react-router-dom';
import { useResourceContentStore } from './stores';
import NotifyObject from '../components/notify-object';
import MouserOverWrapper from '../../../components/mouseOverWrapper';
import FooterButtons from '../components/footer-buttons';
import { useProjectNotifyStore } from '../stores';
import Tips from '../../../components/tips';
import EmptyPage from '../../../components/empty-page';

const { Column } = Table;

export default observer(props => {
  const {
    intlPrefix,
    prefixCls,
    intl: { formatMessage },
    tableDs,
    allSendRoleList,
    permissions,
    resourceStore: { getLoading, getEnabled },
  } = useResourceContentStore();
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

  function handleHeaderChange(value, type, flagName) {
    tableDs.forEach((record) => record.get(flagName) && record.set(type, value));
  }
  
  function renderCheckBoxHeader(name, flagName) {
    const disabled = !tableDs.find((record) => record.get(flagName));
    const isChecked = !disabled && tableDs.totalCount && !tableDs.find((record) => !record.get(name) && record.get(flagName));
    const hasCheckedRecord = tableDs.find((record) => record.get(name) && record.get(flagName));
    return (
      <CheckBox
        checked={!!isChecked}
        indeterminate={!isChecked && !!hasCheckedRecord}
        disabled={disabled}
        onChange={(value) => handleHeaderChange(value, name, flagName)}
      >
        {formatMessage({ id: `${intlPrefix}.${name}` })}
      </CheckBox>
    );
  }

  function handleCheckBoxChange({ record, value, name, flagName }) {
    record.set(name, value);
    if (!record.get('groupId')) {
      tableDs.forEach((tableRecord) => {
        if (tableRecord.get('groupId') === record.get('key') && tableRecord.get(flagName)) {
          tableRecord.set(name, value);
        }
      });
    } else {
      const parentRecord = tableDs.find((tableRecord) => record.get('groupId') === tableRecord.get('key'));
      const parentIsChecked = !tableDs.find((tableRecord) => parentRecord.get('key') === tableRecord.get('groupId') && !tableRecord.get(name) && tableRecord.get(flagName));
      parentRecord.set(name, parentIsChecked && parentRecord.get(flagName));
    }
  }

  function renderCheckBox({ record, name, flagName }) {
    const checked = record.get(name);
    const disabled = !record.get(flagName);
    const isIndeterminate = !record.get('groupId') && !!tableDs.find((tableRecord) => tableRecord.get('groupId') === record.get('key') && tableRecord.get(name) && tableRecord.get(flagName));
    return (
      <CheckBox
        record={record}
        name={name}
        checked={checked}
        disabled={disabled}
        indeterminate={!checked && isIndeterminate}
        onChange={(value) => handleCheckBoxChange({ record, value, name, flagName })}
      />
    );
  }
  
  function renderNotifyObject({ record }) {
    if (!record.get('groupId')) {
      return '-';
    }

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

  function getContent() {
    if (getLoading) {
      return <Spin spinning style={{ textAlign: 'center' }} />;
    }
    if (getEnabled) {
      return (
        <Fragment>
          <Table dataSet={tableDs} mode="tree">
            <Column name="name" />
            <Column
              header={() => renderCheckBoxHeader('pmEnable', 'pmEnabledFlag')}
              renderer={({ record }) => renderCheckBox({ record, name: 'pmEnable', flagName: 'pmEnabledFlag' })}
              editor
              width={150}
              align="left"
            />
            <Column
              header={() => renderCheckBoxHeader('emailEnable', 'emailEnabledFlag')}
              renderer={({ record }) => renderCheckBox({ record, name: 'emailEnable', flagName: 'emailEnabledFlag' })}
              editor
              width={150}
              align="left"
            />
            <Column
              header={() => renderCheckBoxHeader('smsEnable', 'smsEnabledFlag')}
              renderer={({ record }) => renderCheckBox({ record, name: 'smsEnable', flagName: 'smsEnabledFlag' })}
              editor
              width={150}
              align="left"
            />
            <Column
              header={<Tips title={formatMessage({ id: `${intlPrefix}.noticeObject` })} helpText={formatMessage({ id: `${intlPrefix}.noticeObject.resource.tips` })} />}
              renderer={renderNotifyObject}
            />
          </Table>
          <FooterButtons onOk={saveSettings} onCancel={refresh} />
        </Fragment>
      );
    } else {
      return (
        <EmptyPage
          title={formatMessage({ id: `${intlPrefix}.empty.title` })}
          describe={formatMessage({ id: `${intlPrefix}.empty.des` })}
        />
      );
    }
  }

  return (
    <TabPage service={permissions}>
      <Breadcrumb />
      <Prompt message={promptMsg} wrapper="c7n-iam-confirm-modal" when={tableDs.dirty} />
      <Content className={`${prefixCls}-page-content`}>
        {getContent()}
      </Content>
    </TabPage>
  );
});
