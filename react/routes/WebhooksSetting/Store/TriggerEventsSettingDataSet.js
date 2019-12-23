/**
 *
 * @param id
 * @return DataSet
 */
export default (type, id) => ({
  autoQuery: true,
  parentField: 'categoryCode',
  idField: 'code',
  queryFields: [
    { name: 'name', type: 'string', label: '类型/触发事件' },
    { name: 'description', type: 'string', label: '描述' },
  ],
  fields: [
    { name: 'name', type: 'string', label: '类型/触发事件' },
    { name: 'description', type: 'string', label: '描述' },
    { name: 'categoryCode', type: 'string', parentFieldName: 'code' },
    { name: 'code', type: 'string', unique: true },
    { name: 'objectVersionNumber', type: 'number' },
    // { name: 'enabled', type: 'boolean', label: '状态', required: true },
  ],
  transport: {
    read: ({ data, params }) => ({
      url: `notify/v1/projects/${id}/send_settings`,
      method: 'get',
      transformResponse(JSONData) {
        const { sendSettingCategorySelection, sendSettingSelection } = JSON.parse(JSONData);
        const list = (sendSettingCategorySelection || []).map(item => ({ ...item, description: null }));
        return [...list, ...(sendSettingSelection || [])];
      },
      params: {
        ...params,
        name: data.name,
        description: data.description,
      },
    }),
  },
  events: {
    select: ({ dataSet, record }) => {
      if (record.parent) {
        record.parent.isSelected = true;
      } else {
        record.children.forEach((item) => {
          item.isSelected = true;
        });
      }
    },
    unSelect: ({ dataSet, record }) => {
      if (record.parent && record.parent.children.filter(item => item.isSelected).length === 0) {
        record.parent.isSelected = false;
      }
      if (!record.parent) {
        record.children.forEach((item) => {
          item.isSelected = false;
        });
      }
    },
  },
});
