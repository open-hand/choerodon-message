import React, { useContext, useState, useRef, useEffect } from 'react/index';
import { Form, TextField, Select, SelectBox, TextArea, message } from 'choerodon-ui/pro';
import { Button, Modal } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import Editor from '../../../components/editor';
import store from '../Store';
import DetailTemplateDataSet from '../DetailTemplate/Store/DetailTemplateDataSet';

const { Option } = Select;
// 匹配html界面为空白的正则。
const patternHTMLEmpty = /^(((<[^i>]+>)*\s*)|&nbsp;|\s)*$/;

const WrappedEditor = observer(props => {
  const [editor, setEditor] = useState(undefined);
  const [editor2, setEditor2] = useState(undefined);
  const [fullscreen, setFullscreen] = useState(false);
  const { settingType, label, context } = props;
  const setDoc = (value, current) => {
    current.set(`${settingType}Content`, value);
  };

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
  return (
    <div style={{ display: 'inline-block' }}>
      <p style={{ display: 'inline-block' }} className="content-text">{label}：</p>
      <Button style={{ float: 'right' }} icon="zoom_out_map" onClick={handleFullScreen} type="primary">全屏编辑</Button>

      <Editor
        value={props.current.get(`${settingType}Content`)}
        onRef={(node) => {
          setEditor(node);
        }}
        width={694}
        toolbarContainer="toolbar"
        nomore
        onChange={(value) => {
          setDoc(value, props.current);
        }}
      />
      <Modal
        title={label}
        visible={fullscreen}
        width="90%"
        onOk={() => setFullscreen(false)}
        onCancel={() => setFullscreen(false)}
      >
        <Editor
          toolbarContainer="toolbar2"
          value={props.current.get(`${settingType}Content`)}
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

export default props => {
  const { settingType, createTemplateDataSet } = props.context;

  props.modal.handleCancel(() => {
    createTemplateDataSet.current.reset();
  });

  function renderWrappedEditorAndTitle() {
    const templateTitle = <TextField style={{ width: 512 }} name={`${props.context.settingType}Title`} />;
    const wrappedEditor = (
      <WrappedEditor
        current={props.context.createTemplateDataSet.current}
        settingType={props.context.settingType}
        label={props.context.createTemplateDataSet.getField(`${props.context.settingType}Content`).props.label}
      />
    );
    const textArea = <TextArea style={{ width: 512 }} name="smsContent" />;
    if (settingType === 'email') {
      return [templateTitle, wrappedEditor];
    } else if (settingType === 'sms') {
      return textArea;
    } else if (settingType === 'pm') {
      return [templateTitle, wrappedEditor];
    }
  }
  const handleReset = () => {
    props.context.createTemplateDataSet.current.reset();
  };
  async function handleSave() {
    try {
      if (patternHTMLEmpty.test(props.context.createTemplateDataSet.current.get(`${settingType}Content`))) {
        message.info('内容不可为空');
        return false;
      }
      if ((await props.context.createTemplateDataSet.submit())) {
        handleReset();
        props.context.templateDataSet.query();
        return true;
      } else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }
 
  useEffect(() => {
    props.modal.handleOk(handleSave);
  }, []);
  return (
    <Form dataSet={props.context.createTemplateDataSet} labelLayout="float" labelAlign="left" className="crete-temp-form">
      <TextField style={{ width: 512 }} name="name" />
      {props.context.createTemplateDataSet.current ? renderWrappedEditorAndTitle() : null}
      <SelectBox style={{ top: '-0.25rem', position: 'relative' }} name="defaultTemplate" />
    </Form>
  );
};
