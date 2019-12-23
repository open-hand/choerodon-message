import { useLocalStore } from 'mobx-react-lite';
import { axios, Choerodon } from '@choerodon/boot';

const NO_HEADER = [];

export default function useStore() {
  return useLocalStore(() => ({

    loading: true,
    get getLoading() {
      return this.loading;
    },
    setLoading(flag) {
      this.loading = flag;
    },

    enabled: false,
    get getEnabled() {
      return this.enabled;
    },
    setEnabled(data) {
      this.enabled = data;
    },

    async checkEnabled() {
      this.setLoading(true);
      try {
        const res = await axios.get('/notify/v1/notices/send_settings/codes/resourceDeleteConfirmation/check_enabled');
        this.setLoading(false);
        if (res && res.failed) {
          Choerodon.prompt(res.message);
          this.setEnabled(false);
          return false;
        } else {
          this.setEnabled(res);
          return res;
        }
      } catch (e) {
        this.setLoading(false);
        this.setEnabled(false);
        return false;
      }
    },
  }));
}
