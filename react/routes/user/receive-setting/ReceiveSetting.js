import React, { Component, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import { Content, Header, Page, Breadcrumb, Choerodon } from '@choerodon/boot';
import { Table, Button, Checkbox, Modal, Tooltip } from 'choerodon-ui';
import { Prompt } from 'react-router-dom';
import { useStore } from './stores';
import './ReceiveSetting.less';

const intlPrefix = 'user.receive-setting';

// @inject('AppState')
// @injectIntl
// @observer

function ReceiveSetting(props) {
  const context = useStore();
  const { ReceiveSettingStore, AppState, intl } = context;

  const refresh = () => {
    ReceiveSettingStore.setLoading(true);
    Promise.all([
      ReceiveSettingStore.loadReceiveTypeData(AppState.getUserInfo.id),
      ReceiveSettingStore.loadReceiveSettingData(),
      ReceiveSettingStore.loadAllowConfigData(),
    ]).then(() => {
      ReceiveSettingStore.setLoading(false);
      ReceiveSettingStore.setDirty(false);
    }).catch((error) => {
      // Choerodon.prompt(`${error.response.status} ${error.response.statusText}`);
      Choerodon.handleResponseError(error);
      ReceiveSettingStore.setLoading(false);
    });
  };

  const saveSettings = () => {
    if (ReceiveSettingStore.getDirty) {
      ReceiveSettingStore.saveData().then((data) => {
        if (!data.fail) {
          Choerodon.prompt(intl.formatMessage({ id: 'save.success' }));
          ReceiveSettingStore.setDirty(false);
        }
      });
    } else {
      Choerodon.prompt(intl.formatMessage({ id: 'save.success' }));
    }
  };

  const handleCheckAllChange = (type) => {
    if (ReceiveSettingStore.isAllSelected(type)) {
      Modal.confirm({
        className: 'c7n-iam-confirm-modal',
        title: intl.formatMessage({ id: `${intlPrefix}.uncheck-all.title` }, { name: intl.formatMessage({ id: type }) }),
        content: intl.formatMessage({ id: `${intlPrefix}.uncheck-all.content` }),
        onOk: () => {
          ReceiveSettingStore.unCheckAll(type);
          // saveSettings();
        },
      });
    } else {
      ReceiveSettingStore.checkAll(type);
      // saveSettings();
    }
  };


  const handleCheckChange = (e, id, type) => {
    ReceiveSettingStore.check(id, type);
    // forceUpdate();
    // saveSettings();
  };


  const isCheckDisabled = (record, type) => {
    const level = record.id.split('-')[0];
    if ('settings' in record) {
      let allDisable = true;
      ReceiveSettingStore.getAllowConfigData.forEach((value, key) => {
        if (value.type === level) {
          const allowConfigData = ReceiveSettingStore.getAllowConfigData.get(key);
          if (allowConfigData && allowConfigData.disabled && !allowConfigData.disabled[type]) {
            allDisable = false;
          }
        }
      });
      return allDisable;
    }
    const allowConfigData = ReceiveSettingStore.getAllowConfigData.get(parseInt(record.id.split('-')[2], 10));
    return allowConfigData && allowConfigData.disabled && allowConfigData.disabled[type];
  };

  const getCheckbox = (record, type) => {
    if (isCheckDisabled(record, type)) {
      return (
        <Checkbox
          key={record.id ? record.id : `${record.id}-${record.sendSettingId}`}
          disabled
        />
      );
    } else {
      return (
        <Checkbox
          key={record.id ? record.id : `${record.id}-${record.sendSettingId}`}
          indeterminate={record.id ? (type === 'pm' ? record.pmIndeterminate : record.mailIndeterminate) : false}
          onChange={e => handleCheckChange(e, record.id, type)}
          checked={type === 'pm' ? record.pmChecked : record.mailChecked}
        />
      );
    }
  };

  // eslint-disable-next-line arrow-body-style
  const getTitleCheckbox = (type) => {
    return (
      <Checkbox
        key={type}
        indeterminate={!ReceiveSettingStore.isAllSelected(type) && !ReceiveSettingStore.isAllUnSelected(type)}
        checked={ReceiveSettingStore.isAllSelected(type) && !ReceiveSettingStore.getDataSource.every(record => isCheckDisabled(record, type))}
        onChange={() => handleCheckAllChange(type)}
        disabled={ReceiveSettingStore.getDataSource.every(record => isCheckDisabled(record, type))}
      >
        {intl.formatMessage({ id: type })}
      </Checkbox>
    );
  };

  useEffect(() => {
    refresh();
  }, []);

  function render() {
    const promptMsg = intl.formatMessage({ id: 'global.menusetting.prompt.inform.title' }) + Choerodon.STRING_DEVIDER + intl.formatMessage({ id: 'global.menusetting.prompt.inform.message' });
    const columns = [{
      title: '信息类型',
      dataIndex: 'name',
      key: 'name',
    }, {
      title: intl.formatMessage({ id: 'level' }),
      width: '20%',
      render: (text, record) => intl.formatMessage({ id: record.id.split('-')[0] }),
    }, {
      title: getTitleCheckbox('pm'),
      width: '15%',
      render: (text, record) => getCheckbox(record, 'pm'),
    }, {
      title: getTitleCheckbox('email'),
      width: '15%',
      render: (text, record) => getCheckbox(record, 'email'),
    }];

    return (
      <Page
        service={[
          'notify-service.receive-setting.update',
          'notify-service.receive-setting.updateByUserId',
          'notify-service.receive-setting.queryByUserId',
        ]}
      >
        <Breadcrumb />
        <Content
          className="receiveSetting"
        >
          <Prompt message={promptMsg} wrapper="c7n-iam-confirm-modal" when={ReceiveSettingStore.getDirty} />
          <Table
            loading={ReceiveSettingStore.getLoading}
            filterBar
            filterBarPlaceholder="过滤表"
            columns={columns}
            pagination={false}
            dataSource={ReceiveSettingStore.getDataSource}
            childrenColumnName="settings"
            rowKey="id"
            fixed
            className="c7n-permission-info-table"
          />
          <div style={{ marginTop: 25 }}>
            <Button
              funcType="raised"
              type="primary"
              onClick={saveSettings}
            ><FormattedMessage id="save" />
            </Button>
            <Button
              funcType="raised"
              onClick={refresh}
              style={{ marginLeft: 16, color: '#3F51B5' }}
            ><FormattedMessage id="cancel" />
            </Button>
          </div>
        </Content>
      </Page>
    );
  }
  return render();
}
export default observer(ReceiveSetting);
