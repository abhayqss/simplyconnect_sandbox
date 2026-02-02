package com.scnsoft.eldermark.entity.careteam.invitation;

public class ConfirmInviteCareTeamMemberData extends BaseInviteCareTeamMemberData {
    private String token;

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
