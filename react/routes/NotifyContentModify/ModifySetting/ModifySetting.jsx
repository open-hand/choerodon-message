import React, { useContext, useState, useEffect } from 'react/index';
import { Form, TextField, Select, SelectBox } from 'choerodon-ui/pro';
import store from '../Store';

const { Option } = Select;

export default props => {
  const handleCancel = () => {
    props.context.sendSettingDataSet.current.reset();
  };
  useEffect(() => {
    props.modal.handleCancel(handleCancel);
  }, []);
  return (
    <Form dataSet={props.context.sendSettingDataSet} labelLayout="float" labelAlign="left" labelWidth={120}>
      <TextField name="retryCount" />
      <SelectBox name="sendInstantly">
        <Option value>是</Option>
        <Option value={false}>否</Option>
      </SelectBox>
      <SelectBox name="manualRetry">
        <Option value>是</Option>
        <Option value={false}>否</Option>
      </SelectBox>
      <Select name="emailTemplate" />
    </Form>
  );
};
