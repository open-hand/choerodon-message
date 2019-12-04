import { useLocalStore } from 'mobx-react-lite';
import { axios, Choerodon } from '@choerodon/boot';

export default function useStore({ defaultKey }) {
  return useLocalStore(() => ({
    tabKey: defaultKey,
    setTabKey(data) {
      this.tabKey = data;
    },
    get getTabKey() {
      return this.tabKey;
    },
  }));
}
