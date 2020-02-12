export default ({ formatMessage, intlPrefix, projectId, userDs }) => ({
  autoQuery: true,
  selection: false,
  paging: false,
  transport: {
    read: {
      url: `/notify/v1/projects/${projectId}/message_settings/agile`,
      method: 'get',
      transformResponse(response) {
        try {
          const data = JSON.parse(response);
          if (data && data.failed) {
            return data;
          } else {
            return data.customMessageSettingList || [];
          }
        } catch (e) {
          return response;
        }
      },
    },
    submit: ({ data }) => {
      const res = [];
      data.forEach((item) => {
        if (!item.sendRoleList.includes('specifier')) {
          item.specifierIds = [];
          item.userList = [];
        } else {
          item.specifierIds = item.userList.map(({ id }) => id);
        }
        res.push(item);
      });
      return ({
        url: `/notify/v1/projects/${projectId}/message_settings/agile/batch`,
        method: 'put',
        data: res,
      });
    },
  },
  fields: [
    { name: 'name', type: 'string', label: formatMessage({ id: `${intlPrefix}.type` }) },
    { name: 'pmEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.pmEnable` }) },
    { name: 'emailEnable', type: 'boolean', label: formatMessage({ id: `${intlPrefix}.emailEnable` }) },
    { name: 'userList', type: 'object', textField: 'realName', valueField: 'id', options: userDs, multiple: true, label: formatMessage({ id: `${intlPrefix}.choose` }) },
    { name: 'sendRoleList', multiple: true },
  ],
  queryFields: [],
});
