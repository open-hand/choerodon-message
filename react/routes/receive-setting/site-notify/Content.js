import React, { Fragment } from 'react';
import { TabPage, Content, Breadcrumb } from '@choerodon/boot';
import { Table, CheckBox } from 'choerodon-ui/pro';
import { useProjectNotifyStore } from './stores';

import './index.less';

const { Column } = Table;

export default props => {
  const {
    intlPrefix,
    prefixCls,
    intl: { formatMessage },
    tableDs,
  } = useProjectNotifyStore();

  function handleCheckBoxHeaderChange(value, name) {
    tableDs.forEach((record) => record.set(name, value));
  }

  function handlePmChange(value) {
    const record = tableDs.current;
    record.set('pmEnable', value);
  }
  
  function renderCheckBoxHeader(dataSet, name) {
    const isChecked = tableDs.totalCount && !tableDs.find((record) => !record.get(name));
    const pmRecords = tableDs.find((record) => record.get(name));
    return (
      <CheckBox
        checked={isChecked}
        indeterminate={!isChecked && !!pmRecords}
        onChange={(value) => handleCheckBoxHeaderChange(value, name)}
      >
        {formatMessage({ id: `receive.type.${name}` })}
      </CheckBox>
    );
  }

  function renderCheckBox({ record, value, name }) {
    const hasTemplateId = record.get(`${name}TemplateId`);
    return (
      <CheckBox
        record={record}
        name={name}
        checked={!!value}
        disabled={!hasTemplateId}
      />
    );
  }

  function renderEditor(record, name) {
    return !!record.get(`${name}TemplateId`);
  }

  return (
    <Fragment>
      <Breadcrumb />
      <Content className={`${prefixCls}-content`}>
        <Table dataSet={tableDs}>
          <Column name="name" />
          <Column name="pm" header={renderCheckBoxHeader} renderer={renderCheckBox} align="left" editor={renderEditor} />
          <Column name="email" header={renderCheckBoxHeader} renderer={renderCheckBox} align="left" editor={renderEditor} />
        </Table>
      </Content>
    </Fragment>
  );
};
