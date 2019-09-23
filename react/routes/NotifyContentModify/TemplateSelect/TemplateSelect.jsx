import React, { useContext, useState, useEffect } from 'react/index';
import { DataSet, Form, Output, Spin, Table, TextField, NumberField, Password, EmailField, UrlField, DatePicker, Select, SelectBox, Switch, Lov, Button, TextArea, Modal } from 'choerodon-ui/pro';
import { injectIntl, FormattedMessage } from 'react-intl';
import { Action, axios } from '@choerodon/master';
import { observer } from 'mobx-react-lite';
import store from '../Store';
import DetailTemplate from '../DetailTemplate';
import FormHeader from '../common/FormHeader';

const { Column } = Table;

const OutputEmptyValue = ({ value }) => (value ? <span>{value}</span> : <span>无</span>);

// 打开详情页
const detailTemplate = (detailId, context) => {
  Modal.open({
    title: '详情页',
    drawer: true,
    style: {
      width: 740,
    },
    children: (
      <DetailTemplate context={context} detailId={detailId} editing={false} />
    ),
    okText: <div>关闭</div>,
    okCancel: false,
    // beforeClose: (a, b, c) => { debugger;window.console.log('after close'); },
  });
};

// 修改
/**
 * 
 * @param {string} type 邮件/站内信/短信 类型
 * @param {*} detailId 当前模板对应的id
 * @param {*} context 
 * @param {*} index  用于判定是否是当前
 */
const updateLink = (type, detailId, context, index) => {
  Modal.open({
    title: '修改模版',
    drawer: true,
    style: {
      width: 740,
    },
    children: (
      <DetailTemplate context={context} detailId={detailId} isCurrent={index} />
    ),
    okText: <div>保存</div>,
    // okText: <span className="modal-footer-btn">保存</span>,
    cancelText: <div style={{ color: '#3F51B5' }}>取消</div>,
    // onOk: handleSave,
    // onCancel: resetFunc,
  });
};

function renderPredefined({ value, record }) {
  return value ? '预定义' : '自定义';
}

export default (props) => {
  const context = useContext(store);
  const { templateDataSet, intlPrefix, prefixCls, settingType } = context;
  function changeCurrent(id) {
    // 【PUT】/v1/templates/{id}
    axios.put(`notify/v1/templates/${id}`).then((data) => {
      if (data.failed) {
        throw data.message;
      }
      Choerodon.prompt('更改默认成功');
      templateDataSet.query();
    }).catch((error) => {
      Choerodon.prompt(error);
    });
  }
  const deleteLink = (id) => {
    // 'DELETE删除模板/v1/templates/{id}');
    axios.delete(`notify/v1/templates/${id}`).then((data) => {
      if (data.failed) {
        throw data.message;
      }
      Choerodon.prompt('删除模板成功');
      templateDataSet.query();
    }).catch((error) => {
      Choerodon.prompt(error);
    });
  };

  // 渲染消息类型 
  function getNameMethod({ value, text, name, record }) {
    const messageType = record.get('messageType');
    const { index } = record;
    // const id = record.get('id');
    const moduleText = record.get('currentTemplate') ? '设为默认模板' : '取消设为默认模板';
    const actionDatas = [{
      service: [],
      text: '修改',
      action: () => updateLink(settingType, record.get('id'), context, record.get('currentTemplate')),
    },
    {
      service: [],
      text: '删除',
      action: () => deleteLink(record.get('id')),
    }];
    const currentArr = {
      service: [],
      text: '设为默认模板',
      action: () => changeCurrent(record.get('id')),
    };
    if (record.get('predefined')) {
      actionDatas.pop();
    }
    if (!record.get('currentTemplate')) {
      actionDatas.splice(1, 0, currentArr);
    }

    return (
      value ? (
        <React.Fragment>
          <span className={`${prefixCls}-detail`} onClick={detailTemplate.bind(this, record.get('id'), context)}>{value}</span>
          {record.get('currentTemplate') ? <span className={`${prefixCls}-current`}>当前</span> : ''}
          <Action className="action-icon" style={{ float: 'right', marginTop: 6 }} data={actionDatas} />
        </React.Fragment>
      ) : null
    );
  }
  return (
    // <Spin dataSet={templateDataSet}>
    <React.Fragment>
      <FormHeader isHasLine={false} title={<FormattedMessage id={`${intlPrefix}.template.header.title`} />} />
      <Table className="messageService" dataSet={templateDataSet} elementClassName={`${prefixCls}-template-select`}>
        <Column align="left" name="name" renderer={getNameMethod.bind(this)} />
        <Column name={`${settingType}Title`} />
        {settingType === 'sms' ? <Column name={`${settingType}Content`} /> : null}
        <Column name="predefined" renderer={renderPredefined.bind(this)} />
      </Table>
    </React.Fragment>
    // </Spin>
  );
};
