import React, { useContext } from 'react';
import { Form, TextField, Select, SelectBox, Password, NumberField } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import '../index.less';

const { Option } = Select;

export default observer(({ context }) => (
  <Form className="hidden-password" dataSet={context.smsSettingDataSet} labelLayout="float" labelAlign="left">
    <input type="password" style={{ position: 'absolute', top: '-999px' }} />
    <TextField name="signature" />
    <TextField name="hostAddress" />
    <NumberField name="hostPort" />
    <SelectBox name="sendType">
      <Option value="batch">{context.singleSendApiMap.get('batch')}</Option>
      <Option value="single">{context.singleSendApiMap.get('single')}</Option>
      <Option value="async">{context.singleSendApiMap.get('async')}</Option>
    </SelectBox>
    <TextField name={`${context.smsSettingDataSet.current.get('sendType')}SendApi`} />
    <Password name="secretKey" />
  </Form>
));
