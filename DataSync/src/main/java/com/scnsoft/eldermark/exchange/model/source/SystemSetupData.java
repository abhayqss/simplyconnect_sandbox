package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.SourceEntity;

public class SystemSetupData extends SourceEntity {
    public static final String SYSTEM_SETUP_TABLE = "system_setup";
    public static final String LOGIN_COMPANY_ID = "Login_Company_ID";

    private String loginCompanyId;

    public String getLoginCompanyId() {
        return loginCompanyId;
    }

    public void setLoginCompanyId(String loginCompanyId) {
        this.loginCompanyId = loginCompanyId;
    }

    @Override
    public String toString() {
        return "SystemSetupData{" +
                "loginCompanyId='" + loginCompanyId + '\'' +
                '}';
    }
}
