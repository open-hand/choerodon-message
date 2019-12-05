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
    { name: 'pmEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.pmEnable` }) },
    { name: 'emailEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.emailEnable` }) },
    { name: 'smsEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.smsEnable` }) },
    { name: 'noticeObject', type: 'string', label: formatMessage({ id: `${intlPrefix}.noticeObject` }) },
  ],
  queryFields: [],
});
