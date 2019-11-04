import { DataSet } from 'choerodon-ui/pro/lib';

export default function (templateDataSet) {
  return {
    autoQuery: false,
    selection: false,
    paging: false,
    children: {
      templates: templateDataSet,
    },
    fields: [
      { name: 'enabled', type: 'boolean' }, 
      { name: 'allowConfig', type: 'boolean', label: '是否允许配置接收' },
      { name: 'isSendInstantly', type: 'boolean', label: '是否即时发送' }, 
      { name: 'retryCount', type: 'number', label: '邮件默认重发次数' }, 
      { name: 'isManualRetry', type: 'boolean', label: '是否允许手动触发邮件' }, 
      { name: 'backlogFlag', type: 'boolean', label: '是否为代办提醒' }, 
      { name: 'emailEnabledFlag', type: 'boolean', label: '邮件' },
      { name: 'pmEnabledFlag', type: 'boolean', label: '站内信' },
      { name: 'webhookEnabledFlag', type: 'boolean', label: 'webhook' },
      { name: 'smsEnabledFlag', type: 'boolean', label: '短信' },
    ],
    transport: {
      read: {
        url: '/notify/v1/notices/send_settings/detail',
        method: 'get',
      },
      update: ({ data: [data] }) => (
        { 
          url: `/notify/v1/notices/send_settings/${data.id}/send_setting`,
          method: 'put', 
          data,
        }
      ),
    },
  };
}
