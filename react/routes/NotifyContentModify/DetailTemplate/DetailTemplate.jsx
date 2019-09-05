import React, { useContext, useState, useRef, useEffect } from 'react';
import { Form, TextField, Select, SelectBox, TextArea, Output } from 'choerodon-ui/pro';
import { Button, Modal } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import { observer } from 'mobx-react-lite';
import Store from './Store';
import Editor from '../../../components/editor';
import './DetailTemplate.less';

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
        style={{ width: 340 }}
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
  const { detailTemplateDataSet, settingType, editing = true, prefixCls, handleOk, modal, intlPrefix, isCurrent } = context;
  async function handleSave() {
    try {
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
  // eslint-disable-next-line arrow-body-style
  const renderForm = () => {
    // const formArr = [<TextField name="name" />, <TextField name={`${settingType}Title`} />];
    // if (settingType === 'sms') {
    //   formArr.pop();
    //   formArr.push(<TextArea name={`${settingType}Content`} resize="both" />);
    // } else {
    //   formArr.push();
    // }
    // formArr.push();

    return (
      <Form dataSet={detailTemplateDataSet} labelLayout="float" labelAlign="left" className="detail-template-form">
        <TextField name="name" disabled />
        {
          settingType !== 'sms' && detailTemplateDataSet.current
            ? [
              <TextField name={`${settingType}Title`} />, <WrappedEditor
                current={detailTemplateDataSet.current}
                settingType={settingType}
                label={detailTemplateDataSet.getField(`${settingType}Content`).props.label}
              />] : <TextArea name={`${settingType}Content`} resize="both" />
        }
        <SelectBox name="current" />
      </Form>
    );
  };
  const renderDetailContent = ({ value }) => (
    <div className={`${prefixCls}-form-content-wrapper`}>
      <div
        className={`${prefixCls}-form-content-wrapper-html`}
        // eslint-disable-next-line react/no-danger
        dangerouslySetInnerHTML={{ __html: `${value}` }}
      />
    </div>
  );
  const renderDetail = () => (
    <Form dataSet={detailTemplateDataSet} labelLayout="horizontal" labelAlign="left" className={`${prefixCls}-form ${prefixCls}-form-content`}>
      <Output name="name" />
      {settingType !== 'sms' ? <Output name={`${settingType}Title`} /> : null}
      <Output
        name={`${settingType}Content`}
        renderer={renderDetailContent}
      />
    </Form>
  );
  // useEffect(() => {
  //   if (detailTemplateDataSet.current && isCurrent) {
  //     detailTemplateDataSet.current.set('current', isCurrent);
  //   }
  // }, [detailTemplateDataSet.current]);

  return editing ? renderForm() : renderDetail();
});
