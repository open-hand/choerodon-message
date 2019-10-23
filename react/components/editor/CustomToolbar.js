/* eslint-disable jsx-a11y/control-has-associated-label */
/* eslint-disable react/button-has-type */
import React, { useState } from 'react';
import classnames from 'classnames';

export default function CustomToolbar({ toolbarContainer, nomore }) {
  const [showMore, setShowMore] = useState(false);
  return (
    <div id={toolbarContainer}>
      <span className="ql-formats">
        <button className="ql-bold" />
        <button className="ql-italic" />
        <button className="ql-underline" />
        <button className="ql-list" value="ordered" />
        <button className="ql-list" value="bullet" />
        <select className="ql-align" />
        <select className="ql-header">
          <option selected />
          <option value="1">H1</option>
          <option value="2">H2</option>
          <option value="3">H3</option>
          <option value="4">H4</option>
          <option value="5">H5</option>
          <option value="6">H6</option>
        </select>
        {!nomore && <button onClick={() => setShowMore(!showMore)} className={classnames(showMore ? 'ql-close' : 'ql-open')} />}
        <span style={{ display: showMore || nomore ? 'inline-block' : 'none' }}>
          <select className="ql-color" />
          {
        navigator.platform.indexOf('Mac') > -1 ? (
          <select className="ql-font">
            <option selected />
            <option value="STSong">华文宋体</option>
            <option value="STKaiti">华文楷体</option>
            <option value="STHeiti">华文黑体</option>
            <option value="STFangsong">华文仿宋</option>
          </select>
        ) : (
          <select className="ql-font">
            <option selected />
            <option value="SimSun">宋体</option>
            <option value="KaiTi">楷体</option>
            <option value="SimHei">黑体</option>
            <option value="FangSong">仿宋</option>
            <option value="Microsoft-YaHei">微软雅黑</option>
          </select>
        )
      }
          <select className="ql-size">
            <option value="10px" />
            <option value="12px" />
            <option value="14px" />
            <option value="16px" />
            <option value="18px" />
            <option value="20px" />
          </select>
          <button className="ql-link" />
          <button className="ql-image" />
          <button className="ql-code-block" />
        </span>
      </span>
    </div>
  );
}
