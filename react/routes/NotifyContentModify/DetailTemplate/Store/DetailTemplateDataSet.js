const SendApiDynamicProps = ({ record, name }) => (`${record.get('sendType')}SendApi` === name ? { ignore: 'never' } : { ignore: 'always' });

export default (id, intl, intlPrefix) => {
  const name = intl.formatMessage({ id: `${intlPrefix}.name` });
  const emailTitle = intl.formatMessage({ id: `${intlPrefix}.emailTitle` });
  const predefined = intl.formatMessage({ id: `${intlPrefix}.predefined` });
  const emailContent = '模板内容';
  return {
    autoQuery: true,
    selection: false,
    paging: false,
    dataKey: null,
    fields: [
      // { name: 'id', type: 'string' },
      { name: 'name', type: 'string', label: name },
      { name: 'emailTitle', type: 'string', label: emailTitle },
      { name: 'emailContent', type: 'string', label: emailContent },
      { name: 'predefined', type: 'boolean', label: predefined },
    ],
    transport: {
      read: {
        url: `notify/v1/templates/${id}`,
        method: 'get',
      },
    },
  };
};
