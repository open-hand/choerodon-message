import React, { Fragment } from 'react';
import { Form, SelectBox, Select } from 'choerodon-ui/pro';
import { useResourceContentStore } from './stores';
import MouserOverWrapper from '../../../components/mouseOverWrapper';

import './index.less';

const { Option } = Select;

export default ({ record }) => {
  const {
    intlPrefix,
    prefixCls,
    intl: { formatMessage },
    tableDs,
    notifyObject,
  } = useResourceContentStore();


  return (
    <div className={`${prefixCls}-object-content`}>
      <Form record={record}>
        <SelectBox name="notifyObject" vertical>
          <Option value="sendHandler">
            <span className={`${prefixCls}-object-content-checkbox`}>操作者</span>
          </Option>
          <Option value="sendOwner">
            <span className={`${prefixCls}-object-content-checkbox`}>项目所有者</span>
          </Option>
          <Option value="sendSpecifier">
            <span className={`${prefixCls}-object-content-checkbox`}>指定用户</span>
          </Option>
        </SelectBox>
        {record.get('notifyObject').includes('sendSpecifier') && (
          <Select name="userList" maxTagCount={2} searchable />
        )}
      </Form>
    </div>
  );
};
