package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.dto.pointclickcare.model.PCCPagingResponseByPage;
import com.scnsoft.eldermark.dto.pointclickcare.model.facility.PccFacilityDetails;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatient;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PccPatientList;
import com.scnsoft.eldermark.dto.pointclickcare.projection.IdAndOrganizationPccFacUuidAndPccFacilityIdAware;
import com.scnsoft.eldermark.dto.pointclickcare.projection.PccClientMatchProjection;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointClickCareSyncServiceImplTest {

    @InjectMocks
    PointClickCareSyncServiceImpl instance;
    @Mock
    private PointClickCareApiGateway pointClickCareApiGateway;
    @Mock
    private ClientDao clientDao;
    @Mock
    private CommunityDao communityDao;
    @Mock
    private ClientSpecificationGenerator clientSpecificationGenerator;
    @Mock
    private PointClickCarePatientMatchService pointClickCarePatientMatchService;
    @Mock
    private PointClickCarePatientService pointClickCarePatientService;

    @Test
    void syncCommunity_whenNull_throws() {
        assertThrows(BusinessException.class, () -> instance.syncCommunity(null));

        verifyNoInteractions(communityDao);
        verifyNoInteractions(pointClickCareApiGateway);
        verifyNoInteractions(clientDao);
        verifyNoInteractions(clientSpecificationGenerator);
        verifyNoInteractions(pointClickCarePatientMatchService);
        verifyNoInteractions(pointClickCarePatientService);
    }

    @Test
    void syncCommunity_whenNotFoundCommunity_throws() {
        var communityId = 15L;
        var community = createCommunity(communityId, null, null);

        when(communityDao.findById(communityId, IdAndOrganizationPccFacUuidAndPccFacilityIdAware.class))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> instance.syncCommunity(communityId));

        verifyNoInteractions(pointClickCareApiGateway);
        verifyNoInteractions(clientDao);
        verifyNoInteractions(clientSpecificationGenerator);
        verifyNoInteractions(pointClickCarePatientMatchService);
        verifyNoInteractions(pointClickCarePatientService);
    }

    @Test
    void syncCommunity_whenNoPccFacilityId_throws() {
        var communityId = 15L;
        var community = createCommunity(communityId, null, null);

        when(communityDao.findById(communityId, IdAndOrganizationPccFacUuidAndPccFacilityIdAware.class))
                .thenReturn(Optional.of(community));


        assertThrows(BusinessException.class, () -> instance.syncCommunity(communityId));

        verifyNoInteractions(pointClickCareApiGateway);
        verifyNoInteractions(clientDao);
        verifyNoInteractions(clientSpecificationGenerator);
        verifyNoInteractions(pointClickCarePatientMatchService);
        verifyNoInteractions(pointClickCarePatientService);
    }

    @Test
    void syncCommunity_whenNoPccOrgUuid_throws() {
        var communityId = 15L;
        var facilityId = 123L;
        var community = createCommunity(communityId, null, facilityId);

        when(communityDao.findById(communityId, IdAndOrganizationPccFacUuidAndPccFacilityIdAware.class))
                .thenReturn(Optional.of(community));


        assertThrows(BusinessException.class, () -> instance.syncCommunity(communityId));

        verifyNoInteractions(pointClickCareApiGateway);
        verifyNoInteractions(clientDao);
        verifyNoInteractions(clientSpecificationGenerator);
        verifyNoInteractions(pointClickCarePatientMatchService);
        verifyNoInteractions(pointClickCarePatientService);
    }

    @Test
    void syncCommunity_whenPccFacilityNull_throws() {
        var communityId = 15L;
        var orgUuid = "123444321234";
        var facilityId = 123L;
        var community = createCommunity(communityId, orgUuid, facilityId);

        when(communityDao.findById(communityId, IdAndOrganizationPccFacUuidAndPccFacilityIdAware.class))
                .thenReturn(Optional.of(community));
        when(pointClickCareApiGateway.facilityById(orgUuid, facilityId)).thenReturn(null);


        assertThrows(BusinessException.class, () -> instance.syncCommunity(communityId));

        verifyNoInteractions(clientDao);
        verifyNoInteractions(clientSpecificationGenerator);
        verifyNoInteractions(pointClickCarePatientMatchService);
        verifyNoInteractions(pointClickCarePatientService);
    }


    @Test
    void syncCommunity_validData_OneMatchedOneNotMatchedOneCreated() {
        var communityId = 15L;
        var facilityId = 123L;
        var orgUuid = "123444321234";
        var matchedClientId = 1L;
        var matchedPccPatientId = 100L;
        var notMathcedClientId = 2L;
        var newPccPatientId = 101L;
        var community = createCommunity(communityId, orgUuid, facilityId);
        var clientByCommunitySpec = mock(Specification.class);
        var clients = List.of(
                createClientProjection(matchedClientId),
                createClientProjection(notMathcedClientId)
        );
        var createdMatchedClient = new Client();
        var createdNewClient = new Client();
        var pccFacility = new PccFacilityDetails();
        pccFacility.setCountry("USA");
        pccFacility.setTimeZone("timezone");

        //match SC to PCC stage
        when(communityDao.findById(communityId, IdAndOrganizationPccFacUuidAndPccFacilityIdAware.class))
                .thenReturn(Optional.of(community));
        when(pointClickCareApiGateway.facilityById(orgUuid, facilityId)).thenReturn(pccFacility);
        when(clientSpecificationGenerator.byCommunityId(communityId)).thenReturn(clientByCommunitySpec);
        when(clientDao.findAll(clientByCommunitySpec, PccClientMatchProjection.class))
                .thenReturn(clients);
        when(pointClickCarePatientMatchService.match(clients))
                .thenReturn(Map.of(matchedClientId, matchedPccPatientId));
        doNothing().when(clientDao).updatePccPatientId(matchedClientId, matchedPccPatientId);

        //read from PCC to SC stage
        when(pointClickCareApiGateway.listOfPatients(
                eq(orgUuid),
                argThat(pccPatientListFilter -> pccPatientListFilter.getFacId().equals(facilityId)),
                eq(1),
                eq(PointClickCareSyncServiceImpl.PATIENT_LIST_PAGE_SIZE))
        )
                .thenReturn(createPatientList(true, List.of(createPccPatient(matchedPccPatientId))));
        when(pointClickCareApiGateway.listOfPatients(
                eq(orgUuid),
                argThat(pccPatientListFilter -> pccPatientListFilter.getFacId().equals(facilityId)),
                eq(2),
                eq(PointClickCareSyncServiceImpl.PATIENT_LIST_PAGE_SIZE))
        )
                .thenReturn(createPatientList(false, List.of(createPccPatient(newPccPatientId))));
        when(pointClickCarePatientService.createOrUpdateClient(orgUuid, matchedPccPatientId)).thenReturn(createdMatchedClient);
        when(pointClickCarePatientService.createOrUpdateClient(orgUuid, newPccPatientId)).thenReturn(createdNewClient);
        when(clientDao.saveAll(argThat(list ->
                CollectionUtils.isEqualCollection((List<Client>) list, List.of(
                        createdMatchedClient,
                        createdNewClient
                ))
        ))).thenReturn(List.of());

        instance.syncCommunity(communityId);

        verify(communityDao).updatePccFields(communityId, pccFacility.getCountry(), pccFacility.getTimeZone());
    }

    private IdAndOrganizationPccFacUuidAndPccFacilityIdAware createCommunity(Long id, String orgUuid, Long facilityId) {
        return new IdAndOrganizationPccFacUuidAndPccFacilityIdAware() {
            @Override
            public String getPccFacilityCountry() {
                return null;
            }

            @Override
            public String getPccFacilityTimezone() {
                return null;
            }

            @Override
            public Long getId() {
                return id;
            }

            @Override
            public Long getPccFacilityId() {
                return facilityId;
            }

            @Override
            public String getOrganizationPccOrgUuid() {
                return orgUuid;
            }
        };
    }

    private PccClientMatchProjection createClientProjection(Long id) {
        return new PccClientMatchProjection() {
            @Override
            public String getFirstName() {
                return null;
            }

            @Override
            public String getLastName() {
                return null;
            }

            @Override
            public LocalDate getBirthDate() {
                return null;
            }

            @Override
            public String getGenderCodeSystem() {
                return null;
            }

            @Override
            public String getGenderCode() {
                return null;
            }

            @Override
            public String getMedicaidNumber() {
                return null;
            }

            @Override
            public String getMedicareNumber() {
                return null;
            }

            @Override
            public Long getPccPatientId() {
                return null;
            }

            @Override
            public Long getCommunityPccFacilityId() {
                return null;
            }

            @Override
            public Long getId() {
                return id;
            }

            @Override
            public String getOrganizationPccOrgUuid() {
                return null;
            }
        };
    }

    private PccPatientList createPatientList(boolean hasMoreData, List<PCCPatient> data) {
        var result = new PccPatientList();
        result.setData(data);
        result.setPaging(new PCCPagingResponseByPage());
        result.getPaging().setHasMore(hasMoreData);

        return result;
    }

    private PCCPatient createPccPatient(Long pccPatientId) {
        var result = new PCCPatient();
        result.setPatientId(pccPatientId);
        return result;
    }

}