package com.scnsoft.eldermark.shared.carecoordination.service;

/**
 * @author mradzivonenka
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 04-Nov-15.
 */
public class InvitationDto {
    private String creator;
    private String target;
    private String careReceiver;
    /**
     * Url to New User page
     */
    private String url;
    private String toEmail;
    private String portalUrl;
    /**
     * Url to Link Accounts page
     */
    private String linkUrl;


    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCareReceiver() {
        return careReceiver;
    }

    public void setCareReceiver(String careReceiver) {
        this.careReceiver = careReceiver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getPortalUrl() {
        return portalUrl;
    }

    public void setPortalUrl(String portalUrl) {
        this.portalUrl = portalUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
