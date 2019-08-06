import React, { useContext } from 'react/index';
import { DataSet, Form, Output, Spin, Table, TextField, NumberField, Password, EmailField, UrlField, DatePicker, Select, SelectBox, Switch, Lov, Button, TextArea } from 'choerodon-ui/pro';
import { injectIntl, FormattedMessage } from 'react-intl';
import { Action } from '@choerodon/boot';
import store from '../Store';

const { Column } = Table;

const OutputEmptyValue = ({ value }) => (value ? <span>{value}</span> : <span>无</span>);

// 渲染消息类型
function getNameMethod({ value, record }) {
  const messageType = record.get('messageType');
  const id = record.get('id');
  const actionDatas = [{
    service: [],
    text: '修改',
    // action: () => deleteLink('email'),
  },
  {
    service: [],
    text: '删除',
    // action: () => deleteLink('inmail'),
  }];
  return (
    <React.Fragment>
      <span className="name">{value}</span>
      <Action className="action-icon" data={actionDatas} />
    </React.Fragment>
  );
}

export default (props) => {
  const { templateDataSet, intlPrefix } = useContext(store);
  return (
    <Spin dataSet={templateDataSet}>
      <h1><FormattedMessage id={`${intlPrefix}.template.header.title`} /></h1>
      <Table className="messageService" dataSet={templateDataSet}>
        <Column name="name" renderer={getNameMethod} />
        <Column name="emailTitle" />
      </Table>
    </Spin>
  );
};
