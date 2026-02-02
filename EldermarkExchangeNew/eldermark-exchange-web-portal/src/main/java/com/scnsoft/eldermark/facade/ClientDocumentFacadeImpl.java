package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.InternalClientDocumentFilter;
import com.scnsoft.eldermark.dto.EditDocumentDto;
import com.scnsoft.eldermark.dto.UploadClientDocumentDto;
import com.scnsoft.eldermark.dto.document.ClientDocumentListItemDto;
import com.scnsoft.eldermark.dto.document.DocumentDto;
import com.scnsoft.eldermark.dto.filter.ClientDocumentFilter;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.document.ClientDocumentUploadData;
import com.scnsoft.eldermark.entity.document.DocumentEditableData;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureStatus;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.CdaTransformationException;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.ServicePlanService;
import com.scnsoft.eldermark.service.document.ClientDocumentSecurityService;
import com.scnsoft.eldermark.service.document.ClientDocumentService;
import com.scnsoft.eldermark.service.document.UploadClientDocumentService;
import com.scnsoft.eldermark.service.document.cda.CdaToHtmlService;
import com.scnsoft.eldermark.service.document.cda.generator.CcdGeneratorService;
import com.scnsoft.eldermark.service.document.facesheet.FacesheetService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.DocumentUtils;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional
public class ClientDocumentFacadeImpl implements ClientDocumentFacade {

    private final Set<String> ALLOWED_EXTENSIONS = Set.of("DOCX", "PDF", "XLSX", "TXT", "JPG", "JPEG", "GIF", "PNG", "TIFF", "TIF", "XML", "DOC", "XLS");

    @Autowired
    private ClientDocumentSecurityService clientDocumentSecurityService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientDocumentService clientDocumentService;

    @Autowired
    private UploadClientDocumentService uploadClientDocumentService;

    @Autowired
    private CcdGeneratorService ccdGenerator;

    @Autowired
    private FacesheetService facesheetGenerator;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private CdaToHtmlService cdaToHtmlService;

    @Autowired
    private Converter<ClientDocument, ClientDocumentListItemDto> documentListItemDtoConverter;

    @Autowired
    private Converter<ClientDocument, DocumentDto> documentDtoConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ServicePlanService servicePlanService;

    @Override
    @Transactional
    @PreAuthorize("@clientDocumentSecurityService.canViewList()")
    public Page<ClientDocumentListItemDto> find(ClientDocumentFilter documentFilter, Pageable pageRequest) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return clientDocumentService.find(createInternalDocumentFilter(documentFilter),
                permissionFilter,
                PaginationUtils.applyEntitySort(pageRequest, ClientDocumentListItemDto.class)).map(documentListItemDtoConverter::convert);
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canView(#id)")
    public DocumentDto findById(Long id) {
        return documentDtoConverter.convert(clientDocumentService.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientDocumentSecurityService.canViewList()")
    public Long count(ClientDocumentFilter documentFilter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientDocumentService.count(createInternalDocumentFilter(documentFilter), permissionFilter);
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDownload(#id)")
    public void download(@P("id") Long id, HttpServletResponse response, boolean isViewMode) {
        var document = clientDocumentService.findById(id);
        WriterUtils.copyDocumentContentToResponse(document.getDocumentTitle(), () -> clientDocumentService.readDocument(document),
                document.getMimeType(), isViewMode, response);
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDownloadAll(#documentIds)")
    public void downloadMultiple(@P("documentIds") Collection<Long> documentIds, HttpServletResponse response) {
        var documents = clientDocumentService.findAllByIds(documentIds);
        downloadDocuments(documents, response);
    }

    @Override
    public void downloadMultiple(ClientDocumentFilter documentFilter, HttpServletResponse response) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var documents = clientDocumentService.find(createInternalDocumentFilter(documentFilter), permissionFilter, Pageable.unpaged()).getContent();
        downloadDocuments(documents, response);
    }

    private void downloadDocuments(List<ClientDocument> documents, HttpServletResponse response) {
        var documentByTitles = documents.stream().collect(Collectors.groupingBy(ClientDocument::getDocumentTitle));
        var documentData = documentByTitles.values().stream()
                .peek(this::updateDocumentTitle)
                .flatMap(Collection::stream)
                .map(document -> WriterUtils.FileProvider.of(
                        document.getDocumentTitle(),
                        document.getMimeType(),
                        () -> clientDocumentService.readDocument(document)
                ))
                .collect(Collectors.toList());

        var zipData = WriterUtils.generateZip(documentData);
        WriterUtils.copyBytesAsZipToResponse("Documents", zipData, response);
    }

    private void updateDocumentTitle(List<ClientDocument> documents) {
        if (documents.size() > 1) {
            for (var i = 1; i < documents.size(); i++) {
                var document = documents.get(i);
                var index = document.getDocumentTitle().lastIndexOf(".");
                var uniqueTitle = document.getDocumentTitle().substring(0, index) + " (" + i + ")" + document.getDocumentTitle().substring(index);
                document.setDocumentTitle(uniqueTitle);
            }
        }
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDownloadFacesheet(#clientId)")
    public void downloadFacesheet(@P("clientId") Long clientId, HttpServletResponse response, boolean isViewMode, Boolean aggregated, ZoneId zoneId) {
        var document = facesheetGenerator.generate(clientId, Boolean.TRUE.equals(aggregated), zoneId);
        WriterUtils.copyDocumentContentToResponse(document, isViewMode, response);
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDownloadCcd(#clientId)")
    public void downloadCcd(@P("clientId") Long clientId, HttpServletResponse response, boolean isViewMode, Boolean aggregated) {
        var document = ccdGenerator.generate(clientId, Boolean.TRUE.equals(aggregated));
        WriterUtils.copyDocumentContentToResponse(document, isViewMode, response);
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDownload(#documentId)")
    public String cdaToHtml(@P("documentId") Long documentId) {
        ClientDocument document = clientDocumentService.findById(documentId);
        try (var docInputStream = clientDocumentService.readDocument(document)) {
            return cdaToHtml(docInputStream);
        } catch (IOException e) {
            throw new CdaTransformationException(e);
        }
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDownloadCcd(#clientId)")
    public String clientCcdToHtml(@P("clientId") Long clientId, Boolean aggregated) {
        var document = ccdGenerator.generate(clientId, Boolean.TRUE.equals(aggregated));
        return cdaToHtml(document.getInputStream());
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canUpload(#uploadDto)")
    public Long save(@P("uploadDto") UploadClientDocumentDto uploadDto) {
        DocumentUtils.validateUploadedFile(uploadDto.getDocument(), ALLOWED_EXTENSIONS);
        Employee author = loggedUserService.getCurrentEmployee();
        Client client = clientService.findById(uploadDto.getClientId());
        try {
            var uploadData = new ClientDocumentUploadData(
                uploadDto.getDocument(),
                uploadDto.getTitle(),
                client,
                author,
                uploadDto.getSharingOption(),
                uploadDto.getDescription(),
                uploadDto.getCategoryIds()
            );
            return uploadClientDocumentService.upload(uploadData).getId();
        } catch (IOException e) {
            throw new BusinessException(BusinessExceptionType.FILE_SAVE_INTERNAL_ERROR);
        }
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDelete(#id)")
    public void deleteById(@P("id") long id, boolean isTemporary) {
        validateClientActive(id);
        var curEmployee = loggedUserService.getCurrentEmployee();
        if (isTemporary) {
            clientDocumentService.temporaryDelete(id, curEmployee);
        } else {
            clientDocumentService.markInvisible(id, curEmployee);
        }
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDelete(#id)")
    public void restoreById(@P("id") long id) {
        validateClientActive(id);
        var curEmployee = loggedUserService.getCurrentEmployee();
        clientDocumentService.restore(id, curEmployee);
    }

    @Override
    public boolean canAdd(Long clientId) {
        return clientDocumentSecurityService.canUpload(() -> clientId);
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDownloadServicePlanPdf(#clientId)")
    public void downloadServicePlanPdf(@P("clientId") Long clientId, HttpServletResponse response, ZoneId zoneId) {
        Long servicePlanId = servicePlanService.findLatestSharedWithClient(clientId).orElseThrow().getId();
        var report = servicePlanService.getServicePlanPDF(servicePlanId, null, zoneId);
        WriterUtils.copyDocumentContentToResponse(response, report);
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canEdit(#editDocumentDto.id)")
    public Long edit(@P("editDocumentDto") EditDocumentDto editDocumentDto) {
        return clientDocumentService.edit(new DocumentEditableData(
                editDocumentDto.getId(),
                editDocumentDto.getTitle(),
                editDocumentDto.getDescription(),
                editDocumentDto.getCategoryIds()
        ));
    }

    private String cdaToHtml(InputStream documentStream) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        cdaToHtmlService.cdaToHtml(documentStream, output);
        return output.toString();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientDocumentSecurityService.canViewList()")
    public Long findOldestDateByClient(Long clientId) {
        var filter = new InternalClientDocumentFilter();
        filter.setClientId(clientId);
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientDocumentService.findOldestDate(filter, permissionFilter).map(Instant::toEpochMilli).orElse(null);
    }


    private InternalClientDocumentFilter createInternalDocumentFilter(ClientDocumentFilter documentFilter) {
        var filter = new InternalClientDocumentFilter();

        filter.setTitle(documentFilter.getTitle());
        filter.setClientId(documentFilter.getClientId());
        filter.setDescription(documentFilter.getDescription());
        filter.setCategoryIds(documentFilter.getCategoryIds());
        filter.setFromDate(documentFilter.getFromDate());
        filter.setToDate(documentFilter.getToDate());
        filter.setIncludeNotCategorized(documentFilter.getIncludeNotCategorized());
        filter.setIncludeDeleted(documentFilter.getIncludeDeleted());
        filter.setIncludeWithoutSignature(documentFilter.getIncludeWithoutSignature());
        if (CollectionUtils.isNotEmpty(documentFilter.getSignatureStatusNames())) {
            filter.setSignatureStatuses(
                documentFilter.getSignatureStatusNames().stream()
                    .map(DocumentSignatureStatus::valueOf)
                    .map(DocumentSignatureStatus::requestStatuses)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
            );
        }
        filter.setIncludeSearchByCategoryName(documentFilter.getIncludeSearchByCategoryName());

        return filter;
    }

    private void validateClientActive(Long clientDocumentId) {
        var clientDocument = clientDocumentService.findById(clientDocumentId);
        clientService.validateActive(clientDocument.getClientId());
    }
}
