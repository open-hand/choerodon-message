import React, { useContext } from 'react';
import { DataSet, Form, Output, Spin, TextField, NumberField, Password, EmailField, UrlField, DatePicker, Select, SelectBox, Switch, Lov, Button, TextArea } from 'choerodon-ui/pro';
import { injectIntl, FormattedMessage } from 'react-intl';
import store from '../Store';
import FormHeader from '../common/FormHeader';

const { Option } = SelectBox;
const OutputEmptyValue = ({ value }) => (value ? <span>是</span> : <span>否</span>);
const renderPmType = ({ value }) => (value === 'msg' ? <span>消息</span> : <span>通知</span>);
export default (props) => {
  const { sendSettingDataSet, intlPrefix, prefixCls, settingType } = useContext(store);
  const renderForm = (type) => {
    switch (type) {
      case 'email':
        return (
          <Form dataSet={sendSettingDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={136} className={`${prefixCls}-send-setting`}>
            <Output name="retryCount" />
            <Output name="sendInstantly" renderer={OutputEmptyValue} />
            <Output name="manualRetry" renderer={OutputEmptyValue} />
          </Form>
        );
      case 'pm':
        return (
          <Form dataSet={sendSettingDataSet} labelLayout="horizontal" labelAlign="left" labelWidth={136} className={`${prefixCls}-send-setting`}>
            <Output name="pmType" renderer={renderPmType} />
          </Form>
        );

      case 'sms':
        return false;
      default:
        return false;
    }
  };
  const form = renderForm(settingType);
  return form ? (
    <Spin dataSet={sendSettingDataSet}>
      <FormHeader title={<FormattedMessage id={`${intlPrefix}.sendSetting.header.title`} />} />
      {form}
    </Spin>
  ) : null;
};
