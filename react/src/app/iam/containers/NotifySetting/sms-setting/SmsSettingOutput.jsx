import React, { useContext } from 'react';
import { DataSet, Form, Output, Spin, TextField, NumberField, Password, EmailField, UrlField, DatePicker, Select, SelectBox, Switch, Lov, Button, TextArea } from 'choerodon-ui/pro';
import store from '../Store';

const OutputEmptyValue = ({ value }) => (value ? <span>{value}</span> : <span>无</span>);

export default (props) => {
  const { smsSettingDataSet, singleSendApiMap } = useContext(store);
  return (
    <Spin dataSet={smsSettingDataSet}>
      <Form dataSet={smsSettingDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={120}>
        <Output name="signature" />
        <Output name="hostAddress" />
        <Output name="hostPort" renderer={OutputEmptyValue} />
        <Output
          name="sendType"
          renderer={({ value }) => (
            <span>{singleSendApiMap.get(value)}</span>
          )}
        />
        <Output name="singleSendApi" renderer={OutputEmptyValue} />
        <Output name="secretKey" />
      </Form>
    </Spin>
  );
};
