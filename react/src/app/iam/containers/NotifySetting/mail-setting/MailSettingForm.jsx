import React, { useContext } from 'react';
import { Form, TextField, Select, SelectBox } from 'choerodon-ui/pro';
import store from '../Store';

const { Option } = Select;

export default props => (
  <Form dataSet={props.context.mailSettingDataSet} labelLayout="float" labelAlign="left">
    <TextField name="account" />
    <TextField name="password" />
    <TextField name="sendName" />
    <Select name="protocol" disabled />
    <TextField name="host" />
    <SelectBox name="ssl">
      <Option value>是</Option>
      <Option value={false}>否</Option>
    </SelectBox>
    <TextField name="port" />
  </Form>
);
