function formatData(data) {
  const res = [];
  data.forEach(({ messageSettingDTO, id, name }) => {
    res.push({
      id,
      key: String(id),
      name,
    });
    if (messageSettingDTO) {
      messageSettingDTO.forEach((item) => {
        const obj = { ...item };
        obj.key = `${item.categoryId}-${item.id}`;
        obj.categoryId = String(item.categoryId);
        res.push(obj);
      });
    }
  });
  return res;
}

function parentItemIsChecked({ dataSet, record, name }) {
  const parentIsChecked = !dataSet.find((tableRecord) => record.get('key') === tableRecord.get('categoryId') && !tableRecord.get(name));
  record.init(name, parentIsChecked);
}

function handleLoad({ dataSet }) {
  dataSet.forEach((record) => {
    if (!record.get('categoryId')) {
      parentItemIsChecked({ dataSet, record, name: 'pmEnable' });
      parentItemIsChecked({ dataSet, record, name: 'emailEnable' });
    }
  });
}

export default ({ formatMessage, intlPrefix, projectId }) => ({
  autoQuery: true,
  selection: false,
  paging: false,
  autoQueryAfterSubmit: false,
  parentField: 'categoryId',
  idField: 'key',
  primaryKey: 'key',
  transport: {
    read: {
      url: `/notify/v1/notices/${projectId}/message/setting/list`,
      method: 'post',
      data: { notifyType: 'devops' },
      transformResponse(response) {
        try {
          const data = JSON.parse(response);
          if (data && data.failed) {
            return data;
          } else {
            return formatData(data);
          }
        } catch (e) {
          return response;
        }
      },
    },
    submit: ({ data }) => {
      const res = [];
      data.forEach((item) => {
        const { categoryId } = item;
        if (categoryId) {
          item.projectId = projectId;
          item.categoryId = Number(categoryId);
          res.push(item);
        }
      });
      return ({
        url: `/notify/v1/notices/${projectId}/message/setting`,
        method: 'put',
        data: res,
      });
    },
  },
  fields: [
    { name: 'name', type: 'string', label: formatMessage({ id: `${intlPrefix}.type` }) },
    { name: 'pmEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.pmEnable` }) },
    { name: 'emailEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.emailEnable` }) },
    { name: 'targetUserDTOS', type: 'object', label: formatMessage({ id: `${intlPrefix}.noticeObject` }) },
  ],
  queryFields: [],
  events: {
    load: handleLoad,
  },
});
