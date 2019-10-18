export default (id, businessType, type, intl, intlPrefix, templateDataSet) => {
  const retryCount = intl.formatMessage({ id: `${intlPrefix}.retryCount` });
  const sendInstantly = intl.formatMessage({ id: `${intlPrefix}.sendInstantly` });
  const manualRetry = intl.formatMessage({ id: `${intlPrefix}.manualRetry` });
  const emailTemplate = intl.formatMessage({ id: `${intlPrefix}.emailTemplate` });
  const cofingFields = type === 'email' ? [{ name: 'retryCount', type: 'string', label: retryCount, required: true },
    { name: 'sendInstantly', type: 'boolean', label: sendInstantly, required: true },
    { name: 'manualRetry', type: 'boolean', label: manualRetry, required: true }]
    : [{ name: `${type}Type`, type: 'string', label: '站内信类型', required: true }];
  return {
    autoQuery: type !== 'sms',
    selection: false,
    paging: false,
    dataKey: null,
    fields: [
      ...cofingFields,
      { name: `${type}Template`, type: 'object', label: emailTemplate, textField: 'name', valueField: 'id', required: true, ignore: 'always', options: templateDataSet },
      { name: `${type}TemplateName`, type: 'string', bind: `${type}Template.name` },
      { name: `${type}TemplateId`, type: 'string', bind: `${type}Template.id` },


    ],

    transport: {
      read: {
        url: `notify/v1/notices/send_settings/${id}/${type}_send_setting`,
        method: 'get',
        transformResponse(data) {
          return ({
            ...JSON.parse(data),
            // sendInstantly: data.sendInstantly ? data.sendInstantly : false,
            // manualRetry: data.manualRetry ? data.manualRetry : false,
          });
        },
      },

      submit: ({ data }) => ({
        url: `notify/v1/notices/send_settings/${id}/${type}_send_setting`,
        method: 'put',
        data: data[0],
      }),
    },
  };
};
