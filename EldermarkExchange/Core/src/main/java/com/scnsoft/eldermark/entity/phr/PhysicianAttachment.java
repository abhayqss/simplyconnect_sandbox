package com.scnsoft.eldermark.entity.phr;

import javax.persistence.*;

@Entity
@Table(name = "PhysicianAttachment")
public class PhysicianAttachment extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "physician_id", nullable = false)
    private Physician physician;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "`file`", nullable = false)
    private byte[] file;

    public Physician getPhysician() {
        return physician;
    }

    public void setPhysician(Physician physician) {
        this.physician = physician;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        PhysicianAttachment that = (PhysicianAttachment) o;

        if (!getPhysician().equals(that.getPhysician())) {
            return false;
        }
        if (getContentType() != null ? !getContentType().equals(that.getContentType()) : that.getContentType() != null) {
            return false;
        }
        return getOriginalName() != null ? getOriginalName().equals(that.getOriginalName()) : that.getOriginalName() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getPhysician().hashCode();
        result = 31 * result + (getContentType() != null ? getContentType().hashCode() : 0);
        result = 31 * result + (getOriginalName() != null ? getOriginalName().hashCode() : 0);
        return result;
    }
}
