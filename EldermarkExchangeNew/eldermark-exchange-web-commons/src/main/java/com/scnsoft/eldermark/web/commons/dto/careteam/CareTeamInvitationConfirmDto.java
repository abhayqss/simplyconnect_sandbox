package com.scnsoft.eldermark.web.commons.dto.careteam;

import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;


public class CareTeamInvitationConfirmDto extends BaseCareTeamInvitationDto {

    @NotEmpty
    private String token;

    @NotEmpty
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String mobilePhone;

    private String password;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
