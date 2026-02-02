package com.scnsoft.eldermark.service.medispan;

import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.medispan.dto.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class MediSpanApiGatewayImpl implements MediSpanApiGateway {

    private final RestTemplate restTemplate;
    private final String mediSpanDoseFormsUrl;
    private final String mediSpanRoutesUrl;
    private final String mediSpanDispensableDrugsUrl;
    private final String mediSpanPackagedDrugsUrl;
    private final String mediSpanRoutedDrugsUrl;

    public MediSpanApiGatewayImpl(
            @Qualifier("jsonRestTemplateBuilder") RestTemplateBuilder restTemplateBuilder,
            @Value("${medispan.api.url.doseforms}") String mediSpanDoseFormsUrl,
            @Value("${medispan.api.url.routes}") String mediSpanRoutesUrl,
            @Value("${medispan.api.url.dispensabledrugs}") String mediSpanDispensableDrugsUrl,
            @Value("${medispan.api.url.packageddrugs}") String mediSpanPackagedDrugsUrl,
            @Value("${medispan.api.url.routeddrugs}") String mediSpanRoutedDrugsUrl,
            @Value("${medispan.api.user}") String mediSpanUser,
            @Value("${medispan.api.password}") String mediSpanPassword
    ) {
        this.restTemplate = restTemplateBuilder.build();

        this.mediSpanDoseFormsUrl = mediSpanDoseFormsUrl;
        this.mediSpanRoutesUrl = mediSpanRoutesUrl;
        this.mediSpanDispensableDrugsUrl = mediSpanDispensableDrugsUrl;
        this.mediSpanPackagedDrugsUrl = mediSpanPackagedDrugsUrl;
        this.mediSpanRoutedDrugsUrl = mediSpanRoutedDrugsUrl;

        this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(mediSpanUser, mediSpanPassword));
    }

    @Override
    public MediSpanResponse<MediSpanDoseForm> getDoseForms(MediSpanRequest request) {

        return executeRequest(
                mediSpanDoseFormsUrl,
                request,
                MediSpanDoseFormResponse.class
        ).getBody();
    }

    @Override
    public MediSpanResponse<MediSpanDispensableDrug> getDispensableDrugs(MediSpanRequest request) {
        return executeRequest(
                mediSpanDispensableDrugsUrl,
                request,
                MediSpanDispensableDrugResponse.class
        ).getBody();
    }

    @Override
    public MediSpanResponse<MediSpanRoutedDrug> getRoutedDrugs(MediSpanRequest request) {
        return executeRequest(
                mediSpanRoutedDrugsUrl,
                request,
                MediSpanRoutedDrugResponse.class
        ).getBody();
    }

    @Override
    public MediSpanResponse<MediSpanPackagedDrug> getPackagedDrugs(MediSpanRequest request) {
        return executeRequest(
                mediSpanPackagedDrugsUrl,
                request,
                MediSpanPackagedDrugResponse.class
        ).getBody();
    }

    @Override
    public MediSpanResponse<MediSpanRoute> getRoutes(MediSpanRequest request) {
        return executeRequest(
                mediSpanRoutesUrl,
                request,
                MediSpanRouteResponse.class
        ).getBody();
    }

    private <T extends MediSpanResponse<?>> ResponseEntity<T> executeRequest(
            String url,
            MediSpanRequest request,
            Class<T> responseType
    ) {
        var response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request),
                responseType
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new InternalServerException(
                    InternalServerExceptionType.MEDI_SPAN_ERROR,
                    "Unexpected response status code [" + response.getStatusCodeValue() + "] from Medi-Span"
            );
        } else if (Objects.requireNonNull(response.getBody()).getProcessingSuccessful().equals("false")) {
            throw new InternalServerException(
                    InternalServerExceptionType.MEDI_SPAN_ERROR,
                    "Request is not processed at Medi-Span: " + response.getBody().getValidationMessages()
            );
        } else {
            return response;
        }
    }

    private final static class MediSpanDoseFormResponse extends MediSpanResponse<MediSpanDoseForm> {
    }

    private final static class MediSpanRouteResponse extends MediSpanResponse<MediSpanRoute> {
    }

    private final static class MediSpanDispensableDrugResponse extends MediSpanResponse<MediSpanDispensableDrug> {
    }

    private final static class MediSpanPackagedDrugResponse extends MediSpanResponse<MediSpanPackagedDrug> {
    }

    private final static class MediSpanRoutedDrugResponse extends MediSpanResponse<MediSpanRoutedDrug> {
    }
}
