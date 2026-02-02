package com.scnsoft.eldermark.service.xds;

import com.scnsoft.eldermark.dao.MPIDao;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.document.DocumentXdsConnectorFieldsAware;
import com.scnsoft.eldermark.exception.XdsCommunicationException;
import com.scnsoft.eldermark.service.xds.ssl.SslConnectionFactory;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * Created by averazub on 8/16/2016.
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class XdsRegistryConnectorServiceImpl implements XdsRegistryConnectorService {

    private static final Logger logger = LoggerFactory.getLogger(XdsRegistryConnectorServiceImpl.class);

    private Iti42Sender sender;

    private CustomXdsRegistrySender customXdsRegistrySender;

    @Value("${xds.registry.url}")
    private String xdsRegistryUrl;

    @Value("${xds.registry.custom.url}")
    private String xdsRegistryCustomUrl;

    @Value("${home.community.id}")
    private String homeCommunityId;

    @Value("${repository.unique.id}")
    private String repositoryUniqueId;

    @Value("${xds.registry.enabled}")
    private Boolean xdsRegistryEnabled;

    @Value("${xds.exchange.keystore}")
    private String keyStorePath;

    @Value("${xds.exchange.keystore.password}")
    private String keyStorePassword;

    @Value("${xds.exchange.truststore}")
    private String trustStorePath;

    @Value("${xds.exchange.truststore.password}")
    private String trustStorePassword;

    @Value("${xds.custom.auth.username}")
    private String xdsCustomUser;

    @Value("${xds.custom.auth.password}")
    private String xdsCustomPassword;

    @Autowired
    private MPIDao mpiDao;

    @PostConstruct
    void postConstruct() {
        var itiSenderConnectionFactory = new SslConnectionFactory(keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
        this.sender = new Iti42Sender(repositoryUniqueId, homeCommunityId, xdsRegistryUrl, itiSenderConnectionFactory);
        this.customXdsRegistrySender = new CustomXdsRegistrySender(xdsRegistryCustomUrl, xdsCustomUser, xdsCustomPassword);
    }

    protected Boolean sendDocumentMetadata(ExchangeDocumentData data) throws IOException {
        Iti42DocumentData targetData = Iti42Builder.createIti42DocumentData(data, repositoryUniqueId, homeCommunityId);
        sender.sendDocumentMetadata(targetData);
        return true;
    }

    @Override
    public void saveNewFileInRegistry(DocumentXdsConnectorFieldsAware document, Long clientId) {
        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
            try {
                String sourcePatientIdHl7 = getPatientIdByClientId(clientId);

                ExchangeDocumentData documentData = new ExchangeDocumentData(
                        document.getUniqueId(),
                        document.getHash(),
                        document.getSize(),
                        DateTimeUtils.toDate(document.getCreationTime()),
                        document.getMimeType(),
                        document.getUuid(),
                        sourcePatientIdHl7,
                        document.getDocumentTitle(),
                        document.getVisible(),
                        document.isEldermarkShared()
                );
                sendDocumentMetadata(documentData);
            } catch (IOException e) {
                throw new XdsCommunicationException(e);
            }
        }
    }

    @Override
    public void deprecateDocumentInRepository(String documentUUID) {
        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
            try {
                XdsDocumentBriefData srcData = getDocumentDataFromRepository(documentUUID);
                if (Boolean.FALSE.equals(srcData.getExists())) {
                    logger.warn("Trying to deprecate document that doesn't exist in repo");
                } else if (Boolean.TRUE.equals(srcData.getApproved())) {
                    sender.deprecateDocumentEntry(documentUUID);
                }
            } catch (IOException e) {
                throw new XdsCommunicationException(e);
            }
        }
    }

    @Override
    public void approveDocumentInRepository(String documentUUID) {
        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
            try {
                XdsDocumentBriefData srcData = getDocumentDataFromRepository(documentUUID);
                if (Boolean.FALSE.equals(srcData.getExists())) {
                    logger.warn("Trying to approve document that doesn't exist in repo");
                    //TODO SOME ERROR
                } else if (!Boolean.TRUE.equals(srcData.getApproved())) {
                    sender.approveDocumentEntry(documentUUID);
                }
            } catch (IOException e) {
                throw new XdsCommunicationException(e);
            }
        }
    }

    @Override
    public void updateDocumentTitleInRepository(Document document) {
        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
            try {

                XdsDocumentBriefData srcData = getDocumentDataFromRepository(document.getUuid());
                if (Boolean.FALSE.equals(srcData.getExists())) {
                    logger.warn("Trying to update document that doesn't exist in repo");
                    //TODO ERROR
                    return;
                }

                String responseStr = customXdsRegistrySender.updateTitle(document.getUuid(), document.getDocumentTitle());
                if (!responseStr.startsWith("Success")) {
                    throw new XdsCommunicationException(responseStr);
                }
            } catch (Exception e) {
                throw new XdsCommunicationException(e);
            }
        }
    }


    private XdsDocumentBriefData getDocumentDataFromRepository(String uuid) {
        try {

            var responseStr = customXdsRegistrySender.getDocumentData(uuid);

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(responseStr);

            Object existsRaw = obj.get("exists");
            Object approvedRaw = obj.get("approved");
            boolean exists = existsRaw == null ? false : Boolean.valueOf(existsRaw.toString());
            boolean approved = approvedRaw == null ? false : Boolean.valueOf(approvedRaw.toString());

            return new XdsDocumentBriefData(uuid, exists, approved);

        } catch (Exception e) {
            throw new XdsCommunicationException(e);
        }
    }

    private String getPatientIdByClientId(Long clientId) {
        List<MPI> mpiList = mpiDao.getByClientId(clientId);
        if (mpiList.isEmpty()) return null;
        if (mpiList.size() > 1) {
            logger.warn("Client " + clientId + " has more than 1 record in MPI");
        }
        MPI mpi = mpiList.get(0);
        //todo use assigningFacility here
        String sourcePatientIdHl7 = createH17Id(mpi);
        return sourcePatientIdHl7;
    }

    private String createH17Id(MPI mpi) {
        return mpi.getPatientId() + "^^^&amp;" + mpi.getAssigningAuthorityUniversal() + "&amp;" + mpi.getAssigningAuthorityUniversalType();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = XdsCommunicationException.class)
    public String synchronizeDocWithRepository(DocumentXdsConnectorFieldsAware doc, Long residentId) {
        try {
            XdsDocumentBriefData srcData = getDocumentDataFromRepository(doc.getUuid());

            String reportStr = "Document " + doc.getId() + " Information: ";

            boolean visibleInRepository = isDocumentVisibleInRegistry(doc);
            boolean existsInRegistry = Boolean.TRUE.equals(srcData.getExists());

            if (!existsInRegistry && visibleInRepository) {
                reportStr += " Create new record in registry;";
                saveNewFileInRegistry(doc, residentId);
                srcData = getDocumentDataFromRepository(doc.getUuid());
            } else if (existsInRegistry) {
                reportStr += " Already in registry; ";
            }
            boolean approvedInRegistry = existsInRegistry && Boolean.TRUE.equals(srcData.getApproved());

            if (approvedInRegistry && !visibleInRepository) {
                deprecateDocumentInRepository(doc.getUuid());
                reportStr += " Deprecate Document; ";
            } else if (!approvedInRegistry && visibleInRepository) {
                approveDocumentInRepository(doc.getUuid());
                reportStr += " Approve Document; ";
            }
            return reportStr;
        } catch (Exception e) {
            throw new XdsCommunicationException(e);
        }

    }

    @Override
    public boolean isDocumentVisibleInRegistry(DocumentXdsConnectorFieldsAware doc) {
        return !(Boolean.FALSE.equals(doc.getVisible()))/*||(Boolean.FALSE.equals(doc.isEldermarkShared())))*/;
    }

    //below is code from old portal which might be needed
//
//    public void saveCcdInRegistry(Long residentId) {
//        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
//            try {
////                List<MPI> mpiList = mpiDao.getByResidentId(residentId);
////                if (mpiList.isEmpty()) {
////                    logger.error("Resident " + residentId + " not found in MPI");
////                    return;
////                }
////                if (mpiList.size()>1) {
////                    logger.warn("Resident " + residentId + " has more than 1 record in MPI");
////                }
////                String sourcePatientIdHl7 = createIdH17(mpiList.get(0));
//
//                String sourcePatientIdHl7 = getPatientIdByClientId(residentId);
//
//                ExchangeDocumentData documentData = new ExchangeDocumentData(
////                        mpiList.get(0).getAssigningAuthorityUniversal() + ".3." + residentId + ".0",
//                        homeCommunityId + ".3." + residentId + ".0",
//                        null,
//                        null,
//                        new Date(),
//                        "text/xml",
//                        UUID.randomUUID().toString(),
//                        sourcePatientIdHl7,
//                        "CCD.xml",
//                        true,
//                        true
//                );
//                sendDocumentMetadata(documentData);
//            } catch (IOException e) {
//                throw new XdsCommunicationException(e);
//            }
//        }
//
//    }
//
}
