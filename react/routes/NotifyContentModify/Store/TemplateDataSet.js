import { randomString } from '../../../common/util';

const SendApiDynamicProps = ({ record, name }) => (`${record.get('sendType')}SendApi` === name ? { ignore: 'never' } : { ignore: 'always' });

export default (id, businessType, type, datasetType, intl, intlPrefix) => {
  const name = intl.formatMessage({ id: `${intlPrefix}.name` });
  const emailTitle = intl.formatMessage({ id: `${intlPrefix}.emailTitle` });
  const predefined = intl.formatMessage({ id: `${intlPrefix}.predefined` });
  return {
    autoQuery: datasetType === 'query',
    autoCreate: datasetType !== 'query',
    selection: false,
    // dataKey: null,
    paging: true,
    fields: [
      { name: 'name', type: 'string', label: name },
      // { name: 'code', type: 'string', label: name, defaultValue: 'code322222' },

      { name: 'emailTitle', type: 'string', label: emailTitle },
      { name: 'emailContent', type: 'string', defaultValue: '' },
      { name: 'predefined', type: 'boolean', label: predefined },

    ],
    transport: {
      read: {
        url: `notify/v1/templates?businessType=${businessType}&messageType=${type}`,
        method: 'get',
      },
      submit: ({ data }) => ({
        url: `notify/v1/templates/${type}?set_to_the_current=${data[0].predefined}`,
        method: 'post',
        data: {
          ...data[0],
          businessType,
          code: `${businessType}-${randomString(5)}`,
        },
      }),
    },
  };
};
