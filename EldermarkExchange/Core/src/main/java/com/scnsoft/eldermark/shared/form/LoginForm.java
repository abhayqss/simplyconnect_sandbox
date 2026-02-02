package com.scnsoft.eldermark.shared.form;

public class LoginForm {

    private String username;
    private String password;
    private String company;
    private Boolean linkExisting;
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Boolean getLinkExisting() {
        return linkExisting;
    }

    public void setLinkExisting(Boolean linkExisting) {
        this.linkExisting = linkExisting;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
