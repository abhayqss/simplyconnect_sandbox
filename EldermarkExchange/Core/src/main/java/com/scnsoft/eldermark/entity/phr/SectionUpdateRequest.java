package com.scnsoft.eldermark.entity.phr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.phr.converter.SectionUpdateRequestTypeConverter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "SectionUpdateRequest")
public class SectionUpdateRequest extends BaseEntity {

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "created_by_id")
    private Long createdById;

    @Column(name="type")
    @Convert(converter = SectionUpdateRequestTypeConverter.class)
    private SectionUpdateRequest.Type requestType;

    @Column
    private String section;

    @Column
    private String comment;

    @Column
    private Boolean sendToAll;

    @ManyToMany
    @JoinTable(name = "SectionUpdateRequest_Organization",
            joinColumns = @JoinColumn(name = "section_update_request_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id"))
    private Set<Organization> addressees;

    @OneToMany(mappedBy = "sectionUpdateRequest",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<SectionUpdateRequestFile> attachments;


    /**
     * Update Request Type
     */
    public enum Type {
        ADD_NEW('A', "ADD_NEW"),
        UPDATE('U', "UPDATE"),
        DELETE('D', "DELETE");

        private final char valueDb;
        private final String valueJson;

        Type(final char valueDb, final String valueJson) {
            this.valueDb = valueDb;
            this.valueJson = valueJson;
        }

        @Override
        @JsonValue
        public String toString() {
            return valueJson;
        }

        @JsonValue
        public char getValueDb() {
            return valueDb;
        }

        @JsonCreator
        public static Type fromValue(String text) {
            for (Type t : Type.values()) {
                if (String.valueOf(t.valueJson).equals(text)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown SectionUpdateRequest.Type (value = " + text + ")");
        }

        public static Type fromValue(char code) {
            for (Type t : Type.values()) {
                if (t.valueDb == code) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown SectionUpdateRequest.Type (code = " + code + ")");
        }
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Type getRequestType() {
        return requestType;
    }

    public void setRequestType(Type requestType) {
        this.requestType = requestType;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getSendToAll() {
        return sendToAll;
    }

    public void setSendToAll(Boolean sendToAll) {
        this.sendToAll = sendToAll;
    }

    public Set<Organization> getAddressees() {
        return addressees;
    }

    public void setAddressees(Set<Organization> addressees) {
        this.addressees = addressees;
    }

    public Set<SectionUpdateRequestFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<SectionUpdateRequestFile> attachments) {
        this.attachments = attachments;
    }
}
