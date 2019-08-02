import React, { useContext } from 'react';
import { Form, TextField, Select, SelectBox } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';

const { Option } = Select;

export default observer(({ context }) => (
  <Form dataSet={context.smsSettingDataSet} labelLayout="float" labelAlign="left">
    <TextField name="signature" />
    <TextField name="hostAddress" />
    <TextField name="hostPort" />
    <SelectBox name="sendType">
      <Option value="batch">{context.singleSendApiMap.get('batch')}</Option>
      <Option value="single">{context.singleSendApiMap.get('single')}</Option>
      <Option value="async">{context.singleSendApiMap.get('async')}</Option>
    </SelectBox>
    <TextField name={`${context.smsSettingDataSet.current.get('sendType')}SendApi`} />
    <TextField name="secretKey" />
  </Form>
));
