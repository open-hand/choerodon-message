import { DataSet } from 'choerodon-ui/pro/lib';

export default function (optionDs) {
  const queryEnabled = new DataSet({
    autoQuery: true,
    paging: false,
    fields: [
      { name: 'key', type: 'string' },
      { name: 'value', type: 'string' },
    ],
    data: [
      { key: true, value: '启用' },
      { key: false, value: '停用' },
    ],
  });
  const queryAllowConfig = new DataSet({
    autoQuery: true,
    paging: false,
    fields: [
      { name: 'key', type: 'string' },
      { name: 'value', type: 'string' },
    ],
    data: [
      { key: true, value: '允许' },
      { key: false, value: '禁止' },
    ],
  });
  return {
    autoQuery: true,
    selection: false,
    paging: true,
    fields: [{
      name: 'messageType',
      type: 'string',
      label: '消息类型',
    }, {
      name: 'introduce',
      type: 'string',
      label: '说明',
    },
    {
      name: 'level',
      type: 'string',
      label: '层级',
    },
    {
      name: 'enabled',
      type: 'boolean',
      label: '状态',
    },
    {
      name: 'allowConfig',
      type: 'boolean',
      label: '允许配置接收',
    },
    ],
    queryFields: [{
      name: 'messageType',
      type: 'string',
      label: '消息类型',
    }, {
      name: 'introduce',
      type: 'string',
      label: '说明',
    }, {
      name: 'level',
      type: 'string',
      label: '层级',
      textField: 'value',
      valueField: 'key',
      options: optionDs,
    }, {
      name: 'enabled',
      type: 'string',
      label: '状态',
      textField: 'value',
      valueField: 'key',
      options: queryEnabled,
    }, {
      name: 'allowConfig',
      type: 'string',
      label: '允许配置接收',
      textField: 'value',
      valueField: 'key',
      options: queryAllowConfig,
    }],
    transport: {
      read: {
        url: '/notify/v1/notices/send_settings',
        method: 'get',
      },
    },
  };
}
