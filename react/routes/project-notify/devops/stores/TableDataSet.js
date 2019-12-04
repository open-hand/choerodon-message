export default ({ formatMessage, intlPrefix, projectId }) => ({
  autoQuery: false,
  selection: false,
  transport: {
    read: {
      url: '',
      method: 'post',
    },
  },
  fields: [
    { name: 'type', type: 'string', label: formatMessage({ id: `${intlPrefix}.type` }) },
    { name: 'pm', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.pm` }) },
    { name: 'email', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.email` }) },
    { name: 'noticeObject', type: 'string', label: formatMessage({ id: `${intlPrefix}.noticeObject` }) },
  ],
  queryFields: [],
});
