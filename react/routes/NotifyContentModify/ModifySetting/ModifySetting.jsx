import React, { useContext, useState, useEffect, Fragment } from 'react/index';
import { Form, TextField, Select, SelectBox, NumberField } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import store from '../Store';

const { Option } = Select;

export default observer(props => {
  const handleCancel = () => {
    props.context.sendSettingDataSet.current.reset();
  };
  const { settingType } = props.context;
  useEffect(() => {
    props.modal.handleCancel(handleCancel);
  }, []);
  // Fragment 
  return (
    <Form dataSet={props.context.sendSettingDataSet} labelLayout="float" labelAlign="left" labelWidth={120}>
      {settingType === 'email' ? (
        [
          <NumberField name="retryCount" min="0" step={1} />,
          <SelectBox name="sendInstantly">
            <Option value>是</Option>
            <Option value={false}>否</Option>
          </SelectBox>,
          <SelectBox name="manualRetry">
            <Option value>是</Option>
            <Option value={false}>否</Option>
          </SelectBox>]
      )
        : (
          <SelectBox name={`${settingType}Type`}>
            <Option value="msg">消息</Option>
            <Option value="notice">通知</Option>
          </SelectBox>
        )}
      <Select name={`${settingType}Template`} />
    </Form>
  );
});
