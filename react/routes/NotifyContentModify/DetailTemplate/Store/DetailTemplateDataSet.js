const SendApiDynamicProps = ({ record, name }) => (`${record.get('sendType')}SendApi` === name ? { ignore: 'never' } : { ignore: 'always' });

export default (id, type, intl, intlPrefix) => {
  const name = intl.formatMessage({ id: `${intlPrefix}.name` });
  const Title = intl.formatMessage({ id: `${intlPrefix}.${type}Title` });
  const current = intl.formatMessage({ id: `${intlPrefix}.current` });
  const Content = '模板内容';
  return {
    autoQuery: true,
    selection: false,
    paging: false,
    dataKey: null,
    fields: [
      // { name: 'id', type: 'string' },
      { name: 'name', type: 'string', label: name },
      { name: `${type}Title`, type: 'string', label: Title },
      { name: `${type}Content`, type: 'string', label: Content },
      { name: 'current', type: 'boolean', label: current, defaultValue: false },
    ],
    transport: {
      read: {
        url: `notify/v1/templates/${id}`,
        method: 'get',
      },
      submit: ({ data }) => ({
        url: `notify//v1/templates/email/${id}?set_to_the_current=${data[0].predefined}`,
        method: 'put',
        data: {
          ...data[0],
        },
      }),
    },
  };
};
