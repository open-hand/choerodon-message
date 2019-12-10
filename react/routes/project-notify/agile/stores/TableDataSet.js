export default ({ formatMessage, intlPrefix, projectId }) => ({
  autoQuery: true,
  selection: false,
  paging: false,
  transport: {
    read: {
      url: `/notify/v1/notices/${projectId}/message/setting/list`,
      method: 'post',
      data: { notifyType: 'agile' },
    },
  },
  fields: [
    { name: 'name', type: 'string', label: formatMessage({ id: `${intlPrefix}.type` }) },
    { name: 'pmEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.pmEnable` }) },
    { name: 'targetUserDTOS', type: 'object', label: formatMessage({ id: `${intlPrefix}.noticeObject` }) },
  ],
  queryFields: [],
});
