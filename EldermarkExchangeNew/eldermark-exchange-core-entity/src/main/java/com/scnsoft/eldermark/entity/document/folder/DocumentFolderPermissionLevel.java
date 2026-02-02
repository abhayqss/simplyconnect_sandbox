package com.scnsoft.eldermark.entity.document.folder;

import javax.persistence.*;

@Entity
@Table(name = "DocumentFolderPermissionLevel")
public class DocumentFolderPermissionLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private DocumentFolderPermissionLevelCode code;

    @Column(name = "title", nullable = false)
    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentFolderPermissionLevelCode getCode() {
        return code;
    }

    public void setCode(DocumentFolderPermissionLevelCode code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
