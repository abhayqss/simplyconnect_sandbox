package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareApiValidationException;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientMatch;
import com.scnsoft.eldermark.dto.pointclickcare.projection.PccClientMatchProjection;
import com.scnsoft.eldermark.entity.Client;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@ConditionalOnProperty(value = "pcc.patientMatch.enabled", havingValue = "true")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class PointClickCarePatientMatchServiceImpl implements PointClickCarePatientMatchService {
    private static final Logger logger = LoggerFactory.getLogger(PointClickCarePatientMatchServiceImpl.class);

    private final PointClickCareApiGateway pointClickCareApiGateway;
    private final ClientDao clientDao;
    private final PccClientToClientMatchProjectionConverter clientToClientMatchProjectionConverter;
    private final PccClientMatchProjectionToPatientMatchCriteriaConverter clientMatchProjectionToPatientMatchCriteriaConverter;
    private final PointClickCareSpecifications pccSpecifications;

    @Autowired
    public PointClickCarePatientMatchServiceImpl(PointClickCareApiGateway pointClickCareApiGateway, ClientDao clientDao, PccClientToClientMatchProjectionConverter clientToClientMatchProjectionConverter, PccClientMatchProjectionToPatientMatchCriteriaConverter clientMatchProjectionToPatientMatchCriteriaConverter, PointClickCareSpecifications pccSpecifications) {
        this.pointClickCareApiGateway = pointClickCareApiGateway;
        this.clientDao = clientDao;
        this.clientToClientMatchProjectionConverter = clientToClientMatchProjectionConverter;
        this.clientMatchProjectionToPatientMatchCriteriaConverter = clientMatchProjectionToPatientMatchCriteriaConverter;
        this.pccSpecifications = pccSpecifications;
    }

    @Override
    public boolean match(Client client) {
        try {
            var patientIdOpt = match(clientToClientMatchProjectionConverter.convert(client));
            patientIdOpt.ifPresent(id -> {
                //do not replace with method reference because if client is null it will cause NPE
                client.setPccPatientId(id);
            });
            return patientIdOpt.isPresent();
        } catch (PointClickCareApiValidationException validationException) {
            logger.info("PointClickCare Patient Match: Did not send patient match to PCC: {}", validationException.getMessage());
        } catch (Exception ex) {
            logger.warn("PointClickCare Patient Match: Failed to send patient match to PCC", ex);
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Map<Long, Long> match(Collection<PccClientMatchProjection> clients) {
        var result = new HashMap<Long, Long>(clients.size());

        for (var client : clients) {
            try {
                match(client).ifPresent(patientId -> result.put(client.getId(), patientId));
            } catch (PointClickCareApiValidationException validationException) {
                logger.info("PointClickCare Patient Match: Did not send patient match to PCC: {}", validationException.getMessage());
            }
        }

        return result;
    }

    private Optional<Long> match(PccClientMatchProjection client) {
        if (client == null) {
            logger.info("PointClickCare Patient Match: Client is null");
            return Optional.empty();
        }
        if (client.getPccPatientId() != null) {
            logger.info("PointClickCare Patient Match: Client already has PCC patient id");
            return Optional.empty();

        }
        if (StringUtils.isEmpty(client.getOrganizationPccOrgUuid())) {
            logger.info("PointClickCare Patient Match: Client's organization doesn't have PCC orgUuid");
            return Optional.empty();

        }
        if (client.getCommunityPccFacilityId() == null) {
            logger.info("PointClickCare Patient Match: Client's community doesn't have PCC facilityId");
            return Optional.empty();
        }

        var matchResponse = pointClickCareApiGateway.patientMatch(
                client.getOrganizationPccOrgUuid(), clientMatchProjectionToPatientMatchCriteriaConverter.convert(client));

        if (matchResponse == null) {
            logger.info("PointClickCare Patient Match: response was null");
            return Optional.empty();
        }

        if (CollectionUtils.isEmpty(matchResponse.getData())) {
            logger.info("PointClickCare Patient Match: no matches found");
            return Optional.empty();
        }

        logger.info("PointClickCare Patient Match: found {} matches", matchResponse.getData().size());

        var result = matchResponse.getData().stream()
                .filter(this::patientIdNotAlreadyAssociated)
                .findFirst()
                .map(match -> {
                    logger.info("PointClickCare Patient Match: Picked matching patient");
                    return match.getPatientId();
                });

        if (result.isEmpty()) {
            logger.info("PointClickCare Patient Match: All matched patients have already been assigned to SC clients");
        }

        return result;
    }

    private boolean patientIdNotAlreadyAssociated(PCCPatientMatch match) {
        return !clientDao.exists(
                pccSpecifications.clientByPccFacilityIdAndPccPatientId(match.getFacId(), match.getPatientId())
        );
    }
}
