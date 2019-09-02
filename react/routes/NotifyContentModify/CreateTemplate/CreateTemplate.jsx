import React, { useContext, useState, useRef, useEffect } from 'react/index';
import { Form, TextField, Select, SelectBox } from 'choerodon-ui/pro';
import { Button, Modal } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import Editor from '../../../components/editor';
import store from '../Store';

const { Option } = Select;

const setDoc = (value, current) => {
  current.set('emailContent', value);
};

const WrappedEditor = observer(props => {
  const [editor, setEditor] = useState(undefined);
  const [editor2, setEditor2] = useState(undefined);
  const [fullscreen, setFullscreen] = useState(false);

  useEffect(() => {
    if (editor) {
      editor.initEditor();
    }
  }, [editor]);
  const handleFullScreen = () => {
    if (editor2) {
      editor2.initEditor();
    }
    setFullscreen(true);
  };
  // console.log(props);
  return (
    <div style={{ display: 'inline-block' }}>
      <p style={{ display: 'inline-block' }} className="content-text">邮件内容：</p>
      {/* <Editor
      onRef={noop}
      onChange={value => setDoc(value, props.current)}
      value={props.current.get('content')}
    /> */}
      <Button style={{ float: 'right' }} icon="zoom_out_map" onClick={handleFullScreen} type="primary">全屏编辑</Button>

      <Editor
        value={props.current.get('emailContent')}
        onRef={(node) => {
          setEditor(node);
        }}
        toolbarContainer="toolbar"
        onChange={(value) => {
          setDoc(value, props.current);
        }}
      />
      <Modal
        title="编辑公告内容"
        visible={fullscreen}
        width="90%"
        onOk={() => setFullscreen(false)}
        onCancel={() => setFullscreen(false)}
      >
        <Editor
          toolbarContainer="toolbar2"
          value={props.current.get('emailContent')}
          height={500}
          nomore
          onRef={(node) => {
            setEditor2(node);
          }}
          onChange={(value) => {
            setDoc(value, props.current);
          }}
        />
      </Modal>
    </div>
  );
});

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
