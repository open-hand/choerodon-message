package io.choerodon.message.api.vo;

import java.util.Set;

/**
 * @author superlee
 * @since 2021-03-04
 */
public class ProjectMessageVO extends ProjectVO {

    private Set<Long> userIds;

    private Set<String> receiverTypes;

    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }

    public Set<String> getReceiverTypes() {
        return receiverTypes;
    }

    public void setReceiverTypes(Set<String> receiverTypes) {
        this.receiverTypes = receiverTypes;
    }
}
