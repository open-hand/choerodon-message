import React, { Fragment } from 'react';
import { TabPage, Content, Breadcrumb } from '@choerodon/boot';
import { Table, CheckBox, Icon } from 'choerodon-ui/pro';
import { useAgileContentStore } from './stores';
import MouserOverWrapper from '../../../components/mouseOverWrapper';

import './index.less';

const { Column } = Table;

export default props => {
  const {
    intlPrefix,
    prefixCls,
    intl: { formatMessage },
    tableDs,
    notifyObject,
  } = useAgileContentStore();

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
    const sendSpecifier = record.get('sendSpecifier');
    const userList = record.get('userList');
    Object.keys(notifyObject).forEach((key) => {
      if (record.get(key)) {
        data.push(notifyObject[key]);
      }
    });
    if (sendSpecifier && userList && userList.length) {
      userList.forEach(({ realName }) => data.push(realName));
    }

    return (
      <div>
        <MouserOverWrapper width={0.2} text={data.join()}>
          {data.join()}
        </MouserOverWrapper>
      </div>
    );
  }

  return (
    <Fragment>
      <Breadcrumb />
      <Content className={`${prefixCls}-resource-content`}>
        <Table dataSet={tableDs} mode="tree">
          <Column name="name" />
          <Column
            header={() => renderCheckBoxHeader('sendPm')}
            renderer={({ record }) => renderCheckBox({ record, name: 'sendPm' })}
            editor
            width={150}
            align="left"
          />
          <Column
            header={() => renderCheckBoxHeader('sendEmail')}
            renderer={({ record }) => renderCheckBox({ record, name: 'sendEmail' })}
            editor
            width={150}
            align="left"
          />
          <Column
            header={() => renderCheckBoxHeader('sendSms')}
            renderer={({ record }) => renderCheckBox({ record, name: 'sendSms' })}
            editor
            width={150}
            align="left"
          />
          <Column
            header={formatMessage({ id: `${intlPrefix}.noticeObject` })}
            renderer={renderNotifyObject}
          />
        </Table>
      </Content>
    </Fragment>
  );
};
