package com.scnsoft.eldermark.service.twilio.media;

import com.twilio.base.Fetcher;
import com.twilio.exception.ApiConnectionException;
import com.twilio.exception.ApiException;
import com.twilio.exception.RestException;
import com.twilio.http.HttpMethod;
import com.twilio.http.Request;
import com.twilio.http.Response;
import com.twilio.http.TwilioRestClient;

public class MediaFetcher extends Fetcher<Media> {
    private final String pathChatServiceSid;
    private final String pathSid;

    /**
     * Construct a new MediaFetcher.
     *
     * @param pathChatServiceSid  The SID of the Conversation Service that the
     *                            resource is associated with.
     * @param pathSid             A 34 character string that uniquely identifies this resource.
     */
    public MediaFetcher(final String pathChatServiceSid,
                        final String pathSid) {
        this.pathChatServiceSid = pathChatServiceSid;
        this.pathSid = pathSid;
    }

    /**
     * Make the request to the Twilio API to perform the fetch.
     *
     * @param client TwilioRestClient with which to make the request
     * @return Fetched Participant
     */
    @Override
    @SuppressWarnings("checkstyle:linelength")
    public Media fetch(final TwilioRestClient client) {
        Request request = new Request(
                HttpMethod.GET,
                TwilioAdditionalDomains.MEDIA.toString(),
                "/v1/Services/" + this.pathChatServiceSid + "/Media/" + this.pathSid
        );

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Media fetch failed: Unable to connect to server");
        } else if (!TwilioRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return Media.fromJson(response.getStream(), client.getObjectMapper());
    }
}
