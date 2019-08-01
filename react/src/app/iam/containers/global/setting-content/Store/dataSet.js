export default function () {
  return {
    autoQuery: true,
    selection: false,
    paging: true,
    fields: [{
      name: 'messageType',
      type: 'string',
      label: '消息类型',
      required: true,
    },
    {
      name: 'introduce',
      type: 'string',
      label: '说明',
      required: true,
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
      required: true,
    },
    ],
    queryFields: [{
      name: 'messageType',
      type: 'string',
      label: '消息类型',
      required: true,
    },
    {
      name: 'introduce',
      type: 'string',
      label: '说明',
      required: true,
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
      required: true,
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
