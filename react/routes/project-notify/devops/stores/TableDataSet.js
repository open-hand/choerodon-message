function formatData(data) {
  const res = [];
  const notifyEventGroupList = [...data.notifyEventGroupList];
  const customMessageSettingList = [...data.customMessageSettingList];
  notifyEventGroupList.forEach((item) => {
    res.push({
      ...item,
      key: String(item.id),
    });
  });
  customMessageSettingList.forEach((item) => {
    const obj = {
      ...item,
      key: `${item.groupId}-${item.id}`,
      groupId: String(item.groupId),
    };
    res.push(obj);
  });
  return res;
}

function parentItemIsChecked({ dataSet, record, name }) {
  const parentIsChecked = !dataSet.find((tableRecord) => record.get('key') === tableRecord.get('groupId') && !tableRecord.get(name));
  record.init(name, parentIsChecked);
}

function handleLoad({ dataSet }) {
  dataSet.forEach((record) => {
    if (!record.get('groupId')) {
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
  parentField: 'groupId',
  idField: 'key',
  primaryKey: 'key',
  transport: {
    read: {
      url: `/notify/v1/projects/${projectId}/message_settings/devops`,
      method: 'get',
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
        const { groupId } = item;
        if (groupId) {
          item.groupId = Number(groupId);
          res.push(item);
        }
      });
      return ({
        url: `/notify/v1/projects/${projectId}/message_settings/devops/batch`,
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
