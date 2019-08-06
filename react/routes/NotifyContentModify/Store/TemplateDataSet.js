const SendApiDynamicProps = ({ record, name }) => (`${record.get('sendType')}SendApi` === name ? { ignore: 'never' } : { ignore: 'always' });

export default (id, businessType, type, datasetType, intl, intlPrefix) => {
  const name = intl.formatMessage({ id: `${intlPrefix}.name` });
  const emailTitle = intl.formatMessage({ id: `${intlPrefix}.emailTitle` });
  const predefined = intl.formatMessage({ id: `${intlPrefix}.predefined` });
  return {
    autoQuery: datasetType === 'query',
    autoCreate: datasetType !== 'query',
    selection: false,
    dataKey: null,
    fields: [
      { name: 'id', type: 'string' },
      { name: 'name', type: 'string', label: name },
      { name: 'emailTitle', type: 'string', label: emailTitle },
      { name: 'content', type: 'string', defaultValue: '' },
      { name: 'predefined', type: 'boolean', label: predefined },
    ],
    transport: {
      read: {
        url: `notify/v1/templates?businessType=${businessType}&messageType=${type}`,
        method: 'get',
      },
      submit: ({ data }) => ({
        url: `sms/v1/sms/config/${data[0].id || 0}`,
        method: 'put',
        data: data[0],
      }),
    },
  };
};
