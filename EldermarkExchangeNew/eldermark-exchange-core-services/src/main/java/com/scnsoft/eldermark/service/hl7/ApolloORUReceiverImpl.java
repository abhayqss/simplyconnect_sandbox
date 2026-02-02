package com.scnsoft.eldermark.service.hl7;

import com.scnsoft.eldermark.service.sftp.ApolloSftpGateway;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = {"apollo.sftp.enabled", "apollo.sftp.receive.enabled"},
        havingValue = "true"
)
public class ApolloORUReceiverImpl implements ApolloORUReceiver {

    private static final Logger logger = LoggerFactory.getLogger(ApolloORUReceiverImpl.class);

    @Autowired
    private ApolloSftpGateway apolloSftpGateway;

    @Value("${apollo.sftp.oru.path}")
    private String oruPath;

    @Autowired
    private ApolloOruProcessor apolloOruProcessor;

    @Override
    @Scheduled(fixedDelayString = "${apollo.oru.checkPeriod}")
    public void receive() {
        var files = apolloSftpGateway.listFiles(oruPath, ".hl7");
        if (CollectionUtils.isNotEmpty(files)) {
            for (var fileName : files) {
                processFile(fileName);
            }
        }
    }

    private void processFile(String fileName) {
        try {
            logger.info("Processing ORU Apollo file [{}]", fileName);
            var bytes = apolloSftpGateway.get(oruPath, fileName);
            var oruRaw = new String(bytes);

            var orderOru = apolloOruProcessor.process(oruRaw, fileName);
            if (orderOru.isPresent()) {
                //content should been be saved to log file successfully
                apolloSftpGateway.remove(oruPath, fileName);
            }
        } catch (Exception e) {
            logger.warn("Exception during processing file [{}]", fileName);
        }
    }
}
