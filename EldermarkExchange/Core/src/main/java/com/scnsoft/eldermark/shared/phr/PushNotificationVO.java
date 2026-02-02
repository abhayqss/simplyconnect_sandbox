package com.scnsoft.eldermark.shared.phr;

import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;

import java.util.List;
import java.util.Map;

/**
 * @author phomal
 */
public class PushNotificationVO {
    private List<String> tokens;
    private String title;
    private String body;
    private String text;
    private Map<String, Object> payload;
    private PushNotificationRegistration.ServiceProvider serviceProvider;
    private String tag;
    private Map<String, Object> notification;  
    private String attributeKey = null;
    public List<String> getTokens() {
        return tokens;
    }
    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Map<String, Object> getPayload() {
        return payload;
    }
    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
    public PushNotificationRegistration.ServiceProvider getServiceProvider() {
        return serviceProvider;
    }
    public void setServiceProvider(PushNotificationRegistration.ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public Map<String, Object> getNotification() {
        return notification;
    }
    public void setNotification(Map<String, Object> notification) {
        this.notification = notification;
    }
    public String getAttributeKey() {
        return attributeKey;
    }
    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }
}
