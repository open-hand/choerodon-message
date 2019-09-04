import React, { useContext, useState, useRef, useEffect } from 'react';
import { Form, TextField, Select, SelectBox, TextArea } from 'choerodon-ui/pro';
import { Button, Modal } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import { observer } from 'mobx-react-lite';
import Store from './Store';
import Editor from '../../../components/editor';

const { Option } = Select;
const WrappedEditor = observer(props => {
  const [editor, setEditor] = useState(undefined);
  const [editor2, setEditor2] = useState(undefined);
  const [fullscreen, setFullscreen] = useState(false);
  const [current, setCurrent] = useState(undefined);
  const { settingType, label } = props;
  const setDoc = (value, current0) => {
    current0.set(`${settingType}Content`, value);
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
      <p style={{ display: 'inline-block' }} className="content-text">{label}：</p>
      {/* <Editor
      onRef={noop}
      onChange={value => setDoc(value, props.current)}
      value={props.current.get('content')}
    /> */}
      <Button style={{ float: 'right' }} icon="zoom_out_map" onClick={handleFullScreen} type="primary">全屏编辑</Button>

      <Editor
        value={props.current.get(`${settingType}Content`)}
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
            // setCurrent(value);
          }}
        />
      </Modal>
    </div>
  );
});
export default observer(() => {
  const context = useContext(Store);
  // console.log('datail', context);
  const { detailTemplateDataSet, settingType, editing = true, handleOk, modal, intlPrefix, isCurrent } = context;
  async function handleSave() {
    try {
      // const currentArr = context.detailTemplateDataSet.current.get('current');
      // if (currentArr.length === 0) {
      //   context.detailTemplateDataSet.current.set('current', false);
      // } 
      // else if (currentArr.length === 2) {
      //   currentArr.splice(0, 1);
      // }
      if ((await context.detailTemplateDataSet.submit())) {
        context.context.templateDataSet.query();
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
    if (editing) {
      modal.handleOk(handleSave);
    }
  }, []);
  // useEffect(() => {
  //   if (detailTemplateDataSet.current && isCurrent) {
  //     detailTemplateDataSet.current.set('current', isCurrent);
  //   }
  // }, [detailTemplateDataSet.current]);

  return (
    <Form dataSet={detailTemplateDataSet} labelLayout="float" labelAlign="left" className="detail-template-form">
      <TextField name="name" disabled />
      <TextField name={`${settingType}Title`} disabled={!editing} />

      {editing && detailTemplateDataSet.current ? (
        <WrappedEditor
          current={detailTemplateDataSet.current}
          settingType={context.settingType}
          label={detailTemplateDataSet.getField(`${settingType}Content`).props.label}
        />
      ) : <TextArea name={`${settingType}Content`} resize="both" disabled={editing && settingType !== 'sms'} />}
      {editing ? (
        <SelectBox name="current" />
      ) : null}
    </Form>
  );
});
