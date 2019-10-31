/**
 *
 * @param id
 * @return DataSet
 */
export default (id) => ({
  autoQuery: true,
  selection: false,
  fields: [
    { name: 'name', type: 'string', label: 'Webhook名称', required: true },
    { name: 'webhookPath', type: 'string', label: 'Webhook地址', required: true },
    { name: 'type', type: 'string', label: 'Webhook类型' },
    { name: 'enableFlag', type: 'boolean', label: '状态', required: true },
  ],
  transport: {
    read: {
      url: `notify/v1/projects/${id}/web_hooks`,
      method: 'get',
    },
    destroy: ({ data: [record, ...etc] }) => ({
      url: `notify/v1/projects/${id}/web_hooks/${record.id}`,
      method: 'delete',
      data: undefined,
    }),
  },
});
