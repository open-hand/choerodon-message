function formatData(data) {
  const newData = [];
  data.forEach((item) => {
    const res = { ...item };
    const { groupId, id } = item;
    if (groupId) {
      res.key = `${groupId}-${id}`;
      res.groupId = String(groupId);
    } else {
      res.key = String(id);
    }
    newData.push(res);
  });
  return newData;
}

function parentItemIsChecked({ dataSet, record, name, flagName }) {
  const parentIsChecked = !dataSet.find((tableRecord) => record.get('key') === tableRecord.get('groupId') && !tableRecord.get(name) && tableRecord.get(flagName));
  const canCheck = !!dataSet.find((tableRecord) => record.get('key') === tableRecord.get('groupId') && tableRecord.get(flagName));
  record.init(flagName, canCheck);
  record.init(name, parentIsChecked && canCheck);
}

function handleLoad({ dataSet }) {
  dataSet.forEach((record) => {
    if (!record.get('groupId')) {
      parentItemIsChecked({ dataSet, record, name: 'pmEnable', flagName: 'pmEnabledFlag' });
      parentItemIsChecked({ dataSet, record, name: 'emailEnable', flagName: 'emailEnabledFlag' });
    }
  });
}

export default ({ formatMessage, intlPrefix, projectId }) => ({
  autoQuery: true,
  selection: false,
  paging: false,
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
            const { notifyEventGroupList, customMessageSettingList } = data;
            const res = notifyEventGroupList.concat(customMessageSettingList);
            return formatData(res);
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
    { name: 'notifyObject', type: 'string', label: formatMessage({ id: `${intlPrefix}.noticeObject` }) },
  ],
  queryFields: [],
  events: {
    load: handleLoad,
  },
});
