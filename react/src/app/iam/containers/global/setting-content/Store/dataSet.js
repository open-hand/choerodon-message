export default function () {
  return {
    autoQuery: true,
    selection: false,
    paging: true,
    fields: [{
      name: 'messageType',
      type: 'string',
      label: '消息类型',
    },
    {
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
    },
    {
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
      type: 'string',
      label: '状态',
    },
    {
      name: 'allowConfig',
      type: 'string',
      label: '允许配置接收',
    },
    ],
    transport: {
      read: {
        url: '/notify/v1/notices/send_settings',
        method: 'get',
      },
    },
  };
}
