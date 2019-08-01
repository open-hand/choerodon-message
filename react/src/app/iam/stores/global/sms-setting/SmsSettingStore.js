import { action, computed, observable } from 'mobx';
import { axios, store } from '@choerodon/boot';

@store('MailSettingStore')
class MailSettingStore {
  @observable settingData = {};

  @action setSettingData(data) {
    this.settingData = data;
  }

  @computed get getSettingData() {
    return this.settingData;
  }

  @action cleanData() {
    this.settingData = {};
  }

  loadData = () => {
    this.cleanData();
    return axios.get('/sms/v1/sms/config?organization_id=0');
  }

  updateData = data => axios.put(`/sms/v1/sms/config/${this.settingData.id || 0}`, JSON.stringify(data));

  testConnection = data => axios.post('notify/v1/notices/configs/email/test', JSON.stringify(data));
}

const mailSettingStore = new MailSettingStore();
export default mailSettingStore;
