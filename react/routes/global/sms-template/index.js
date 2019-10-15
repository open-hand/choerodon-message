import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const index = asyncRouter(() => (import('./SmsTemplate')), () => import('../../../stores/global/sms-template'));
const create = asyncRouter(() => import('./SmsTemplateCreate'));
const modify = asyncRouter(() => import('./SmsTemplateModify'));

const Index = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={index} />
    <Route path={`${match.url}/create`} component={create} />
    <Route path={`${match.url}/modify/:id`} component={modify} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default Index;
