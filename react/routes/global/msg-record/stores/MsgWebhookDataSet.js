export default () => ({
  autoQuery: true,
  selection: false,
  transport: {
    read: {
      url: '/notify/v1/web_hook_records',
      method: 'get',
    },
  },
  // fields: [
  //   { name: 'promptCode', type: 'string', label: '编码', pattern: /^([a-zA-Z]|[0-9])([a-zA-Z0-9]|_|-|\.|\/)*/, required: true },
  //   { name: 'lang', type: 'string', label: '语言', required: true, textField: 'text', valueField: 'value', options: langOptionsDs },
  //   { name: 'description', type: 'string', label: '描述', required: true },
  //   { name: 'serviceCode', type: 'string', label: '所属微服务', required: true, textField: 'service', valueField: 'service', options: serviceOptionsDataSet },
  // ],
  queryFields: [
    { name: 'sendTime', type: 'date', label: '发送时间' },
    { name: 'status', type: 'string', label: '状态' },
    { name: 'failedReason', type: 'string', label: '失败原因' },
    { name: 'projectName', type: 'string', label: '项目' },
    { name: 'webhookPath', type: 'string', label: 'webhook地址' },
  ],
});
