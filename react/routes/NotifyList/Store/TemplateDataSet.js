import { DataSet } from 'choerodon-ui/pro/lib';

export default function () {
  return {
    autoQuery: false,
    selection: false,
    autoQueryAfterSubmit: false,
    paging: true,

    fields: [
      { name: 'type', type: 'string', label: '类型' },
      { name: 'content', type: 'string' },
    ],
    transport: {
      read: {
        url: '/notify/v1/notices/send_settings',
        method: 'get',
      },
      create: ({ data: [data] }) => ({
        url: '/notify/v1/templates',
        method: 'post',
        data,
      }),
      update: ({ data: [data] }) => ({
        url: '/notify/v1/templates',
        method: 'post',
        data,
      }),
    },
  };
}
