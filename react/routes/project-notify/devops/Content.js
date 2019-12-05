import React, { Fragment } from 'react';
import { TabPage, Content, Breadcrumb } from '@choerodon/boot';
import { Table, CheckBox } from 'choerodon-ui/pro';
import { useAgileContentStore } from './stores';

import './index.less';

const { Column } = Table;

export default props => {
  const {
    intlPrefix,
    prefixCls,
    intl: { formatMessage },
    tableDs,
  } = useAgileContentStore();

  function handlePmHeaderChange(value, type) {
    tableDs.forEach((record) => record.set(type, value));
  }

  function handlePmChange(value) {
    const record = tableDs.current;
    record.set('pmEnable', value);
  }
  
  function renderCheckBoxHeader(dataSet, name) {
    const isChecked = tableDs.totalCount && !tableDs.find((record) => !record.get(name));
    const hasCheckedRecord = tableDs.find((record) => record.get(name));
    return (
      <CheckBox
        checked={isChecked}
        indeterminate={!!hasCheckedRecord}
        onChange={(value) => handlePmHeaderChange(value, name)}
      >
        {formatMessage({ id: `${intlPrefix}.${name}` })}
      </CheckBox>
    );
  }

  function renderCheckBox({ record, value, name }) {
    return (
      <CheckBox
        record={record}
        name={name}
        checked={value}
        // onChange={handlePmChange}
      />
    );
  }

  return (
    <Fragment>
      <Breadcrumb />
      <Content className={`${prefixCls}-devops-content`}>
        <Table dataSet={tableDs}>
          <Column name="name" />
          <Column name="pmEnable" header={renderCheckBoxHeader} renderer={renderCheckBox} align="left" />
          <Column name="emailEnable" header={renderCheckBoxHeader} renderer={renderCheckBox} align="left" />
          <Column name="targetUserDTOS" />
        </Table>
      </Content>
    </Fragment>
  );
};
