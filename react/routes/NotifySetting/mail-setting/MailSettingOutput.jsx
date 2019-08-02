import React, { useContext } from 'react';
import { DataSet, Form, Output, Spin, TextField, NumberField, Password, EmailField, UrlField, DatePicker, Select, SelectBox, Switch, Lov, Button, TextArea } from 'choerodon-ui/pro';
import store from '../Store';

const OutputEmptyValue = ({ value }) => (value ? <span>{value}</span> : <span>无</span>);

export default (props) => {
  const { mailSettingDataSet } = useContext(store);
  return (
    <Spin dataSet={mailSettingDataSet}>
      <Form dataSet={mailSettingDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={120}>
        <Output name="account" />
        <Output name="password" />
        <Output name="sendName" renderer={OutputEmptyValue} />
        <Output name="protocol" />
        <Output name="host" />
        <Output
          name="ssl"
          renderer={({ value }) => (
            <span>{value ? '是' : '否'}</span>
          )}
        />
        <Output name="port" />
      </Form>
    </Spin>
  );
};
