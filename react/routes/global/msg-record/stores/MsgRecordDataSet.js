import { DataSet } from 'choerodon-ui/pro/lib';

const getTypePath = (type, orgId, method = 'get') => {
  const path = type === 'site' ? '' : `/organizations/${orgId}`;
  return path;
};
export default (orgId, type, intl, intlPrefix) => {
  const email = intl.formatMessage({ id: `${intlPrefix}.email` });
  const status = intl.formatMessage({ id: `${intlPrefix}.status` });
  const templateType = intl.formatMessage({ id: `${intlPrefix}.templateType` });
  const failedReason = intl.formatMessage({ id: `${intlPrefix}.failedReason` });
  const retryCount = intl.formatMessage({ id: `${intlPrefix}.send.count` });
  const creationDate = intl.formatMessage({ id: `${intlPrefix}.creationDate` });

  const queryPredefined = new DataSet({
    autoQuery: true,
    paging: false,
    fields: [
      { name: 'key', type: 'string' },
      { name: 'value', type: 'string' },
    ],
    data: [
      { key: 'COMPLETED', value: '完成' },
      { key: 'FAILED', value: '失败' },
    ],
  });
  const optionDataSet = new DataSet({
    selection: 'multiple',
    data: [
      { text: intl.formatMessage({ id: `${intlPrefix}.current` }), value: true },
    ],
  });

  return {
    autoQuery: true,
    // autoCreate: datasetType !== 'query',
    selection: false,
    // dataKey: null,
    paging: true,
    fields: [
      { name: 'email', type: 'string', label: email },
      { name: 'status', type: 'string', label: status },
      { name: 'templateType', type: 'string', label: templateType },
      { name: 'failedReason', type: 'string', label: failedReason },
      { name: 'retryCount', type: 'number', label: retryCount },
      { name: 'creationDate', type: 'string', label: creationDate },


    ],
    queryFields: [

      // receiveEmail 字段
      { name: 'receiveEmail', type: 'string', label: email },
      { name: 'status', type: 'string', label: status, textField: 'value', valueField: 'key', options: queryPredefined },
      { name: 'templateType', type: 'string', label: templateType },
      { name: 'failedReason', type: 'string', label: failedReason },

    ],

    transport: {
      read: {
        url: '/notify/v1/records/emails',
        method: 'get',
      },
    },
  };
};
