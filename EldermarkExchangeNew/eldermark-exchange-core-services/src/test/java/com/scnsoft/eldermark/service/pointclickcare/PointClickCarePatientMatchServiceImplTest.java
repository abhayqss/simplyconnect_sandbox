package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dto.pointclickcare.filter.patient.PCCPatientFilterExactMatchCriteria;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientMatch;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientMatchResponse;
import com.scnsoft.eldermark.dto.pointclickcare.projection.PccClientMatchProjectionAdapter;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointClickCarePatientMatchServiceImplTest {

    @InjectMocks
    PointClickCarePatientMatchServiceImpl instance;
    @Mock
    private PointClickCareApiGateway pointClickCareApiGateway;
    @Mock
    private ClientDao clientDao;
    @Mock
    private PccClientToClientMatchProjectionConverter clientToClientMatchProjectionConverter;
    @Mock
    private PointClickCareSpecifications pccSpecifications;
    @Mock
    private PccClientMatchProjectionToPatientMatchCriteriaConverter clientMatchProjectionToPatientMatchCriteriaConverter;

    @Test
    void match_clientIsNull_returnsFalse() {
        when(clientToClientMatchProjectionConverter.convert(null)).thenReturn(null);

        assertThat(instance.match((Client) null)).isFalse();


        verifyNoInteractions(clientMatchProjectionToPatientMatchCriteriaConverter);
        verifyNoInteractions(pointClickCareApiGateway);
        verifyNoInteractions(pccSpecifications);
        verifyNoInteractions(clientDao);
    }

    @Test
    void match_clientIsAssigned_returnsFalse() {
        var client = createClient(null, null);
        client.setPccPatientId(222L);

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(new PccClientMatchProjectionAdapter(client));

        assertThat(instance.match(client)).isFalse();
        assertThat(client.getPccPatientId()).isEqualTo(222L);

        verifyNoInteractions(clientMatchProjectionToPatientMatchCriteriaConverter);
        verifyNoInteractions(pointClickCareApiGateway);
        verifyNoInteractions(pccSpecifications);
        verifyNoInteractions(clientDao);
    }


    @Test
    void match_clientOrganizationIsNotPccEnabled_returnsFalse() {
        var client = createClient(null, null);

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(new PccClientMatchProjectionAdapter(client));

        assertThat(instance.match(client)).isFalse();
        assertThat(client.getPccPatientId()).isNull();

        verifyNoInteractions(clientMatchProjectionToPatientMatchCriteriaConverter);
        verifyNoInteractions(pointClickCareApiGateway);
        verifyNoInteractions(pccSpecifications);
        verifyNoInteractions(clientDao);
    }

    @Test
    void match_clientCommunityIsNotPccEnabled_returnsFalse() {
        var orgUuid = "123455554321";
        var client = createClient(orgUuid, null);

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(new PccClientMatchProjectionAdapter(client));

        assertThat(instance.match(client)).isFalse();
        assertThat(client.getPccPatientId()).isNull();

        verifyNoInteractions(clientMatchProjectionToPatientMatchCriteriaConverter);
        verifyNoInteractions(pointClickCareApiGateway);
        verifyNoInteractions(pccSpecifications);
        verifyNoInteractions(clientDao);
    }

    @Test
    void match_PccGatewayThrows_returnsFalse() {
        var orgUuid = "123455554321";
        var facId = 2L;
        var client = createClient(orgUuid, facId);
        var clientProjection = new PccClientMatchProjectionAdapter(client);
        var criteria = new PCCPatientFilterExactMatchCriteria();

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(clientProjection);
        when(clientMatchProjectionToPatientMatchCriteriaConverter.convert(clientProjection)).thenReturn(criteria);
        when(pointClickCareApiGateway.patientMatch(orgUuid, criteria)).thenThrow(RuntimeException.class);

        assertThat(instance.match(client)).isFalse();

        verifyNoInteractions(pccSpecifications);
        verifyNoInteractions(clientDao);
    }

    @Test
    void match_matchResponseIsNull_returnsFalse() {
        var orgUuid = "123455554321";
        var facId = 2L;
        var client = createClient(orgUuid, facId);
        var clientProjection = new PccClientMatchProjectionAdapter(client);
        var criteria = new PCCPatientFilterExactMatchCriteria();

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(clientProjection);
        when(clientMatchProjectionToPatientMatchCriteriaConverter.convert(clientProjection)).thenReturn(criteria);
        when(pointClickCareApiGateway.patientMatch(orgUuid, criteria)).thenReturn(null);

        assertThat(instance.match(client)).isFalse();
        assertThat(client.getPccPatientId()).isNull();

        verifyNoInteractions(pccSpecifications);
        verifyNoInteractions(clientDao);
    }


    @Test
    void match_matchedListNull_returnsFalse() {
        var orgUuid = "123455554321";
        var facId = 2L;
        var client = createClient(orgUuid, facId);
        var clientProjection = new PccClientMatchProjectionAdapter(client);
        var criteria = new PCCPatientFilterExactMatchCriteria();
        var matchResponse = new PCCPatientMatchResponse();

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(clientProjection);
        when(clientMatchProjectionToPatientMatchCriteriaConverter.convert(clientProjection)).thenReturn(criteria);
        when(pointClickCareApiGateway.patientMatch(orgUuid, criteria)).thenReturn(matchResponse);

        assertThat(instance.match(client)).isFalse();
        assertThat(client.getPccPatientId()).isNull();

        verifyNoInteractions(pccSpecifications);
        verifyNoInteractions(clientDao);
    }

    @Test
    void match_matchedListIsEmpty_returnsFalse() {
        var orgUuid = "123455554321";
        var facId = 2L;
        var client = createClient(orgUuid, facId);
        var clientProjection = new PccClientMatchProjectionAdapter(client);
        var criteria = new PCCPatientFilterExactMatchCriteria();
        var matchResponse = new PCCPatientMatchResponse();
        matchResponse.setData(new ArrayList<>());

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(clientProjection);
        when(clientMatchProjectionToPatientMatchCriteriaConverter.convert(clientProjection)).thenReturn(criteria);
        when(pointClickCareApiGateway.patientMatch(orgUuid, criteria)).thenReturn(matchResponse);

        assertThat(instance.match(client)).isFalse();
        assertThat(client.getPccPatientId()).isNull();

        verifyNoInteractions(pccSpecifications);
        verifyNoInteractions(clientDao);
    }

    @Test
    void match_matchedOneAndTaken_returnsFalse() {
        var orgUuid = "123455554321";
        var facId = 2L;
        var client = createClient(orgUuid, facId);
        var clientProjection = new PccClientMatchProjectionAdapter(client);
        var criteria = new PCCPatientFilterExactMatchCriteria();
        var matchResponse = new PCCPatientMatchResponse();
        matchResponse.setData(new ArrayList<>());
        var match1 = new PCCPatientMatch();
        match1.setPatientId(222L);
        match1.setFacId(facId);
        matchResponse.getData().add(match1);
        var specification = (Specification<Client>) mock(Specification.class);

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(clientProjection);
        when(clientMatchProjectionToPatientMatchCriteriaConverter.convert(clientProjection)).thenReturn(criteria);
        when(pointClickCareApiGateway.patientMatch(orgUuid, criteria)).thenReturn(matchResponse);
        when(pccSpecifications.clientByPccFacilityIdAndPccPatientId(facId, 222L)).thenReturn(specification);
        when(clientDao.exists(specification)).thenReturn(true);

        assertThat(instance.match(client)).isFalse();
        assertThat(client.getPccPatientId()).isNull();
    }

    @Test
    void match_matchedOneAndNotTaken_returnsTrueAndSetsPccPatientId() {
        var orgUuid = "123455554321";
        var facId = 2L;
        var client = createClient(orgUuid, facId);
        var clientProjection = new PccClientMatchProjectionAdapter(client);
        var criteria = new PCCPatientFilterExactMatchCriteria();
        var matchResponse = new PCCPatientMatchResponse();
        matchResponse.setData(new ArrayList<>());
        var match1 = new PCCPatientMatch();
        match1.setPatientId(222L);
        match1.setFacId(facId);
        matchResponse.getData().add(match1);
        var specification = (Specification<Client>) mock(Specification.class);

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(clientProjection);
        when(clientMatchProjectionToPatientMatchCriteriaConverter.convert(clientProjection)).thenReturn(criteria);
        when(pointClickCareApiGateway.patientMatch(orgUuid, criteria)).thenReturn(matchResponse);
        when(pccSpecifications.clientByPccFacilityIdAndPccPatientId(facId, match1.getPatientId())).thenReturn(specification);
        when(clientDao.exists(specification)).thenReturn(false);

        assertThat(instance.match(client)).isTrue();
        assertThat(client.getPccPatientId()).isEqualTo(match1.getPatientId());
    }

    @Test
    void match_matchedTwoFirstTaken_returnsTrueAndSetsSecondPccPatientId() {
        var orgUuid = "123455554321";
        var facId = 2L;
        var client = createClient(orgUuid, facId);
        var clientProjection = new PccClientMatchProjectionAdapter(client);
        var criteria = new PCCPatientFilterExactMatchCriteria();
        var matchResponse = new PCCPatientMatchResponse();
        matchResponse.setData(new ArrayList<>());
        var match1 = new PCCPatientMatch();
        match1.setFacId(facId);
        match1.setPatientId(222L);
        matchResponse.getData().add(match1);
        var match2 = new PCCPatientMatch();
        match2.setPatientId(223L);
        match2.setFacId(facId);
        matchResponse.getData().add(match2);
        var specification1 = (Specification<Client>) mock(Specification.class);
        var specification2 = (Specification<Client>) mock(Specification.class);

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(clientProjection);
        when(clientMatchProjectionToPatientMatchCriteriaConverter.convert(clientProjection)).thenReturn(criteria);
        when(pointClickCareApiGateway.patientMatch(orgUuid, criteria)).thenReturn(matchResponse);
        when(pccSpecifications.clientByPccFacilityIdAndPccPatientId(facId, match1.getPatientId())).thenReturn(specification1);
        when(pccSpecifications.clientByPccFacilityIdAndPccPatientId(facId, match2.getPatientId())).thenReturn(specification2);
        when(clientDao.exists(specification1)).thenReturn(true);
        when(clientDao.exists(specification2)).thenReturn(false);

        assertThat(instance.match(client)).isTrue();
        assertThat(client.getPccPatientId()).isEqualTo(match2.getPatientId());
    }

    @Test
    void match_matchedTwoFirstNotTaken_returnsTrueAndSetsFirstPccPatientId() {
        var orgUuid = "123455554321";
        var facId = 2L;
        var client = createClient(orgUuid, facId);
        var clientProjection = new PccClientMatchProjectionAdapter(client);
        var criteria = new PCCPatientFilterExactMatchCriteria();
        var matchResponse = new PCCPatientMatchResponse();
        matchResponse.setData(new ArrayList<>());
        var match1 = new PCCPatientMatch();
        match1.setFacId(facId);
        match1.setPatientId(222L);
        matchResponse.getData().add(match1);
        var match2 = new PCCPatientMatch();
        match2.setPatientId(223L);
        match2.setFacId(facId);
        matchResponse.getData().add(match2);
        var specification1 = (Specification<Client>) mock(Specification.class);

        when(clientToClientMatchProjectionConverter.convert(client)).thenReturn(clientProjection);
        when(clientMatchProjectionToPatientMatchCriteriaConverter.convert(clientProjection)).thenReturn(criteria);
        when(pointClickCareApiGateway.patientMatch(orgUuid, criteria)).thenReturn(matchResponse);
        when(pccSpecifications.clientByPccFacilityIdAndPccPatientId(facId, match1.getPatientId())).thenReturn(specification1);
        when(clientDao.exists(specification1)).thenReturn(false);

        assertThat(instance.match(client)).isTrue();
        assertThat(client.getPccPatientId()).isEqualTo(match1.getPatientId());

        verifyNoMoreInteractions(clientDao);
        verifyNoMoreInteractions(pccSpecifications);
    }


    private Client createClient(String orgUuid, Long facilityId) {
        var organization = new Organization();
        organization.setPccOrgUuid(orgUuid);

        var community = new Community();
        community.setPccFacilityId(facilityId);

        var client = new Client();
        client.setOrganization(organization);
        client.setCommunity(community);

        return client;
    }
}