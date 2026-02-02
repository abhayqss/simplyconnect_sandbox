package org.openhealthtools.openxds.entity.segment;

import org.hibernate.annotations.Cascade;
import org.openhealthtools.openxds.entity.datatype.HDHierarchicDesignator;
import org.openhealthtools.openxds.entity.datatype.MSGMessageType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "MSH_MessageHeaderSegment")
public class MSHMessageHeaderSegment implements AdtBaseMessageSegment, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "field_separator")
    private String fieldSeparator;

    @Column(name = "encoding_characters")
    private String encodingCharacters;

    @ManyToOne
    @JoinColumn(name = "sending_application_id")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    private HDHierarchicDesignator sendingApplication;

    @ManyToOne
    @JoinColumn(name = "sending_facility_id")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    private HDHierarchicDesignator sendingFacility;

    @ManyToOne
    @JoinColumn(name = "receiving_application_id")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    private HDHierarchicDesignator receivingApplication;

    @ManyToOne
    @JoinColumn(name = "receiving_facility_id")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    private HDHierarchicDesignator receivingFacility;

    @Column(name = "datetime")
    private Date datetime;

    @OneToOne
    @JoinColumn(name = "message_type_id")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    private MSGMessageType messageType;  //TODO make it manytoone to constant in DB. Not persist if exist.

    @Column(name = "message_control_id")
    private String messageControlId;

    @Column(name = "version_id")
    private String versionId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public String getEncodingCharacters() {
        return encodingCharacters;
    }

    public void setEncodingCharacters(String encodingCharacters) {
        this.encodingCharacters = encodingCharacters;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public MSGMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MSGMessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessageControlId() {
        return messageControlId;
    }

    public void setMessageControlId(String messageControlId) {
        this.messageControlId = messageControlId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public HDHierarchicDesignator getSendingApplication() {
        return sendingApplication;
    }

    public void setSendingApplication(HDHierarchicDesignator sendingApplication) {
        this.sendingApplication = sendingApplication;
    }

    public HDHierarchicDesignator getSendingFacility() {
        return sendingFacility;
    }

    public void setSendingFacility(HDHierarchicDesignator sendingFacility) {
        this.sendingFacility = sendingFacility;
    }

    public HDHierarchicDesignator getReceivingApplication() {
        return receivingApplication;
    }

    public void setReceivingApplication(HDHierarchicDesignator receivingApplication) {
        this.receivingApplication = receivingApplication;
    }

    public HDHierarchicDesignator getReceivingFacility() {
        return receivingFacility;
    }

    public void setReceivingFacility(HDHierarchicDesignator receivingFacility) {
        this.receivingFacility = receivingFacility;
    }
}
