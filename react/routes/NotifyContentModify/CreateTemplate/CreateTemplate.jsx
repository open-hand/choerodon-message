import React, { useContext, useState } from 'react/index';
import { Form, TextField, Select, SelectBox } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Editor from '../../../components/editor';
import store from '../Store';

const { Option } = Select;

const setDoc = (value, current) => {
  current.set('content', value);
};
function noop() {

}
const WrappedEditor = observer(props => (
  <div>
    <p>邮件内容</p>
    <Editor
      onRef={noop}
      onChange={value => setDoc(value, props.current)}
      value={props.current.get('content')}
    />
  </div>
));

export default props => (
  <Form dataSet={props.context.createTemplateDataSet} labelLayout="float" labelAlign="left">
    <TextField name="name" />
    <TextField name="emailTitle" />
    <WrappedEditor
      current={props.context.createTemplateDataSet.current}
    />
    <SelectBox name="predefined">
      <Option value>是</Option>
      <Option value={false}>否</Option>
    </SelectBox>
  </Form>
);
