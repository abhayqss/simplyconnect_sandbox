package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.HDHierarchicDesignator;
import com.scnsoft.eldermark.entity.xds.datatype.MSGMessageType;
import com.scnsoft.eldermark.entity.xds.datatype.PTProcessingType;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "MSH_MessageHeaderSegment")
public class MSHMessageHeaderSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "field_separator")
    private String fieldSeparator;

    @Column(name = "encoding_characters")
    private String encodingCharacters;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sending_application_id")
    private HDHierarchicDesignator sendingApplication;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sending_facility_id")
    private HDHierarchicDesignator sendingFacility;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "receiving_application_id")
    private HDHierarchicDesignator receivingApplication;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "receiving_facility_id")
    private HDHierarchicDesignator receivingFacility;

    @Column(name = "datetime")
    private Instant datetime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "message_type_id")
    private MSGMessageType messageType;

    @Column(name = "message_control_id")
    private String messageControlId;

    @Column(name = "version_id")
    private String versionId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "processing_id")
    private PTProcessingType processingId;

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

    public Instant getDatetime() {
        return datetime;
    }

    public void setDatetime(Instant datetime) {
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

    public PTProcessingType getProcessingId() {
        return processingId;
    }

    public void setProcessingId(PTProcessingType processingId) {
        this.processingId = processingId;
    }
}
