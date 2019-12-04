import React from 'react';
import { observer } from 'mobx-react-lite';
import { Page, Breadcrumb, Content, TabPage } from '@choerodon/boot';
import { Tabs } from 'choerodon-ui/pro';
import { useProjectNotifyStore } from './stores';
import AgileContent from './agile';
import WebhookContent from '../WebhooksSetting';

const { TabPane } = Tabs;

export default observer(props => {
  const {
    permissions,
    ProjectNotifyStore,
    intl: { formatMessage },
    intlPrefix,
    prefixCls,
    tabs,
  } = useProjectNotifyStore();

  function handleTabChange(value) {
    ProjectNotifyStore.setTabKey(value);
  }

  function getContent() {
    let content;
    switch (ProjectNotifyStore.getTabKey) {
      case 'agile':
        content = <AgileContent />;
        break;
      case 'devops':
        content = <div>devops</div>;
        break;
      case 'resource':
        content = <div>resource</div>;
        break;
      case 'webhook':
        content = <WebhookContent />;
        break;
      default:
    }
    return content;
  }

  return (
    <TabPage service={permissions}>
      <Breadcrumb />
      <Content className={`${prefixCls}.content`}>
        <Tabs activeKey={ProjectNotifyStore.getTabKey} onChange={handleTabChange}>
          {tabs.map(item => (
            <TabPane key={item} tab={formatMessage({ id: `${intlPrefix}.tabs.${item}` })} />
          ))}
        </Tabs>
        {getContent()}
      </Content>
    </TabPage>
  );
});
