const config = {
  server: 'http://api.staging.saas.hand-china.com',
  master: './node_modules/@choerodon/master/lib/master.js',
  masterName: 'master',
  projectType: 'choerodon',
  buildType: 'single',
  dashboard: {},
  resourcesLevel: ['site', 'organization', 'project', 'user'],
};

module.exports = config;
