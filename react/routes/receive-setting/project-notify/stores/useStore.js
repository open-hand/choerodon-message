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

    projectData: [],
    get getProjectData() {
      return this.projectData.slice();
    },
    setProjectData(data) {
      this.projectData = data;
    },

    async loadReceiveData(organizationId) {
      try {
        const [receive, projects] = await axios.all([
          axios.get('notify/v1/notices/receive_setting?source_type=project'),
          axios.get(`base/v1/users/${organizationId}/organization_project`),
        ]);
        if (receive && receive.failed) {
          Choerodon.prompt(receive.message);
        } else {
          this.setReceiveData(receive);
        }
        if (projects && projects.failed) {
          Choerodon.prompt(projects.message);
        } else {
          this.setProjectData(projects.projectList);
        }
      } catch (e) {
        Choerodon.handleResponseError(e);
      }
    },
  }));
}
