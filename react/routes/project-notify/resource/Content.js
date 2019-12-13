import React, { Fragment } from 'react';
import { TabPage, Content, Breadcrumb, Choerodon } from '@choerodon/boot';
import { Table, CheckBox, Icon, Dropdown, Button } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import { useResourceContentStore } from './stores';
import NotifyObject from '../components/notify-object/NotifyObject';
import MouserOverWrapper from '../../../components/mouseOverWrapper';
import FooterButtons from '../components/footer-buttons';

const { Column } = Table;

export default props => {
  const {
    intlPrefix,
    prefixCls,
    intl: { formatMessage },
    tableDs,
    allSendRoleList,
  } = useResourceContentStore();

  async function refresh() {
    tableDs.query();
  }

  async function saveSettings() {
    try {
      await tableDs.submit();
    } catch (e) {
      Choerodon.handleResponseError(e);
    }
  }

  function handleHeaderChange(value, type) {
    tableDs.forEach((record) => record.set(type, value));
  }
  
  function renderCheckBoxHeader(name) {
    const isChecked = tableDs.totalCount && !tableDs.find((record) => !record.get(name));
    const hasCheckedRecord = tableDs.find((record) => record.get(name));
    return (
      <CheckBox
        checked={isChecked}
        indeterminate={!isChecked && !!hasCheckedRecord}
        onChange={(value) => handleHeaderChange(value, name)}
      >
        {formatMessage({ id: `${intlPrefix}.${name}` })}
      </CheckBox>
    );
  }

  function handleCheckBoxChange({ record, value, name }) {
    record.set(name, value);
    if (!record.get('envId')) {
      tableDs.forEach((tableRecord) => {
        if (tableRecord.get('envId') === record.get('key')) {
          tableRecord.set(name, value);
        }
      });
    } else {
      const parentRecord = tableDs.find((tableRecord) => record.get('envId') === tableRecord.get('key'));
      const parentIsChecked = !tableDs.find((tableRecord) => parentRecord.get('key') === tableRecord.get('envId') && !tableRecord.get(name));
      parentRecord.set(name, parentIsChecked);
    }
  }

  function renderCheckBox({ record, name }) {
    let isChecked = true;
    let isIndeterminate = false;
    if (!record.get('envId')) {
      isChecked = !tableDs.find((tableRecord) => tableRecord.get('envId') === record.get('key') && !tableRecord.get(name));
      isIndeterminate = !!tableDs.find((tableRecord) => tableRecord.get('envId') === record.get('key') && tableRecord.get(name));
    }
    return (
      <CheckBox
        record={record}
        name={name}
        checked={record.get(name)}
        indeterminate={!isChecked && isIndeterminate}
        onChange={(value) => handleCheckBoxChange({ record, value, name })}
      />
    );
  }
  
  function renderNotifyObject({ record }) {
    if (!record.get('envId')) {
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

  return (
    <Fragment>
      <Breadcrumb />
      <Content className={`${prefixCls}-page-content`}>
        <Table dataSet={tableDs} mode="tree">
          <Column name="name" />
          <Column
            header={() => renderCheckBoxHeader('pmEnable')}
            renderer={({ record }) => renderCheckBox({ record, name: 'pmEnable' })}
            editor
            width={150}
            align="left"
          />
          <Column
            header={() => renderCheckBoxHeader('emailEnable')}
            renderer={({ record }) => renderCheckBox({ record, name: 'emailEnable' })}
            editor
            width={150}
            align="left"
          />
          <Column
            header={() => renderCheckBoxHeader('smsEnable')}
            renderer={({ record }) => renderCheckBox({ record, name: 'smsEnable' })}
            editor
            width={150}
            align="left"
          />
          <Column
            header={formatMessage({ id: `${intlPrefix}.noticeObject` })}
            renderer={renderNotifyObject}
          />
        </Table>
        <FooterButtons onOk={saveSettings} onCancel={refresh} />
      </Content>
    </Fragment>
  );
};
