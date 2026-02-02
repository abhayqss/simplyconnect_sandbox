package com.scnsoft.eldermark.authentication;

/**
 * @author phomal
 * Created on 7/26/2017.
 */
public final class UsernameBuilder {

    private String companyId;
    private String login;
    private String linkLogin;
    private String token;

    private static final String DELIMITER = "/";

    private UsernameBuilder() {
    }

    public static UsernameBuilder anUsername() {
        return new UsernameBuilder();
    }

    public UsernameBuilder withCompanyId(String companyId) {
        this.companyId = companyId;
        return this;
    }

    public UsernameBuilder withLogin(String login) {
        this.login = login;
        return this;
    }

    public UsernameBuilder withLinkLogin(String linkLogin) {
        this.linkLogin = linkLogin;
        return this;
    }

    public UsernameBuilder withToken(String token) {
        this.token = token;
        return this;
    }

    public static String getDelimiter() {
        return DELIMITER;
    }

    public String build() {
        String username = companyId + getDelimiter() + login;
        if (linkLogin != null || token != null) {
            username += getDelimiter() + linkLogin + getDelimiter() + token;
        }
        return username;
    }

}
