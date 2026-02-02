package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.dao.document.folder.DocumentFolderPermissionLevelDao;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevel;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;
import com.scnsoft.eldermark.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentFolderPermissionLevelServiceImpl implements DocumentFolderPermissionLevelService {

    @Autowired
    private DocumentFolderPermissionLevelDao permissionLevelDao;

    @Override
    public DocumentFolderPermissionLevel findById(Long id) {
        return permissionLevelDao.findById(id)
            .orElseThrow(() -> new ValidationException("Invalid document folder permission level id"));
    }

    @Override
    public List<DocumentFolderPermissionLevel> findAll() {
        return permissionLevelDao.findAll();
    }

    @Override
    public DocumentFolderPermissionLevel findByCode(DocumentFolderPermissionLevelCode code) {
        return permissionLevelDao.findByCode(code)
            .orElseThrow(() -> new ValidationException("Invalid document folder permission level code"));
    }
}
