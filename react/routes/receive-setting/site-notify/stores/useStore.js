import { useLocalStore } from 'mobx-react-lite';
import { axios, Choerodon } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({

    receiveData: [],
    get getReceiveData() {
      return this.receiveData.slice();
    },
    setReceiveData(data) {
      this.receiveData = data;
    },

    async loadReceiveData() {
      try {
        const res = await axios.get('notify/v1/notices/receive_setting?source_type=site');
        if (res && res.failed) {
          Choerodon.prompt(res.message);
        } else {
          this.setReceiveData(res);
        }
      } catch (e) {
        Choerodon.handleResponseError(e);
      }
    },
  }));
}
