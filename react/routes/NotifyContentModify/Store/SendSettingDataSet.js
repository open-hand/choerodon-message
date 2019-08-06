export default (id, intl, intlPrefix) => {
  const retryCount = intl.formatMessage({ id: `${intlPrefix}.retryCount` });
  const sendInstantly = intl.formatMessage({ id: `${intlPrefix}.sendInstantly` });
  const manualRetry = intl.formatMessage({ id: `${intlPrefix}.manualRetry` });
  const emailTemplate = intl.formatMessage({ id: `${intlPrefix}.emailTemplate` });
  return {
    autoQuery: true,
    selection: false,
    paging: false,
    dataKey: null,
    fields: [
      { name: 'retryCount', type: 'string', label: retryCount, required: true },
      { name: 'sendInstantly', type: 'boolean', label: sendInstantly, required: true, defaultValue: false },
      { name: 'manualRetry', type: 'boolean', label: manualRetry, required: true },
      { name: 'emailTemplate', type: 'object', label: emailTemplate, textField: 'name', valueField: 'id', required: true, ignore: 'always', lookupUrl: 'notify/v1/notices/emails/templates' },
      { name: 'emailTemplateTitle', type: 'string', bind: 'emailTemplate.name' },
      { name: 'emailTemplateId', type: 'string', bind: 'emailTemplate.id' },
    ],

    transport: {
      read: {
        url: `notify/v1/notices/send_settings/${id}/email_send_setting`,
        method: 'get',
        transformResponse(data) {
          return ({
            ...JSON.parse(data),
            sendInstantly: data.sendInstantly ? data.sendInstantly : false,
            manualRetry: data.manualRetry ? data.manualRetry : false,
          });
        },
      },

      submit: ({ data }) => ({
        url: 'notify/v1/notices/configs/email',
        method: 'put',
        data: data[0],
      }),
    },
  };
};
