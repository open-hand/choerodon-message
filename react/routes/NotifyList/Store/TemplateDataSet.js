import { DataSet } from 'choerodon-ui/pro/lib';

export default function () {
  return {
    autoQuery: false,
    selection: false,
    paging: true,
    fields: [{
      name: 'type',
      type: 'string',
      label: '类型',
    }, {
      name: 'content',
      type: 'string',
      label: '预览',
    }, {
      name: 'theme',
      type: 'string',
      label: '发送主题',
    }],
    transport: {
      read: {
        url: '/notify/v1/notices/send_settings',
        method: 'get',
      },
    },
  };
}
