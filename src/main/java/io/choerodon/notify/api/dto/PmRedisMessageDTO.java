package io.choerodon.notify.api.dto;

public class PmRedisMessageDTO {

    private Long id;

    private String code;

    private String pm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public PmRedisMessageDTO(Long id, String code, String pm) {
        this.id = id;
        this.code = code;
        this.pm = pm;
    }

    public PmRedisMessageDTO() {
    }

    @Override
    public String toString() {
        return "PmRedisMessageDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", pm='" + pm + '\'' +
                '}';
    }
}
