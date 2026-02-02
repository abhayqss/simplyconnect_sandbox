package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-04T15:48:31.843+03:00")
public class AccountStatusDto {

    @JsonProperty("lockout")
    private Lockout lockout = null;

    @ApiModelProperty
    public Lockout getLockout() {
        return lockout;
    }

    public void setLockout(Lockout lockout) {
        this.lockout = lockout;
    }

    @Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-04T15:48:31.843+03:00")
    public static class Lockout {

        @JsonProperty("isActive")
        private Boolean isActive = null;

        @JsonProperty("message")
        private String message = null;


        /**
         * true if account is locked out, false otherwise
         */
        @ApiModelProperty(value = "true if account is locked out, false otherwise")
        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        /**
         * account locking description
         */
        @ApiModelProperty(value = "account locking description",
                example = "Your account has been locked out because you have reached the maximum number of invalid logon attempts. Please try again in 4 minute(s).")
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

}
