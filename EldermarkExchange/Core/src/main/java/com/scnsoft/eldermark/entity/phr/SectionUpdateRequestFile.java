package com.scnsoft.eldermark.entity.phr;

import javax.persistence.*;

@Entity
@Table(name = "SectionUpdateRequestFile")
public class SectionUpdateRequestFile extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "section_update_request_id")
    private SectionUpdateRequest sectionUpdateRequest;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "`file`")
    private byte[] file;

    public SectionUpdateRequest getSectionUpdateRequest() {
        return sectionUpdateRequest;
    }

    public void setSectionUpdateRequest(SectionUpdateRequest sectionUpdateRequest) {
        this.sectionUpdateRequest = sectionUpdateRequest;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
