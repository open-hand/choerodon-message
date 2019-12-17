import { useLocalStore } from 'mobx-react-lite';
import { axios, Choerodon } from '@choerodon/boot';

const NO_HEADER = [];

export default function useStore() {
  return useLocalStore(() => ({

    expandedKeys: [],
    setExpandedKeys(keys) {
      this.expandedKeys = keys;
    },
    get getExpandedKeys() {
      return this.expandedKeys.slice();
    },
  }));
}
