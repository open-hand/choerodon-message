function formatData(data) {
  const newData = [];
  data.forEach((item) => {
    const res = { ...item };
    const { envId, id } = res;
    if (envId) {
      res.key = `${envId}-${id}`;
      res.envId = String(envId);
    } else {
      res.key = String(id);
    }
    newData.push(res);
  });
  return newData;
}

function parentItemIsChecked({ dataSet, record, name }) {
  const parentIsChecked = !dataSet.find((tableRecord) => record.get('key') === tableRecord.get('envId') && !tableRecord.get(name));
  record.init(name, parentIsChecked);
}

function handleLoad({ dataSet }) {
  dataSet.forEach((record) => {
    if (!record.get('envId')) {
      parentItemIsChecked({ dataSet, record, name: 'sendPm' });
      parentItemIsChecked({ dataSet, record, name: 'sendEmail' });
      parentItemIsChecked({ dataSet, record, name: 'sendSms' });
    }
  });
}

export default ({ formatMessage, intlPrefix, projectId }) => ({
  autoQuery: true,
  selection: false,
  paging: false,
  parentField: 'envId',
  idField: 'key',
  primaryKey: 'key',
  transport: {
    read: {
      url: `/devops/v1/projects/${projectId}/notification/group_by_env`,
      method: 'get',
      transformResponse(response) {
        try {
          const data = JSON.parse(response);
          if (data && data.failed) {
            return data;
          } else {
            const { devopsEnvironmentList, devopsNotificationList } = data;
            const res = devopsEnvironmentList.concat(devopsNotificationList);
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
        if (item.envId) {
          const obj = {
            ...item,
            envId: Number(item.envId),
            projectId,
          };
          newData.push(obj);
        }
      });
      return ({
        url: `/devops/v1/projects/${projectId}/notification/batch`,
        method: 'put',
        data: newData,
      });
    },
  },
  fields: [
    { name: 'name', type: 'string', label: formatMessage({ id: `${intlPrefix}.type` }) },
    { name: 'sendEmail', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.pmEnable` }) },
    { name: 'sendPm', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.emailEnable` }) },
    { name: 'sendSms', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.smsEnable` }) },
    { name: 'userList', type: 'object', textField: 'realName', valueField: 'id' },
  ],
  queryFields: [],
  events: {
    load: handleLoad,
  },
});
