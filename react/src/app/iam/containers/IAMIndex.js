import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { ModalContainer } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { asyncLocaleProvider, asyncRouter, nomatch } from '@choerodon/boot';

const sendSetting = asyncRouter(() => import('./global/send-setting'));
const mailTemplate = asyncRouter(() => import('./global/mail-template'));
const inmailTemplate = asyncRouter(() => import('./global/inmail-template'));
const mailSetting = asyncRouter(() => import('./NotifySetting/mail-setting'));
const msgRecord = asyncRouter(() => import('./global/msg-record'));
const announcement = asyncRouter(() => import('./global/announcement'));
const userMsg = asyncRouter(() => import('./user/user-msg'));
const receiveSetting = asyncRouter(() => import('./user/receive-setting'));
const smsTemplate = asyncRouter(() => import('./global/sms-template'));
const smsSetting = asyncRouter(() => import('./NotifySetting/sms-setting'));
const notifySetting = asyncRouter(() => import('./NotifySetting'));

@inject('AppState')
class IAMIndex extends React.Component {
  render() {
    const { match, AppState } = this.props;
    const langauge = AppState.currentLanguage;
    const IntlProviderAsync = asyncLocaleProvider(langauge, () => import(`../locale/${langauge}`));
    return (
      <IntlProviderAsync>
        <div>
          <Switch>
            <Route path={`${match.url}/send-setting`} component={sendSetting} />
            <Route path={`${match.url}/msg-record`} component={msgRecord} />
            <Route path={`${match.url}/announcement`} component={announcement} />

            <Route path={`${match.url}/sms-template`} component={smsTemplate} />
            <Route path={`${match.url}/mail-template`} component={mailTemplate} />
            <Route path={`${match.url}/inmail-template`} component={inmailTemplate} />
            <Route path={`${match.url}/sms-setting`} component={smsSetting} />
            <Route path={`${match.url}/mail-setting`} component={mailSetting} />
            <Route path={`${match.url}/receive-setting`} component={receiveSetting} />
            <Route path={`${match.url}/user-msg`} component={userMsg} />
            <Route path={`${match.url}/notify-setting`} component={notifySetting} />
            <Route path="*" component={nomatch} />
          </Switch>
          <ModalContainer />
        </div>
      </IntlProviderAsync>
    );
  }
}

export default IAMIndex;
