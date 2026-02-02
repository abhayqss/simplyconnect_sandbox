package com.scnsoft.eldermark.consana.sync.client.consana;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.interceptor.SimpleRequestHeaderInterceptor;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import com.scnsoft.eldermark.consana.sync.client.consana.auth.ConsanaAuthenticationManager;
import com.scnsoft.eldermark.consana.sync.client.consana.fhir.ConsanaApolloFhirClient;
import com.scnsoft.eldermark.consana.sync.client.consana.fhir.ConsanaFhirClient;
import com.scnsoft.eldermark.consana.sync.client.consana.fhir.model.XOwningOrganization;
import org.hl7.fhir.instance.model.IdType;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.PrimitiveType;
import org.hl7.fhir.instance.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

@Service
public class ConsanaFhirGatewayImpl implements ConsanaGateway {

    private static final Map<String, String> ORGANIZATION_ID_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> EXTERNAL_ORGANIZATION_ID_CACHE = new ConcurrentHashMap<>();

    private final FhirContext fhirContext;

    private final ConsanaAuthenticationManager consanaAuthenticationManager;

    private final RetryTemplate retryTemplate;

    @Value("${consana.endpoint.apollo.fhir}")
    private String consanaApolloFhirEndpoint;

    @Value("${consana.endpoint.fhir}")
    private String consanaFhirEndpoint;

    @Autowired
    public ConsanaFhirGatewayImpl(FhirContext fhirContext, ConsanaAuthenticationManager consanaAuthenticationManager, RetryTemplate retryTemplate) {
        this.fhirContext = fhirContext;
        this.consanaAuthenticationManager = consanaAuthenticationManager;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public String getXCLOrganizationId(String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<String, BaseServerResponseException>) retryContext ->
                ORGANIZATION_ID_CACHE.computeIfAbsent(xOwningOrgScId, c -> {
                    var apolloFhirClient = fhirContext.newRestfulClient(ConsanaApolloFhirClient.class, consanaApolloFhirEndpoint);
                    consanaAuthenticationManager.authenticate(apolloFhirClient);
                    var xOwningOrganization = apolloFhirClient.getXOwningOrganization(xOwningOrgScId);
                    return Optional.ofNullable(xOwningOrganization)
                            .map(Resource::getIdElement)
                            .map(IdType::getIdPart)
                            .orElse(null);
                }));
    }

    private String getXCLOrganizationExternalId(String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<String, BaseServerResponseException>) retryContext ->
                EXTERNAL_ORGANIZATION_ID_CACHE.computeIfAbsent(xOwningOrgScId, c -> {
                    var apolloFhirClient = fhirContext.newRestfulClient(ConsanaApolloFhirClient.class, consanaApolloFhirEndpoint);
                    consanaAuthenticationManager.authenticate(apolloFhirClient);
                    var xOwningOrganization = apolloFhirClient.getXOwningOrganization(xOwningOrgScId);
                    return ofNullable(xOwningOrganization)
                            .map(XOwningOrganization::getExternalId)
                            .map(PrimitiveType::getValueAsString)
                            .orElse(null);
                }));
    }

    @Override
    public Patient getPatient(String patientXrefId, String xOwningOrgScId) {
        return retryTemplate.execute((RetryCallback<Patient, BaseServerResponseException>) retryContext -> {
            var xclOrganizationId = getXCLOrganizationExternalId(xOwningOrgScId);
            return fetchPatient(patientXrefId, xclOrganizationId);
        });
    }

    private Patient fetchPatient(String patientId, String xclOrganizationId) {
        var consanaFhirClient = createConsanaFhirClient(xclOrganizationId);
        return consanaFhirClient.getPatient(patientId);
    }

    private ConsanaFhirClient createConsanaFhirClient(String xclOrganizationId) {
        if (xclOrganizationId == null) {
            throw new IllegalStateException("xclOrganizationId shouldn't be null!");
        }
        var consanaFhirClient = fhirContext.newRestfulClient(ConsanaFhirClient.class, consanaFhirEndpoint);
        consanaAuthenticationManager.authenticate(consanaFhirClient);
        consanaFhirClient.registerInterceptor(new SimpleRequestHeaderInterceptor("x-xcl-organization", xclOrganizationId));
        return consanaFhirClient;
    }
}
