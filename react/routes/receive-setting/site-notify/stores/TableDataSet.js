export default ({ formatMessage, intlPrefix, receiveStore, userId }) => {
  function isChecked(record, type, templateIdName) {
    const hasTemplateId = record.get(templateIdName);
    const isCheck = hasTemplateId && !receiveStore.getReceiveData.some(({ sendSettingId, sendingType }) => (
      sendSettingId === record.get('id') && sendingType === type
    ));
    record.init(type, isCheck);
  }

  function handleLoad({ dataSet }) {
    dataSet.forEach((record) => {
      isChecked(record, 'pm', 'pmTemplateId');
      isChecked(record, 'email', 'emailTemplateId');
    });
  }

  return ({
    autoQuery: false,
    selection: false,
    paging: false,
    autoQueryAfterSubmit: false,
    transport: {
      read: {
        url: '/notify/v1/notices/send_settings/list/allow_config?source_type=site',
        method: 'get',
      },
      submit: ({ data }) => {
        const res = [];
        data.forEach(({ pm, email, id }) => {
          if (!pm) {
            res.push({
              sendingType: 'pm',
              disable: true,
              sourceType: 'site',
              sendSettingId: id,
              userId,
            });
          }
          if (!email) {
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
          url: '/notify/v1/notices/receive_setting/all',
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
      { name: 'sourceId', type: 'boolean', label: formatMessage({ id: 'receive.type.email' }) },
    ],
    queryFields: [],
    events: {
      load: handleLoad,
    },
  });
};
