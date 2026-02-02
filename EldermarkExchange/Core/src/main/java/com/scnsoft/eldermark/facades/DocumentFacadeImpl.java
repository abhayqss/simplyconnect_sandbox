package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoIntegrationDocument;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.facades.exceptions.DatabaseNotFoundException;
import com.scnsoft.eldermark.facades.exceptions.EmployeeNotFoundException;
import com.scnsoft.eldermark.services.*;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.services.cda.CdaFacade;
import com.scnsoft.eldermark.shared.DocumentDto;
import com.scnsoft.eldermark.shared.DocumentType;
import com.scnsoft.eldermark.shared.exceptions.CcdGenerationException;
import com.scnsoft.eldermark.shared.exceptions.DocumentSharePolicyViolation;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Component
@Transactional
public class DocumentFacadeImpl implements DocumentFacade {
    private static final Logger logger = LoggerFactory.getLogger(DocumentFacadeImpl.class);

    public static final String CCD_NAME = "ccd.xml";
    public static final String FACESHEET_NAME = "facesheet.pdf";
    public static final String MARCO_DOCUMENTS_DATASOURCE = "Simply Connect HIE";

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private DatabasesService databasesService;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private ReportGeneratorFactory reportGeneratorFactory;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CdaFacade cdaFacade;

    @Value("${xds.exchange.user}")
    private String xdsUserName;

    @Value("${xds.exchange.company}")
    private String xdsUserOrganisation;

    @Override
    public Page<DocumentDto> queryForDocuments(Long residentId, String filter, final Pageable pageable,
                                               boolean aggregated) {
        int limit = pageable.getPageSize();
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be a positive integer");
        }

        int offset = pageable.getOffset();
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }

        boolean ccdMatchesFilter = StringUtils.isBlank(filter) || CCD_NAME.matches(".*" + filter.toLowerCase() + ".*");
        boolean facesheetMatchesFilter = StringUtils.isBlank(filter)
                || FACESHEET_NAME.matches(".*" + filter.toLowerCase() + ".*");
        boolean showCcd = false;
        boolean showFacesheet = false;

        if (ccdMatchesFilter && !facesheetMatchesFilter) {
            if (offset == 0) {
                showCcd = true;
                limit--;
            } else {
                offset--;
            }
        } else if (!ccdMatchesFilter && facesheetMatchesFilter) {
            if (offset == 0) {
                showFacesheet = true;
                limit--;
            } else {
                offset--;
            }
        } else if (ccdMatchesFilter && facesheetMatchesFilter) {
            if (offset == 0) {
                if (limit == 1) {
                    showCcd = true;
                    limit--;
                } else {
                    showCcd = true;
                    showFacesheet = true;
                    limit -= 2;
                }
            } else if (offset == 1) {
                showFacesheet = true;
                limit--;
                offset = 0;
            } else {
                offset -= 2;
            }
        }

        Resident resident = residentService.getResident(residentId);
        Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();
        List<Document> customDocs = documentService.queryForDocuments(resident, filter, employee, offset, limit);

        if (aggregated) {
            Collection<Resident> mergedResidents = residentService.getDirectMergedResidents(resident);
            for (Resident mergedResident : mergedResidents) {
                List<Document> customDocsOfMergedResident = documentService.queryForDocuments(mergedResident, filter,
                        employee, offset, limit);
                customDocs.addAll(customDocsOfMergedResident);
            }
        }

        List<DocumentDto> dtoList = new ArrayList<DocumentDto>();
        if (showCcd) {
            dtoList.add(createReportMetadataEntry(DocumentType.CCD));
        }
        if (showFacesheet) {
            dtoList.add(createReportMetadataEntry(DocumentType.FACESHEET));
        }
        for (Document doc : customDocs) {
            dtoList.add(createDto(doc));
        }

        Long documentCount = documentService.getCustomDocumentCount(resident, filter, employee);
        if (ccdMatchesFilter) {
            documentCount++;
        }
        if (facesheetMatchesFilter) {
            documentCount++;
        }

        if (pageable.getSort() != null) {
            Collections.sort(dtoList, new Comparator<DocumentDto>() {
                public int compare(DocumentDto d1, DocumentDto d2) {
                    int result = 0;
                    Sort.Order sortOrder;
                    if ((sortOrder = pageable.getSort().getOrderFor("documentTitle")) != null) {
                        result = d1.getDocumentTitle().toLowerCase().compareTo(d2.getDocumentTitle().toLowerCase());
                    } else if ((sortOrder = pageable.getSort().getOrderFor("authorPerson")) != null) {
                        result = d1.getAuthorPerson().toLowerCase().compareTo(d2.getAuthorPerson().toLowerCase());
                    } else if ((sortOrder = pageable.getSort().getOrderFor("size")) != null) {
                        Integer size1 = d1.getSize() == null ? 0 : d1.getSize();
                        Integer size2 = d2.getSize() == null ? 0 : d2.getSize();
                        result = size1.compareTo(size2);
                    } else if ((sortOrder = pageable.getSort().getOrderFor("creationTime")) != null) {
                        Long time1 = d1.getCreationTime() == null ? 0 : d1.getCreationTime().getTime();
                        Long time2 = d2.getCreationTime() == null ? 0 : d2.getCreationTime().getTime();
                        result = time1.compareTo(time2);
                    }

                    if (Sort.Direction.DESC.equals(sortOrder.getDirection())) {
                        result *= -1;
                    }
                    return result;
                }
            });
        }

        return new PageImpl<>(dtoList, pageable, documentCount);
    }

    @Override
    public Page<DocumentDto> queryForDocuments(Long residentId, String filter, int offset, int limit) {
        return queryForDocuments(residentId, filter, new PageRequest(offset / limit, limit), Boolean.FALSE);
    }

    @Override
    public List<DocumentDto> queryForDocuments(Long residentId) {
        Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();
        List<DocumentDto> dtoList = new ArrayList<DocumentDto>();

        Resident resident = residentService.getResident(residentId);
        List<Document> customDocs = documentService.queryForDocuments(resident, employee);
        for (Document doc : customDocs) {
            dtoList.add(createDto(doc));
        }

        logger.info("Found {} custom documents for resident = {}", dtoList.size(), residentId);

        dtoList.add(createReportMetadataEntry(DocumentType.CCD));
        dtoList.add(createReportMetadataEntry(DocumentType.FACESHEET));

        return dtoList;
    }

    @Override
    public Document saveDocument(DocumentMetadata metadata, long residentId, long authorId, boolean isSharedWithAll,
                                 List<Long> idsOfDatabasesToShareWith, SaveDocumentCallback callback) {
        return saveDocument(metadata, residentId, authorId, isSharedWithAll, idsOfDatabasesToShareWith, false,
                callback, false);
    }

    @Override
    public Document saveDocument(DocumentMetadata metadata, long residentId, long authorId, boolean isSharedWithAll, List<Long> idsOfDatabasesToShareWith, boolean includeOptOutResident, SaveDocumentCallback callback, Boolean isCloud) {
        return saveDocument(metadata, residentId, authorId, isSharedWithAll, idsOfDatabasesToShareWith, false,
                callback, false, null);
    }

    @Override
    public Document saveDocument(DocumentMetadata metadata, long residentId, long authorId, boolean isSharedWithAll,
            List<Long> idsOfDatabasesToShareWith, boolean includeOptOutResident, SaveDocumentCallback callback, Boolean isCloud, byte[] fileContent) {
        Employee author = employeeDao.getEmployee(authorId);
        if (author == null) {
            throw new EmployeeNotFoundException(authorId);
        }

        Resident resident = residentService.getResident(residentId, includeOptOutResident);

        Set<Long> idsOfDatabasesSet = new LinkedHashSet<Long>(idsOfDatabasesToShareWith);
        idsOfDatabasesToShareWith = new ArrayList<Long>(idsOfDatabasesSet);

        List<Database> databasesToShareWith = databasesService.getDatabasesByIds(idsOfDatabasesToShareWith);
        if (databasesToShareWith.size() != databasesToShareWith.size()) {
            throw new DatabaseNotFoundException(null);
        }
        try {
            Document document = documentService.saveDocument(metadata, resident, author, isSharedWithAll,
                    databasesToShareWith, callback, isCloud, fileContent);
            return document;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Document saveDocument(DocumentMetadata metadata, long residentId, long authorId, boolean isSharedWithAll,
                                 List<Long> idsOfDatabasesToShareWith, final File document) {
        return saveDocument(metadata, residentId, authorId, true,
                Collections.<Long>emptyList(), new SaveDocumentCallbackImpl() {
                    @Override
                    public void saveToFile(File file) {
                        try {
                            FileCopyUtils.copy(new FileInputStream(document), new FileOutputStream(file));
                        } catch (IOException e) {
                            logger.error("Failed to save file, due to - {}", e.getMessage());
                            throw new FileIOException("Failed to save file " + document.getName(), e);
                        }
                    }
                });
    }

    @Override
    public void deleteDocument(long id) {
        deleteDocument(id, false);
    }

    @Override
    public void deleteDocument(long id, boolean couldBeRestored) {
        try {
            documentService.deleteDocument(id, couldBeRestored);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void restoreDocument(long id) {
        restoreDocument(id, false);
    }

    @Override
    public void restoreDocument(long id, boolean includeOptOutResident) {
        try {
            documentService.restoreDocument(id, includeOptOutResident);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DocumentBean findDocument(long id) {
        Document document = documentService.findDocument(id);
        /*
         * Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();
         *
         * if (!enforceSharingPolicy(document, employee, Collections.EMPTY_LIST)) {
         * throw new DocumentSharePolicyViolation(document.getId ()); }
         */
        return findDocument(document);
    }

    @Override
    public DocumentBean findDocument(long documentId, long employeeId, List<Long> orSharedWith, boolean visibleOnly,
            boolean includeOptOutResident) {
        Document document = documentService.findDocument(documentId, visibleOnly, includeOptOutResident);
        Employee employee = employeeService.getEmployee(employeeId);

        if (!enforceSharingPolicy(document, employee, orSharedWith)) {
            throw new DocumentSharePolicyViolation(document.getId());
        }

        return findDocument(document);
    }

    private DocumentBean findDocument(Document document) {

        Resident resident = documentService.getResident(document); // May be null

        Employee author = documentService.getAuthor(document); //May be null

        DocumentBean documentBean = new DocumentBean();
        documentBean.setId(document.getId());
        documentBean.setDocumentTitle(document.getDocumentTitle());
        documentBean.setMimeType(document.getMimeType());

        if (author != null) {
            documentBean.setAuthorId(author.getId());
            documentBean.setAuthorFirstName(author.getFirstName());
            documentBean.setAuthorLastName(author.getLastName());
        }

        documentBean.setOriginalFileName(document.getOriginalFileName());
        documentBean.setCreationTime(document.getCreationTime());

        File documentFile = documentService.getDocumentFile(document);
        documentBean.setFile(documentFile);
        documentBean.setFileContent(documentService.readDocument(document));
        documentBean.setSize(document.getSize());
        if (resident != null) {
            documentBean.setResidentId(resident.getId());
        }
        return documentBean;
    }

    /**
     * Counts all documents of resident excluding the CCD and Facesheet
     *
     * @param residentId resident ID
     * @param aggregated if {@code true}, counts all documents of matched residents
     */
    @Override
    public long getCustomDocumentCount(Long residentId, String filter, boolean aggregated) {
        Resident resident = residentService.getResident(residentId);
        Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();
        Long count = documentService.getCustomDocumentCount(resident, filter, employee);
        if (aggregated) {
            Collection<Resident> mergedResidents = residentService.getDirectMergedResidents(resident);
            for (Resident mergedResident : mergedResidents) {
                Long countForMerged = documentService.getCustomDocumentCount(mergedResident, filter, employee);
                count += countForMerged;
            }
        }
        return count;
    }

    /**
     * Counts all documents of resident including the CCD and Facesheet
     *
     * @param residentId resident ID
     * @param aggregated if {@code true}, counts all documents of matched residents
     */
    @Override
    public long getDocumentCount(Long residentId, String filter, boolean aggregated) {
        return getCustomDocumentCount(residentId, filter, aggregated) + 2;
    }

    @Override
    public boolean isAttachedToResident(Long residentId, Long documentId) {
        if (residentId == null || documentId == null)
            return false;

        return residentId.equals(documentService.getResident(documentId));
    }

    @Override
    public boolean isAttachedToMergedResidents(Long residentId, Long documentId) {
        if (residentId == null || documentId == null)
            return false;

        final Resident resident = residentService.getResident(residentId);
        Long id = documentService.getResident(documentId);
        Collection<Resident> mergedResidents = residentService.getDirectMergedResidents(resident);
        for (Resident mergedResident : mergedResidents) {
            if (mergedResident.getId().equals(id))
                return true;
        }

        return false;
    }

    @Override
    public void updateDocumentTitle(long documentId, long employeeId, List<Long> orSharedWith, String title,
            boolean includeOptOutResident) {
        Document document = documentService.findDocument(documentId, false, includeOptOutResident);
        Employee employee = employeeService.getEmployee(employeeId);

        if (!enforceSharingPolicy(document, employee, orSharedWith)) {
            throw new DocumentSharePolicyViolation(document.getId());
        }

        document.setDocumentTitle(title.trim());
        document.setOriginalFileName(title.trim());
        try {
            documentService.updateDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateDocumentSharingPolicy(long documentId, long employeeId, List<Long> orSharedWith,
                                            List<Long> idsOfDatabasesToShareWith, boolean includeOptOutResident) {
        Document document = documentService.findDocument(documentId, false, includeOptOutResident);
        Employee employee = employeeService.getEmployee(employeeId);

        if (!enforceSharingPolicy(document, employee, orSharedWith)) {
            throw new DocumentSharePolicyViolation(document.getId());
        }

        List<Database> databasesToShareWith = databasesService.getDatabasesByIds(idsOfDatabasesToShareWith);
        if (databasesToShareWith.size() != databasesToShareWith.size()) {
            throw new DatabaseNotFoundException(null);
        }

        document.setSharedWithDatabases(databasesToShareWith);
        try {
            documentService.updateDocument(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void downloadOrViewCustomDocument(DocumentBean document, HttpServletResponse response, boolean isViewMode) {
        String openType = isViewMode ? "inline" : "attachment";

        response.setContentType(document.getMimeType());
        response.setHeader("Content-Disposition", openType + ";filename=\"" + document.getOriginalFileName() + "\"");

        try {
            FileCopyUtils.copy(new FileInputStream(document.getFile()), response.getOutputStream());
        } catch (IOException e) {
            throw new FileIOException("Failed to save file " + document.getOriginalFileName(), e);
        }
    }

    @Override
    public List<Document> setIsCdaDocument(List<Document> documents) {
        return documentService.setIsCdaDocument(documents);
    }

    @Override
    public void setIsCdaDocument(Document document) {
        documentService.setIsCdaDocument(document);
    }

    @Override
    public void downloadOrViewReport(Long mainResidentId, List<Long> residentIds, String reportType,
                                     HttpServletResponse response, boolean isViewMode) {
        downloadOrViewReport(mainResidentId, residentIds, reportType, response, isViewMode, null);
    }

    @Override
    public void downloadOrViewReport(Long mainResidentId, List<Long> residentIds, String reportType,
                                     HttpServletResponse response, boolean isViewMode, Integer timeZoneOffsetInMinutes) {
        ReportGenerator generator = reportGeneratorFactory.getGenerator(reportType);
        Report document = generator.generate(mainResidentId, residentIds, timeZoneOffsetInMinutes);

        copyDocumentContentToResponse(document, response, isViewMode);
    }

    @Override
    public void downloadOrViewReport(Long residentId, String reportType, HttpServletResponse response,
                                     boolean isViewMode, Boolean aggregated) {
        downloadOrViewReport(residentId, reportType, response, isViewMode, aggregated, null);
    }

    @Override
    public void downloadOrViewReport(Long residentId, String reportType, HttpServletResponse response,
                                     boolean isViewMode, Boolean aggregated, Integer timeZoneOffsetInMinutes) {
        ReportGenerator generator = reportGeneratorFactory.getGenerator(reportType);
        Report document = generator.generate(residentId, Boolean.TRUE.equals(aggregated), timeZoneOffsetInMinutes);

        copyDocumentContentToResponse(document, response, isViewMode);
    }

    public Report generateReport(Long residentId, boolean aggregated, String reportType) {
        final ReportGenerator generator = reportGeneratorFactory.getGenerator(reportType);
        return generator.generate(residentId, aggregated);
    }

    private static void copyDocumentContentToResponse(Report document, HttpServletResponse response,
                                                      boolean isViewMode) {
        String contentType = isViewMode ? "text/plain" : document.getMimeType();
        String openType = isViewMode ? "inline" : "attachment";

        response.setContentType(contentType);
        response.setHeader("Content-Disposition", openType + ";filename=\"" + document.getDocumentTitle() + "\"");

        try {
            FileCopyUtils.copy(document.getInputStream(), response.getOutputStream());
        } catch (IOException e) {
            throw new CcdGenerationException();
        }
    }

    private boolean enforceSharingPolicy(Document document, Employee employee, List<Long> orSharedWith) {
        boolean sharedWithAll = document.isEldermarkShared();
        boolean sharedWithMyCompany = document.getSharedWithDatabasesIds().contains(employee.getDatabaseId());

        List<Long> common = new ArrayList<Long>(document.getSharedWithDatabasesIds());
        common.retainAll(orSharedWith);
        boolean sharedWithSomeCompany = !common.isEmpty();

        return sharedWithAll || sharedWithMyCompany || sharedWithSomeCompany;
    }

    private DocumentDto createReportMetadataEntry(DocumentType type) {
        ReportGenerator generator = reportGeneratorFactory.getGenerator(type.getName().toLowerCase());
        Report metadata = generator.metadata();

        DocumentDto document = new DocumentDto();
        document.setId("0");
        document.setDocumentTitle(metadata.getDocumentTitle());
        document.setAuthorId(null);
        document.setAuthorPerson("SYSTEM");
        document.setMimeType(metadata.getMimeType());
        document.setSize(null);
        document.setCreationTime(null);
        document.setOriginalFileName(null);
        document.setDocumentType(type);
        document.setDataSource("Simply Connect HIE");

        return document;
    }

    private DocumentDto createDto(Document document) {
        String authorFirstName = null;
        String authorLastName = null;
        Long authorId = null;

        DocumentDto documentDto = new DocumentDto();

        Database dataSource;
        Organization community = null;
        Employee author = documentService.getAuthor(document); // May be null
        if (author != null) {
            authorFirstName = author.getFirstName();
            authorLastName = author.getLastName();
            authorId = author.getId();

            if (xdsUserName.equals(author.getLoginName())
                    && xdsUserOrganisation.equalsIgnoreCase(author.getDatabaseAlternativeId())) {
                // document came from xds channel, use residents's datasource
                // instead of author's
                final Resident resident = documentService.getResident(document);
                if (resident != null) {
                    dataSource = resident.getDatabase();
                    community = resident.getFacility();
                } else {
                    dataSource = databasesService
                            .getDatabaseByAlternativeId(document.getResidentDatabaseAlternativeId());
                }
            } else {
                dataSource = author.getDatabase();
                Long authorCommunityId = author.getCommunityId();
                if (authorCommunityId != null) {
                    community = organizationService.getOrganization(authorCommunityId);
                }
            }
        } else {
            dataSource = databasesService.getDatabaseByAlternativeId(document.getAuthorDatabaseAlternativeId());
        }
        documentDto.setDataSource(dataSource.getName());
        documentDto.setDataSourceOid(dataSource.getOid());
        if (community != null) {
            documentDto.setCommunity(community.getName());
            documentDto.setCommunityOid(community.getOid());
        }

        String authorFullName;
        if (authorFirstName != null && authorLastName != null) {
            authorFullName = authorFirstName + " " + authorLastName;
        } else if (authorFirstName != null) {
            authorFullName = authorFirstName;
        } else if (authorLastName != null) {
            authorFullName = authorLastName;
        } else {
            authorFullName = "";
        }

        documentDto.setId(document.getId());
        documentDto.setDocumentTitle(document.getDocumentTitle());
        documentDto.setAuthorId(authorId);
        documentDto.setAuthorPerson(authorFullName);
        documentDto.setCreationTime(document.getCreationTime());
        documentDto.setMimeType(document.getMimeType());
        documentDto.setSize(document.getSize());
        documentDto.setDocumentType(DocumentType.CUSTOM);
        documentDto.setOriginalFileName(document.getOriginalFileName());
        documentDto.setCdaViewable(document.getIsCDA());

        adjustDocumentForMarcoIntegration(document, documentDto);
        adjustLabResultsDocument(document, documentDto);

        return documentDto;
    }

    // change author name and document type here for lab results document
    private void adjustLabResultsDocument(Document document, DocumentDto documentDto) {
        if (document.getLabResearchOrder() != null) {
            documentDto.setDocumentType(DocumentType.LAB_RESULTS);
            documentDto.setAuthorPerson("SYSTEM");
        }
    }

    // change author name and document type here if the document came from marco
    private void adjustDocumentForMarcoIntegration(Document document, DocumentDto documentDto) {
        MarcoIntegrationDocument marcoIntegrationDocLog = document.getMarcoIntegrationDocument();
        if (marcoIntegrationDocLog != null) {
            if (StringUtils.isNotEmpty(marcoIntegrationDocLog.getAuthor())) {
                documentDto.setAuthorPerson(marcoIntegrationDocLog.getAuthor());
            }
            documentDto.setDataSource(MARCO_DOCUMENTS_DATASOURCE);
            documentDto.setDocumentType(DocumentType.FAX);
        }
    }
}
