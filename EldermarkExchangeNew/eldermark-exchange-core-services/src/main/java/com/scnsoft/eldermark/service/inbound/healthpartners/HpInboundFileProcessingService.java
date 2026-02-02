package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersFileLogDao;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersFileLog;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersFileProcessedEvent;
import com.scnsoft.eldermark.service.inbound.AbstractInboundFilesProcessingService;
import com.scnsoft.eldermark.service.inbound.InboundFileGateway;
import com.scnsoft.eldermark.service.inbound.healthpartners.fileprocessors.HealthPartnersFileProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.util.List;

@Service
@ConditionalOnProperty(
        value = "healthPartners.integration.enabled",
        havingValue = "true"
)
public class HpInboundFileProcessingService
        extends AbstractInboundFilesProcessingService<File, HpFileProcessingSummary<?>> {
    private static final Logger logger = LoggerFactory.getLogger(HpInboundFileProcessingService.class);

    @Autowired
    private InboundFileGateway<File, HpFileProcessingSummary<?>> inboundFileGateway;

    @Autowired
    private List<HealthPartnersFileProcessor> fileProcessors;

    @Autowired
    private HpFileTargetResolver hpFileTargetResolver;

    @Autowired
    private HealthPartnersFileLogDao healthPartnersFileLogDao;

    @Autowired
    private HpFileNameSupport hpFileNameSupport;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    //todo consider other ways to pass file log id to afterProcessingStatusError
    private final ThreadLocal<Long> fileLogIdHolder = new ThreadLocal<>();

    @Override
    protected List<File> loadFiles() {
        var files = inboundFileGateway.loadFiles();
        if (CollectionUtils.isEmpty(files)) {
            logger.debug("No HealthPartners files loaded");
        }
        return files;
    }

    @Override
    protected HpFileProcessingSummary<?> process(File localFile) throws Exception {
        logger.info("Process {}", localFile.getName());
        var start = System.currentTimeMillis();
        var fileLogId = createFileLog(localFile);
        fileLogIdHolder.set(fileLogId);

        var description = describeOrThrow(localFile);

        var processor = resolveProcessor(description.getType());
        var targetCommunityId = hpFileTargetResolver.resolveTargetCommunityId(description.getSource());

        var fileSummary = processor.process(fileLogId, localFile, targetCommunityId);

        if (fileSummary.getStatus() != ProcessingSummary.ProcessingStatus.ERROR) {
            fileSummary.propagateStatusAndMessage();
        }
        logger.info("Processing took {} milliseconds", System.currentTimeMillis() - start);

        return fileSummary;
    }

    private Long createFileLog(File localFile) {
        var log = new HealthPartnersFileLog();
        log.setReceivedAt(Instant.now());

        log.setFileName(localFile.getName());
        log.setFileType(hpFileNameSupport.typeFromFileName(localFile.getName())
                .map(HpFileType::name)
                .orElse(null)
        );

        log.setSuccess(false);
        log.setErrorMessage("In progress yet");

        return healthPartnersFileLogDao.saveInNewTx(log).getId();
    }

    @Override
    protected void afterProcessingStatusOk(File localFile, HpFileProcessingSummary<?> processingSummary) {
        logger.info("Successfully processed {}", localFile.getName());
        inboundFileGateway.afterProcessingStatusOk(localFile, processingSummary);
        healthPartnersFileLogDao.writeSuccessInNewTx(fileLogIdHolder.get());
        publishProcessedFileEvent(localFile.getName(), fileLogIdHolder.get());
    }

    @Override
    protected void afterProcessingStatusWarn(File localFile, HpFileProcessingSummary<?> processingSummary) {
        logger.info("Processed {} with warnings ", localFile.getName());
        inboundFileGateway.afterProcessingStatusWarn(localFile, processingSummary);
        healthPartnersFileLogDao.writeFailInNewTx(fileLogIdHolder.get(), generateWarnLogMessage(processingSummary));

        publishProcessedFileEvent(localFile.getName(), fileLogIdHolder.get());
    }

    private String generateWarnLogMessage(HpFileProcessingSummary<?> processingSummary) {
        var result = "WARN STATUS:" + processingSummary.getMessage();

        if (StringUtils.isNotEmpty(processingSummary.getStackTrace())) {
            result = result + "\n" + processingSummary.getStackTrace();
        }
        return result;
    }

    @Override
    protected void afterProcessingStatusError(File localFile, Exception exception) {
        logger.info("Processed {} with error, exception is: {}", localFile.getName(), ExceptionUtils.getStackTrace(exception));

        //create as anonymous subclass in case file type was not resolved
        var summary = new HpFileProcessingSummary<>(hpFileNameSupport.typeFromFileName(localFile.getName()).orElse(null)) {
        };

        summary.setFileName(localFile.getName());
        summary.setProcessedAt(Instant.now());

        fillProcessingSummaryErrorFields(summary, exception);

        healthPartnersFileLogDao.writeFailInNewTx(fileLogIdHolder.get(), summary.getStackTrace());

        inboundFileGateway.afterProcessingStatusError(localFile, summary);

        publishProcessedFileEvent(localFile.getName(), fileLogIdHolder.get());
    }


    private void publishProcessedFileEvent(String fileName, Long hpFileLogId) {
        var event = new HealthPartnersFileProcessedEvent(fileName, hpFileLogId);
        applicationEventPublisher.publishEvent(event);
    }

    private HpFileDescription describeOrThrow(File file) {
        return hpFileNameSupport.describe(file.getName())
                .orElseThrow(() -> new HpFileProcessingException("Unknown file type and source"));
    }

    private HealthPartnersFileProcessor resolveProcessor(HpFileType fileType) {
        return fileProcessors.stream()
                .filter(p -> fileType == p.supportedFileType())
                .findFirst()
                .orElseThrow(
                        () -> new HpFileProcessingException("Failed to determine file processing flow: " + fileType)
                );
    }
}
