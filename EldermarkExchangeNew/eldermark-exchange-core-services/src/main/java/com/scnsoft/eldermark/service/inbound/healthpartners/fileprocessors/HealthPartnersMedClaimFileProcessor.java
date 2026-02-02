package com.scnsoft.eldermark.service.inbound.healthpartners.fileprocessors;

import com.scnsoft.eldermark.dto.healthpartners.MedClaimCSVDto;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersMedClaim;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpMedClaimFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpMedClaimProcessingSummary;
import com.scnsoft.eldermark.jms.producer.ClientUpdateQueueProducer;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersRecordService;
import com.scnsoft.eldermark.service.inbound.ProcessingSummarySupport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

@Service
@ConditionalOnProperty(
        value = "healthPartners.integration.enabled",
        havingValue = "true"
)
public class HealthPartnersMedClaimFileProcessor
        extends BaseHpFileProcessor<HpMedClaimProcessingSummary, HpMedClaimFileProcessingSummary, MedClaimCSVDto> {
    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersMedClaimFileProcessor.class);
    private static final ZoneId CT_ZONE = ZoneId.of("America/Chicago");

    @Autowired
    private HealthPartnersRecordService recordService;

    @Autowired
    public HealthPartnersMedClaimFileProcessor(ClientUpdateQueueProducer clientUpdateQueueProducer,
                                               DocumentEncryptionService documentEncryptionService) {
        super(MedClaimCSVDto.class, '|', HpFileType.CONSANA_MEDICAL,
                Comparator.comparing((Function<MedClaimCSVDto, String>) csv -> StringUtils.defaultString(csv.getMemberIdentifier()))
                        .thenComparing(csv -> Optional.ofNullable(csv.getClaimNo()).map(String::length).orElse(0))
                        .thenComparing(csv -> StringUtils.defaultString(csv.getClaimNo()))
                        .thenComparing(csv -> StringUtils.defaultString(csv.getDiagnosisCode())), clientUpdateQueueProducer, documentEncryptionService);
    }

    @Override
    protected HpMedClaimProcessingSummary processRecord(Long fileLogId, MedClaimCSVDto record, Long communityId) {
        var recordProcessingSummary = new HpMedClaimProcessingSummary();
        try {
            recordProcessingSummary.setMemberIdentifier(record.getMemberIdentifier());
            recordProcessingSummary.setClaimNO(record.getClaimNo());
            recordProcessingSummary.setDiagnosisCode(record.getDiagnosisCode());
            recordProcessingSummary.setLineNumber(record.getLineNumber());

            var claim = convert(record);
            claim.setHpFileLogId(fileLogId);
            recordService.processMedClaim(claim, communityId);

            recordProcessingSummary.setClientId(claim.getClientId());
            recordProcessingSummary.setUpdateTypes(claim.getUpdateTypes());

            if (claim.isSuccess()) {
                recordProcessingSummary.setDuplicate(claim.getDuplicate());
                recordProcessingSummary.setProblemObservationId(claim.getProblemObservationId());
                recordProcessingSummary.setStatus(ProcessingSummary.ProcessingStatus.OK);
            } else {
                if (claim.getProcessingException() != null) {
                    ProcessingSummarySupport.fillProcessingSummaryErrorFields(recordProcessingSummary, claim.getProcessingException());
                } else {
                    recordProcessingSummary.setStatus(ProcessingSummary.ProcessingStatus.ERROR);
                    recordProcessingSummary.setMessage(claim.getErrorMessage());
                }
            }

        } catch (Exception exception) {
            logger.info("Error during med claim processing", exception);
            ProcessingSummarySupport.fillProcessingSummaryErrorFields(recordProcessingSummary, exception);
        }

        return recordProcessingSummary;
    }

    private HealthPartnersMedClaim convert(MedClaimCSVDto medClaimCSVDto) {
        var claim = new HealthPartnersMedClaim();
        fillBase(medClaimCSVDto, claim);

        claim.setLineNumber(medClaimCSVDto.getLineNumber());
        claim.setClaimNo(nullIfEmpty(medClaimCSVDto.getClaimNo()));
        claim.setServiceDate(Optional.ofNullable(medClaimCSVDto.getServiceDate())
                .map(dt -> dt.atStartOfDay(CT_ZONE).toInstant())
                .orElse(null));
        claim.setIcdVersion(medClaimCSVDto.getIcdVersion());
        claim.setDiagnosisCode(nullIfEmpty(medClaimCSVDto.getDiagnosisCode()));
        claim.setDiagnosisTxt(nullIfEmpty(medClaimCSVDto.getDiagnosisTxt()));
        claim.setPhysicianFirstName(nullIfEmpty(medClaimCSVDto.getPhysicianFirstName()));
        claim.setPhysicianMiddleName(nullIfEmpty(medClaimCSVDto.getPhysicianMiddleName()));
        claim.setPhysicianLastName(nullIfEmpty(medClaimCSVDto.getPhysicianLastName()));

        return claim;
    }
}
