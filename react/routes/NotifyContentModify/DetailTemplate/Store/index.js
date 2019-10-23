import React, { createContext, useMemo } from 'react/index';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import DetailTemplateDataSet from './DetailTemplateDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children, detailId, isCurrent } = props;
    const { settingId, settingBusinessType, settingType } = props.context.match.params;
    const intlPrefix = 'global.notifyContent';
    const prefixCls = 'notify-content-detail';
    const detailTemplateDataSet = useMemo(() => new DataSet(DetailTemplateDataSet(detailId, settingType, settingBusinessType, isCurrent, intl, `${intlPrefix}.template`)));
    const value = {
      ...props,
      settingId,
      isCurrent,
      prefixCls,
      settingBusinessType,
      settingType,
      intlPrefix,
      detailTemplateDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
