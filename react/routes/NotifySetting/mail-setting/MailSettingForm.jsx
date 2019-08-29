import React, { useContext } from 'react';
import { Form, TextField, Select, SelectBox, Password } from 'choerodon-ui/pro';
import '../index.less';

const { Option } = Select;

export default (props) => (
  <Form className="hidden-password" dataSet={props.context.mailSettingDataSet} labelLayout="float" labelAlign="left">
    <input type="password" style={{ position: 'absolute', top: '-999px' }} />
    <TextField name="account" />
    <Password name="password" />
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
