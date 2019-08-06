export default () => ({
  autoQuery: false,
  selection: false,
  fields: [
    { name: 'key', type: 'string' },
    { name: 'value', type: 'string' },
  ],
  data: [
    { key: 'site', value: '平台' },
    { key: 'project', value: '项目' },
    { key: 'organization', value: '组织' },
  ],
});
