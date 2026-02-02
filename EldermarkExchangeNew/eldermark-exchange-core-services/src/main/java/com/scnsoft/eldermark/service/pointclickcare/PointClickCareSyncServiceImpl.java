package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.dto.pointclickcare.filter.patient.PCCPatientListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatient;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientDetails;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PccPatientList;
import com.scnsoft.eldermark.dto.pointclickcare.projection.IdAndOrganizationPccFacUuidAndPccFacilityIdAware;
import com.scnsoft.eldermark.dto.pointclickcare.projection.PccClientMatchProjection;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@ConditionalOnProperty(value = "pcc.integration.enabled", havingValue = "true")
public class PointClickCareSyncServiceImpl implements PointClickCareSyncService {
    private final Logger logger = LoggerFactory.getLogger(PointClickCareSyncServiceImpl.class);
    static final int PATIENT_LIST_PAGE_SIZE = 100;
    private final PointClickCareApiGateway pointClickCareApiGateway;
    private final ClientDao clientDao;
    private final CommunityDao communityDao;
    private final ClientSpecificationGenerator clientSpecificationGenerator;
    private final PointClickCarePatientMatchService pointClickCarePatientMatchService;
    private final PointClickCarePatientService pointClickCarePatientService;

    public PointClickCareSyncServiceImpl(PointClickCareApiGateway pointClickCareApiGateway, ClientDao clientDao, CommunityDao communityDao, ClientSpecificationGenerator clientSpecificationGenerator, PointClickCarePatientMatchService pointClickCarePatientMatchService, PointClickCarePatientService pointClickCarePatientService) {
        this.pointClickCareApiGateway = pointClickCareApiGateway;
        this.clientDao = clientDao;
        this.communityDao = communityDao;
        this.clientSpecificationGenerator = clientSpecificationGenerator;
        this.pointClickCarePatientMatchService = pointClickCarePatientMatchService;
        this.pointClickCarePatientService = pointClickCarePatientService;
    }


    @Override
    @Transactional
    public void syncCommunity(Long communityId) {
        if (communityId == null) {
            throw new BusinessException("Community id is null");
        }

        var community = communityDao.findById(communityId, IdAndOrganizationPccFacUuidAndPccFacilityIdAware.class)
                .orElseThrow();

        if (community.getPccFacilityId() == null) {
            throw new BusinessException("Can't sync community " + communityId + ": facility id is not set");
        }

        if (StringUtils.isEmpty(community.getOrganizationPccOrgUuid())) {
            throw new BusinessException("Can't sync community " + communityId + ": orgUuid is not set");
        }

        logger.info("PointClickCare Community Sync: Synchronizing community {}", communityId);
        var pccFacility = pointClickCareApiGateway.facilityById(community.getOrganizationPccOrgUuid(), community.getPccFacilityId());
        if (pccFacility == null) {
            throw new BusinessException("Can't sync community " + communityId + ": pcc facility is null by orgUuid="
                    + community.getOrganizationPccOrgUuid() + ", facId=" + community.getPccFacilityId());
        }
        logger.info("PointClickCare Community Sync: Fetched PCC community");

        communityDao.updatePccFields(community.getId(), pccFacility.getCountry(), pccFacility.getTimeZone());
        logger.info("PointClickCare Community Sync: Updated community {} with country = {}, timezone = {}", communityId,
                pccFacility.getCountry(), pccFacility.getTimeZone());

        matchAllExisting(communityId);
        readAllFromPccToSc(community.getOrganizationPccOrgUuid(), community.getPccFacilityId(), List.of(PCCPatientDetails.PATIENT_STATUS_NEW, PCCPatientDetails.PATIENT_STATUS_CURRENT));
    }

    @Override
    public void updateNewPatients(Long communityId) {
        if (communityId == null) {
            throw new BusinessException("Community id is null");
        }

        var community = communityDao.findById(communityId, IdAndOrganizationPccFacUuidAndPccFacilityIdAware.class)
                .orElseThrow();

        if (community.getPccFacilityId() == null) {
            throw new BusinessException("Can't sync community " + communityId + ": facility id is not set");
        }

        if (StringUtils.isEmpty(community.getOrganizationPccOrgUuid())) {
            throw new BusinessException("Can't sync community " + communityId + ": orgUuid is not set");
        }

        logger.info("PointClickCare Community Sync: Synchronizing community {}", communityId);

        if (StringUtils.isAnyEmpty(community.getPccFacilityCountry(), community.getPccFacilityTimezone())) {
            var pccFacility = pointClickCareApiGateway.facilityById(community.getOrganizationPccOrgUuid(), community.getPccFacilityId());
            if (pccFacility == null) {
                throw new BusinessException("Can't sync community " + communityId + ": pcc facility is null by orgUuid="
                        + community.getOrganizationPccOrgUuid() + ", facId=" + community.getPccFacilityId());
            }
            logger.info("PointClickCare Community Sync: Fetched PCC community");

            communityDao.updatePccFields(community.getId(), pccFacility.getCountry(), pccFacility.getTimeZone());
            logger.info("PointClickCare Community Sync: Updated community {} with country = {}, timezone = {}", communityId,
                    pccFacility.getCountry(), pccFacility.getTimeZone());
        } else {
            logger.info("PointClickCare Community Sync: No need to update community fields");
        }

        readAllFromPccToSc(community.getOrganizationPccOrgUuid(), community.getPccFacilityId(), List.of(PCCPatientDetails.PATIENT_STATUS_NEW));
    }

    private void matchAllExisting(Long communityId) {
        logger.info("PointClickCare Community Sync: Matching existing patients in community {}", communityId);

        var clients = clientDao.findAll(
                clientSpecificationGenerator.byCommunityId(communityId),
                PccClientMatchProjection.class
        );
        logger.info("PointClickCare Community Sync: Found {} patients in community {}", clients.size(), communityId);

        var mapping = pointClickCarePatientMatchService.match(clients);
        logger.info("PointClickCare Community Sync: Mapped {}/{} patients", mapping.size(), clients.size());

        mapping.forEach(clientDao::updatePccPatientId);
    }

    private void readAllFromPccToSc(String pccOrgUuid, Long pccFacilityId, List<String> patientStatuses) {
        logger.info("PointClickCare Community Sync: Reading PointClickCare patients to SimplyConnect");
        var filter = new PCCPatientListFilter(pccFacilityId);
        filter.setPatientStatus(patientStatuses);
        var page = 1;
        var pccPatientIds = new ArrayList<Long>();
        PccPatientList pccPatients;
        do {
            pccPatients = pointClickCareApiGateway.listOfPatients(pccOrgUuid, filter, page, PATIENT_LIST_PAGE_SIZE);
            if (pccPatients == null) {
                break;
            }
            if (pccPatients.getData() == null) {
                break;
            }

            pccPatients.getData().stream().map(PCCPatient::getPatientId).forEach(pccPatientIds::add);
            page++;
        } while (pccPatients.getPaging() != null && pccPatients.getPaging().isHasMore());

        logger.info("PointClickCare Community Sync: Total of {} patients are in PointClickCare", pccPatientIds.size());

        var clients = IntStream.range(0, pccPatientIds.size())
                .mapToObj(idx -> saveClient(pccOrgUuid, pccPatientIds.get(idx), idx + 1, pccPatientIds.size()))
                .collect(Collectors.toList());
        clientDao.saveAll(clients);
    }

    private Client saveClient(String pccOrgUuid, Long pccPatientId, int current, int total) {
        logger.info("PointClickCare Community Sync: Syncing patient {}/{}, PCC patient id {}...", current, total, pccPatientId);
        var client = pointClickCarePatientService.createOrUpdateClient(pccOrgUuid, pccPatientId);
        logger.info("PointClickCare Community Sync: Synced patient {}/{}, PCC patient id {}", current, total, pccPatientId);
        return client;
    }
}
