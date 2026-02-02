package com.scnsoft.eldermark.service.hl7;

import com.scnsoft.eldermark.entity.lab.LabResearchOrderORM;
import com.scnsoft.eldermark.exception.SftpGatewayException;
import com.scnsoft.eldermark.service.sftp.ApolloSftpGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ApolloORMSftpSenderImpl implements ApolloORMSender {

    private static final Logger logger = LoggerFactory.getLogger(ApolloORMSftpSenderImpl.class);

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddyyyy_HHmmss").withZone(ZoneId.of("UTC"));

    @Value("${apollo.sftp.orm.path}")
    private String sftpOrmPath;

    @Autowired
    private ApolloSftpGateway apolloSftpGateway;

    @Override
    @Transactional
    public boolean send(LabResearchOrderORM orm) {
        if (orm.getOrder().getClient().getOrganization().isLabsResearchTesting()) {
            logger.info("Lab order is created in testing organization. Won't push Hl7 to Apollo");
            return true;
        }
        if (!apolloSftpGateway.isSftpEnabled()) {
            logger.info("Apollo SFTP disabled - don't push ORM to SFTP and proceed with order creation");
            return true;
        }

        try {
            var inputStream = new ByteArrayInputStream(orm.getOrmRaw().getBytes());
            var fileName = createFileName(orm);
            if (apolloSftpGateway.put(inputStream, sftpOrmPath, fileName)) {
                orm.setSentDatetime(Instant.now());
                return true;
            }
            return false;
        } catch (SftpGatewayException e) {
            logger.warn("Couldn't send ORM to Apollo: ", e);
            return false;
        }
    }

    private String createFileName(LabResearchOrderORM orm) {
        return dateTimeFormatter.format(Instant.now())
                + "_"
                + orm.getOrder().getClient().getId()
                + ".hl7";
    }
}
