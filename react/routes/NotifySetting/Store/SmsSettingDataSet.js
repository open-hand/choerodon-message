const SendApiDynamicProps = ({ record, name }) => (`${record.get('sendType')}SendApi` === name ? { ignore: 'never' } : { ignore: 'always' });

export default (intl, intlPrefix) => {
  const signature = intl.formatMessage({ id: `${intlPrefix}.signature` });
  const hostAddress = intl.formatMessage({ id: `${intlPrefix}.hostAddress` });
  const hostPort = intl.formatMessage({ id: `${intlPrefix}.hostPort` });
  const sendType = intl.formatMessage({ id: `${intlPrefix}.sendType` });
  const singleSendApi = intl.formatMessage({ id: `${intlPrefix}.singleSendApi` });
  const batchSendApi = intl.formatMessage({ id: `${intlPrefix}.batchSendApi` });
  const asyncSendApi = intl.formatMessage({ id: `${intlPrefix}.asyncSendApi` });
  const secretKey = intl.formatMessage({ id: `${intlPrefix}.secretKey` });
  return {
    autoQuery: true,
    selection: false,
    paging: false,
    dataKey: false,
    fields: [
      { name: 'id', type: 'string' },
      { name: 'signature', type: 'string', label: signature, required: true },
      { name: 'hostAddress', type: 'string', label: hostAddress, required: true },
      { name: 'hostPort', type: 'string', label: hostPort },
      { name: 'sendType', type: 'string', label: sendType },
      { name: 'singleSendApi', type: 'string', label: singleSendApi, dynamicProps: SendApiDynamicProps },
      { name: 'batchSendApi', type: 'string', label: batchSendApi, dynamicProps: SendApiDynamicProps },
      { name: 'asyncSendApi', type: 'string', label: asyncSendApi, dynamicProps: SendApiDynamicProps },
      { name: 'secretKey', type: 'string', label: secretKey, required: true },
    ],
    transport: {
      read: {
        url: '/notify/v1/sms/config?organization_id=0',
        method: 'get',
      },
      submit: ({ data }) => ({
        url: `/notify/v1/sms/config/${data[0].id || 0}`,
        method: 'put',
        data: data[0],
      }),
    },
  };
};
