package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.InternalClientDocumentFilter;
import com.scnsoft.eldermark.beans.pagination.PrependedElementsPageable;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureStatus;
import com.scnsoft.eldermark.exception.CdaTransformationException;
import com.scnsoft.eldermark.mobile.dto.document.DocumentDto;
import com.scnsoft.eldermark.mobile.dto.document.DocumentListItemDto;
import com.scnsoft.eldermark.mobile.filter.MobileDocumentFilter;
import com.scnsoft.eldermark.service.document.ClientDocumentSecurityService;
import com.scnsoft.eldermark.service.document.ClientDocumentService;
import com.scnsoft.eldermark.service.document.cda.CdaToHtmlService;
import com.scnsoft.eldermark.service.document.cda.generator.CcdGeneratorService;
import com.scnsoft.eldermark.service.document.facesheet.FacesheetService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientDocumentFacadeImpl implements ClientDocumentFacade {

    @Autowired
    private ClientDocumentService clientDocumentService;

    @Autowired
    private CcdGeneratorService ccdGenerator;

    @Autowired
    private FacesheetService facesheetGenerator;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private Converter<ClientDocument, DocumentListItemDto> documentListItemDtoConverter;

    @Autowired
    private Converter<ClientDocument, DocumentDto> documentDtoConverter;

    @Autowired
    private ClientDocumentSecurityService clientDocumentSecurityService;

    @Autowired
    private CdaToHtmlService cdaToHtmlService;

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canViewList()")
    public Page<DocumentListItemDto> find(MobileDocumentFilter documentFilter, Pageable originalPageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        var adjustedPageable = PaginationUtils.applyEntitySort(originalPageable, DocumentListItemDto.class);

        var addCCD = false;
        var addFacesheet = false;
        if (documentFilter.isIncludeGenerated()) {
            addCCD = clientDocumentSecurityService.canDownloadCcd(documentFilter.getClientId());
            addFacesheet = clientDocumentSecurityService.canDownloadFacesheet(documentFilter.getClientId());
        }

        var generatedDocumentEntryBuilders = new ArrayList<Supplier<DocumentListItemDto>>();
        if (addCCD) {
            generatedDocumentEntryBuilders.add(this::createCcdDocumentEntry);
        }
        if (addFacesheet) {
            generatedDocumentEntryBuilders.add(this::createFacesheetDocumentEntry);
        }

        var additionalElementsCount = generatedDocumentEntryBuilders.size();
        if (additionalElementsCount > 0) {
            adjustedPageable = new PrependedElementsPageable(adjustedPageable, additionalElementsCount);
        }

        List<DocumentListItemDto> dbPageElements;
        long totalCount;
        if (adjustedPageable.getPageSize() > 0) {
            //at least some elements should be loaded
            var page = clientDocumentService.find(
                    createInternalClientDocumentFilter(documentFilter),
                    permissionFilter,
                    adjustedPageable
            ).map(documentListItemDtoConverter::convert);

            if (!page.getPageable().getClass().equals(adjustedPageable.getClass())) {
                throw new RuntimeException("Request and response Pageable classes don't match - " +
                        page.getPageable().getClass().getSimpleName() + " vs " +
                        adjustedPageable.getClass().getSimpleName());
            }
            dbPageElements = page.getContent();
            totalCount = page.getTotalElements();
        } else {
            //otherwise no need to fetch from db, for example page=0 and pageSize=1 or 2
            dbPageElements = Collections.emptyList();
            totalCount = clientDocumentService.count(
                    createInternalClientDocumentFilter(documentFilter),
                    permissionFilter
            );
        }

        List<DocumentListItemDto> pageElements;
        var pageSizeDiff = originalPageable.getPageSize() - adjustedPageable.getPageSize();
        if (pageSizeDiff > 0) {
            //it means that we'll load less data from DB and prepend data programmatically
            var computedStart = (int) originalPageable.getOffset();
            var computedEnd = computedStart + Math.min(pageSizeDiff, originalPageable.getPageSize());
            pageElements = new ArrayList<>(originalPageable.getPageSize());

            for (int i = computedStart; i < computedEnd; ++i) {
                pageElements.add(generatedDocumentEntryBuilders.get(i).get());
            }
            pageElements.addAll(dbPageElements);
        } else {
            //means that either it is normal pagination or pagination with generated entries, but page doesn't contain them
            pageElements = dbPageElements;
        }

        return new PageImpl<>(pageElements, originalPageable, totalCount + additionalElementsCount);
    }

    private DocumentListItemDto createCcdDocumentEntry() {
        var item = new DocumentListItemDto();
        item.setTitle("CCD");
        item.setDocumentType(DocumentType.CCD);
        item.setMimeType(MediaType.TEXT_XML_VALUE);
        item.setCategories(Collections.emptyList());
        item.setCreatedOrModifiedDate(Instant.now().toEpochMilli());
        return item;
    }

    private DocumentListItemDto createFacesheetDocumentEntry() {
        var item = new DocumentListItemDto();
        item.setTitle("Facesheet");
        item.setDocumentType(DocumentType.FACESHEET);
        item.setMimeType(MediaType.APPLICATION_PDF_VALUE);
        item.setCategories(Collections.emptyList());
        item.setCreatedOrModifiedDate(Instant.now().toEpochMilli());
        return item;
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canViewList()")
    public long count(MobileDocumentFilter documentFilter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        int additionalEntries = getGeneratedCount(documentFilter);

        return clientDocumentService.count(createInternalClientDocumentFilter(documentFilter), permissionFilter) + additionalEntries;
    }

    private int getGeneratedCount(MobileDocumentFilter documentFilter) {
        var additionalEntries = 0;
        if (documentFilter.isIncludeGenerated()) {
            additionalEntries += BooleanUtils.toInteger(
                    clientDocumentSecurityService.canDownloadCcd(documentFilter.getClientId())
            );
            additionalEntries += BooleanUtils.toInteger(
                    clientDocumentSecurityService.canDownloadFacesheet(documentFilter.getClientId())
            );
        }
        return additionalEntries;
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canViewList()")
    public List<NamedTitledValueEntityDto<Long>> countGroupedBySignatureStatus(MobileDocumentFilter documentFilter) {

        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        var counts = clientDocumentService.countGroupedBySignatureStatus(
                createInternalClientDocumentFilter(documentFilter),
                permissionFilter
        );

        var finalCounts = new HashMap<DocumentSignatureStatus, Long>();
        finalCounts.put(null, (long) getGeneratedCount(documentFilter));

        counts.forEach(countItem -> finalCounts.compute(
                countItem.getStatus() == null ? null : DocumentSignatureStatus.fromRequestStatus(countItem.getStatus()),
                (status, count) -> (count == null ? 0 : count) + countItem.getCount()
        ));

        return finalCounts.entrySet().stream()
                .map(it -> new NamedTitledValueEntityDto<>(
                        it.getKey() == null ? null : it.getKey().name(),
                        it.getKey() == null ? null : it.getKey().getTitle(),
                        it.getValue()
                ))
                .collect(Collectors.toList());
    }

    private InternalClientDocumentFilter createInternalClientDocumentFilter(MobileDocumentFilter mobileDocumentFilter) {
        var filter = new InternalClientDocumentFilter();
        filter.setClientId(mobileDocumentFilter.getClientId());
        if (CollectionUtils.isNotEmpty(mobileDocumentFilter.getSignatureStatuses())) {
            filter.setSignatureStatuses(
                    mobileDocumentFilter.getSignatureStatuses().stream()
                            .map(DocumentSignatureStatus::requestStatuses)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList())
            );
        }
        filter.setIncludeWithoutSignature(mobileDocumentFilter.isIncludeWithoutSignature());
        return filter;
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canView(#documentId)")
    public DocumentDto findById(Long documentId) {
        return documentDtoConverter.convert(clientDocumentService.findById(documentId));
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDownload(#documentId)")
    public void download(Long documentId, HttpServletResponse response) {
        var document = clientDocumentService.findById(documentId);
        WriterUtils.copyDocumentContentToResponse(document.getDocumentTitle(), () -> clientDocumentService.readDocument(document),
                document.getMimeType(), false, response);
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDownloadCcd(#clientId)")
    public void downloadCcd(Long clientId, HttpServletResponse response) {
        var document = ccdGenerator.generate(clientId, true);
        WriterUtils.copyDocumentContentToResponse(document, false, response);
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canDownloadFacesheet(#clientId)")
    public void downloadFacesheet(Long clientId, HttpServletResponse response, ZoneId zoneId) {
        var document = facesheetGenerator.generate(clientId, true, zoneId);
        WriterUtils.copyDocumentContentToResponse(document, false, response);
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
    public String clientCcdToHtml(@P("clientId") Long clientId) {
        var document = ccdGenerator.generate(clientId, true);
        return cdaToHtml(document.getInputStream());
    }

    private String cdaToHtml(InputStream documentStream) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        cdaToHtmlService.cdaToHtml(documentStream, output);
        return output.toString();
    }
}
