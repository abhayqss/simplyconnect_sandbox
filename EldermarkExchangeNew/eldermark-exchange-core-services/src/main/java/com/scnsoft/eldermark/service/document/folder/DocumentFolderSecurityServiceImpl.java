package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service("documentFolderSecurityService")
@Transactional(readOnly = true)
public class DocumentFolderSecurityServiceImpl extends BaseDocumentFolderSecurityService {

    private final Map<DocumentFolderType, DocumentFolderSecurityService> typeToSecurityServiceType;

    public DocumentFolderSecurityServiceImpl(
            @Qualifier("documentRegularFolderSecurityService") DocumentFolderSecurityService regularFolderSecurityService,
            @Qualifier("documentTemplateFolderSecurityService") DocumentFolderSecurityService templateFolderSecurityService
    ) {
        typeToSecurityServiceType = Map.of(
                DocumentFolderType.REGULAR, regularFolderSecurityService,
                DocumentFolderType.TEMPLATE, templateFolderSecurityService
        );
    }

    @Override
    public boolean canAdd(DocumentFolderSecurityFieldsAware folder) {
        var securityService = folder.getParentId() == null
                ? typeToSecurityServiceType.get(DocumentFolderType.REGULAR)
                : getFolderSecurityService(folder.getParentId());
        return securityService.canAdd(folder);
    }

    @Override
    public boolean canEdit(Long id) {
        return getFolderSecurityService(id).canEdit(id);
    }

    @Override
    public boolean canDelete(Long id) {
        return getFolderSecurityService(id).canDelete(id);
    }

    @Override
    public boolean canRestore(Long id) {
        return getFolderSecurityService(id).canRestore(id);
    }

    private DocumentFolderSecurityService getFolderSecurityService(Long id) {
        var parentFolder = folderService.findById(id);
        return typeToSecurityServiceType.get(parentFolder.getType());
    }
}
