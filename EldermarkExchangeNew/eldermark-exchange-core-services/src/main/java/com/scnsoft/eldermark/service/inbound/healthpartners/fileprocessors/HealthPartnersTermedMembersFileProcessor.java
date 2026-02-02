package com.scnsoft.eldermark.service.inbound.healthpartners.fileprocessors;

import com.scnsoft.eldermark.dto.healthpartners.TermedMembersCSVDto;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersTermedMember;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpTermedMemberProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpTermedMembersFileProcessingSummary;
import com.scnsoft.eldermark.jms.producer.ClientUpdateQueueProducer;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersRecordService;
import com.scnsoft.eldermark.service.inbound.ProcessingSummarySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "healthPartners.integration.enabled",
        havingValue = "true"
)
public class HealthPartnersTermedMembersFileProcessor
        extends BaseHpFileProcessor<HpTermedMemberProcessingSummary, HpTermedMembersFileProcessingSummary, TermedMembersCSVDto> {
    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersTermedMembersFileProcessor.class);

    @Autowired
    private HealthPartnersRecordService recordService;

    @Autowired
    public HealthPartnersTermedMembersFileProcessor(ClientUpdateQueueProducer clientUpdateQueueProducer,
                                                    DocumentEncryptionService documentEncryptionService) {
        super(TermedMembersCSVDto.class, '|', HpFileType.CONSANA_TERMED_MEMBERS, null, clientUpdateQueueProducer, documentEncryptionService);
    }

    private HealthPartnersTermedMember convert(TermedMembersCSVDto termedMembersCSVDto) {
        var claim = new HealthPartnersTermedMember();
        fillBase(termedMembersCSVDto, claim);
        return claim;
    }

    @Override
    protected HpTermedMemberProcessingSummary processRecord(Long fileLogId, TermedMembersCSVDto record, Long communityId) {
        var recordProcessingSummary = new HpTermedMemberProcessingSummary();
        try {
            recordProcessingSummary.setMemberIdentifier(record.getMemberIdentifier());

            var termedRecord = convert(record);
            termedRecord.setHpFileLogId(fileLogId);
            recordService.processTermedMember(termedRecord, communityId);

            recordProcessingSummary.setClientId(termedRecord.getClientId());
            recordProcessingSummary.setUpdateTypes(termedRecord.getUpdateTypes());

            if (termedRecord.isSuccess()) {
                recordProcessingSummary.setClientId(termedRecord.getClientId());
                recordProcessingSummary.setClientIsNew(termedRecord.getClientIsNew());
                recordProcessingSummary.setStatus(ProcessingSummary.ProcessingStatus.OK);
            } else {
                if (termedRecord.getProcessingException() != null) {
                    ProcessingSummarySupport.fillProcessingSummaryErrorFields(recordProcessingSummary, termedRecord.getProcessingException());
                } else {
                    recordProcessingSummary.setStatus(ProcessingSummary.ProcessingStatus.ERROR);
                    recordProcessingSummary.setMessage(termedRecord.getErrorMessage());
                }
            }

        } catch (Exception exception) {
            logger.info("Error during termed client processing", exception);
            ProcessingSummarySupport.fillProcessingSummaryErrorFields(recordProcessingSummary, exception);
        }

        return recordProcessingSummary;
    }
}
