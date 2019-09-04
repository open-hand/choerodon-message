import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/master';
// import TableMessage from './index';
const TableMessage = asyncRouter(() => import('./index'));
const notifyContentModify = asyncRouter(() => import('../NotifyContentModify'));
const Index = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={TableMessage} />
    <Route path={`${match.url}/send-setting/:settingId/:settingBusinessType/:settingType`} component={notifyContentModify} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default Index;
