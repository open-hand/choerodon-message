import { action, computed, observable } from 'mobx';
import { axios, store } from '@choerodon/master';
import querystring from 'query-string';

@store('SmsTemplateStore')
class SmsTemplateStore {
  @observable loading = true;

  @observable mailTemplate = [];

  @observable templateType = [];

  @observable currentDetail = {};

  @observable editorContent = '';

  @observable selectType = 'create';

  @action setLoading(flag) {
    this.loading = flag;
  }

  @action setMailTemplate(data) {
    this.mailTemplate = data;
  }

  @action setTemplateType(data) {
    this.templateType = data;
  }

  @computed get getTemplateType() {
    return this.templateType;
  }

  @action setCurrentDetail(data) {
    this.currentDetail = data;
  }

  @computed get getCurrentDetail() {
    return this.currentDetail;
  }

  @action setEditorContent(data) {
    this.editorContent = data;
  }

  @computed get getEditorContent() {
    return this.editorContent;
  }

  @action setSelectType(data) {
    this.selectType = data;
  }

  getMailTemplate() {
    return this.mailTemplate;
  }

  loadMailTemplate = (
    { current, pageSize },
    { name, code, type, isPredefined },
    { columnKey = 'id', order = 'descend' },
    params,
  ) => {
    const queryObj = {
      name: name && name[0],
      type: type && type[0],
      code: code && code[0],
      isPredefined: isPredefined && isPredefined[0],
      params,
    };

    if (columnKey) {
      const sorter = [];
      sorter.push(columnKey);
      if (order === 'descend') {
        sorter.push('desc');
      }
      queryObj.sort = sorter.join(',');
    }
    return axios.get(`/sms/v1/templates?page=${current}&size=${pageSize}&${querystring.stringify(queryObj)}`);
  };

  loadTemplateType = (appType, orgId) => {
    const path = appType === 'site' ? '' : `/organizations/${orgId}`;
    return axios.get(`/notify/v1/notices/send_settings/names${path}`);
  };

  createTemplate = (data, appType, orgId) => axios.post('sms/v1/templates', JSON.stringify({ ...data, messageType: 'sms' }));

  deleteMailTemplate = (id, appType, orgId) => axios.delete(`/sms/v1/templates/${id}`);

  getTemplateDetail = (id, appType, orgId) => {
    const path = appType === 'site' ? '' : `/organizations/${orgId}`;
    return axios.get(`/sms/v1/templates/${id}`);
  };

  updateTemplateDetail = (id, data, appType, orgId) => {
    const path = appType === 'site' ? '' : `/organizations/${orgId}`;
    return axios.put(`/sms/v1/templates/${id}${path}`, JSON.stringify(data));
  }
}

const mailTemplateStore = new SmsTemplateStore();
export default mailTemplateStore;
