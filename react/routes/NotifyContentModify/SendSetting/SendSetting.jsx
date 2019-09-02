import React, { useContext } from 'react/index';
import { DataSet, Form, Output, Spin, TextField, NumberField, Password, EmailField, UrlField, DatePicker, Select, SelectBox, Switch, Lov, Button, TextArea } from 'choerodon-ui/pro';
import { injectIntl, FormattedMessage } from 'react-intl';
import store from '../Store';

const OutputEmptyValue = ({ value }) => (value ? <span>是</span> : <span>否</span>);

export default (props) => {
  const { sendSettingDataSet, intlPrefix } = useContext(store);
  return (
    <Spin dataSet={sendSettingDataSet}>
      <h1><FormattedMessage id={`${intlPrefix}.sendSetting.header.title`} /></h1>
      
      <Form dataSet={sendSettingDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={130}>
        <Output name="retryCount" />
        <Output name="sendInstantly" renderer={OutputEmptyValue} />
        <Output name="manualRetry" renderer={OutputEmptyValue} />
      </Form>
    </Spin>
  );
};
