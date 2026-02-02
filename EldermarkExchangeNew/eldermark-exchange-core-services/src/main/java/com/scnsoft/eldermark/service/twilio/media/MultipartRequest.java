package com.scnsoft.eldermark.service.twilio.media;

import com.twilio.http.HttpMethod;
import com.twilio.http.Request;
import org.apache.http.HttpEntity;

public class MultipartRequest extends Request {

    private HttpEntity httpEntity;


    public MultipartRequest(String url) {
        super(HttpMethod.POST, url);
    }

    public MultipartRequest(String domain, String uri) {
        super(HttpMethod.POST, domain, uri);
    }

    public MultipartRequest(String domain, String uri, String region) {
        super(HttpMethod.POST, domain, uri, region);
    }

    public MultipartRequest setHttpEntity(HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
        return this;
    }

    public HttpEntity getHttpEntity() {
        return httpEntity;
    }
}
