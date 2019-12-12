import React, { Fragment } from 'react';
import { TabPage, Content, Breadcrumb, Choerodon } from '@choerodon/boot';
import { Table, CheckBox, Icon, Dropdown, Button } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import { useResourceContentStore } from './stores';
import NotifyObject from '../components/notify-object/NotifyObject';
import MouserOverWrapper from '../../../components/mouseOverWrapper';
import FooterButtons from '../components/footer-buttons';

import './index.less';

const { Column } = Table;

export default props => {
  const {
    intlPrefix,
    prefixCls,
    intl: { formatMessage },
    tableDs,
    notifyObject,
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
    const recordObject = record.get('notifyObject');
    Object.keys(notifyObject).forEach((key) => {
      if (recordObject.includes(key)) {
        data.push(notifyObject[key]);
      }
    });
    if (recordObject.includes('sendSpecifier') && userList && userList.length) {
      userList.forEach(({ realName }) => data.push(realName));
    }

    return (
      <Dropdown
        overlay={<NotifyObject record={record} />}
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
      <FooterButtons onOk={saveSettings} onCancel={refresh} />
    </Fragment>
  );
};
