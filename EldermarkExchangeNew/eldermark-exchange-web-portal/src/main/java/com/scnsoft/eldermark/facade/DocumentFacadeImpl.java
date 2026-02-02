package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.security.projection.dto.CommunityDocumentSecurityFieldsAware;
import com.scnsoft.eldermark.dto.EditCommunityDocumentDto;
import com.scnsoft.eldermark.dto.UploadDocumentDto;
import com.scnsoft.eldermark.dto.document.CommunityDocumentItemDto;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.ClientDocumentUploadData;
import com.scnsoft.eldermark.entity.document.CommunityDocumentEditableData;
import com.scnsoft.eldermark.entity.document.SharingOption;
import com.scnsoft.eldermark.entity.document.community.CommunityDocument;
import com.scnsoft.eldermark.entity.document.community.CommunityDocumentUploadData;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.document.ClientDocumentSecurityService;
import com.scnsoft.eldermark.service.document.CommunityDocumentSecurityService;
import com.scnsoft.eldermark.service.document.UploadClientDocumentService;
import com.scnsoft.eldermark.service.document.community.CommunityDocumentService;
import com.scnsoft.eldermark.service.document.community.UploadCommunityDocumentService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.DocumentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Service
public class DocumentFacadeImpl implements DocumentFacade {

    private final Set<String> ALLOWED_EXTENSIONS = Set.of("DOCX", "PDF", "XLSX", "TXT", "JPG", "JPEG", "GIF", "PNG", "TIFF", "TIF", "XML", "DOC", "XLS");

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DocumentFolderService folderService;

    @Autowired
    private UploadClientDocumentService uploadClientDocumentService;

    @Autowired
    private UploadCommunityDocumentService uploadCommunityDocumentService;

    @Autowired
    private ClientDocumentSecurityService clientDocumentSecurityService;

    @Autowired
    private CommunityDocumentSecurityService communityDocumentSecurityService;

    @Autowired
    private Converter<CommunityDocument, CommunityDocumentItemDto> documentDtoConverter;

    @Autowired
    private CommunityDocumentService communityDocumentService;

    @Override
    @PreAuthorize("#uploadDto.clientId != null " +
        "? @clientDocumentSecurityService.canUpload(#uploadDto) " +
        ": @communityDocumentSecurityService.canUpload(#uploadDto)"
    )
    @Transactional
    public Long save(UploadDocumentDto uploadDto) {
        DocumentUtils.validateUploadedFile(uploadDto.getDocument(), ALLOWED_EXTENSIONS);
        var author = loggedUserService.getCurrentEmployee();
        if (uploadDto.getClientId() != null) {
            var uploadData = constructClientUploadData(uploadDto, author);
            return uploadClientDocumentService.upload(uploadData).getId();
        } else if (uploadDto.getCommunityId() != null) {
            var uploadData = constructCommunityUploadData(uploadDto, author);
            return uploadCommunityDocumentService.upload(uploadData).getId();
        } else {
            throw new ValidationException("Invalid request");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long communityId, Long folderId, Long clientId) {
        if (communityId != null && clientId == null) {
            return communityDocumentSecurityService.canUpload(CommunityDocumentSecurityFieldsAware.of(communityId, folderId));
        } else if (clientId != null && communityId == null) {
            return clientDocumentSecurityService.canUpload(() -> clientId);
        } else {
            throw new ValidationException("Invalid request parameters");
        }
    }

    @Override
    @PreAuthorize("@communityDocumentSecurityService.canView(#documentId)")
    public CommunityDocumentItemDto findById(Long documentId) {
        return documentDtoConverter.convert(communityDocumentService.findById(documentId));
    }

    @Override
    @PreAuthorize("@communityDocumentSecurityService.canDownload(#documentId)")
    public void download(Long documentId, HttpServletResponse response, boolean isViewMode) {
        var document = communityDocumentService.findById(documentId);
        WriterUtils.copyDocumentContentToResponse(document.getDocumentTitle(), () -> communityDocumentService.readDocument(document),
            document.getMimeType(), isViewMode, response);
    }

    @Override
    @PreAuthorize("@communityDocumentSecurityService.canDelete(#id)")
    public void deleteById(Long id, boolean isTemporary) {
        var curEmployee = loggedUserService.getCurrentEmployee();
        if (isTemporary) {
            communityDocumentService.temporaryDelete(id, curEmployee);
        } else {
            communityDocumentService.markInvisible(id, curEmployee);
        }
    }

    @Override
    @PreAuthorize("@communityDocumentSecurityService.canDelete(#id)")
    public void restoreById(Long id) {
        var curEmployee = loggedUserService.getCurrentEmployee();
        communityDocumentService.restore(id, curEmployee);
    }

    @Override
    @PreAuthorize(
        "@communityDocumentSecurityService.canEdit(#editDocumentDto.id) "
            + "&& @communityDocumentSecurityService.canUpload(#editDocumentDto)"
    )
    public Long edit(EditCommunityDocumentDto editDocumentDto) {
        return communityDocumentService.edit(new CommunityDocumentEditableData(
            editDocumentDto.getId(),
            editDocumentDto.getTitle(),
            editDocumentDto.getDescription(),
            editDocumentDto.getCategoryIds(),
            editDocumentDto.getFolderId(),
            editDocumentDto.getCommunityId()
        ));
    }

    private ClientDocumentUploadData constructClientUploadData(UploadDocumentDto dto, Employee author) {
        var client = clientService.findById(dto.getClientId());
        try {
            return new ClientDocumentUploadData(
                dto.getDocument(),
                dto.getTitle(),
                client,
                author,
                SharingOption.MY_COMPANY,
                dto.getDescription(),
                dto.getCategoryIds()
            );
        } catch (IOException e) {
            throw new BusinessException(BusinessExceptionType.FILE_SAVE_INTERNAL_ERROR);
        }
    }

    private CommunityDocumentUploadData constructCommunityUploadData(UploadDocumentDto dto, Employee author) {
        var community = communityService.findById(dto.getCommunityId());
        var folder = Optional.ofNullable(dto.getFolderId()).map(folderService::findById);
        try {
            return new CommunityDocumentUploadData(
                dto.getDocument(),
                dto.getTitle(),
                author,
                community,
                folder.orElse(null),
                dto.getDescription(),
                dto.getCategoryIds()
            );
        } catch (IOException e) {
            throw new BusinessException(BusinessExceptionType.FILE_SAVE_INTERNAL_ERROR);
        }
    }
}
