import React, { useState } from 'react';
import ContentEditable from 'react-contenteditable';
import { Button, TextArea, Row, Col } from 'choerodon-ui/pro';
import './index.less';

export default function ChoerodonEditor(props) {
  const { value, onRef, onChange, wrapperClassName, style } = props;
  const [showCode, setShowCode] = useState(false);

  function getCodeArea() {
    return (
      <TextArea style={style || { width: '100%', height: '4rem' }} value={value} onChange={onChange} />
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
        <Button onClick={() => setShowCode(!showCode)} icon={showCode ? 'navigate_before' : 'code'} />
      </div>
      {showCode ? getCodeArea() : getContentEditable()}
    </div>
  );
}
