package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareApiException;
import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareApiValidationException;
import com.scnsoft.eldermark.dto.pointclickcare.filter.PccGetParamsFilter;
import com.scnsoft.eldermark.dto.pointclickcare.filter.adt.PccAdtListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.filter.patient.PCCPatientFilterExactMatchCriteria;
import com.scnsoft.eldermark.dto.pointclickcare.filter.patient.PCCPatientListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.filter.webhook.PccWebhookSubscriptionListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.model.adt.PccADTRecordDetailsList;
import com.scnsoft.eldermark.dto.pointclickcare.model.facility.PccFacilityDetails;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientDetails;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientMatchResponse;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PccPatientList;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPostWebhookSubscription;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPostWebhookSubscriptionResponse;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPublicGetWebhookSubscriptionList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Conditional(PccIntegrationOrPatientMatchEnabledCondition.class)
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
class PointClickCareApiGatewayImpl implements PointClickCareApiGateway {
    private static final Logger logger = LoggerFactory.getLogger(PointClickCareApiGatewayImpl.class);

    final static String facilityByIdEndpoint = "/public/preview1/orgs/{orgUuid}/facs/{facId}";
    final static String patientByIdEndpoint = "/public/preview1/orgs/{orgUuid}/patients/{patientId}";
    final static String patientMatchEndpoint = "/public/preview1/orgs/{orgUuid}/patients/match";
    final static String patientListEndpoint = "/public/preview1/orgs/{orgUuid}/patients";
    final static String webhookSubscriptionsEndpoint = "/public/preview1/webhook-subscriptions";
    final static String adtRecordsListEndpoint = "/public/preview1/orgs/{orgUuid}/adt-records";

    private static final int MIN_PAGE = 1;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 200;

    private static final String REQUEST_LIMIT_HEADER = "X-Quota-Limit";
    private static final String REMAINING_REQUESTS_HEADER = "X-Quota-Remaining";
    private static final String RESET_TIME_HEADER = "X-Quota-Time-To-Reset";

    private final static Lock requestLock = new ReentrantLock();
    private final static int REQUEST_DELAY_MILLIS = 30;

    private final Long HOUR_AND_MINUTE_MILLIS = 60 * 61 * 1000L;
    private final RestTemplate restTemplate;
    private final String host;

    private final PointClickCareNotificationService notificationService;

    private final List<Integer> sendDailyRateNotificationsThresholds;

    private volatile Instant lastRequestDatetime;

    private boolean stopApiCalls = false;
    private Instant resetStopAt;


    public PointClickCareApiGatewayImpl(PointClickCareAuthenticationTokenManager authenticationManager,
                                        @Qualifier("pccRestTemplateBuilder") RestTemplateBuilder restTemplateBuilder,
                                        @Value("${pcc.api.host}") String host,
                                        PointClickCareNotificationService notificationService,
                                        @Value("#{'${pcc.dailyRate.notification.thresholds}'.split(',')}") List<Integer> sendDailyRateNotificationsThresholds) {
        this.notificationService = notificationService;
        this.sendDailyRateNotificationsThresholds = sendDailyRateNotificationsThresholds;
        this.restTemplate = restTemplateBuilder
                .additionalInterceptors((httpRequest, bytes, clientHttpRequestExecution) -> {
                    httpRequest.getHeaders().add("Authorization", "Bearer " + authenticationManager.getBearerToken());
                    return clientHttpRequestExecution.execute(httpRequest, bytes);
                })
                .build();
        this.host = host;
    }

    @Override
    public PccFacilityDetails facilityById(String orgUuid, Long facilityId) {
        validateFacilityByIdInput(orgUuid, facilityId);

        return executeGet(
                facilityByIdEndpoint,
                Map.of(
                        "orgUuid", orgUuid,
                        "facId", facilityId),
                PccFacilityDetails.class
        );
    }

    private void validateFacilityByIdInput(String orgUuid, Long facilityId) {
        var missingParams = new ArrayList<>();

        if (StringUtils.isEmpty(orgUuid)) {
            missingParams.add("orgUuid");

        }
        if (facilityId == null) {
            missingParams.add("facilityId");
        }

        if (!missingParams.isEmpty()) {
            throw new PointClickCareApiValidationException("Missing required params " + missingParams);
        }
    }

    @Override
    public PCCPatientDetails patientById(String orgUuid, Long patientId) {
        validatePatientByIdInput(orgUuid, patientId);

        return executeGet(
                patientByIdEndpoint,
                Map.of(
                        "orgUuid", orgUuid,
                        "patientId", patientId),
                PCCPatientDetails.class
        );
    }

    private void validatePatientByIdInput(String orgUuid, Long patientId) {
        var missingParams = new ArrayList<>();

        if (StringUtils.isEmpty(orgUuid)) {
            missingParams.add("orgUuid");

        }
        if (patientId == null) {
            missingParams.add("patientId");
        }

        if (!missingParams.isEmpty()) {
            throw new PointClickCareApiValidationException("Missing required params " + missingParams);
        }
    }

    @Override
    public PCCPatientMatchResponse patientMatch(String orgUuid, PCCPatientFilterExactMatchCriteria matchCriteria) {
        validatePatientMatchInput(orgUuid, matchCriteria);

        return executePost(patientMatchEndpoint, Map.of("orgUuid", orgUuid), matchCriteria, PCCPatientMatchResponse.class);
    }

    private void validatePatientMatchInput(String orgUuid, PCCPatientFilterExactMatchCriteria matchCriteria) {
        var missingParams = new ArrayList<>();

        if (StringUtils.isEmpty(orgUuid)) {
            missingParams.add("orgUuid");
        }

        if (matchCriteria == null) {
            missingParams.add("actual criteria body");
        } else {
            if (matchCriteria.getFacId() == null) {
                missingParams.add("facId");
            }

            if (StringUtils.isEmpty(matchCriteria.getFirstName())) {
                missingParams.add("firstName");
            }

            if (matchCriteria.getGender() == null) {
                missingParams.add("gender");
            }

            if (StringUtils.isEmpty(matchCriteria.getLastName())) {
                missingParams.add("lastName");
            }

            if (matchCriteria.getBirthDate() == null) {
                missingParams.add("birthDate");
            }
        }

        if (!missingParams.isEmpty()) {
            throw new PointClickCareApiValidationException("Missing required params " + missingParams);
        }
    }

    @Override
    public PccPatientList listOfPatients(String orgUuid, PCCPatientListFilter filter, int page, int pageSize) {
        validateListOfPatientsInput(orgUuid, filter, page, pageSize);
        return executeGet(patientListEndpoint, Map.of("orgUuid", orgUuid), filter, page, pageSize, PccPatientList.class);
    }

    private void validateListOfPatientsInput(String orgUuid, PCCPatientListFilter filter, int page, int pageSize) {
        var missingParams = new ArrayList<>();

        if (StringUtils.isEmpty(orgUuid)) {
            missingParams.add("orgUuid");
        }

        if (filter == null) {
            missingParams.add("filtering params");
        } else {
            if (filter.getFacId() == null) {
                missingParams.add("facId");
            }
        }

        if (!missingParams.isEmpty()) {
            throw new PointClickCareApiValidationException("Missing required params " + missingParams);
        }

        validatePaginationParams(page, pageSize);
    }

    private void validatePaginationParams(int page, int pageSize) {
        if (page < MIN_PAGE) {
            throw new PointClickCareApiValidationException("Invalid page (should be >=" + MIN_PAGE + "): " + page);
        }

        if (pageSize < MIN_PAGE_SIZE || pageSize > MAX_PAGE_SIZE) {
            throw new PointClickCareApiValidationException("Invalid pageSize (should be between " + MIN_PAGE_SIZE + " and "
                    + MAX_PAGE_SIZE + "): " + pageSize);
        }
    }

    @Override
    public PccPublicGetWebhookSubscriptionList listOfWebhookSubscriptions(PccWebhookSubscriptionListFilter filter, int page, int pageSize) {
        validateListOfWebhookSubscriptionsInput(filter, page, pageSize);
        return executeGet(webhookSubscriptionsEndpoint, Map.of(), filter, page, pageSize, PccPublicGetWebhookSubscriptionList.class);
    }

    private void validateListOfWebhookSubscriptionsInput(PccWebhookSubscriptionListFilter filter, int page, int pageSize) {
        var missingParams = new ArrayList<>();

        if (filter == null) {
            missingParams.add("filtering params");
        } else {
            if (StringUtils.isEmpty(filter.getApplicationName())) {
                missingParams.add("applicationName");
            }
        }

        if (!missingParams.isEmpty()) {
            throw new PointClickCareApiValidationException("Missing required params " + missingParams);
        }

        validatePaginationParams(page, pageSize);
    }

    @Override
    public PccPostWebhookSubscriptionResponse subscribeWebhook(PccPostWebhookSubscription body) {
        validateSubscribeWebhookInput(body);
        return executePost(webhookSubscriptionsEndpoint, Map.of(), body, PccPostWebhookSubscriptionResponse.class);
    }

    private void validateSubscribeWebhookInput(PccPostWebhookSubscription body) {
        var missingParams = new ArrayList<>();

        if (body == null) {
            missingParams.add("actual body");
        } else {
            if (StringUtils.isEmpty(body.getEndUrl())) {
                missingParams.add("endUrl");
            }
            if (CollectionUtils.isEmpty(body.getEventGroupList())) {
                missingParams.add("eventGroupList");
            }
        }

        if (!missingParams.isEmpty()) {
            throw new PointClickCareApiValidationException("Missing required params " + missingParams);
        }
    }

    @Override
    public PccADTRecordDetailsList adtList(String orgUuid, PccAdtListFilter filter, int page, int pageSize) {
        validateAdtListInput(orgUuid, filter, page, pageSize);
        if (CollectionUtils.isNotEmpty(filter.getAdtRecordIds())) {
            return executeGet(adtRecordsListEndpoint, Map.of("orgUuid", orgUuid), filter, null, null, PccADTRecordDetailsList.class);
        } else {
            return executeGet(adtRecordsListEndpoint, Map.of("orgUuid", orgUuid), filter, page, pageSize, PccADTRecordDetailsList.class);
        }
    }

    private void validateAdtListInput(String orgUuid, PccAdtListFilter filter, int page, int pageSize) {
        var missingParams = new ArrayList<>();

        if (StringUtils.isEmpty(orgUuid)) {
            missingParams.add("orgUuid");
        }

        if (filter == null) {
            missingParams.add("filtering params");
        } else {
            if (CollectionUtils.isEmpty(filter.getAdtRecordIds())) {
                if (filter.getPatientId() == null) {
                    missingParams.add("patientId");
                }
            } else {
                if (filter.getFacId() == null) {
                    missingParams.add("facId");
                }

                if (filter.getPatientId() != null) {
                    throw new PointClickCareApiValidationException("patientId should not be present when adtRecordIds is provided");

                }
            }
        }

        if (!missingParams.isEmpty()) {
            throw new PointClickCareApiValidationException("Missing required params " + missingParams);
        }

        validatePaginationParams(page, pageSize);
    }

    private <R> R executeGet(String endpoint,
                             Map<String, Object> pathVariables,
                             Class<R> responseClass) {
        return executeGet(endpoint, pathVariables, null, null, null, responseClass);
    }

    private <R> R executeGet(String endpoint,
                             Map<String, Object> pathVariables,
                             PccGetParamsFilter filter,
                             Integer page,
                             Integer pageSize,
                             Class<R> responseClass) {
        HttpHeaders headers = prepareHeaders();
        var entity = new HttpEntity<>(headers);

        var componentsBuilder = UriComponentsBuilder.fromHttpUrl(host + endpoint);

        if (filter != null) {
            componentsBuilder = componentsBuilder.queryParams(filter.toParamsMap());
        }

        if (page != null && pageSize != null) {
            componentsBuilder = componentsBuilder
                    .queryParam("page", page)
                    .queryParam("pageSize", pageSize);
        }

        UriComponents builder = componentsBuilder.build();

        return execute(HttpMethod.GET, builder.toUriString(), pathVariables, entity, responseClass);
    }

    private <R, B> R executePost(String endpoint,
                                 Map<String, Object> pathVariables,
                                 B body,
                                 Class<R> responseClass) {

        HttpHeaders headers = prepareHeaders();
        var entity = new HttpEntity<>(body, headers);

        return execute(HttpMethod.POST, host + endpoint, pathVariables, entity, responseClass);
    }

    private <R> R execute(HttpMethod method,
                          String url,
                          Map<String, Object> pathVariables,
                          HttpEntity<?> entity,
                          Class<R> responseClass) {
        try {
            requestLock.lock();
            logger.info("PointClickCare API: Executing {} {}", method.name(), url);

            resetIfResetDatetimeReached();
            if (stopApiCalls) {
                throw new PointClickCareApiException("Reached daily threshold, reset at " + resetStopAt);
            }

            enforceRateLimitPerSecond();


            ResponseEntity<R> response;
            try {
                response = restTemplate.exchange(url,
                        method,
                        entity,
                        responseClass,
                        pathVariables
                );
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                    //possible when daily limit was reached in another module
                    stopApiCalls = true;

                    //possible when application was restarted and daily threshold already reached
                    if (resetStopAt == null) {
                        resetStopAt = Instant.now().truncatedTo(ChronoUnit.HOURS).plusMillis(HOUR_AND_MINUTE_MILLIS);
                    }
                }
                throw new PointClickCareApiException(e);
            } catch (Exception e) {
                throw new PointClickCareApiException(e);
            }

            lastRequestDatetime = Instant.now();

            processDailyRateResponseHeaders(response.getHeaders());

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("PointClickCare API: Request {} {} successful", method.name(), url);
                return response.getBody();
            } else {
                throw new PointClickCareApiException("Request failed with status code " + response.getStatusCodeValue());
            }
        } catch (InterruptedException e) {
            throw new PointClickCareApiException("Error during rate limit wait", e);
        } catch (PointClickCareApiException e) {
            notificationService.sendApiError(e, method, url, pathVariables);
            throw e;
        } finally {
            requestLock.unlock();
        }
    }

    private void enforceRateLimitPerSecond() throws InterruptedException {
        if (lastRequestDatetime != null && Instant.now().minusMillis(REQUEST_DELAY_MILLIS).isBefore(lastRequestDatetime)) {
            Thread.sleep(REQUEST_DELAY_MILLIS);
        }
    }

    private void processDailyRateResponseHeaders(HttpHeaders headers) {
        var limitStr = headers.getFirst(REQUEST_LIMIT_HEADER);
        var remainingStr = headers.getFirst(REMAINING_REQUESTS_HEADER);
        var resetStr = headers.getFirst(RESET_TIME_HEADER);

        logger.info("PCC daily rate headers: {} out of {} requests, reset at {}", remainingStr, limitStr, resetStr);

        if (StringUtils.isNotEmpty(resetStr) && StringUtils.isNotEmpty(remainingStr) && StringUtils.isNotEmpty(limitStr)) {
            var limit = Integer.parseInt(limitStr);
            var remaining = Integer.parseInt(remainingStr);
            resetStopAt = Instant.ofEpochMilli(Long.parseLong(resetStr));

            var percentsRemaining = (int) Math.ceil(((double) remaining / limit) * 100);

            if (sendDailyRateNotificationsThresholds.contains(percentsRemaining)) {
                notificationService.sendDailyThresholdAboutToHit(percentsRemaining, remaining, limit, resetStopAt);
            }

            if (remaining == 0) {
                stopApiCalls = true;
                notificationService.sendDailyThresholdReached(resetStopAt);
            }
        }
    }

    private void resetIfResetDatetimeReached() {
        if (resetStopAt != null && resetStopAt.isBefore(Instant.now())) {
            stopApiCalls = false;
            notificationService.resetDailyThreshold(resetStopAt);
        }
    }

    private HttpHeaders prepareHeaders() {
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
