export default () => ({
  autoQuery: true,
  selection: false,
  transport: {
    read: {
      url: '/notify/v1/web_hook_records',
      method: 'get',
    },
  },
  fields: [
    { name: 'sendTime', type: 'date', label: '发送时间' },
    { name: 'status', type: 'string', label: '状态' },
    { name: 'failedReason', type: 'string', label: '失败原因' },
    { name: 'projectName', type: 'string', label: '项目' },
    { name: 'webhookPath', type: 'string', label: 'webhook地址' },
  ],
  queryFields: [
    { name: 'sendTime', type: 'date', label: '发送时间' },
    { name: 'status', type: 'string', label: '状态' },
    { name: 'failedReason', type: 'string', label: '失败原因' },
    { name: 'projectName', type: 'string', label: '项目' },
    { name: 'webhookPath', type: 'string', label: 'webhook地址' },
  ],
});
