import React, { useContext, useState, useRef, useEffect } from 'react';
import { Form, TextField, Select, SelectBox, TextArea } from 'choerodon-ui/pro';
import { Button, Modal } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import Store from './Store';
import Editor from '../../../components/editor';

const { Option } = Select;
const WrappedEditor = observer(props => {
  const [editor, setEditor] = useState(undefined);
  const [editor2, setEditor2] = useState(undefined);
  const [fullscreen, setFullscreen] = useState(false);
  const [current, setCurrent] = useState(undefined);
  const setDoc = (value, current0) => {
    current0.set('emailContent', value);
  };
  useEffect(() => {
    if (editor) {
      editor.initEditor();
    }
  }, [props.current]);
  const handleFullScreen = () => {
    if (editor2) {
      editor2.initEditor();
    }
    setFullscreen(true);
  };
  // return (<div>11</div>);
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
          // current.set('emailContent', value);
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
            // setCurrent(value);
          }}
        />
      </Modal>
    </div>
  );
});
export default observer(() => {
  const context = useContext(Store);
  const { detailTemplateDataSet, editing = true, handleOk, modal } = context;
  async function handleSave() {
    try {
      if ((await context.detailTemplateDataSet.submit())) {
        // setTimeout(() => { window.location.reload(true); }, 1000);
        return true;
      } else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }
  useEffect(() => {
    modal.handleOk(handleSave);
  }, []);

  return (
    <Form dataSet={detailTemplateDataSet} labelLayout="float" labelAlign="left">
      <TextField name="name" disabled={!editing} />
      <TextField name="emailTitle" disabled={!editing} />

      {editing && detailTemplateDataSet.current ? (
        <WrappedEditor
          current={detailTemplateDataSet.current}
        />
      ) : <TextArea name="emailContent" resize="both" disabled />}
      {editing ? (
        <SelectBox name="predefined">
          <Option value>是</Option>
          <Option value={false}>否</Option>
        </SelectBox>
      ) : null}
    </Form>
  );
});
