package com.scnsoft.eldermark.service.twilio.media;

import com.twilio.base.Creator;
import com.twilio.exception.ApiConnectionException;
import com.twilio.exception.ApiException;
import com.twilio.exception.RestException;
import com.twilio.http.Request;
import com.twilio.http.Response;
import com.twilio.http.TwilioRestClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public class MediaCreator extends Creator<Media> {

    private final String pathChatServiceSid;
    private byte[] content;
    private String contentType;
    private String filename;
    private Media.WebhookEnabledType xTwilioWebhookEnabled;

    public MediaCreator(final String pathChatServiceSid) {
        this.pathChatServiceSid = pathChatServiceSid;
    }

    public MediaCreator setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public MediaCreator setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public MediaCreator setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    /**
     * The X-Twilio-Webhook-Enabled HTTP request header.
     *
     * @param xTwilioWebhookEnabled The X-Twilio-Webhook-Enabled HTTP request header
     * @return this
     */
    public MediaCreator setXTwilioWebhookEnabled(final Media.WebhookEnabledType xTwilioWebhookEnabled) {
        this.xTwilioWebhookEnabled = xTwilioWebhookEnabled;
        return this;
    }

    @Override
    public Media create(TwilioRestClient client) {
        MultipartRequest request = new MultipartRequest(
                TwilioAdditionalDomains.MEDIA.toString(),
                "/v1/Services/" + this.pathChatServiceSid + "/Media"
        );

        addPostParams(request);
        addHeaderParams(request);
        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Media creation failed: Unable to connect to server");
        } else if (!TwilioRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return Media.fromJson(response.getStream(), client.getObjectMapper());
    }

    /**
     * Add the requested header parameters to the Request.
     *
     * @param request Request to add header params to
     */
    private void addHeaderParams(final Request request) {
        if (xTwilioWebhookEnabled != null) {
            request.addHeaderParam("X-Twilio-Webhook-Enabled", xTwilioWebhookEnabled.toString());
        }
    }

    /**
     * Add the requested post parameters to the Request.
     *
     * @param request Request to add post params to
     */
    private void addPostParams(final MultipartRequest request) {
        if (content != null && StringUtils.isNotEmpty(contentType) && StringUtils.isNotEmpty(filename)) {
            var httpEntity = MultipartEntityBuilder.create()
                    .addBinaryBody("media", content, ContentType.create(this.contentType), filename)
                    .build();

            request.setHttpEntity(httpEntity);
        }
    }
}
