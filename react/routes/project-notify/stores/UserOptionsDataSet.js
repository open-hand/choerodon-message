export default (projectId) => ({
  autoQuery: true,
  selection: false,
  paging: false,
  transport: {
    read: {
      url: `/base/v1/projects/${projectId}/users/search_by_name`,
      method: 'get',
    },
  },
});
