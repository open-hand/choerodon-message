import React, { Component } from 'react';
import { Route, Switch } from 'react-router-dom';
import { inject, observer } from 'mobx-react';
import { asyncLocaleProvider, asyncRouter, nomatch } from '@choerodon/boot';
import { ModalContainer } from 'choerodon-ui/pro';

const sendSetting = asyncRouter(() => import('./routes/global/send-setting'));
const mailTemplate = asyncRouter(() => import('./routes/global/mail-template'));
const inmailTemplate = asyncRouter(() => import('./routes/global/inmail-template'));
const mailSetting = asyncRouter(() => import('./routes/NotifySetting/mail-setting'));
const msgRecord = asyncRouter(() => import('./routes/global/msg-record'));
const announcement = asyncRouter(() => import('./routes/announcement'));
const userMsg = asyncRouter(() => import('./routes/user/user-msg'));
const receiveSetting = asyncRouter(() => import('./routes/user/receive-setting'));
const smsTemplate = asyncRouter(() => import('./routes/global/sms-template'));
const smsSetting = asyncRouter(() => import('./routes/NotifySetting/sms-setting'));
const notifySetting = asyncRouter(() => import('./routes/NotifySetting'));
const notifyList = asyncRouter(() => import('./routes/NotifyList'));
const notifyContentModify = asyncRouter(() => import('./routes/NotifyContentModify'));

function LowCodeIndex({ match, AppState: { currentLanguage: language } }) {
  const IntlProviderAsync = asyncLocaleProvider(language, () => import(`./locale/${language}`));
  return (
    <IntlProviderAsync>
      <div>
        <Switch>
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
          <Route path={`${match.url}/send-setting-content`} component={notifyList} />
          <Route path={`${match.url}/send-setting/:settingId/:settingBusinessType/:settingType`} component={notifyContentModify} />
          <Route path="*" component={nomatch} />
        </Switch>
        <ModalContainer />
      </div>
    </IntlProviderAsync>
  );
}

export default inject('AppState')(LowCodeIndex);
