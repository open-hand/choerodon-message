import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { inject } from 'mobx-react';
import { asyncLocaleProvider, asyncRouter, nomatch } from '@choerodon/boot';

const sendSetting = asyncRouter(() => import('./global/send-setting'));
const mailTemplate = asyncRouter(() => import('./global/mail-template'));
const inmailTemplate = asyncRouter(() => import('./global/inmail-template'));
const mailSetting = asyncRouter(() => import('./global/mail-setting'));
const msgRecord = asyncRouter(() => import('./global/msg-record'));
const announcement = asyncRouter(() => import('./global/announcement'));
const userMsg = asyncRouter(() => import('./user/user-msg'));
const receiveSetting = asyncRouter(() => import('./user/receive-setting'));

@inject('AppState')
class IAMIndex extends React.Component {
  render() {
    const { match, AppState } = this.props;
    const langauge = AppState.currentLanguage;
    const IntlProviderAsync = asyncLocaleProvider(langauge, () => import(`../locale/${langauge}`));
    return (
      <IntlProviderAsync>
        <Switch>
          <Route path={`${match.url}/send-setting`} component={sendSetting} />
          <Route path={`${match.url}/mail-template`} component={mailTemplate} />
          <Route path={`${match.url}/inmail-template`} component={inmailTemplate} />
          <Route path={`${match.url}/mail-setting`} component={mailSetting} />
          <Route path={`${match.url}/msg-record`} component={msgRecord} />
          <Route path={`${match.url}/announcement`} component={announcement} />
          <Route path={`${match.url}/user-msg`} component={userMsg} />
          <Route path={`${match.url}/receive-setting`} component={receiveSetting} />
          <Route path="*" component={nomatch} />
        </Switch>
      </IntlProviderAsync>
    );
  }
}

export default IAMIndex;
