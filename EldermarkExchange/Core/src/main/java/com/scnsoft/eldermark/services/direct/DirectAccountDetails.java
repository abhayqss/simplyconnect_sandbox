package com.scnsoft.eldermark.services.direct;

public class DirectAccountDetails {
    private final String secureMessagingLogin;
    private final String companyCode;
    private final String domain;

    DirectAccountDetails(String login, String companyCode, String domain) {
        this.secureMessagingLogin = login;
        this.companyCode = companyCode;
        this.domain = domain;
    }

    public String getCompany() {
        return companyCode;
    }

    public String getSecureEmail() {
        if (secureMessagingLogin == null)
            return getRootSecureEmail();
        else
            return secureMessagingLogin;
    }

    public String getRootSecureEmail() {
        return String.format("%s@%s", companyCode, domain);
    }

    public String getSecureMessagingLogin() { return secureMessagingLogin; }
}
