package com.scnsoft.eldermark.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BitlyUrlShortenerServiceImpl implements UrlShortenerService {

    private static final Logger logger = LoggerFactory.getLogger(BitlyUrlShortenerServiceImpl.class);

    @Value("${bitly.token}")
    private String bitlyToken;

    @Value("${bitly.group.id}")
    private String bitlyGroupId;

    @Value("${bitly.shorten.url}")
    private String bitlyUrl;

    @Autowired
    @Qualifier("jsonRestTemplateBuilder")
    private RestTemplateBuilder restTemplateBuilder;

    @Override
    public String getShortUrl(String longUrl) {
        try {

            var requestPayload = prepareRequest(longUrl);

            logger.info("Sending request to bitly");
            var response = restTemplateBuilder.build().postForEntity(bitlyUrl, requestPayload, BitlyResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Bitly request succeded");
                return response.getBody().getLink();
            }

            logger.info("Bitly request failed with status {} {}", response.getStatusCode().value(), response.getStatusCode().getReasonPhrase());
            return longUrl;

        } catch (Exception e) {
            logger.error("Error communication with Bitly services", e);
            return longUrl;
        }
    }

    private HttpEntity<BitlyRequestPayload> prepareRequest(String longUrl) {
        //Bitly doesn't support url's like localhost:8080, so local URL's will not work
        var payload = new BitlyRequestPayload(longUrl, bitlyGroupId);

        return new HttpEntity<>(payload, requestHeaders());
    }

    private HttpHeaders requestHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, bitlyToken);
        return headers;
    }

    private static class BitlyRequestPayload {
        private String long_url;
        private String group_guid;

        public BitlyRequestPayload(String long_url, String group_guid) {
            this.long_url = long_url;
            this.group_guid = group_guid;
        }

        public String getLong_url() {
            return long_url;
        }

        public String getGroup_guid() {
            return group_guid;
        }
    }

    private static class BitlyResponse {
        private String link;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
