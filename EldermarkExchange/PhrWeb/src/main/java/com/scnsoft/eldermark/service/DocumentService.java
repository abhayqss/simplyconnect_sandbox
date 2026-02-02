package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.services.Report;
import com.scnsoft.eldermark.services.ReportGenerator;
import com.scnsoft.eldermark.services.ReportGeneratorFactory;
import com.scnsoft.eldermark.services.cda.CdaFacade;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.shared.DocumentType;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.exceptions.DocumentNotFoundException;
import com.scnsoft.eldermark.shared.utils.MathUtils;
import com.scnsoft.eldermark.web.entity.DateDto;
import com.scnsoft.eldermark.web.entity.DocumentInfoDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 * Created on 5/17/2017.
 */
@Service
@Transactional(readOnly = true)
public class DocumentService extends BasePhrService {

    @Autowired
    DocumentDao documentDao;

    @Autowired
    DocumentFacade documentFacade;

    @Autowired
    ReportGeneratorFactory reportGeneratorFactory;

    @Autowired
    ResidentDao residentDao;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    private CdaFacade cdaFacade;

    public List<DocumentInfoDto> getDocumentsForUser(Long userId, Pageable pageable) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);

        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);
        return getDocuments(activeResidentIds, getRequestingEmployee(userId), pageable);
    }

    public List<DocumentInfoDto> getDocumentsForReceiver(Long receiverId, Pageable pageable) {
        final ResidentCareTeamMember careTeamMember = residentCareTeamMemberDao.get(receiverId);
        careTeamSecurityUtils.checkAccessToCareTeamMemberOrThrow(careTeamMember, AccessRight.Code.MY_PHR);

        final Collection<Long> activeResidentIds = mpiService.listResidentWithMergedResidents(careTeamMember.getResidentId());
        return getDocuments(activeResidentIds, getEmployeeOrThrow(careTeamSecurityUtils.getCurrentUser()), pageable);
    }

    private List<DocumentInfoDto> getDocuments(Collection<Long> activeResidentIds, Employee requestingEmployee, Pageable pageable) {
        final List<Document> documents;
        if (pageable != null) {
            // shift 2 items (ccd, facesheet)
            final int offset = pageable.getPageNumber() == 0 ? 0 : pageable.getOffset() - 2;
            final int pageSize = pageable.getPageNumber() == 0 ? pageable.getPageSize() - 2 : pageable.getPageSize();
            documents = documentDao.queryForDocumentsByResidentIdIn(activeResidentIds, requestingEmployee, offset, pageSize);
        } else {
            documents = documentDao.queryForDocumentsByResidentIdIn(activeResidentIds, requestingEmployee, null);
        }

        String dataSource;
        if (activeResidentIds.size() > 1) {
            dataSource = "Merged data";
        } else {
            final Long residentId = activeResidentIds.iterator().next();
            final Resident resident = residentDao.get(residentId);
            dataSource = resident.getFacility().getName();
        }

        documentFacade.setIsCdaDocument(documents);
        List<DocumentInfoDto> dtos = transform(documents);
        if (pageable == null || pageable.getPageNumber() == 0) {
            dtos.add(0, createReportMetadataEntry(DocumentType.FACESHEET, dataSource));
            dtos.add(0, createReportMetadataEntry(DocumentType.CCD, dataSource));
        }

        return dtos;
    }

    public DocumentInfoDto getDocument(Long userId, Long documentId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);

        Document document = documentDao.findDocument(documentId);
        documentFacade.setIsCdaDocument(document);
        validateDocumentAssociation(userId, document);

        return transform(document);
    }

    private Long countDocuments(Collection<Long> activeResidentIds, Employee requestingEmployee) {
        final Long totalCount = documentDao.countDocumentsByResidentIdIn(activeResidentIds, requestingEmployee);

        return totalCount + 2;
    }

    public Long countDocumentsForUser(Long userId) {
        final Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);
        final Employee requestingEmployee = getRequestingEmployee(userId);
        return countDocuments(activeResidentIds, requestingEmployee);
    }

    public Long countDocumentsForReceiver(Long receiverId) {
        final ResidentCareTeamMember careTeamMember = residentCareTeamMemberDao.get(receiverId);
        final Collection<Long> activeResidentIds = mpiService.listResidentWithMergedResidents(careTeamMember.getResidentId());
        final Employee requestingEmployee = getEmployeeOrThrow(careTeamSecurityUtils.getCurrentUser());

        return countDocuments(activeResidentIds, requestingEmployee);
    }

    private Employee getRequestingEmployee(Long userId) {
        Employee requestingEmployee = null;
        if (!PhrSecurityUtils.checkAccessToUserInfo(userId)) {
            Long currentUserId = PhrSecurityUtils.getCurrentUserId();
            Long employeeId = getEmployeeIdOrThrow(currentUserId);
            requestingEmployee = employeeDao.get(employeeId);
        }
        return requestingEmployee;
    }

    public void downloadCustomDocument(Long userId, Long documentId, HttpServletResponse response) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);

        try {
            DocumentBean document = documentFacade.findDocument(documentId);
            validateDocumentAssociation(userId, document);
            documentFacade.downloadOrViewCustomDocument(document, response, false);
        } catch (DocumentNotFoundException exc) {
            exc.printStackTrace();
            throw new PhrException(PhrExceptionType.DOCUMENT_NOT_FOUND);
        }
    }

    public void downloadCustomDocumentForReceiver(Long careReceiverId, Long documentId, HttpServletResponse response) {
        final ResidentCareTeamMember careTeamMember = residentCareTeamMemberDao.get(careReceiverId);
        careTeamSecurityUtils.checkAccessToCareTeamMemberOrThrow(careTeamMember, AccessRight.Code.MY_PHR);
        try {
            DocumentBean document = documentFacade.findDocument(documentId);
            validateDocumentAssociationWithCareReceiver(careTeamMember, document);
            documentFacade.downloadOrViewCustomDocument(document, response, false);
        } catch (DocumentNotFoundException exc) {
            exc.printStackTrace();
            throw new PhrException(PhrExceptionType.DOCUMENT_NOT_FOUND);
        }
    }

    public void downloadContinuityOfCareDocument(Long userId, HttpServletResponse response) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);

        final Long mainResidentId = getResidentIdOrThrow(userId);
        Collection<Long> residentIds = getResidentIdsOrThrow(userId);
        documentFacade.downloadOrViewReport(mainResidentId, new ArrayList<>(residentIds), "ccd", response, false);
    }

    public void downloadContinuityOfCareDocumentForReceiver(Long receiverId, HttpServletResponse response) {
        downloadReportForReceiver(receiverId, "ccd", response);
    }

    public void downloadFacesheetReport(Long userId, HttpServletResponse response) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);

        User user = careTeamSecurityUtils.getCurrentUser();
        final Long mainResidentId = getResidentIdOrThrow(userId);
        Collection<Long> residentIds = getResidentIdsOrThrow(userId);
        documentFacade.downloadOrViewReport(mainResidentId, new ArrayList<>(residentIds), "facesheet", response, false, user.getTimeZoneOffset() != null ? -user.getTimeZoneOffset() : null);
    }

    public void downloadFacesheetReportForReceiver(Long receiverId, HttpServletResponse response) {
        downloadReportForReceiver(receiverId, "facesheet", response);
    }

    public String getCdaHtmlViewForDocumentForUser(Long userId, Long documentId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        return cdaFacade.getCdaHtmlViewForDocument(documentId);
    }

    public String getCdaHtmlViewForDocumentForReceiver(Long receiverId, Long documentId) {
        careTeamSecurityUtils.checkAccessToCareTeamMemberOrThrow(receiverId, AccessRight.Code.MY_PHR);
        return cdaFacade.getCdaHtmlViewForDocument(documentId);
    }

    private void downloadReportForReceiver(Long receiverId, String reportType, HttpServletResponse response) {
        final ResidentCareTeamMember residentCareTeamMember = residentCareTeamMemberDao.get(receiverId);
        careTeamSecurityUtils.checkAccessToCareTeamMemberOrThrow(residentCareTeamMember, AccessRight.Code.MY_PHR);

        final Long mainResidentId = residentCareTeamMember.getResidentId();
        final Collection<Long> residentIds = mpiService.listResidentWithMergedResidents(mainResidentId);
        documentFacade.downloadOrViewReport(mainResidentId, new ArrayList<>(residentIds), reportType, response, false);
    }

    private void validateDocumentAssociation(Long userId, Document document) {
        if (document == null) {
            throw new PhrException(PhrExceptionType.DOCUMENT_NOT_FOUND);
        }
        final Long residentId = residentDao.getResidentId(document.getResidentDatabaseAlternativeId(), document.getResidentLegacyId());
        Collection<Long> residentIds = getResidentIds(userId);
        if (!residentIds.contains(residentId)) {
            throw new PhrException(PhrExceptionType.DOCUMENT_NOT_FOUND);
        }
    }

    private void validateDocumentAssociationWithCareReceiver(ResidentCareTeamMember careTeamMember, DocumentBean document) {
        final Collection<Long> residentIds = mpiService.listResidentWithMergedResidents(careTeamMember.getResidentId());
        if (document == null || !residentIds.contains(document.getResidentId())) {
            throw new PhrException(PhrExceptionType.DOCUMENT_NOT_FOUND);
        }
    }

    private void validateDocumentAssociation(Long userId, DocumentBean document) {
        Collection<Long> residentIds = getResidentIds(userId);
        if (document == null || !residentIds.contains(document.getResidentId())) {
            throw new PhrException(PhrExceptionType.DOCUMENT_NOT_FOUND);
        }
    }

    private static DocumentInfoDto transform(Document document) {
        DocumentInfoDto dto = new DocumentInfoDto();
        dto.setId(document.getId());
        dto.setTitle(FilenameUtils.getBaseName(document.getDocumentTitle()));
        dto.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(document.getDocumentTitle())));
        if (document.getCreationTime() != null) {
            DateDto dateDto = new DateDto();
            dateDto.setDateTime(document.getCreationTime().getTime());
            dateDto.setDateTimeStr(DATE_TIME_FORMAT.format(document.getCreationTime()));
            dto.setCreatedOn(dateDto);
        }
        dto.setSizeKb(MathUtils.round(document.getSize() / 1024.0, 2));
        dto.setHash(document.getHash());
        dto.setMimeType(document.getMimeType());

        final DocumentType documentType = document.getIsCDA() ? DocumentType.CCD : DocumentType.CUSTOM;
        dto.setType(StringUtils.capitalize(StringUtils.lowerCase(documentType.getName())));
        dto.setIsCdaViewable(document.getIsCDA());

        dto.setDataSource(document.getResidentDatabaseAlternativeId());

        return dto;
    }

    private DocumentInfoDto createReportMetadataEntry(DocumentType type, String dataSource) {
        String typeName = StringUtils.lowerCase(type.getName());
        ReportGenerator generator = reportGeneratorFactory.getGenerator(typeName);
        Report metadata = generator.metadata();

        DocumentInfoDto reportDto = new DocumentInfoDto();
        reportDto.setId(null);
        reportDto.setTitle(FilenameUtils.getBaseName(metadata.getDocumentTitle()));
        reportDto.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(metadata.getDocumentTitle())));
        reportDto.setCreatedOn(null);
        reportDto.setSizeKb(null);
        reportDto.setMimeType(metadata.getMimeType());
        reportDto.setType("ccd".equals(typeName) ? "CCD" : StringUtils.capitalize(typeName));
        reportDto.setDataSource(dataSource);

        return reportDto;
    }

    private static List<DocumentInfoDto> transform(List<Document> documents) {
        List<DocumentInfoDto> dtos = new ArrayList<>();
        for (Document document : documents) {
            dtos.add(transform(document));
        }
        return dtos;
    }

}
