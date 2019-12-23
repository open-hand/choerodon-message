export default ({ formatMessage, intlPrefix, receiveStore, userId }) => {
  function parentItemIsChecked({ dataSet, record, name }) {
    const parentIsChecked = !dataSet.find((tableRecord) => record.get('sequenceId') === tableRecord.get('parentId') && !tableRecord.get(name) && !tableRecord.get(`${name}Disabled`));
    const disabled = !dataSet.find((tableRecord) => tableRecord.get('parentId') === record.get('sequenceId') && !tableRecord.get(`${name}Disabled`));
    const realValue = parentIsChecked && !disabled;
    record.init(name, realValue);
    record.init(`${name}Disabled`, disabled);
  }

  function isChecked(record, type, templateIdName, enabledName) {
    const hasTemplateId = record.get(templateIdName) && record.get(enabledName);
    const isCheck = hasTemplateId && !receiveStore.getReceiveData.some(({ sendSettingId, sendingType }) => (
      sendSettingId === record.get('id') && sendingType === type
    ));
    record.init(type, isCheck);
    record.init(`${type}Disabled`, !hasTemplateId);
  }

  function handleLoad({ dataSet }) {
    dataSet.forEach((record) => {
      if (record.get('parentId')) {
        isChecked(record, 'pm', 'pmTemplateId', 'pmEnabledFlag');
        isChecked(record, 'email', 'emailTemplateId', 'emailEnabledFlag');
      }
    });
    dataSet.forEach((record) => {
      if (!record.get('parentId')) {
        parentItemIsChecked({ dataSet, record, name: 'pm' });
        parentItemIsChecked({ dataSet, record, name: 'email' });
      }
    });
  }

  return ({
    autoQuery: false,
    selection: false,
    paging: false,
    autoQueryAfterSubmit: false,
    parentField: 'parentId',
    idField: 'sequenceId',
    transport: {
      read: {
        url: '/notify/v1/notices/send_settings/list/allow_config?source_type=site',
        method: 'get',
      },
      submit: ({ dataSet }) => {
        const res = [];
        const data = dataSet.toData();
        data.forEach(({ pm, email, id, parentId, pmDisabled, emailDisabled }) => {
          if (!parentId) return;
          if (!pm && !pmDisabled) {
            res.push({
              sendingType: 'pm',
              disable: true,
              sourceType: 'site',
              sendSettingId: id,
              userId,
            });
          }
          if (!email && !emailDisabled) {
            res.push({
              sendingType: 'email',
              disable: true,
              sourceType: 'site',
              sendSettingId: id,
              userId,
            });
          }
        });

        return ({
          url: '/notify/v1/notices/receive_setting/all?source_type=site',
          method: 'put',
          data: res,
        });
      },
    },
    fields: [
      { name: 'id', type: 'number' },
      { name: 'name', type: 'string', label: formatMessage({ id: 'receive.type' }) },
      { name: 'pm', type: 'boolean', label: formatMessage({ id: 'receive.type.pm' }) },
      { name: 'email', type: 'boolean', label: formatMessage({ id: 'receive.type.email' }) },
    ],
    queryFields: [],
    events: {
      load: handleLoad,
    },
  });
};
