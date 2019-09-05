import { DataSet } from 'choerodon-ui/pro/lib';

const SendApiDynamicProps = ({ record, name }) => (`${record.get('sendType')}SendApi` === name ? { ignore: 'never' } : { ignore: 'always' });
function reqCurrentData(data) {
  if (typeof (data) === 'undefined') { return false; }
  // .split(',')[1] ? data[0].current.split(',')[1] : Boolean(data[0].current)
  const dataArr = data.split(',');
  if (dataArr.length === 2) { return dataArr[1]; } else { return dataArr[0]; }
}
export default (id, type, isCurrent, intl, intlPrefix) => {
  const name = intl.formatMessage({ id: `${intlPrefix}.name` });
  const Title = intl.formatMessage({ id: `${intlPrefix}.${type}Title` });
  const current = intl.formatMessage({ id: `${intlPrefix}.current` });
  const content = intl.formatMessage({ id: `${intlPrefix}.${type}Content` });
  const optionDataSet = new DataSet({
    selection: 'multiple',
    data: [
      { text: current, value: true },
    ],
  });
  return {
    autoQuery: true,
    selection: false,
    paging: false,
    dataKey: null,
    fields: [
      // { name: 'id', type: 'string' },
      { name: 'name', type: 'string', label: name, required: true },
      type !== 'sms' ? { name: `${type}Title`, type: 'string', label: Title, required: true } : {},
      { name: `${type}Content`, type: 'string', label: content, required: true },
      {
        name: 'current',
        type: 'boolean',
        textField: 'text',
        multiple: ',',
        valueField: 'value',
        options: optionDataSet,
        //  ignore: 'always',
      },
      // { name: 'current', type: 'boolean', multiple: ',' },

    ],
    transport: {
      read: {
        url: `notify/v1/templates/${id}`,
        method: 'get',
        transformResponse(data) {
          return ({
            ...JSON.parse(data),
            current: isCurrent,
          });
        },
      },
      submit: ({ data }) => ({
        url: `notify/v1/templates/${type}/${id}?set_to_the_current=${reqCurrentData(data[0].current)}`,
        method: 'put',
        data: {
          ...data[0],
        },
      }),
    },
  };
};
