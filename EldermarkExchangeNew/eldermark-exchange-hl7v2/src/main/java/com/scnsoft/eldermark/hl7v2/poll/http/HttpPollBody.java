package com.scnsoft.eldermark.hl7v2.poll.http;

public class HttpPollBody {
    private final String identifier;
    private final String content;
    private final String contentType;

    public HttpPollBody(String identifier, String content, String contentType) {
        this.identifier = identifier;
        this.content = content;
        this.contentType = contentType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }
}
