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

  function handlePmHeaderChange(value) {
    tableDs.forEach((record) => record.set('pmEnable', value));
  }
  
  function renderPmHeader(dataSet) {
    const isChecked = tableDs.totalCount && !tableDs.find((record) => !record.get('pmEnable'));
    const pmRecords = tableDs.find((record) => record.get('pmEnable'));
    return (
      <CheckBox
        checked={isChecked}
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

  return (
    <Fragment>
      <Breadcrumb />
      <Content className={`${prefixCls}-agile-content`}>
        <Table dataSet={tableDs}>
          <Column name="name" />
          <Column header={renderPmHeader} renderer={renderPm} align="left" />
          <Column name="targetUserDTOS" />
        </Table>
      </Content>
    </Fragment>
  );
};
