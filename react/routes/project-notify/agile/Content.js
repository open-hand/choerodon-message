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
    tableDs.forEach((record) => record.set('pm', value));
  }

  function handlePmChange(value) {
    const record = tableDs.current;
    record.set('pm', value);
  }
  
  function renderPmHeader({ dataSet }) {
    const isChecked = tableDs.totalCount && !tableDs.find((record) => !record.get('pm'));
    const pmRecords = tableDs.find((record) => record.get('pm'));
    return (
      <CheckBox
        checked={isChecked}
        indeterminate={!!pmRecords}
        onChange={handlePmHeaderChange}
      >
        {formatMessage({ id: `${intlPrefix}.pm` })}
      </CheckBox>
    );
  }

  function renderPm({ record, value }) {
    return (
      <CheckBox
        record={record}
        name="pm"
        checked={value}
        // onChange={handlePmChange}
      />
    );
  }

  return (
    <Fragment>
      <Breadcrumb />
      <Content className={`${prefixCls}-agile-content`}>
        <Table dataSet={tableDs}>
          <Column name="type" />
          <Column name="pm" header={renderPmHeader} renderer={renderPm} align="left" />
          <Column name="noticeObject" />
        </Table>
      </Content>
    </Fragment>
  );
};
