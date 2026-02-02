package com.scnsoft.eldermark.consana.sync.server.validator;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;
import com.scnsoft.eldermark.consana.sync.server.dao.OrganizationDao;
import com.scnsoft.eldermark.consana.sync.server.model.ConsanaValidationException;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Organization;
import com.scnsoft.eldermark.consana.sync.server.services.consumers.impl.ReceiveConsanaPatientQueueConsumerImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ReceivePatientDtoValidator {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveConsanaPatientQueueConsumerImpl.class);

    private final OrganizationDao organizationDao;

    @Autowired
    public ReceivePatientDtoValidator(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    public void validate(ReceiveConsanaPatientQueueDto dto) throws ConsanaValidationException {
        var errors = new ArrayList<>();
        if (dto.getConsanaXRefId() == null) {
            errors.add("consanaXRefId cannot be null");
        }
        if (dto.getOrganizationId() == null) {
            errors.add("organizationId cannot be null");
        }
        if (dto.getCommunityId() == null) {
            errors.add("communityId cannot be null");
        }

        if (dto.getUpdateType() == null) {
            errors.add("updateType cannot be null");
        }

        if (ObjectUtils.allNotNull(dto.getOrganizationId(), dto.getCommunityId())) {
            var targetCommunity = organizationDao.getFirstByConsanaOrgIdAndDatabaseConsanaXOwningId(dto.getCommunityId(), dto.getOrganizationId());
            if (isIntegrationDisabled(targetCommunity) && isNotInitialSync(targetCommunity)) {
                errors.add("Consana integration is not enabled for target community");
            }
        }

        if (CollectionUtils.isNotEmpty(errors)) {
            throw new ConsanaValidationException(errors.toString());
        }
    }

    private boolean isIntegrationDisabled(Organization targetCommunity) {
        return BooleanUtils.isNotTrue(targetCommunity.getIsConsanaIntegrationEnabled());
    }

    private boolean isNotInitialSync(Organization targetComunity) {
        return BooleanUtils.isNotTrue(targetComunity.getIsConsanaInitialSync());
    }
}
