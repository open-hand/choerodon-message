import React, { Fragment } from 'react';
import { Content, Breadcrumb, Choerodon, TabPage } from '@choerodon/boot';
import { Table, CheckBox, Button } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import { Prompt } from 'react-router-dom';
import { observer } from 'mobx-react-lite';
import { useProjectNotifyStore } from './stores';
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
    AppState: { getUserInfo: { id } },
  } = useProjectNotifyStore();
  const {
    promptMsg,
  } = useReceiveSettingStore();

  async function refresh() {
    await receiveStore.loadReceiveData(id);
    tableDs.query();
  }

  async function saveSettings() {
    try {
      if (await tableDs.submit() !== false) {
        refresh();
      }
    } catch (e) {
      Choerodon.handleResponseError(e);
    }
  }

  function handleCheckBoxHeaderChange(value, name) {
    tableDs.forEach((record) => {
      if (!record.get(`${name}Disabled`)) {
        record.set(name, value);
      }
    });
  }

  function renderCheckBoxHeader(dataSet, name) {
    const isChecked = tableDs.totalCount && !tableDs.find((record) => !record.get(name) && !record.get(`${name}Disabled`));
    const pmRecords = tableDs.find((record) => record.get(name) && !record.get(`${name}Disabled`));
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

  function getChildrenId(key) {
    const childrenId = [];
    tableDs.forEach((tableRecord) => {
      if (key === tableRecord.get('sourceId')) {
        childrenId.push(tableRecord.get('key'));
      }
    });
    return childrenId;
  }

  function parentItemIsChecked({ record, name }) {
    const parentIsChecked = !tableDs.find((tableRecord) => record.get('key') === tableRecord.get('sourceId') && !tableRecord.get(name) && !tableRecord.get(`${name}Disabled`));
    const realValue = parentIsChecked && !record.get(`${name}Disabled`);
    record.set(name, realValue);
  }

  function handleChecked(name) {
    tableDs.forEach((record) => {
      if (record.get('treeType') === 'group') {
        parentItemIsChecked({ record, name });
      }
    });
    tableDs.forEach((record) => {
      if (record.get('treeType') === 'project') {
        parentItemIsChecked({ record, name });
      }
    });
  }

  function handleCheckBoxChange(record, value, name) {
    if (record.get('treeType') === 'group') {
      tableDs.forEach((tableRecord) => {
        if (tableRecord.get('sourceId') === record.get('key') && !tableRecord.get(`${name}Disabled`)) {
          tableRecord.set(name, value);
        } else if (record.get('sourceId') === tableRecord.get('key')) {
          parentItemIsChecked({ record: tableRecord, name });
        }
      });
    } else if (record.get('treeType') === 'project') {
      const childrenId = getChildrenId(record.get('key'));
      tableDs.forEach((tableRecord) => {
        if (tableRecord.get('sourceId') === record.get('key') && !tableRecord.get(`${name}Disabled`)) {
          tableRecord.set(name, value);
        } else if (childrenId.includes(tableRecord.get('sourceId')) && !tableRecord.get(`${name}Disabled`)) {
          tableRecord.set(name, value);
        }
      });
    } else {
      handleChecked(name);
    }
  }

  function renderCheckBox({ record, name }) {
    const isDisabled = record.get(`${name}Disabled`);
    const isChecked = record.get(name);
    let checkedRecords;
    if (record.get('treeType') === 'group') {
      checkedRecords = tableDs.find((tableRecord) => record.get('key') === tableRecord.get('sourceId') && tableRecord.get(name) && !tableRecord.get(`${name}Disabled`));
    } else if (record.get('treeType') === 'project') {
      const childrenId = getChildrenId(record.get('key'));
      checkedRecords = tableDs.find((tableRecord) => childrenId.includes(tableRecord.get('sourceId')) && tableRecord.get(name) && !tableRecord.get(`${name}Disabled`));
    }

    return (
      <CheckBox
        record={record}
        name={name}
        checked={isChecked}
        disabled={isDisabled}
        indeterminate={!isChecked && !!checkedRecords}
        onChange={(checkBoxValue) => handleCheckBoxChange(record, checkBoxValue, name)}
      />
    );
  }

  function renderEditor(record, name) {
    return !record.get(`${name}Disabled`);
  }

  return (
    <TabPage>
      <Breadcrumb />
      <Prompt message={promptMsg} wrapper="c7n-iam-confirm-modal" when={tableDs.dirty} />
      <Content className={`${prefixCls}-content`}>
        <Table dataSet={tableDs} mode="tree">
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
