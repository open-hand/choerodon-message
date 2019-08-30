import React, { Component } from 'react';
import { inject, observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Input, Button, Select, Table, Tooltip, Form, Spin, Radio } from 'choerodon-ui';
import { axios, Content, Header, Page, Permission } from '@choerodon/master';
import { injectIntl, FormattedMessage } from 'react-intl';
import classnames from 'classnames';
import './SmsSetting.scss';
import SmsSettingStore from '../../../stores/global/sms-setting/index.js';

const intlPrefix = 'global.smssetting';
const FormItem = Form.Item;
const { Option } = Select;
const RadioGroup = Radio.Group;
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 100 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 9 },
  },
};

@Form.create({})
@withRouter
@injectIntl
@inject('AppState')
@observer
export default class SmsSetting extends Component {
  constructor(props) {
    super(props);
    this.state = this.getInitState();
  }

  componentDidMount() {
    this.loadSetting();
  }

  getInitState() {
    return {
      loading: true,
      saving: false,
    };
  }

  loadSetting = () => {
    this.setState({ loading: true });
    SmsSettingStore.loadData().then((data) => {
      if (!data.failed) {
        SmsSettingStore.setSettingData(data || {});
      } else {
        Choerodon.prompt(data.message);
      }
      this.setState({ loading: false });
    }).catch(Choerodon.handleResponseError);
  }


  handleRefresh = () => {
    this.loadSetting();
  }

  testContact = () => {
    const { intl, form } = this.props;
    const { getFieldsValue } = form;
    const values = getFieldsValue();
    const setting = {
      ...values,
      ssl: values.ssl === 'Y',
      port: Number(values.port),
      objectVersionNumber: SmsSettingStore.getSettingData.objectVersionNumber,
    };
    SmsSettingStore.testConnection(setting).then((data) => {
      if (data.failed) {
        Choerodon.prompt(data.message);
      } else {
        Choerodon.prompt(intl.formatMessage({ id: `${intlPrefix}.connect.success` }));
      }
    }).catch((error) => {
      Choerodon.handleResponseError(error);
    });
  }

  handleSubmit = (e) => {
    e.preventDefault();
    const { intl } = this.props;
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          saving: true,
        });
        const setting = {
          ...values,
          hostPort: values.hostPort ? Number(values.hostPort) : undefined,
          objectVersionNumber: SmsSettingStore.getSettingData.objectVersionNumber,
        };
        SmsSettingStore.updateData(setting).then((data) => {
          if (data.failed) {
            Choerodon.prompt(data.message);
          } else {
            Choerodon.prompt(intl.formatMessage({ id: 'save.success' }));
            SmsSettingStore.setSettingData(data);
          }
          this.setState({
            saving: false,
          });
        }).catch((error) => {
          Choerodon.handleResponseError(error);
          this.setState({
            saving: false,
          });
        });
      }
    });
  }

  render() {
    const { intl, form, AppState } = this.props;
    const { loading, saving } = this.state;
    const { getFieldDecorator } = form;
    const mainContent = (
      <div className={classnames('c7n-smssetting-container', { 'c7n-smssetting-loading-container': loading })}>
        {loading ? <Spin size="large" /> : (
          <Form onSubmit={this.handleSubmit} layout="vertical">
            <h3 className="c7n-smssetting-container-title">服务器设置</h3>
            <div className="c7n-smssetting-container-wrap">
              <FormItem
                {...formItemLayout}
              >
                {getFieldDecorator('signature', {
                  rules: [{
                    required: true,
                    message: '短信签名是必输字段',
                  }],
                  initialValue: SmsSettingStore.getSettingData.signature,
                })(
                  <Input label="短信签名" autoComplete="off" />,
                )}
              </FormItem>
              <FormItem
                {...formItemLayout}
              >
                {getFieldDecorator('hostAddress', {
                  rules: [{
                    required: true,
                    whitespace: true,
                    message: intl.formatMessage({ id: `${intlPrefix}.host.required` }),
                  }],
                  initialValue: SmsSettingStore.getSettingData.hostAddress,
                })(
                  <Input label="短信服务器地址" autoComplete="off" />,
                )}
              </FormItem>
              <FormItem
                {...formItemLayout}
              >
                {getFieldDecorator('hostPort', {
                  rules: [{
                    // required: true,
                    // whitespace: true,
                    // message: intl.formatMessage({ id: `${intlPrefix}.port.required` }),
                  }, {
                    pattern: /^[1-9]\d*$/,
                    message: intl.formatMessage({ id: `${intlPrefix}.port.pattern` }),
                  }],
                  initialValue: SmsSettingStore.getSettingData.hostPort ? String(SmsSettingStore.getSettingData.hostPort) : undefined,
                })(
                  <Input label="短信服务器端口" autoComplete="off" />,
                )}
              </FormItem>
              <FormItem
                {...formItemLayout}
              >
                {getFieldDecorator('sendType', {
                  initialValue: SmsSettingStore.getSettingData.sendType || 'batch',
                })(
                  <RadioGroup
                    className="sslRadioGroup"
                    label="调用方式"
                  >
                    <Radio value="batch">批量调用</Radio>
                    <Radio value="single">单体调用</Radio>
                    <Radio value="async">异步调用</Radio>
                  </RadioGroup>,
                )}
              </FormItem>
              {
                this.props.form.getFieldValue('sendType') === 'batch' && (
                  <FormItem
                    {...formItemLayout}
                  >
                    {getFieldDecorator('batchSendApi', {
                      rules: [{
                      }],
                      initialValue: SmsSettingStore.getSettingData.batchSendApi,
                    })(
                      <Input label="批量调用地址" autoComplete="off" />,
                    )}
                  </FormItem>
                )
              }
              {
                this.props.form.getFieldValue('sendType') === 'single' && (
                  <FormItem
                    {...formItemLayout}
                  >
                    {getFieldDecorator('singleSendApi', {
                      rules: [{
                      }],
                      initialValue: SmsSettingStore.getSettingData.singleSendApi,
                    })(
                      <Input label="单体调用地址" autoComplete="off" />,
                    )}
                  </FormItem>
                )
              }
              {
                this.props.form.getFieldValue('sendType') === 'async' && (
                  <FormItem
                    {...formItemLayout}
                  >
                    {getFieldDecorator('asyncSendApi', {
                      rules: [{
                      }],
                      initialValue: SmsSettingStore.getSettingData.asyncSendApi,
                    })(
                      <Input label="异步调用地址" autoComplete="off" />,
                    )}
                  </FormItem>
                )
              }
              <FormItem
                {...formItemLayout}
              >
                {getFieldDecorator('secretKey', {
                  rules: [{
                    required: true,
                    whitespace: true,
                    message: intl.formatMessage({ id: `${intlPrefix}.password.required` }),
                  }],
                  initialValue: SmsSettingStore.getSettingData.secretKey,
                })(
                  <Input type="password" label="短信服务器密钥" autoComplete="off" showPasswordEye />,
                )}
              </FormItem>
            </div>
            <hr className="divider" />
            <div className="btnGroup">
              <Button
                funcType="raised"
                type="primary"
                htmlType="submit"
                loading={saving}
              >
                <FormattedMessage id="save" />
              </Button>
              <Button
                funcType="raised"
                onClick={() => {
                  const { resetFields } = this.props.form;
                  resetFields();
                }}
                style={{ color: '#3F51B5' }}
                disabled={saving}
              >
                <FormattedMessage id="cancel" />
              </Button>
            </div>
          </Form>
        )}
      </div>
    );


    return (
      <Page>
        <Header
          title="短信配置"
        >
          <Button
            onClick={this.handleRefresh}
            icon="refresh"
          >
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Content
          code={intlPrefix}
          values={{ name: AppState.getSiteInfo.systemName || 'Choerodon' }}
        >
          {mainContent}
        </Content>
      </Page>
    );
  }
}
