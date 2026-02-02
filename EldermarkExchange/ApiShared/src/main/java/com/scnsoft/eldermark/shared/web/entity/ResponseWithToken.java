package com.scnsoft.eldermark.shared.web.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * Response with token and user accounts.
 *
 * @author phomal
 * Created by phomal on 1/11/2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWithToken<T> extends Response<T> {
    private String token;
    private List<AccountTypeDto> accounts;

    @XmlTransient
    public static <T> ResponseWithToken<T> tokenResponse(Token token, T data) {
        return tokenResponse(token, data, null);
    }

    @XmlTransient
    public static <T> ResponseWithToken<T> tokenResponse(Token token, T data, List<AccountTypeDto> accounts) {
        ResponseWithToken<T> response = new ResponseWithToken<>();
        response.setToken(Token.base64encode(token));
        response.setStatusCode(SUCCESS_STATUS_CODE);
        response.setAccounts(accounts);
        response.setBody(new ResponseBody<>(true, null, data));
        return response;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<AccountTypeDto> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountTypeDto> accounts) {
        this.accounts = accounts;
    }

}
