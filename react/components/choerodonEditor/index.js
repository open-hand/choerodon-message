import React, { useState } from 'react';
import ContentEditable from 'react-contenteditable';
import { Button, TextArea, Col } from 'choerodon-ui/pro';
import CodeMirror from 'react-codemirror';
import { Tooltip } from 'choerodon-ui';
import './index.less';

require('codemirror/lib/codemirror.css');

export default function ChoerodonEditor(props) {
  const { value, onRef, onChange, wrapperClassName, style } = props;
  const [showCode, setShowCode] = useState(true);
  const options = {
    lineNumbers: true,
    lineWrapping: true,
    readOnly: false,
    mode: 'htmlmixed',
  };

  function getCodeArea() {
    return (
      <CodeMirror style={style || { width: '100%', height: '4rem' }} options={options} value={value} onChange={onChange} />
    );
  }
  function getContentEditable() {
    return (
      <ContentEditable
        className="c7n-html-editor-content"
        innerRef={onRef}
        style={style}
        html={value}
        disabled={false}
        onChange={(evt) => onChange(evt.target.value)}
        tagName="div"
      />
    );
  }
  
  return (
    <div className={`c7n-html-editor-wrapper ${wrapperClassName || ''}`}>
      <div className="c7n-html-editor-toolbar">
        <Button color="primary" onClick={() => setShowCode(!showCode)} icon={showCode ? 'navigate_before' : 'code'}>{showCode ? (<Tooltip style={{ zIndex: 1000 }} title="html编辑器的所见即所得编辑可以直接粘贴word、excel和网页中直接复制的内容">切换至所见即所得编辑</Tooltip>) : '切换至代码编辑'}</Button>
      </div>
      {showCode ? getCodeArea() : getContentEditable()}
    </div>
  );
}
