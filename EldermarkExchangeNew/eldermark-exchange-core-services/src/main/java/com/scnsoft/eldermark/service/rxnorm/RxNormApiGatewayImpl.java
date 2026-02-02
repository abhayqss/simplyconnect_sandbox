package com.scnsoft.eldermark.service.rxnorm;

import com.scnsoft.eldermark.service.rxnorm.dto.NDCStatus;
import com.scnsoft.eldermark.service.rxnorm.dto.NDCStatusResponse;
import com.scnsoft.eldermark.service.rxnorm.dto.RxNormVersion;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
//todo unit test
public class RxNormApiGatewayImpl implements RxNormApiGateway {

    private final String ndcStatusUrl;
    private final String versionUrl;
    private final RetryTemplate retryTemplate;
    private final RestTemplate restTemplate;

    public RxNormApiGatewayImpl(
            @Qualifier("rxnormRetryTemplate") RetryTemplate retryTemplate,
            @Qualifier("ndcApiRestTemplate") RestTemplate restTemplate,
            @Value("${rxnorm.ndc.status.url}") String ndcStatusUrl,
            @Value("${rxnorm.version.url}") String versionUrl) {
        this.ndcStatusUrl = ndcStatusUrl;
        this.versionUrl = versionUrl;
        this.retryTemplate = retryTemplate;
        this.restTemplate = restTemplate;
    }

    @Override
    public NDCStatus getNDCStatus(String nationalDrugCode) {
        return retryTemplate.execute(retryContext -> {
            var urlBuilder = UriComponentsBuilder.fromHttpUrl(ndcStatusUrl)
                    .queryParam("ndc", nationalDrugCode)
                    .queryParam("history", "1");

            var responseBody = restTemplate.exchange(
                    urlBuilder.toUriString(),
                    HttpMethod.GET,
                    null,
                    NDCStatusResponse.class
            ).getBody();

            if (responseBody == null) {
                return null;
            }
            return responseBody.getNdcStatus();
        });
    }

    @Override
    public String getVersion() {
        return retryTemplate.execute(retryContext -> {
            var responseBody = restTemplate.exchange(
                    versionUrl,
                    HttpMethod.GET,
                    null,
                    RxNormVersion.class
            ).getBody();

            if (responseBody == null) {
                return null;
            }
            return responseBody.getVersion();
        });
    }
}
