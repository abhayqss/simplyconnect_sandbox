package com.scnsoft.eldermark.services;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.shared.exceptions.DocumentNotFoundException;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import com.scnsoft.eldermark.shared.exceptions.NHINIoException;
import com.scnsoft.eldermark.shared.exceptions.ResidentOptedOutException;
import com.scnsoft.eldermark.xds.XdsRegistryConnectorService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private DocumentDao documentDao;
    @Autowired
    private ResidentDao residentDao;
    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private DatabasesDao databasesDao;

    @Value("${documents.upload.basedir}")
    private String documentsUploadBaseDir;

    @Value("${home.community.id}")
    private String homeCommunityId;

    @Autowired
    XdsRegistryConnectorService xdsRegistryConnectorService;

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    @Override
    public List<Document> queryForDocuments(Resident resident, Employee requestingEmployee) {
        return setIsCdaDocument(documentDao.queryForDocuments(resident, requestingEmployee));
    }

    @Override
    public List<Document> queryForDocuments(Resident resident, String filter, Employee requestingEmployee,
                                            int offset, int limit) {
        return setIsCdaDocument(documentDao.queryForDocuments(resident, filter, requestingEmployee, offset, limit));
    }

    @Override
    public List<Document> queryForDocuments(Resident resident, String filter, Employee requestingEmployee, Pageable pageable) {
        return setIsCdaDocument(documentDao.queryForDocuments(resident, filter, requestingEmployee, pageable));
    }

    @Override
    public Long getCustomDocumentCount(Resident resident, String filter, Employee requestingEmployee) {
        return documentDao.getDocumentCount(resident, filter, requestingEmployee);
    }

    @Override
    public List<Document> queryForDocuments(Resident resident, Employee requestingEmployee, List<Long> orSharedWith, boolean visibleOnly) {
        return setIsCdaDocument(documentDao.queryForDocuments(resident, requestingEmployee, orSharedWith, visibleOnly));
    }

    @Override
    @Transactional
    public Document saveDocumentFromXds(DocumentMetadata metadata, Resident resident, Employee author, String uuid, String uniqueId, SaveDocumentCallback callback) throws IOException {
        String uploadDirName = getEmployeeUploadsFolder(author.getDatabaseAlternativeId(), author.getLegacyId());
        File uploadDir = new File(uploadDirName);
        uploadDir.mkdirs();

        String fileName = buildFileName(uuid);
        File uploadedFile = new File(uploadDir, fileName);

        boolean fileCreated;
        try {
            fileCreated = uploadedFile.createNewFile();
        } catch (IOException e) {
            throw new FileIOException("I/O error occurred during file upload");
        }

        if (!fileCreated) {
            throw new FileIOException("File" + fileName + " already exists");
        }

        try {
            callback.saveToFile(uploadedFile);

            String hash = Files.hash(uploadedFile, Hashing.md5()).toString();
            Document document = new Document();
            document.setResidentDatabaseAlternativeId(resident.getDatabaseAlternativeId());
            document.setAuthorDatabaseAlternativeId(author.getDatabaseAlternativeId());
            document.setResidentLegacyId(resident.getLegacyId());
            document.setAuthorLegacyId(author.getLegacyId());
            document.setCreationTime(new Date());
            document.setDocumentTitle(metadata.getDocumentTitle());
            document.setOriginalFileName(metadata.getFileName());
            document.setMimeType(metadata.getMimeType());
            document.setSize((int) uploadedFile.length());
            document.setVisible(true);
            document.setUuid(uuid);
            document.setHash(hash);
            document.setEldermarkShared(true);
            document.setUniqueId(uniqueId);

            documentDao.saveDocument(document);

            return document;
        } catch (Exception e) {

            callback.rollbackSaveToFile(uploadedFile);

            throw new IOException(e);
        }

    }


    @Override
    @Transactional
    public Document saveDocument(DocumentMetadata metadata, Resident resident, Employee author,
                                 boolean isSharedWithAll, List<Database> databasesToShareWith,
                                 SaveDocumentCallback callback, Boolean isCloud, byte[] fileContent) throws IOException {
        String uploadDirName = getEmployeeUploadsFolder(author.getDatabaseAlternativeId(), author.getLegacyId());
        File uploadDir = new File(uploadDirName);
        uploadDir.mkdirs();

        String uuid = UUID.randomUUID().toString();
        String fileName = buildFileName(uuid);
        File uploadedFile = new File(uploadDir, fileName);

        boolean fileCreated;
        try {
            fileCreated = uploadedFile.createNewFile();
        } catch (IOException e) {
            throw new FileIOException("I/O error occurred during file upload");
        }

        if (!fileCreated) {
            throw new FileIOException("File" + fileName + " already exists");
        }
        String hash = null;
        //TODO remove "else" part, encrypt fileContent in all cases (it should be required here)
        if (fileContent != null) {
            byte[] encrypted = documentEncryptionService.encrypt(fileContent);
            java.nio.file.Files.copy(new ByteArrayInputStream(encrypted), uploadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            hash = Hashing.md5().hashBytes(fileContent).toString();
        } else {
            callback.saveToFile(uploadedFile);
            hash = Files.hash(uploadedFile, Hashing.md5()).toString();
        }

        Document document = new Document();
        document.setResidentDatabaseAlternativeId(resident.getDatabaseAlternativeId());
        document.setAuthorDatabaseAlternativeId(author.getDatabaseAlternativeId());
        document.setResidentLegacyId(resident.getLegacyId());
        document.setAuthorLegacyId(author.getLegacyId());
        document.setCreationTime(new Date());
        document.setDocumentTitle(metadata.getDocumentTitle());
        document.setOriginalFileName(metadata.getFileName());
        document.setMimeType(metadata.getMimeType());
        document.setSize((int) uploadedFile.length());
        document.setVisible(true);
        document.setUuid(uuid);
        document.setHash(hash);
        document.setEldermarkShared(isSharedWithAll);
        document.setSharedWithDatabases(databasesToShareWith);
        document.setIsCloud(isCloud);

        documentDao.saveDocument(document);

        document.setUniqueId(homeCommunityId + ".3." + document.getId());
        documentDao.saveDocument(document);

        try {
            xdsRegistryConnectorService.saveNewFileInRegistry(uploadedFile, document, resident.getId());
            if (!xdsRegistryConnectorService.isDocumentVisibleInRegistry(document)) {
                xdsRegistryConnectorService.deprecateDocumentInRepository(document.getUuid());
            }
        } catch (NHINIoException e) {
            callback.rollbackSaveToFile(uploadedFile);
            throw e;
        }

        return document;
    }

    @Override
    @Transactional
    public void deleteDocumentFromXds(long id) throws IOException {
        Document document = documentDao.findDocument(id);
        File file = getDocumentFile(document);
        documentDao.deleteDocument(id);
        try {
            if (!file.delete()) {
                throw new FileIOException("Failed delete from storage file " + document.getOriginalFileName());
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    @Transactional
    public void deleteDocument(long id, boolean couldBeRestored) throws IOException {
        Document document = documentDao.findDocument(id);
        if (couldBeRestored) {
            documentDao.makeInvisible(id);
            xdsRegistryConnectorService.deprecateDocumentInRepository(document.getUuid());
        } else {
            documentDao.deleteDocument(id);
            xdsRegistryConnectorService.deprecateDocumentInRepository(document.getUuid());
            try {
                File file = getDocumentFile(document);
                if (!file.delete()) {
                    throw new FileIOException("Failed delete from storage file " + document.getOriginalFileName());
                }
            } catch (Exception e) {
                if (xdsRegistryConnectorService.isDocumentVisibleInRegistry(document)) {
                    xdsRegistryConnectorService.approveDocumentInRepository(document.getUuid());
                }
                throw new IOException(e);
            }
        }
    }

	@Override
	@Transactional
	public void restoreDocument(long id) throws NHINIoException {
		 restoreDocument(id, false);
	}
	
	@SuppressWarnings("unlikely-arg-type")
    @Override
    @Transactional
    public void restoreDocument(long id, boolean includeOptOutResident) throws NHINIoException {
        Document document = documentDao.findDocument(id);
        document.setOriginalFileName(document.getOriginalFileName().trim());
        
        Long residentId = residentDao.getResidentId(document.getResidentDatabaseAlternativeId(),
                document.getResidentLegacyId());
        Resident resident = residentDao.getResident(residentId, includeOptOutResident);
        List<Document> resDocuments = documentDao.queryForDocuments(resident, null);
        List<String> nameList = new ArrayList<String>();
        int count = 0;
        Boolean isduplicate = false;
        StringBuilder originalFileName = new StringBuilder(document.getOriginalFileName());
        int renameIndex = 0;
        String rename = "";

        for (Document resDocument : resDocuments) {
			nameList.add(resDocument.getOriginalFileName().toLowerCase().trim());
        }

		while (!nameList.isEmpty() && nameList.contains(document.getOriginalFileName().toLowerCase())) {
            isduplicate = true;

            if (count != 0) {
                originalFileName.delete(renameIndex, originalFileName.lastIndexOf("."));
                renameIndex = originalFileName.lastIndexOf(".");
                rename = "- recovered(" + Integer.toString(count) + ")";

                originalFileName.insert(renameIndex, rename);
                document.setOriginalFileName(originalFileName.toString());
            } else {
                rename = "- recovered";
                renameIndex = originalFileName.lastIndexOf(".");

                originalFileName.insert(renameIndex, rename);
                document.setOriginalFileName(originalFileName.toString());
            }

            count++;
        }

        if (isduplicate) {
            document.setDocumentTitle(document.getOriginalFileName());
            documentDao.updateDocument(document);
            xdsRegistryConnectorService.updateDocumentTitleInRepository(document);

            if (xdsRegistryConnectorService.isDocumentVisibleInRegistry(document)) {
                xdsRegistryConnectorService.approveDocumentInRepository(document.getUuid());
            } else {
                xdsRegistryConnectorService.deprecateDocumentInRepository(document.getUuid());
            }
        }

        File file = getDocumentFile(document);
        if (!file.exists()) {
            throw new FileIOException("Document " + document.getOriginalFileName() + "does not exist in a file store");
        }

        documentDao.makeVisible(id);
        if (xdsRegistryConnectorService.isDocumentVisibleInRegistry(document)) {
            xdsRegistryConnectorService.approveDocumentInRepository(document.getUuid());
        }

    }

    @Override
    @Transactional
    public Document findDocument(long id) {
        return findDocument(id, false);
    }

    @Override
    @Transactional
    public Document findDocument(long id, boolean visibleOnly) {
        return findDocument(id, visibleOnly, false);
    }
    
    @Override
    @Transactional
    public Document findDocument(long id, boolean visibleOnly, boolean includeOptOutResident) {
        Document document = documentDao.findDocument(id);
        if (document == null) {
            throw new DocumentNotFoundException(id);
        }

        if (visibleOnly && !document.getVisible()) {
            throw new DocumentNotFoundException(id);
        }

        if (document.getLabResearchOrder() != null &&
            document.getLabResearchOrder().getStatus() != LabResearchOrderStatus.REVIEWED) {
            throw new DocumentNotFoundException(id);
        }

        Resident resident = getResident(document);
        if (resident == null || isInvisible(resident)) {
            throw new DocumentNotFoundException(id);
        }

        if (!includeOptOutResident && resident.isOptOut()) {
            throw new ResidentOptedOutException();
        }
        setIsCdaDocument(document);
        return document;
    }


    @Override
    public Document findDocumentByUniqueId(String uniqueId) {
        Document document = documentDao.findDocumentByUniqueId(uniqueId);
        if (document == null) return null;
        if (!document.getVisible()) return null;
        Resident resident = getResident(document);
        if (resident == null || isInvisible(resident)) return null;
        if (resident.isOptOut()) return null;
        setIsCdaDocument(document);
        return document;
    }


    @Override
    public Document findDocumentByUniqueIdOrThrow(String uniqueId) {
        Document document = documentDao.findDocumentByUniqueId(uniqueId);
        if (document == null) {
            throw new DocumentNotFoundException(uniqueId);
        }

        if (!document.getVisible()) {
            throw new DocumentNotFoundException(uniqueId);
        }

        Resident resident = getResident(document);
        if (resident == null || isInvisible(resident)) {
            throw new DocumentNotFoundException(uniqueId);
        }

        if (resident.isOptOut()) {
            throw new ResidentOptedOutException();
        }
        setIsCdaDocument(document);
        return document;
    }


    @Override
    public Resident getResident(Document document) {
        return getResident(document, false);
    }
    
    @Override
    public Resident getResident(Document document, boolean includeOptOutResident) {
        String databaseAltId = document.getResidentDatabaseAlternativeId();
        Database database = databasesDao.getDatabaseByAlternativeId(databaseAltId);
        long databaseId = database.getId();
        String residentLegacyId = document.getResidentLegacyId();
        return residentDao.getResident(databaseId, residentLegacyId, true); //May be null
    }

    @Override
    public Long getResident(long documentId) {
        Document document = documentDao.findDocument(documentId);
        if (document == null) {
            throw new DocumentNotFoundException(documentId);
        }

        String databaseAltId = document.getResidentDatabaseAlternativeId();
        Database database = databasesDao.getDatabaseByAlternativeId(databaseAltId);
        long databaseId = database.getId();
        String residentLegacyId = document.getResidentLegacyId();

        Resident resident = residentDao.getResident(databaseId, residentLegacyId);
        if (resident == null) {
            throw new DocumentNotFoundException(documentId);
        } else {
            return resident.getId();
        }
    }

    @Override
    public Employee getAuthor(Document document) {
        String databaseAltId = document.getAuthorDatabaseAlternativeId();
        Database database = databasesDao.getDatabaseByAlternativeId(databaseAltId);
        long databaseId = database.getId();
        String authorLegacyId = document.getAuthorLegacyId();
        return employeeDao.getEmployee(databaseId, authorLegacyId); //May be null
    }

    @Override
    public File getDocumentFile(Document document) {
        String documentFolder = getEmployeeUploadsFolder(document.getAuthorDatabaseAlternativeId(),
                document.getAuthorLegacyId());
        String documentPath = documentFolder + File.separator + buildFileName(document.getUuid());
        return new File(documentPath);
    }

    @Override
    public InputStream getDocumentInputStream(Document document) throws FileNotFoundException {
        return new FileInputStream(getDocumentFile(document));
    }

    @Override
    @Transactional
    public void updateDocument(Document document) throws IOException {
        documentDao.updateDocument(document);
        xdsRegistryConnectorService.updateDocumentTitleInRepository(document);
        if (xdsRegistryConnectorService.isDocumentVisibleInRegistry(document)) {
            xdsRegistryConnectorService.approveDocumentInRepository(document.getUuid());
        } else {
            xdsRegistryConnectorService.deprecateDocumentInRepository(document.getUuid());
        }
    }

    private boolean isInvisible(Resident resident) {
        Organization facilityOrganization = resident.getFacility();
        boolean isInvisible = (facilityOrganization.getInactive() != null && facilityOrganization.getInactive())
                || (facilityOrganization.getTestingTraining() != null && facilityOrganization.getTestingTraining());
        return isInvisible;
    }

    private String getEmployeeUploadsFolder(String employeeDatabaseAlternativeId, String employeeLegacyId) {
        return documentsUploadBaseDir + File.separator + "database_" + employeeDatabaseAlternativeId
                + "_user_" + employeeLegacyId;
    }

    private String buildFileName(String documentUuid) {
        return "file_" + documentUuid;
    }

    public void setDocumentsUploadBaseDir(String documentsUploadBaseDir) {
        this.documentsUploadBaseDir = documentsUploadBaseDir;
    }

    public void setDocumentDao(DocumentDaoImpl documentDao) {
        this.documentDao = documentDao;
    }

    public void setResidentDao(ResidentDao residentDao) {
        this.residentDao = residentDao;
    }

    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public void setDatabasesDao(DatabasesDao databasesDao) {
        this.databasesDao = databasesDao;
    }

    public void synchronizeAllDocumentsWithXdsRegistry(Writer writer, Date fromTime) throws IOException {
        List<Long> ids = documentDao.findAllIds(fromTime);
        System.out.println("Found " + ids.size() + " documents");
        writer.write("Found " + ids.size() + " documents");

        for (Long id : ids) {
            Document doc = findDocument(id);
            Boolean existsInExchange = doc.getVisible();
            File f = existsInExchange ? getDocumentFile(doc) : null;
            Resident res = getResident(doc);
            try {
                String docResponse = xdsRegistryConnectorService.synchronizeDocWithRepository(f, doc, res.getId());
                System.out.println(docResponse + "\n");
                writer.write(docResponse + "<br/>");
            } catch (NHINIoException e) {
                System.out.println("Document " + id + " Failed: " + e.getMessage());
                writer.write("Document " + id + " Failed: " + e.getMessage());
            }

        }
    }

    @Override
    @Transactional
    public List<Document> setIsCdaDocument(List<Document> documents) {
        if (CollectionUtils.isNotEmpty(documents)) {
            for (Document document : documents) {
                setIsCdaDocument(document);
            }
        }
        return documents;
    }

    @Override
    @Transactional
    public void setIsCdaDocument(Document document) {
        if (document.getIsCDA() == null) {
            document.setIsCDA(defineIsCdaDocument(document));
            documentDao.saveDocument(document);
        }
    }

    @Override
    public boolean defineIsCdaDocument(Document document) {
        try {
            final ClinicalDocument clinicalDocument = CDAUtil.load(new FileInputStream(getDocumentFile(document)));
            logger.info("Document with id=[{}] parsed as CDA", document.getId());
            return true;
        } catch (Exception ex) {
            logger.info("Document with id=[{}] not parsed as CDA, reason is: {}", document.getId(), ExceptionUtils.getMessage(ex));
            return false;
        }
    }

    @Override
    public byte[] readDocument(Document document) {
        try {
            String documentFolder = getEmployeeUploadsFolder(document.getAuthorDatabaseAlternativeId(),
                    document.getAuthorLegacyId());
            String documentPath = documentFolder + File.separator + buildFileName(document.getUuid());
            byte[] encrypted = java.nio.file.Files.readAllBytes(Paths.get(documentPath));
            return documentEncryptionService.decrypt(encrypted);
        }
        catch (IOException e) {
            throw new RuntimeException("I/O error occurred during file load", e);
        }
    }
}
