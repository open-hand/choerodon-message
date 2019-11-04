import { DataSet } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';

const typeOptionDataSet = new DataSet({
  fields: [
    { name: 'name', type: 'string' },
    { name: 'value', type: 'string' },
  ],
  data: [
    { name: '钉钉', value: 'DingTalk' },
    { name: '企业微信', value: 'WeChat' },
    { name: 'JSON', value: 'Json' },
  ],
});

/**
 *
 * @param type(create/edit)
 * @param id(create: projectId / edit: webhookId)
 * @returns DataSet
 */
export default function (type, id, children) {
  const validateWebhooksPath = async (value) => {
    const res = await axios.get(`notify/v1/projects/${id}/web_hooks/check_path`, {
      params: {
        id,
        path: value,
      },
    });
    if (!res) {
      return '路径重复';
    }
    return true;
  };
  return {
    autoQuery: false,
    queryUrl: '',
    selection: false,
    paging: false,
    dataKey: false,
    fields: [
      { name: 'id', type: 'string' },
      { name: 'name', type: 'string', label: 'Webhooks名称', required: true },
      { name: 'type', type: 'string', label: 'Webhooks类型', options: typeOptionDataSet, valueField: 'value', textField: 'name', required: true },
      { name: 'webhookPath', type: 'string', label: 'Webhooks地址', validator: validateWebhooksPath, required: true },
      { name: 'secret', type: 'string', label: 'secret' },
      { name: 'id', type: 'number' },
      { name: 'objectVersionNumber', type: 'number' },
      { name: 'triggerEventSelection', ignore: 'always' },
    ],
    transport: {
      read: ({ dataSet }) => ({
        url: dataSet.queryUrl,
        method: 'get',
        transformResponse(data) {
          const { sendSettingIdList, triggerEventSelection } = JSON.parse(data);
          const { sendSettingCategorySelection, sendSettingSelection } = triggerEventSelection;
          return {
            ...JSON.parse(data),
            triggerEventSelection: [...sendSettingCategorySelection, ...sendSettingSelection],
          };
        },
      }),
      update: ({ data }) => ({
        url: `notify/v1/projects/${id}/web_hooks/${data[0].id}`,
        method: 'put',
        data: {
          ...data[0],
          sendSettingIdList: children.toJSONData(true).filter(item => !!item.categoryCode).map(item => item.id),
        },
      }),
    },
    events: {
      load: ({ dataSet }) => {
        children.forEach((item) => {
          if (dataSet.current.get('sendSettingIdList').find(selectId => selectId === item.get('id'))) {
            item.isSelected = true;
          }
        });
      },
    },
  };
}
