export default ({ formatMessage, intlPrefix }) => ({
  autoQuery: false,
  selection: false,
  paging: false,
  transport: {
    read: {
      url: '/notify/v1/notices/send_settings/list/allow_config?source_type=site',
      method: 'get',
    },
  },
  fields: [
    { name: 'name', type: 'string', label: formatMessage({ id: 'receive.type' }) },
    { name: 'pm', type: 'boolean', label: formatMessage({ id: 'receive.type.pm' }) },
    { name: 'email', type: 'boolean', label: formatMessage({ id: 'receive.type.email' }) },
  ],
  queryFields: [],
});
