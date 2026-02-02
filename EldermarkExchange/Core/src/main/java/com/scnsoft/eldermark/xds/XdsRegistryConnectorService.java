package com.scnsoft.eldermark.xds;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.scnsoft.eldermark.dao.MPIDao;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.XdsDocumentBriefData;
import com.scnsoft.eldermark.shared.exceptions.NHINIoException;
import com.scnsoft.eldermark.xds.ssl.SslConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by averazub on 8/16/2016.
 */
@Service
public class XdsRegistryConnectorService {

    private static final Logger logger = LoggerFactory.getLogger(XdsRegistryConnectorService.class);

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
        SslConnectionFactory itiSenderConnectionFactory = new SslConnectionFactory(keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
        this.sender = new Iti42Sender(repositoryUniqueId, homeCommunityId, xdsRegistryUrl, itiSenderConnectionFactory);
        this.customXdsRegistrySender = new CustomXdsRegistrySender(xdsRegistryCustomUrl, xdsCustomUser, xdsCustomPassword);
    }

    protected Boolean sendDocumentMetadata(ExchangeDocumentData data) throws IOException {
        Iti42DocumentData targetData = Iti42Builder.createIti42DocumentData(data, repositoryUniqueId, homeCommunityId);
        sender.sendDocumentMetadata(targetData);
        return true;
    }

    public void saveNewFileInRegistry(File file, Document document, Long residentId) throws NHINIoException {
        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
            try {
                String hc = StringUtils.isEmpty(document.getHash()) ? Files.hash(file, Hashing.md5()).toString() : document.getHash();
                String sourcePatientIdHl7 = getPatientIdByResidentId(residentId);

                ExchangeDocumentData documentData = new ExchangeDocumentData(
                        document.getUniqueId(),
                        hc,
                        document.getSize(),
                        document.getCreationTime(),
                        document.getMimeType(),
                        document.getUuid(),
                        sourcePatientIdHl7,
                        document.getDocumentTitle(),
                        document.getVisible(),
                        document.isEldermarkShared()
                );
                sendDocumentMetadata(documentData);
            } catch (IOException e) {
                throw new NHINIoException(e);
            }
        }

    }

    public void saveCcdInRegistry(Long residentId) throws NHINIoException {
        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
            try {
//                List<MPI> mpiList = mpiDao.getByResidentId(residentId);
//                if (mpiList.isEmpty()) {
//                    logger.error("Resident " + residentId + " not found in MPI");
//                    return;
//                }
//                if (mpiList.size()>1) {
//                    logger.warn("Resident " + residentId + " has more than 1 record in MPI");
//                }
//                String sourcePatientIdHl7 = createIdH17(mpiList.get(0));

                String sourcePatientIdHl7 = getPatientIdByResidentId(residentId);

                ExchangeDocumentData documentData = new ExchangeDocumentData(
//                        mpiList.get(0).getAssigningAuthorityUniversal() + ".3." + residentId + ".0",
                        homeCommunityId + ".3." + residentId + ".0",
                        null,
                        null,
                        new Date(),
                        "text/xml",
                        UUID.randomUUID().toString(),
                        sourcePatientIdHl7,
                        "CCD.xml",
                        true,
                        true
                );
                sendDocumentMetadata(documentData);
            } catch (IOException e) {
                throw new NHINIoException(e);
            }
        }

    }

    public String synchronizeDocWithRepository(File f, Document doc, Long residentId) throws NHINIoException {
        XdsDocumentBriefData srcData = getDocumentDataFromRepository(doc.getUuid());

        String reportStr = "Document " + doc.getId() + " Information: ";

        boolean visibleInRepository = isDocumentVisibleInRegistry(doc);
        boolean existsInRegistry = Boolean.TRUE.equals(srcData.getExists());

        if ((!existsInRegistry) && (visibleInRepository)) {
            reportStr += " Create new record in registry;";
            saveNewFileInRegistry(f, doc, residentId);
            srcData = getDocumentDataFromRepository(doc.getUuid());
        } else if (existsInRegistry) {
            reportStr += " Already in registry; ";
        }
        boolean approvedInRegistry = existsInRegistry && Boolean.TRUE.equals(srcData.getApproved());

        if ((approvedInRegistry) && (!visibleInRepository)) {
            deprecateDocumentInRepository(doc.getUuid());
            reportStr += " Deprecate Document; ";
        } else if ((!approvedInRegistry) && (visibleInRepository)) {
            approveDocumentInRepository(doc.getUuid());
            reportStr += " Approve Document; ";
        }
        return reportStr;
    }

    public void deprecateDocumentInRepository(String documentUUID) throws NHINIoException {
        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
            try {
                XdsDocumentBriefData srcData = getDocumentDataFromRepository(documentUUID);
                if (Boolean.FALSE.equals(srcData.getExists())) {
                    System.err.println("Trying to deprecate document that is not exist in repo");
                    //TODO SOME ERROR
                } else if (Boolean.TRUE.equals(srcData.getApproved())) {
                    sender.deprecateDocumentEntry(documentUUID);
                }
            } catch (IOException e) {
                throw new NHINIoException(e);
            }
        }
    }

    public void approveDocumentInRepository(String documentUUID) throws NHINIoException {
        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
            try {
                XdsDocumentBriefData srcData = getDocumentDataFromRepository(documentUUID);
                if (Boolean.FALSE.equals(srcData.getExists())) {
                    System.err.println("Trying to approve document that is not exist in repo");
                    //TODO SOME ERROR
                } else if (!Boolean.TRUE.equals(srcData.getApproved())) {
                    sender.approveDocumentEntry(documentUUID);
                }
            } catch (IOException e) {
                throw new NHINIoException(e);
            }
        }
    }

    public void updateDocumentTitleInRepository(Document document) throws NHINIoException {
        if (Boolean.TRUE.equals(xdsRegistryEnabled)) {
            try {

                XdsDocumentBriefData srcData = getDocumentDataFromRepository(document.getUuid());
                if (Boolean.FALSE.equals(srcData.getExists())) {
                    System.err.println("Trying to update document that is not exist in repo");
                    //TODO ERROR
                    return;
                }

                String responseStr = customXdsRegistrySender.updateTitle(document.getUuid(), document.getDocumentTitle());

                if (!responseStr.startsWith("Success")) {
                    throw new NHINIoException(responseStr);
                }
            } catch (Exception e) {
                throw new NHINIoException(e);
            }
        }
    }

    protected XdsDocumentBriefData getDocumentDataFromRepository(String uuid) throws NHINIoException {
        try {
            String responseStr = customXdsRegistrySender.getDocumentData(uuid);

            JSONParser parser = new org.json.simple.parser.JSONParser();
            JSONObject obj = (JSONObject) parser.parse(responseStr);

            Object existsRaw = obj.get("exists");
            Object approvedRaw = obj.get("approved");
            boolean exists = existsRaw == null ? false : Boolean.valueOf(existsRaw.toString());
            boolean approved = approvedRaw == null ? false : Boolean.valueOf(approvedRaw.toString());

            return new XdsDocumentBriefData(uuid, exists, approved);

        } catch (Exception e) {
            throw new NHINIoException(e);
        }
    }

    public boolean isDocumentVisibleInRegistry(Document doc) {
        return !(Boolean.FALSE.equals(doc.getVisible()))/*||(Boolean.FALSE.equals(doc.isEldermarkShared())))*/;
    }

    protected String getPatientIdByResidentId(Long residentId) {
        List<MPI> mpiList = mpiDao.getByResidentId(residentId);
        if (mpiList.isEmpty()) return null;
        if (mpiList.size() > 1) {
            logger.warn("Resident " + residentId + " has more than 1 record in MPI");
        }
        MPI mpi = mpiList.get(0);
        //todo use assigningFacility here
        String sourcePatientIdHl7 = mpi.getPatientId() + "^^^&amp;" + mpi.getAssigningAuthorityUniversal() + "&amp;" + mpi.getAssigningAuthorityUniversalType();
        return sourcePatientIdHl7;
    }

//    protected String createIdH17(MPI mpi){
//        return mpi.getPatientId() +"^^^&amp;"+mpi.getAssigningAuthorityUniversal()+"&amp;"+mpi.getAssigningAuthorityUniversalType();
//    }

}
