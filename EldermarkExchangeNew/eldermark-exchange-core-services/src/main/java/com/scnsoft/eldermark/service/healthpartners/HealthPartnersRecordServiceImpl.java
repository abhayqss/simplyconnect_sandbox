package com.scnsoft.eldermark.service.healthpartners;

import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersMedClaimDao;
import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersRxClaimDao;
import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersTermedMemberDao;
import com.scnsoft.eldermark.entity.healthpartner.BaseHealthPartnersRecord;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersMedClaim;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersTermedMember;
import com.scnsoft.eldermark.service.healthpartners.processor.med.HealthPartnersMedClaimProcessor;
import com.scnsoft.eldermark.service.healthpartners.processor.rx.HealthPartnersRxClaimProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class HealthPartnersRecordServiceImpl implements HealthPartnersRecordService {
    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersRecordServiceImpl.class);

    @Autowired
    private HealthPartnersRxClaimDao healthPartnersRxClaimDao;

    @Autowired
    private HealthPartnersRxClaimProcessor healthPartnersRxClaimProcessor;

    @Autowired
    private HealthPartnersTermedMemberDao healthPartnersTermedMemberDao;

    @Autowired
    private HealthPartnersTermedMemberProcessor healthPartnersTermedMemberProcessor;

    @Autowired
    private HealthPartnersMedClaimDao healthPartnersMedClaimDao;

    @Autowired
    private HealthPartnersMedClaimProcessor healthPartnersMedClaimProcessor;

    @Override
    @Transactional
    public void processRxClaim(HealthPartnersRxClaim claim, Long communityId) {
        process(claim, communityId,
                this::validateRxClaim,
                healthPartnersRxClaimDao,
                healthPartnersRxClaimProcessor);
    }

    @Override
    @Transactional
    public void processTermedMember(HealthPartnersTermedMember termedMember, Long communityId) {
        process(termedMember, communityId,
                this::validateTermedMember,
                healthPartnersTermedMemberDao,
                healthPartnersTermedMemberProcessor);
    }

    @Override
    @Transactional
    public void processMedClaim(HealthPartnersMedClaim claim, Long communityId) {
        process(claim, communityId,
                this::validateMedClaim,
                healthPartnersMedClaimDao,
                healthPartnersMedClaimProcessor);
    }

    private <T extends BaseHealthPartnersRecord> void process(T record, Long communityId,
                                                              Consumer<T> validator,
                                                              JpaRepository<T, Long> dao,
                                                              HealthPartnersRecordProcessor<T> processor) {
        validator.accept(record);

        if (!record.isSuccess()) {
            save(record, dao);
            return;
        }

        //processor processes in new transaction so that claim is saved even in case of failure
        logger.info("Starting processing record {}", processor.getClass().getSimpleName());
        processor.process(record, communityId);
        logger.info("Processed record");

        save(record, dao);
    }

    private <T extends BaseHealthPartnersRecord> void save(T record, JpaRepository<T, Long> dao) {
        try {
            dao.save(record);
        } catch (Exception e) {
            logger.warn("Failed to insert: {}", record, e);
            throw e;
        }
    }

    private void validateRxClaim(HealthPartnersRxClaim claim) {
        var errors = new ArrayList<String>();

        validateMember(errors, claim);

        //todo other fields after figure out which are optional - refill number, ndc, drug name

        HealthPartnersUtils.updateClaimWithValidationResult(errors, claim);
    }

    private void validateTermedMember(HealthPartnersTermedMember termedMember) {
        var errors = new ArrayList<String>();

        validateMember(errors, termedMember);

        HealthPartnersUtils.updateClaimWithValidationResult(errors, termedMember);
    }

    private void validateMedClaim(HealthPartnersMedClaim claim) {
        var errors = new ArrayList<String>();

        validateMember(errors, claim);

        if (StringUtils.isEmpty(claim.getDiagnosisCode())) {
            errors.add("Diagnosis code is empty");
        }

        //todo more validations

        HealthPartnersUtils.updateClaimWithValidationResult(errors, claim);
    }

    private void validateMember(List<String> errors, BaseHealthPartnersRecord record) {
        if (StringUtils.isEmpty(record.getMemberIdentifier())) {
            errors.add("Member identifier is empty");
        }

        if (StringUtils.isEmpty(record.getMemberFirstName())) {
            errors.add("Member first name is empty");
        }

        if (StringUtils.isEmpty(record.getMemberLastName())) {
            errors.add("Member last name is empty");
        }

        if (record.getBirthDate() == null) {
            errors.add("Birth date is null");
        }
    }
}
