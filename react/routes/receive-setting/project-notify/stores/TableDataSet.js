export default ({ formatMessage, intlPrefix, receiveStore, userId }) => {
  function parentItemIsChecked({ dataSet, record, name, judgeTemplate = false }) {
    const parentIsChecked = !dataSet.find((tableRecord) => record.get('key') === tableRecord.get('sourceId') && !tableRecord.get(name) && (judgeTemplate || tableRecord.get(`${name}TemplateId`)));
    record.init(name, parentIsChecked);
  }

  function handleLoad({ dataSet }) {
    dataSet.forEach((record) => {
      if (record.get('treeType') === 'group') {
        parentItemIsChecked({ dataSet, record, name: 'pm' });
        parentItemIsChecked({ dataSet, record, name: 'email' });
      }
    });
    dataSet.forEach((record) => {
      if (record.get('treeType') === 'project') {
        parentItemIsChecked({ dataSet, record, name: 'pm', judgeTemplate: true });
        parentItemIsChecked({ dataSet, record, name: 'email', judgeTemplate: true });
      }
    });
  }

  function initChecked(item, type, templateIdName) {
    const hasTemplateId = item[templateIdName];
    const isCheck = hasTemplateId && !receiveStore.getReceiveData.some(({ sendSettingId, sourceId, sendingType }) => (
      sendSettingId === item.id && Number(item.sourceId.split('-')[0]) === sourceId && sendingType === type
    ));
    return isCheck;
  }

  function formatData(data) {
    const res = [...receiveStore.getProjectData];
    const newData = [];
    res.forEach((project) => {
      const projectId = project.id;
      project.key = `${projectId}`;
      project.treeType = 'project';
      const newLists = data.map((item) => {
        const { id, parentId, sequenceId } = item;
        const children = { ...item };
        if (id) {
          children.key = `${projectId}-${parentId}-${id}`;
          children.sourceId = `${projectId}-${parentId}`;
          children.treeType = 'item';
          children.pm = initChecked(children, 'pm', 'pmTemplateId');
          children.email = initChecked(children, 'email', 'emailTemplateId');
        } else {
          children.key = `${projectId}-${sequenceId}`;
          children.sourceId = `${projectId}`;
          children.treeType = 'group';
        }
        return children;
      });
      newData.push(project, ...newLists);
    });
    return newData;
  }

  return ({
    autoQuery: false,
    selection: false,
    paging: false,
    autoQueryAfterSubmit: false,
    parentField: 'sourceId',
    idField: 'key',
    primaryKey: 'key',
    transport: {
      read: {
        url: '/notify/v1/notices/send_settings/list/allow_config?source_type=project',
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
        data.forEach(({ pm, email, id, treeType, sourceId }) => {
          if (treeType === 'item') {
            const projectId = sourceId.split('-')[0];
            if (!pm) {
              res.push({
                sendingType: 'pm',
                disable: true,
                sendSettingId: id,
                sourceType: 'project',
                sourceId: projectId,
                userId,
              });
            }
            if (!email) {
              res.push({
                sendingType: 'email',
                disable: true,
                sourceType: 'project',
                sendSettingId: id,
                sourceId: projectId,
                userId,
              });
            }
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
      { name: 'parentId', type: 'number' },
      { name: 'sequenceId', type: 'number' },
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
