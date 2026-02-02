package com.scnsoft.eldermark.consana.sync.server.services.gateway.impl;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.interceptor.SimpleRequestHeaderInterceptor;
import com.scnsoft.eldermark.consana.sync.server.consana.auth.ConsanaAuthenticationManager;
import com.scnsoft.eldermark.consana.sync.server.consana.fhir.ConsanaApolloFhirClient;
import com.scnsoft.eldermark.consana.sync.server.consana.fhir.ConsanaFhirClient;
import com.scnsoft.eldermark.consana.sync.server.consana.template.ConsanaRestTemplateService;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.XOwningOrganization;
import org.hl7.fhir.instance.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class ConsanaFhirGatewayImplTest {

    @Mock
    private ConsanaAuthenticationManager consanaAuthenticationManager;

    @Mock
    private FhirContext fhirContext;

    @Mock
    private ConsanaRestTemplateService consanaRestTemplateService;

    private ConsanaFhirGatewayImpl instance;

    private static final String FHIR_ENDPOINT = "endpoint";
    private static final String APOLLO_FHIR_ENDPOINT = "endpoint1";

    @BeforeEach
    void setup() {
        instance = new ConsanaFhirGatewayImpl(fhirContext, consanaAuthenticationManager, new RetryTemplate(), consanaRestTemplateService);
        ReflectionTestUtils.setField(instance, "consanaFhirEndpoint", FHIR_ENDPOINT);
        ReflectionTestUtils.setField(instance, "consanaApolloFhirEndpoint", APOLLO_FHIR_ENDPOINT);
    }

    @Test
    void getPatient_shouldResolveOrganizationAndGetPatient() {
        var patientId = "111";
        var xOwningOrgScId = "222";
        var consanaApolloFhirClient= Mockito.mock(ConsanaApolloFhirClient.class);
        var consanaFhirClient = Mockito.mock(ConsanaFhirClient.class);
        var xclOrgId = "1";
        XOwningOrganization xOwningOrganization = new XOwningOrganization(xclOrgId, xOwningOrgScId);
        Patient patient = new Patient();

        when(fhirContext.newRestfulClient(ConsanaApolloFhirClient.class, APOLLO_FHIR_ENDPOINT)).thenReturn(consanaApolloFhirClient);
        when(consanaApolloFhirClient.getXOwningOrganization(xOwningOrgScId)).thenReturn(xOwningOrganization);
        when(fhirContext.newRestfulClient(ConsanaFhirClient.class, FHIR_ENDPOINT)).thenReturn(consanaFhirClient);
        when(consanaFhirClient.getPatient(patientId)).thenReturn(patient);

        var result = instance.getPatient(patientId, xOwningOrgScId);

        verify(consanaAuthenticationManager).authenticate(consanaApolloFhirClient);
        verify(consanaAuthenticationManager).authenticate(consanaFhirClient);
        verify(consanaFhirClient).registerInterceptor(argThat(iClientInterceptor ->
                ((SimpleRequestHeaderInterceptor) iClientInterceptor).getHeaderName().equals("x-xcl-organization") &&
                        ((SimpleRequestHeaderInterceptor) iClientInterceptor).getHeaderValue().equals(xclOrgId)

        ));
        assertEquals(patient, result);
    }

//    @Test
//    void getPatient_WhenFailed_ShouldRetry() {
//        var patientId = "111";
//        var orgOid = "222";
//        var consanaApolloFhirClient= Mockito.mock(ConsanaApolloFhirClient.class);
//        var consanaFhirClient = Mockito.mock(ConsanaFhirClient.class);
//        var xclOrgId = "1";
//        XOwningOrganization xOwningOrganization = new XOwningOrganization(xclOrgId, orgOid);
//        Patient patient = new Patient();
//
//        when(fhirContext.newRestfulClient(ConsanaApolloFhirClient.class, APOLLO_FHIR_ENDPOINT)).thenReturn(consanaApolloFhirClient);
//        when(consanaApolloFhirClient.getXOwningOrganization(orgOid)).thenReturn(xOwningOrganization);
//        when(fhirContext.newRestfulClient(ConsanaFhirClient.class, FHIR_ENDPOINT)).thenReturn(consanaFhirClient);
//        when(consanaFhirClient.getPatient(patientId)).thenReturn(patient);
//
//        var result = instance.getPatient(patientId, orgOid);
//
//        verify(consanaAuthenticationManager).authenticate(consanaApolloFhirClient);
//        verify(consanaAuthenticationManager).authenticate(consanaFhirClient);
//        verify(consanaFhirClient).registerInterceptor(argThat(iClientInterceptor ->
//                ((SimpleRequestHeaderInterceptor) iClientInterceptor).getHeaderName().equals("x-xcl-organization") &&
//                        ((SimpleRequestHeaderInterceptor) iClientInterceptor).getHeaderValue().equals(xclOrgId)
//
//        ));
//        assertEquals(patient, result);
//    }

}