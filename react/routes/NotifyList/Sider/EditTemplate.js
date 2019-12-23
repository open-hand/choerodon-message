import React from 'react';
import { Form, TextField, TextArea, NumberField, message } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import ChoerodonEditor from '../../../components/editor';
import MdEditor from '../../../components/MdEditor';
import './index.less';

export default observer(({ context, modal, type }) => {
  const { messageTypeDetailDataSet } = context;
  const dataSet = messageTypeDetailDataSet.children.templates;
  let record = messageTypeDetailDataSet.children.templates.find((item) => item.getPristineValue('sendingType') === type);
  if (!record) {
    dataSet.create({ content: '', sendingType: type, isPredefined: false, sendSettingCode: messageTypeDetailDataSet.current.get('code') });
    record = dataSet.current;
  }
  modal.handleOk(async () => {
    try {
      if (await dataSet.submit() !== false) {
        messageTypeDetailDataSet.query();
      } else {
        return false;
      }
    } catch (err) {
      messageTypeDetailDataSet.query();
      return false;
    }
  });
  modal.handleCancel(() => {
    dataSet.reset();
  });
  switch (type) {
    case 'email':
    case 'pm':
      return (
        <div className="c7n-notify-contentList-sider">
          <Form record={record}>
            <TextField label={type === 'email' ? '邮件主题' : '站内信标题'} name="title" />
            <ChoerodonEditor
              nomore
              toolbarContainer="toolbar"
              value={record.get('content')}
              onChange={(value) => record.set('content', value)}
            />
          </Form>
        </div>
      );
    case 'webhook':
      return (
        <div className="c7n-notify-contentList-sider">
          <Form record={record}>
            <TextField label="模板主题" name="title" />
            <MdEditor
              value={record.get('content')}
              onChange={(value) => record.set('content', value)}
            />
          </Form>
        </div>
      );
    case 'sms':
      return (
        <div className="c7n-notify-contentList-sider">
          <Form record={record}>
            <TextArea resize="vertical" label="短信内容" name="content" />
          </Form>
        </div>
      );
    default:
      return null;
  }
});
