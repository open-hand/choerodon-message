import React, { Fragment } from 'react';
import { Content, Breadcrumb, Choerodon, TabPage } from '@choerodon/boot';
import { Table, CheckBox, Button } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import { Prompt } from 'react-router-dom';
import { observer } from 'mobx-react-lite';
import { useSiteNotifyStore } from './stores';
import { useReceiveSettingStore } from '../stores';

import './index.less';

const { Column } = Table;

export default observer(props => {
  const {
    intlPrefix,
    prefixCls,
    intl: { formatMessage },
    tableDs,
    receiveStore,
  } = useSiteNotifyStore();
  const {
    promptMsg,
  } = useReceiveSettingStore();

  async function refresh() {
    await receiveStore.loadReceiveData();
    tableDs.query();
  }

  async function saveSettings() {
    try {
      await tableDs.submit();
    } catch (e) {
      Choerodon.handleResponseError(e);
    }
  }

  function handleCheckBoxHeaderChange(value, name) {
    tableDs.forEach((record) => {
      const hasTemplateId = record.get(`${name}TemplateId`);
      if (hasTemplateId) {
        record.set(name, value);
      }
    });
  }
  
  function renderCheckBoxHeader(dataSet, name) {
    const isChecked = tableDs.totalCount && !tableDs.find((record) => !record.get(name) && (record.get(`${name}TemplateId`)));
    const pmRecords = tableDs.find((record) => record.get(name) && (record.get(`${name}TemplateId`)));
    return (
      <CheckBox
        checked={!!isChecked}
        indeterminate={!isChecked && !!pmRecords}
        onChange={(value) => handleCheckBoxHeaderChange(value, name)}
      >
        {formatMessage({ id: `receive.type.${name}` })}
      </CheckBox>
    );
  }

  function renderCheckBox({ record, name }) {
    const isDisabled = !record.get(`${name}TemplateId`);
    return (
      <CheckBox
        record={record}
        name={name}
        checked={record.get(name)}
        disabled={!!isDisabled}
        onChange={(value) => record.set(name, value)}
      />
    );
  }

  function renderEditor(record, name) {
    return !!(record.get(`${name}TemplateId`));
  }

  return (
    <TabPage>
      <Breadcrumb />
      <Prompt message={promptMsg} wrapper="c7n-iam-confirm-modal" when={tableDs.dirty} />
      <Content className={`${prefixCls}-content`}>
        <Table dataSet={tableDs}>
          <Column name="name" />
          <Column
            header={(dataSet) => renderCheckBoxHeader(dataSet, 'pm')}
            renderer={({ record }) => renderCheckBox({ record, name: 'pm' })}
            align="left"
            editor={(record) => renderEditor(record, 'pm')}
          />
          <Column
            header={(dataSet) => renderCheckBoxHeader(dataSet, 'email')}
            renderer={({ record }) => renderCheckBox({ record, name: 'email' })}
            align="left"
            editor={(record) => renderEditor(record, 'email')}
          />
        </Table>
        <div className={`${prefixCls}-buttons`}>
          <Button
            funcType="raised"
            color="primary"
            onClick={saveSettings}
          >
            <FormattedMessage id="save" />
          </Button>
          <Button
            funcType="raised"
            onClick={refresh}
            style={{ marginLeft: 16, color: '#3F51B5' }}
          ><FormattedMessage id="cancel" />
          </Button>
        </div>
      </Content>
    </TabPage>
  );
});
