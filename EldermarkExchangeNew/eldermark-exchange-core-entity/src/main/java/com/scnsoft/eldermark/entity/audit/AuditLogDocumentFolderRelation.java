package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "AuditLogRelation_DocumentFolder")
public class AuditLogDocumentFolderRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "folder_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DocumentFolder folder;

    @Column(name = "folder_id", nullable = false)
    private Long folderId;

    @Column(name = "folder_name", nullable = false)
    private String folderName;

    @Column(name = "old_folder_name")
    private String oldFolderName;

    public DocumentFolder getFolder() {
        return folder;
    }

    public void setFolder(DocumentFolder folder) {
        this.folder = folder;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getOldFolderName() {
        return oldFolderName;
    }

    public void setOldFolderName(String oldFolderName) {
        this.oldFolderName = oldFolderName;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(folderId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Stream.of(folderName, oldFolderName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.DOCUMENT_FOLDER;
    }
}
