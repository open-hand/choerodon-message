import { DataSet } from 'choerodon-ui/pro/lib';
import { randomString } from '../../../common/util';

const SendApiDynamicProps = ({ record, name }) => (`${record.get('sendType')}SendApi` === name ? { ignore: 'never' } : { ignore: 'always' });

export default (id, businessType, type, datasetType, intl, intlPrefix) => {
  const name = intl.formatMessage({ id: `${intlPrefix}.name` });
  const Title = intl.formatMessage({ id: `${intlPrefix}.${type}Title` });
  const predefined = intl.formatMessage({ id: `${intlPrefix}.source` });
  const content = intl.formatMessage({ id: `${intlPrefix}.${type}Content` });
  const queryPredefined = new DataSet({
    autoQuery: true,
    paging: false,
    fields: [
      { name: 'key', type: 'string' },
      { name: 'value', type: 'string' },
    ],
    data: [
      { key: true, value: '预定义' },
      { key: false, value: '自定义' },
    ],
  });
  const optionDataSet = new DataSet({
    selection: 'multiple',
    data: [
      { text: intl.formatMessage({ id: `${intlPrefix}.current` }), value: true },
    ],
  });
  return {
    autoQuery: datasetType === 'query',
    autoCreate: datasetType !== 'query',
    selection: false,
    // dataKey: null,
    paging: true,
    fields: [
      { name: 'name', type: 'string', label: name, required: true },
      // { name: 'code', type: 'string', label: name, defaultValue: 'code322222' },

      type !== 'sms' ? { name: `${type}Title`, type: 'string', label: Title, required: true } : {},
      { name: `${type}Content`, type: 'string', label: content, required: true, defaultValue: '' },
      { name: 'predefined', type: 'boolean', label: predefined },
      {
        name: 'current',
        type: 'string',
        label: intl.formatMessage({ id: `${intlPrefix}.current` }),
        // textField: 'emailTemplateTitle',
        // valueField: 'emailTemplateId',
        // textField: 'emailTemplateId',
        // valueField: 'emailTemplateTitle',
        // options: currentDataSet,
      },
      { name: 'defaultTemplate', type: 'boolean', textField: 'text', multiple: ',', valueField: 'value', options: optionDataSet },

      { name: 'eideid', type: 'object', label: '1111', bind: 'current0.emailTemplateId' },
    ],
    queryFields: [
      { name: 'name', type: 'string', label: name },
      // { name: 'code', type: 'string', label: name, defaultValue: 'code322222' },

      { name: `${type}Title`, type: 'string', label: Title },
      // { name: `${type}Content`, type: 'string', defaultValue: '' },
      { name: 'predefined', type: 'string', label: predefined, textField: 'value', valueField: 'key', options: queryPredefined },

    ],

    transport: {
      read: {
        url: `notify/v1/templates?businessType=${businessType}&messageType=${type}`,
        method: 'get',

      },
      submit: ({ data }) => ({
        url: `notify/v1/templates/${type}?set_to_the_current=${data[0].defaultTemplate.split(',')[1] ? data[0].defaultTemplate.split(',')[1] : data[0].defaultTemplate}`,
        method: 'post',
        data: {
          ...data[0],
          businessType,
          code: `${businessType}-${randomString(7)}`,
        },
      }),
    },
  };
};
