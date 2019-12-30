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
  record.init(name, parentIsChecked && canCheck);
  record.init(flagName, canCheck);
}

function handleLoad({ dataSet }) {
  dataSet.get(0).init('expand', true);
  dataSet.forEach((record) => {
    if (!record.get('groupId')) {
      parentItemIsChecked({ dataSet, record, name: 'pmEnable', flagName: 'pmEnabledFlag' });
      parentItemIsChecked({ dataSet, record, name: 'emailEnable', flagName: 'emailEnabledFlag' });
      parentItemIsChecked({ dataSet, record, name: 'smsEnable', flagName: 'smsEnabledFlag' });
    }
  });
}

export default ({ formatMessage, intlPrefix, projectId, userDs }) => ({
  autoQuery: false,
  selection: false,
  paging: false,
  parentField: 'groupId',
  idField: 'key',
  primaryKey: 'key',
  expandField: 'expand',
  transport: {
    read: {
      url: `/notify/v1/projects/${projectId}/message_settings/resourceDelete`,
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
      const newData = [];
      data.forEach((item) => {
        if (item.groupId) {
          const obj = {
            ...item,
            groupId: Number(item.groupId),
          };
          if (!item.sendRoleList.includes('specifier')) {
            obj.userList = [];
            obj.specifierIds = [];
          } else {
            obj.specifierIds = item.userList.map(({ id }) => id);
          }
          newData.push(obj);
        }
      });
      return ({
        url: `/notify/v1/projects/${projectId}/message_settings/resourceDelete/batch`,
        method: 'put',
        data: newData,
      });
    },
  },
  fields: [
    { name: 'name', type: 'string', label: formatMessage({ id: `${intlPrefix}.type` }) },
    { name: 'emailEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.pmEnable` }) },
    { name: 'pmEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.emailEnable` }) },
    { name: 'smsEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.smsEnable` }) },
    { name: 'userList', type: 'object', textField: 'realName', valueField: 'id', options: userDs, multiple: true, label: '请选择' },
    { name: 'sendRoleList', multiple: true },
  ],
  queryFields: [],
  events: {
    load: handleLoad,
  },
});
