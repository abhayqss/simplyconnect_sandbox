package com.scnsoft.eldermark.shared;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scnsoft.eldermark.shared.json.CustomDateSerializer;

@XmlType(name = "Document")
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentDto implements Serializable {
    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "documentTitle")
    private String documentTitle;

    @XmlElement(name = "authorId")
    private Long authorId;

    @XmlElement(name = "authorName")
    private String authorPerson;

    @XmlElement(name = "creationTime")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date creationTime;

    @XmlElement(name = "mimeType")
    private String mimeType;

    @XmlElement(name = "size")
    private Integer size;

    @XmlElement(name = "documentType")
    private DocumentType documentType;

    @XmlElement(name = "originalFileName")
    private String originalFileName;

    @XmlElement(name = "databaseId")
    private String databaseId;

    @XmlTransient
    private String dataSource;

    @XmlTransient
    private String dataSourceOid;

    @XmlTransient
    private String community;

    @XmlTransient
    private String communityOid;

    @XmlTransient
    private boolean isCdaViewable;

    public DocumentDto() {
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorPerson() {
        return authorPerson;
    }

    public void setAuthorPerson(String authorPerson) {
        this.authorPerson = authorPerson;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId(Long id) {
        if (id != null)
            this.id = id.toString();
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSourceOid() {
        return dataSourceOid;
    }

    public void setDataSourceOid(String dataSourceOid) {
        this.dataSourceOid = dataSourceOid;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getCommunityOid() {
        return communityOid;
    }

    public void setCommunityOid(String communityOid) {
        this.communityOid = communityOid;
    }

    public boolean isCdaViewable() {
        return isCdaViewable;
    }

    public void setCdaViewable(boolean cdaViewable) {
        isCdaViewable = cdaViewable;
    }
}
