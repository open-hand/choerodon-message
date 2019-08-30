import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Form, Output, Spin, TextField, NumberField, Password, EmailField, UrlField, DatePicker, Select, SelectBox, Switch, Lov, Button, TextArea } from 'choerodon-ui/pro';
import store from '../Store';

const OutputEmptyValue = ({ value }) => (value ? <span>{value}</span> : <span>无</span>);

export default observer((props) => {
  const { smsSettingDataSet, singleSendApiMap } = useContext(store);
  const sendType = smsSettingDataSet.current && smsSettingDataSet.current.getPristineValue('sendType');
  return (
    <Spin dataSet={smsSettingDataSet}>
      <Form pristine dataSet={smsSettingDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={120}>
        <Output name="signature" />
        <Output name="hostAddress" />
        <Output name="hostPort" renderer={OutputEmptyValue} />
        <Output
          name="sendType"
          renderer={({ value }) => (
            <span>{singleSendApiMap.get(value)}</span>
          )}
        />
        <Output name={`${sendType}SendApi`} renderer={OutputEmptyValue} />
        <Output renderer={() => '••••••'} name="secretKey" />
      </Form>
    </Spin>
  );
});
