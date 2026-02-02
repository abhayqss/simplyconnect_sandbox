package com.scnsoft.eldermark.consana.sync.server.services.gateway.impl;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IBasicClient;
import ca.uhn.fhir.rest.client.interceptor.SimpleRequestHeaderInterceptor;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import com.scnsoft.eldermark.consana.sync.server.consana.auth.ConsanaAuthenticationManager;
import com.scnsoft.eldermark.consana.sync.server.consana.fhir.ConsanaApolloFhirClient;
import com.scnsoft.eldermark.consana.sync.server.consana.fhir.ConsanaFhirClient;
import com.scnsoft.eldermark.consana.sync.server.consana.template.ConsanaRestTemplateService;
import com.scnsoft.eldermark.consana.sync.server.model.entity.IndividualPerson;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.*;
import com.scnsoft.eldermark.consana.sync.server.services.gateway.ConsanaGateway;
import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

@Service
public class ConsanaFhirGatewayImpl implements ConsanaGateway {

    private static final Logger logger = LoggerFactory.getLogger(ConsanaFhirGatewayImpl.class);

    @Value("${consana.endpoint.fhir}")
    private String consanaFhirEndpoint;

    @Value("${consana.endpoint.apollo.fhir}")
    private String consanaApolloFhirEndpoint;

    @Value("${consana.endpoint.apollo.fhir.medication.action.plans}")
    private String consanaMedicationActionPlansFhirEndpoint;

    @Value("${consana.endpoint.apollo.fhir.media}")
    private String consanaMediaFhirEndpoint;

    private final FhirContext fhirContext;
    private final ConsanaAuthenticationManager consanaAuthenticationManager;
    private final RetryTemplate retryTemplate;
    private final ConsanaRestTemplateService consanaRestTemplateService;

    private static final Map<String, String> XCL_ORGANIZATION_CACHE = new ConcurrentHashMap<>();


    @Autowired
    public ConsanaFhirGatewayImpl(FhirContext fhirContext, ConsanaAuthenticationManager consanaAuthenticationManager, RetryTemplate retryTemplate, ConsanaRestTemplateService consanaRestTemplateService) {
        this.fhirContext = fhirContext;
        this.consanaAuthenticationManager = consanaAuthenticationManager;
        this.retryTemplate = retryTemplate;
        this.consanaRestTemplateService = consanaRestTemplateService;
    }

    @Override
    public Patient getPatient(String patientXrefId, String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<Patient, BaseServerResponseException>) retryContext -> {
            var xclOrganizationId = getXCLOrganizationId(xOwningOrgScId);
            return fetchPatient(patientXrefId, xclOrganizationId);
        });
    }

    @Override
    public String getXCLOrganizationId(String xOwningOrgScId) {
        if (XCL_ORGANIZATION_CACHE.containsKey(xOwningOrgScId)) {
            return XCL_ORGANIZATION_CACHE.get(xOwningOrgScId);
        }

        final ConsanaApolloFhirClient apolloFhirClient = fhirContext.newRestfulClient(ConsanaApolloFhirClient.class, consanaApolloFhirEndpoint);
        consanaAuthenticationManager.authenticate(apolloFhirClient);

        var res = apolloFhirClient.getXOwningOrganization(xOwningOrgScId);
        var xclOrgId = ofNullable(res)
                .map(XOwningOrganization::getExternalId)
                .map(PrimitiveType::getValueAsString);

        if (xclOrgId.isPresent()) {
            XCL_ORGANIZATION_CACHE.put(xOwningOrgScId, xclOrgId.get());
            return xclOrgId.get();
        }
        return null;
    }

    @Override
    public XCoverage getXCoverage(Patient patient, String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<XCoverage, BaseServerResponseException>) retryContext -> {
            var xclOrganizationId = getXCLOrganizationId(xOwningOrgScId);
            return fetchXCoverage(patient, xclOrganizationId);
        });
    }

    @Override
    public List<MedicationOrder> getMedicationOrders(Patient patient, String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<List<MedicationOrder>, BaseServerResponseException>) retryContext -> {
            var xclOrganizationId = getXCLOrganizationId(xOwningOrgScId);
            return fetchMedicationOrders(patient, xclOrganizationId);
        });
    }

    @Override
    public Medication getMedication(String id) {
        return retryTemplate.execute((RetryCallback<Medication, BaseServerResponseException>) retryContext ->
                createConsanaFhirClient().getMedication(new IdType(id)));
    }

    @Override
    public List<Condition> getConditions(String patientId, String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<List<Condition>, BaseServerResponseException>) retryContext -> {
            var xclOrganizationId = getXCLOrganizationId(xOwningOrgScId);
            return fetchConditions(patientId, xclOrganizationId);
        });
    }

    @Override
    public List<AllergyIntolerance> getAllergyIntolerances(String patientId, String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<List<AllergyIntolerance>, BaseServerResponseException>) retryContext -> {
            var xclOrganizationId = getXCLOrganizationId(xOwningOrgScId);
            return fetchAllergyIntolerances(patientId, xclOrganizationId);
        });
    }

    @Override
    public List<Encounter> getEncounters(String patientId, String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<List<Encounter>, BaseServerResponseException>) retryContext -> {
            var xclOrganizationId = getXCLOrganizationId(xOwningOrgScId);
            return fetchEncounters(patientId, xclOrganizationId);
        });
    }

    @Override
    public IndividualPerson getIndividualPerson(IIdType idType) {
        DomainResource person = null;
        if ("Practitioner".equals(idType.getResourceType())) {
            person = retryTemplate.execute((RetryCallback<Practitioner, BaseServerResponseException>) retryContext ->
                    createConsanaFhirClient().getPractitioner(idType));
        }
        if ("RelatedPerson".equals(idType.getResourceType())) {
            person = retryTemplate.execute((RetryCallback<RelatedPerson, BaseServerResponseException>) retryContext ->
                    createConsanaFhirClient().getRelatedPerson(idType));
        }
        return new IndividualPerson(person);
    }

    private ConsanaFhirClient createConsanaFhirClient() {
        var consanaFhirClient = fhirContext.newRestfulClient(ConsanaFhirClient.class, consanaFhirEndpoint);
        consanaAuthenticationManager.authenticate(consanaFhirClient);
        return consanaFhirClient;
    }

    @Override
    public List<XMedicationActionPlan> getXMedicationActionPlans(String patientId, String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<List<XMedicationActionPlan>, BaseServerResponseException>) retryContext -> {
            var restTemplate = consanaRestTemplateService.getConsanaRestTemplate();
            var xclOrganizationId = getXCLOrganizationId(xOwningOrgScId);
            HttpEntity<String> entity = new HttpEntity<>(consanaRestTemplateService.createConsanaHttpHeaders(xclOrganizationId));
            ResponseEntity<XMedicationActionPlanWrapper> result = restTemplate
                    .exchange(consanaMedicationActionPlansFhirEndpoint, HttpMethod.GET, entity, XMedicationActionPlanWrapper.class, patientId);
            return result.getBody() != null ? result.getBody().getPlans() : null;
        });
    }

    private String getUrl(String... parts) {
        return String.join("/", Arrays.asList(parts));
    }

    @Override
    public XMedia getXMedia(String mapId, String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<XMedia, BaseServerResponseException>) retryContext -> {
            var restTemplate = consanaRestTemplateService.getConsanaRestTemplate();
            var xclOrganizationId = getXCLOrganizationId(xOwningOrgScId);
            HttpEntity<String> entity = new HttpEntity<>(consanaRestTemplateService.createConsanaHttpHeaders(xclOrganizationId));
            ResponseEntity<XMedia> result = restTemplate.exchange(consanaMediaFhirEndpoint, HttpMethod.GET, entity, XMedia.class, mapId);
            return result.getBody();
        });
    }

    private List<MedicationOrder> fetchMedicationOrders(Patient patient, String xclOrganizationId) {
        var consanaFhirClient = createConsanaFhirClient(xclOrganizationId);
        return consanaFhirClient.getMedicationOrderList(patient.getId());
    }

    private XCoverage fetchXCoverage(Patient patient, String xclOrganizationId) {
        if (xclOrganizationId == null) {
            return null;
        }
        final ConsanaApolloFhirClient apolloFhirClient = fhirContext.newRestfulClient(ConsanaApolloFhirClient.class, consanaApolloFhirEndpoint);
        consanaAuthenticationManager.authenticate(apolloFhirClient);

        registerInterceptor(apolloFhirClient, "x-xcl-organization", xclOrganizationId);
        return apolloFhirClient.getXCoverage(patient.getId());
    }

    private Patient fetchPatient(String patientId, String xclOrganizationId) {
        var consanaFhirClient = createConsanaFhirClient(xclOrganizationId);
        return consanaFhirClient.getPatient(patientId);
    }

    private void registerInterceptor(IBasicClient client, String headerName, String headerValue) {
        client.registerInterceptor(new SimpleRequestHeaderInterceptor(headerName, headerValue));
    }

    private List<Condition> fetchConditions(String patientId, String xclOrganizationId) {
        var consanaFhirClient = createConsanaFhirClient(xclOrganizationId);
        return consanaFhirClient.getConditionList(patientId);
    }

    private List<AllergyIntolerance> fetchAllergyIntolerances(String patientId, String xclOrganizationId) {
        var consanaFhirClient = createConsanaFhirClient(xclOrganizationId);
        return consanaFhirClient.getAllergyIntoleranceList(patientId);
    }

    private List<Encounter> fetchEncounters(String patientId, String xclOrganizationId) {
        var consanaFhirClient = createConsanaFhirClient(xclOrganizationId);
        return consanaFhirClient.getEncounterList(patientId);
    }

    private ConsanaFhirClient createConsanaFhirClient(String xclOrganizationId) {
        if (xclOrganizationId == null) {
            throw new IllegalStateException("xclOrganizationId shouldn't be null!");
        }
        final ConsanaFhirClient consanaFhirClient = fhirContext.newRestfulClient(ConsanaFhirClient.class, consanaFhirEndpoint);
        consanaAuthenticationManager.authenticate(consanaFhirClient);
        registerInterceptor(consanaFhirClient, "x-xcl-organization", xclOrganizationId);
        return consanaFhirClient;
    }

    @Override
    //temporary method
    public Patient getPatientById(String patientId, String xclOrganizationId) {
        final ConsanaFhirClient consanaFhirClient = fhirContext.newRestfulClient(ConsanaFhirClient.class, consanaFhirEndpoint);
        consanaAuthenticationManager.authenticate(consanaFhirClient);
        consanaFhirClient.registerInterceptor(new SimpleRequestHeaderInterceptor("x-xcl-organization", xclOrganizationId));
        return consanaFhirClient.getResourceById(new IdType(patientId));
    }
}
