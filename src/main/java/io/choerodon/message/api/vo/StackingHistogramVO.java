package io.choerodon.message.api.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈功能简述〉
 * 〈堆积柱状图展示VO〉
 *
 * @author wanghao
 * @Date 2020/2/24 21:08
 */
public class StackingHistogramVO {
    private List<String> dates = new ArrayList<>();
    private List<Long> successNums = new ArrayList<>();
    private List<Long> failedNums = new ArrayList<>();
    private List<Long> totalNums = new ArrayList<>();

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<Long> getSuccessNums() {
        return successNums;
    }

    public void setSuccessNums(List<Long> successNums) {
        this.successNums = successNums;
    }

    public List<Long> getFailedNums() {
        return failedNums;
    }

    public void setFailedNums(List<Long> failedNums) {
        this.failedNums = failedNums;
    }

    public List<Long> getTotalNums() {
        return totalNums;
    }

    public void setTotalNums(List<Long> totalNums) {
        this.totalNums = totalNums;
    }
}
