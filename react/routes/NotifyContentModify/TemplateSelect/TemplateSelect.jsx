import React, { useContext, useState, useEffect } from 'react/index';
import { DataSet, Form, Output, Spin, Table, TextField, NumberField, Password, EmailField, UrlField, DatePicker, Select, SelectBox, Switch, Lov, Button, TextArea, Modal } from 'choerodon-ui/pro';
import { injectIntl, FormattedMessage } from 'react-intl';
import { Action } from '@choerodon/master';
import { observer } from 'mobx-react-lite';
import store from '../Store';
import DetailTemplate from '../DetailTemplate';

const { Column } = Table;

const OutputEmptyValue = ({ value }) => (value ? <span>{value}</span> : <span>无</span>);

// 打开详情页
const detailTemplate = (detailId) => {
  Modal.open({
    title: '详情页',
    drawer: true,
    style: {
      width: 380,
    },
    children: (
      <DetailTemplate detailId={detailId} editing={false} />
    ),
    // onOk: handleSave,
    okCancel: false,
    // beforeClose: (a, b, c) => { debugger;window.console.log('after close'); },
  });
};
// 修改
const updateLink = (type, detailId, context) => {
  Modal.open({
    title: '修改模版',
    drawer: true,
    style: {
      width: 380,
    },
    children: (
      <DetailTemplate detailId={detailId} />
    ),
    // onOk: handleSave,
    // onCancel: resetFunc,
    // beforeClose: (a, b, c) => { debugger;window.console.log('after close'); },
  });
};


const dataSet = new DataSet({
  autoQuery: true,
  paging: true,
  fields: [
    // { name: 'id', type: 'string' },
    { name: 'name', type: 'string', label: '名字' },
    { name: 'emailTitle', type: 'string', label: '111' },
    { name: 'predefined', type: 'boolean', label: '2222' },
  ],
  transport: {
    read: {
      url: 'notify/v1/templates?businessType=feedback-created&messageType=email',
      method: 'get',
    },
  },
});
export default (props) => {
  const context = useContext(store);
  const { templateDataSet, intlPrefix } = context;
  // 渲染消息类型
  function getNameMethod({ value, text, name, record }) {
    const messageType = record.get('messageType');
    // const id = record.get('id');
    const actionDatas = [{
      service: [],
      text: '修改',
      action: () => updateLink('email', record.get('id'), context),
    },
    {
      service: [],
      text: '删除',
      // action: () => deleteLink('inmail'),
    }];
    return (
      value ? (
        <React.Fragment>
          <span className="name" onClick={detailTemplate.bind(this, record.get('id'))}>{value}</span>
          <Action className="action-icon" data={actionDatas} />
        </React.Fragment>
      ) : null
    );
  }
  return (
    <Spin dataSet={templateDataSet}>
      <h1><FormattedMessage id={`${intlPrefix}.template.header.title`} /></h1>
      <Table className="messageService" dataSet={templateDataSet}>
        <Column name="name" renderer={getNameMethod.bind(this)} />
        <Column name="emailTitle" />
        <Column name="predefined" />
      </Table>
    </Spin>
  );
};
