package com.scnsoft.eldermark.services.integration.qualifacts;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.exception.integration.qualifacts.DocumentWithoutResidentException;
import com.scnsoft.eldermark.exception.integration.qualifacts.MissingClientIdException;
import com.scnsoft.eldermark.exception.integration.qualifacts.QualifactsDocumentsGatewayException;
import com.scnsoft.eldermark.services.DocumentService;
import com.scnsoft.eldermark.services.merging.MPIService;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;

@Service
@Transactional
@Conditional(QualifactsIntegrationEnabledCondition.class)
public class QualifactsDocumentsGatewayImpl implements QualifactsDocumentsGateway {

    private static final Logger logger = LoggerFactory.getLogger(QualifactsDocumentsGatewayImpl.class);
    private static final String FILE_METADATA_SEPARATOR = "_";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("MMddyyyy" + FILE_METADATA_SEPARATOR + "HHmmss");

    @Value("${qualifacts.sftp.disabled}")
    private Boolean isSendingOutDisabled;

    @Value("${qualifacts.sftp.baseDir}")
    private String baseQsiSftpFolder;

    @Value("${qualifacts.sftp.lssi.oid}")
    private String lssiDatabaseOid;

    private final SessionManager sessionManager;
    private final DocumentService documentService;
    private final MPIService mpiService;

    @Autowired
    public QualifactsDocumentsGatewayImpl(@Qualifier("qualifactsSftpSessionManager") SessionManager sessionManager,
                                          DocumentService documentService,
                                          MPIService mpiService) {
        this.sessionManager = sessionManager;
        this.documentService = documentService;
        this.mpiService = mpiService;
    }


    @Override
    public void sendDocumentToQualifacts(Document document) {
        if (isSendingOutDisabled) {
            logger.info("Attempt to send document [{}] to Qualifacts: Sending out disabled", document.getId());
            return;
        }
        ChannelSftp channelSftp = null;
        try {
            final String documentName = generateQsiDocumentName(document);
            channelSftp = (ChannelSftp) sessionManager.getSession().openChannel("sftp");
            channelSftp.connect();
            channelSftp.cd(baseQsiSftpFolder);
            channelSftp.put(documentService.getDocumentInputStream(document), documentName);
            logger.info("document [{}] was sent to Qualifacts with name {}", document.getId(), documentName);
        } catch (JSchException | FileNotFoundException | SftpException e) {
            throw new QualifactsDocumentsGatewayException("Error during sending document [" + document.getId() + "] out to Qualifacts", e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }
    }

    private String generateQsiDocumentName(Document document) {
        return new StringBuilder("LSSI")
                .append(FILE_METADATA_SEPARATOR)
                .append(resolveClientId(document))
                .append(FILE_METADATA_SEPARATOR)
                .append(createTimestamp(document))
                .append(fetchDocumentExtension(document))
                .toString();
    }

    private String resolveClientId(Document document) {
        final Resident resident = documentService.getResident(document);
        if (resident == null) {
            throw new DocumentWithoutResidentException("Could not find owning resident for document [" + document.getId() + "]");
        }

        final MPI mpi = mpiService.findMpiForResidentOrMergedAndDatabaseOid(resident.getId(), lssiDatabaseOid);

        if (mpi == null) {
            throw new MissingClientIdException("Qualifacts clientId wasn't found for resident " + resident.getId());
        }

        return mpi.getPatientId();
    }

    private String createTimestamp(Document document) {
        return DATE_TIME_FORMATTER.print(document.getCreationTime().getTime());
    }

    private String fetchDocumentExtension(Document document) {
        final int lastDot = document.getDocumentTitle().lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return document.getDocumentTitle().substring(lastDot);
    }

}
