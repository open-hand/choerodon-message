export default (intl, intlPrefix) => {
  const title = intl.formatMessage({ id: `${intlPrefix}.title` });
  const content = intl.formatMessage({ id: `${intlPrefix}.content` });
  const status = intl.formatMessage({ id: 'status' });
  const sendDate = intl.formatMessage({ id: `${intlPrefix}.send-date` });
  const sendNotices = intl.formatMessage({ id: `${intlPrefix}.send-notice` });
  const sticky = intl.formatMessage({ id: `${intlPrefix}.sticky` });
  return {
    autoQuery: true,
    selection: false,
    paging: false,
    dataKey: null,
    fields: [
      { name: 'title', type: 'string', label: title, required: true },
      { name: 'content', type: 'string', label: content, required: true },
      { name: 'status', type: 'string', label: status },
      { name: 'sendDate', type: 'string', label: sendDate },
      { name: 'sendNotices', type: 'boolean', label: sendNotices },
      { name: 'sticky', type: 'boolean', label: sticky },
    ],
    transport: {
      read: {
        url: 'notify/v1/system_notice/all/list',
        method: 'post',
      },
      submit: ({ data }) => ({
        url: 'notify/v1/system_notice/create',
        method: 'post',
        data: data[0],
      }),
    },
  };
};
