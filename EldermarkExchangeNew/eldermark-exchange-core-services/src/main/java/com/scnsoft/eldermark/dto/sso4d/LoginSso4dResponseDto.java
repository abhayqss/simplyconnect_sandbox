package com.scnsoft.eldermark.dto.sso4d;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LoginSso4dResponseDto {

    @JsonProperty("__COUNT")
    private Long count;

    @JsonProperty("__ENTITIES")
    private List<LoginSSo4dDetails> logins;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<LoginSSo4dDetails> getLogins() {
        return logins;
    }

    public void setLogins(List<LoginSSo4dDetails> logins) {
        this.logins = logins;
    }
}
