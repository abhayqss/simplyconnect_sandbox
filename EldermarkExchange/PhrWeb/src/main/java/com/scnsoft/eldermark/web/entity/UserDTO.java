package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.entity.phr.chat.PhrChatHandset;

import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-03-28T19:32:17.348+03:00")
public class UserDTO {

    @JsonProperty("profile")
    private Profile profile = new Profile();

    @JsonProperty("userId")
    private Long userId = null;

    @JsonProperty("type")
    private AccountType.Type type = null;
    
    @JsonProperty("chatUserId")
    private Long chatUserId = null;
    
    @JsonProperty("chatServer")
    private String chatServer = null;
    
    @JsonProperty("chatUrl")
    private String chatUrl = null;
    
    @JsonProperty("phrChatHandset")
    private PhrChatHandset phrChatHandset = null;

    public PhrChatHandset getPhrChatHandset() {
        return phrChatHandset;
    }

    public void setPhrChatHandset(PhrChatHandset phrChatHandset) {
        this.phrChatHandset = phrChatHandset;
    }

    public Long getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(Long chatUserId) {
        this.chatUserId = chatUserId;
    }

    public String getChatServer() {
        return chatServer;
    }

    public void setChatServer(String chatServer) {
        this.chatServer = chatServer;
    }

    public String getChatUrl() {
        return chatUrl;
    }

    public void setChatUrl(String chatUrl) {
        this.chatUrl = chatUrl;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /**
     * User role (Current account type)
     *
     * @return Type
     */
    @ApiModelProperty(value = "User role")
    public AccountType.Type getType() {
        return type;
    }

    public void setType(AccountType.Type type) {
        this.type = type;
    }

    /**
     * user id
     * minimum: 1
     *
     * @return userId
     */
    @ApiModelProperty(value = "user id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
