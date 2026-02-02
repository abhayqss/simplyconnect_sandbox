package com.scnsoft.eldermark.service.inbound.healthpartners.fileprocessors;

import com.scnsoft.eldermark.dto.healthpartners.RxClaimCSVDto;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpRxClaimFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpRxClaimProcessingSummary;
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

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

@Service
@ConditionalOnProperty(
        value = "healthPartners.integration.enabled",
        havingValue = "true"
)
public class HealthPartnersRxClaimFileProcessor
        extends BaseHpFileProcessor<HpRxClaimProcessingSummary, HpRxClaimFileProcessingSummary, RxClaimCSVDto> {
    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersRxClaimFileProcessor.class);

    @Autowired
    private HealthPartnersRecordService recordService;

    @Autowired
    public HealthPartnersRxClaimFileProcessor(ClientUpdateQueueProducer clientUpdateQueueProducer,
                                              DocumentEncryptionService documentEncryptionService) {
        super(RxClaimCSVDto.class, '|', HpFileType.CONSANA_RX,
                Comparator.comparing((Function<RxClaimCSVDto, String>) csv -> StringUtils.defaultString(csv.getMemberIdentifier()))
                        .thenComparing(csv -> Optional.ofNullable(csv.getClaimNo()).map(String::length).orElse(0))
                        .thenComparing(csv -> StringUtils.defaultString(csv.getClaimNo())), clientUpdateQueueProducer, documentEncryptionService);
    }

    @Override
    protected HpRxClaimProcessingSummary processRecord(Long fileLogId, RxClaimCSVDto record, Long communityId) {
        var recordProcessingSummary = new HpRxClaimProcessingSummary();
        try {
            recordProcessingSummary.setMemberIdentifier(record.getMemberIdentifier());
            recordProcessingSummary.setClaimNO(record.getClaimNo());
            recordProcessingSummary.setLineNumber(record.getLineNumber());

            var claim = convert(record);
            claim.setHpFileLogId(fileLogId);
            recordService.processRxClaim(claim, communityId);

            recordProcessingSummary.setClientId(claim.getClientId());
            recordProcessingSummary.setUpdateTypes(claim.getUpdateTypes());

            if (claim.isSuccess()) {
                recordProcessingSummary.setDuplicate(claim.getDuplicate());
                recordProcessingSummary.setProcessedAsAdjustment(Boolean.TRUE.equals(claim.getAdjustment()));
                recordProcessingSummary.setMedicationDispenseId(claim.getMedicationDispenseId());
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
            logger.info("Error during RX claim processing", exception);
            ProcessingSummarySupport.fillProcessingSummaryErrorFields(recordProcessingSummary, exception);
        }

        return recordProcessingSummary;
    }

    private HealthPartnersRxClaim convert(RxClaimCSVDto rxClaimCSVDto) {
        var claim = new HealthPartnersRxClaim();
        fillBase(rxClaimCSVDto, claim);

        claim.setLineNumber(rxClaimCSVDto.getLineNumber());
        claim.setDaysSupply(rxClaimCSVDto.getDaysSupply());
        claim.setPrescriberFirstName(nullIfEmpty(rxClaimCSVDto.getPrescriberFirstName()));
        claim.setPrescriberMiddleName(nullIfEmpty(rxClaimCSVDto.getPrescriberMiddleName()));
        claim.setPrescriberLastName(nullIfEmpty(rxClaimCSVDto.getPrescriberLastName()));
        claim.setPrescribingPhysicianNPI(nullIfEmpty(rxClaimCSVDto.getPrescribingPhysicianNPI()));
        claim.setCompoundCode(nullIfEmpty(rxClaimCSVDto.getCompoundCode()));
        claim.setDAWProductSelectionCode(nullIfEmpty(rxClaimCSVDto.getDAWProductSelectionCode()));
        claim.setRefillNumber(rxClaimCSVDto.getRefillNumber());
        claim.setPrescriptionOriginCode(nullIfEmpty(rxClaimCSVDto.getPrescriptionOriginCode()));
        claim.setDrugName(nullIfEmpty(rxClaimCSVDto.getDrugName()));
        claim.setPlanReportedBrandGenericCode(nullIfEmpty(rxClaimCSVDto.getPlanReportedBrandGenericCode()));
        claim.setNationalDrugCode(nullIfEmpty(rxClaimCSVDto.getNationalDrugCode()));
        claim.setServiceDate(Optional.ofNullable(rxClaimCSVDto.getServiceDate())
                .map(sd -> sd.atStartOfDay().atZone(CT_ZONE).toInstant()).orElse(null));
        claim.setClaimNo(nullIfEmpty(rxClaimCSVDto.getClaimNo()));
        claim.setRXNumber(nullIfEmpty(rxClaimCSVDto.getRXNumber()));
        claim.setClaimAdjustedFromIdentifier(nullIfEmpty(rxClaimCSVDto.getClaimAdjustedFromIdentifier()));
        claim.setRelatedClaimRelationship(nullIfEmpty(rxClaimCSVDto.getRelatedClaimRelationship()));
        claim.setQuantityDispensed(rxClaimCSVDto.getQuantityDispensed());
        claim.setQuantityQualifierCode(nullIfEmpty(rxClaimCSVDto.getQuantityQualifierCode()));
        claim.setPharmacyName(nullIfEmpty(rxClaimCSVDto.getPharmacyName()));
        claim.setClaimBillingProvider(nullIfEmpty(rxClaimCSVDto.getClaimBillingProvider()));
        claim.setPharmacyNPI(nullIfEmpty(rxClaimCSVDto.getPharmacyNPI()));

        return claim;
    }
}
