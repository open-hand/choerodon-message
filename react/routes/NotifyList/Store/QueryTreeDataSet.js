export default function ({ setCurrentPageType, store }) {
  return {
    autoQuery: true,
    selection: false,
    paging: true,
    parentField: 'parentId',
    primaryKey: 'id',
    expandField: 'expand',
    idField: 'id',
    queryFields: [
      { name: 'id', type: 'number' },
    ],
    fields: [
      { name: 'parentId', type: 'number' },
      { name: 'id', type: 'number' },
      { name: 'name', type: 'string', label: '节点名称' },
      { name: 'enabled', type: 'boolean', label: '是否启用' },
      { name: 'code', type: 'string' },
      { name: 'expand', type: 'boolean' },
    ],
    transport: {
      read: {
        url: 'notify/v1/notices/send_settings/tree',
        method: 'get',
      },
    },
    events: {
      load: ({ dataSet }) => {
        const expandsKeys = store.getExpandedKeys;
        if (expandsKeys && expandsKeys.length) {
          dataSet.forEach(record => {
            record.set('expand', expandsKeys.includes(String(record.get('id'))));
          });
        } else {
          dataSet.get(0).set('expand', true);
        }
      },
    },
  };
}
