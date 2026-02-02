package com.scnsoft.eldermark.service.twilio.media;

import com.twilio.exception.ApiConnectionException;
import com.twilio.exception.ApiException;
import com.twilio.exception.RestException;
import com.twilio.http.HttpMethod;
import com.twilio.http.Request;
import com.twilio.http.Response;
import com.twilio.http.TwilioRestClient;
import org.apache.http.Header;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.Arrays;

public class MediaContentDownloader {
    private static final String CONTENT_LINK = "content";
    private static final String REDIRECT_LOCATION_HEADER = "Location";

    private final Media media;

    public MediaContentDownloader(Media media) {
        this.media = media;
    }

    public byte[] download(TwilioRestClient client) {
        Request request = new Request(
                HttpMethod.GET,
                TwilioAdditionalDomains.MEDIA.toString(),
                this.media.getLinks().get(CONTENT_LINK)
        );

        Response response = makeRequest(request, client);

        if (response.getStatusCode() >= 300 && response.getStatusCode() < 400) {
            var redirectUrl = findRedirectUrl(response.getHeaders());
            request = new Request(HttpMethod.GET, redirectUrl);
            response = makeRequest(request, client);
        }

        try {
            return response.getStream().readAllBytes();
        } catch (IOException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    private Response makeRequest(Request request, TwilioRestClient client) {
        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Media download failed: Unable to connect to server");
        } else if (!TwilioRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }
        return response;
    }


    private String findRedirectUrl(Header[] headers) {
        return Arrays.stream(headers)
                .filter(header -> header.getName().equals(REDIRECT_LOCATION_HEADER))
                .findFirst()
                .map(NameValuePair::getValue)
                .orElseThrow(() -> new ApiException("No redirect location"));
    }
}
