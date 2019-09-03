import React, { useContext } from 'react';
import './FormHeader.less';

export default (props) => {
  const { title, isHasLine = true } = props;
  return (
    <div className="formheader">
      <span className="title">{title}</span>
      {isHasLine
        ? <div className="line" /> : ''}
    </div>
  );
};
