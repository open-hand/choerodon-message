import React, { useContext } from 'react/index';
import { Form, TextField, Select, SelectBox } from 'choerodon-ui/pro';
import store from '../Store';

const { Option } = Select;

export default props => (
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
