import React, { Fragment } from 'react';
import { TabPage, Content, Breadcrumb, Choerodon } from '@choerodon/boot';
import { Table, CheckBox, Icon, Button } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import { useAgileContentStore } from './stores';
import FooterButtons from '../components/footer-buttons';

import './index.less';

const { Column } = Table;

export default props => {
  const {
    intlPrefix,
    prefixCls,
    intl: { formatMessage },
    tableDs,
  } = useAgileContentStore();

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
    if (!record.get('groupId')) {
      tableDs.forEach((tableRecord) => {
        if (tableRecord.get('groupId') === record.get('key')) {
          tableRecord.set(name, value);
        }
      });
    } else {
      const parentRecord = tableDs.find((tableRecord) => record.get('groupId') === tableRecord.get('key'));
      const parentIsChecked = !tableDs.find((tableRecord) => parentRecord.get('key') === tableRecord.get('groupId') && !tableRecord.get(name));
      parentRecord.set(name, parentIsChecked);
    }
  }

  function renderCheckBox({ record, name }) {
    let isChecked = true;
    let isIndeterminate = false;
    if (!record.get('groupId')) {
      isChecked = !tableDs.find((tableRecord) => tableRecord.get('groupId') === record.get('key') && !tableRecord.get(name));
      isIndeterminate = !!tableDs.find((tableRecord) => tableRecord.get('groupId') === record.get('key') && tableRecord.get(name));
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
    if (!record.get('groupId')) {
      return '-';
    }
  }

  return (
    <Fragment>
      <Breadcrumb />
      <Content className={`${prefixCls}-resource-content`}>
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
            header={formatMessage({ id: `${intlPrefix}.noticeObject` })}
            renderer={renderNotifyObject}
          />
        </Table>
        <FooterButtons onOk={saveSettings} onCancel={refresh} />
      </Content>
    </Fragment>
  );
};
