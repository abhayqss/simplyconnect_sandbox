package com.scnsoft.eldermark.entity.phr;

import java.util.Collection;
import java.util.Map;

public class ApnsModel {
    
    private Map<String, Object> aps;
    
    private Map<String, Object> data;
    
    private Collection<String> token;
    
    private String apnsKey;

    public Map<String, Object> getAps() {
        return aps;
    }

    public void setAps(Map<String, Object> aps) {
        this.aps = aps;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Collection<String> getToken() {
        return token;
    }

    public void setToken(Collection<String> token) {
        this.token = token;
    }

    public String getApnsKey() {
        return apnsKey;
    }

    public void setApnsKey(String apnsKey) {
        this.apnsKey = apnsKey;
    }
    
}
