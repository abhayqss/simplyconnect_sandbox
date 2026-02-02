package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.util.Date;

@Table("communications")
public class CommunicationData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "communications";
    public static final String COMMUNICATION_ID = "Communication_ID";

    @Id
    @Column("Communication_ID")
    private long id;

    @Column("Completed_By_Empl_ID")
    private String completedByEmplId;

    @Column("Completed_Date")
    private Date completedDate;

    @Column("Prospect_ID")
    private Long prospectId;

    @Column("Prof_Contact_ID")
    private Long profContactId;

    @Column("Inquiry_ID")
    private Long inquiryId;

    @Column("Parent_Type")
    private String parentType;

    @Column("ParentRec_ID")
    private Long parentRecId;

    @Column("Communication_Type")
    private String communicationType;

    @Column("Due_By_Date")
    private Date dueByDate;

    @Column("Create_User")
    private String createdByEmplId;

    @Column("Notes")
    private String notes;

    @Column("Facility")
    private String facility;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompletedByEmplId() {
        return completedByEmplId;
    }

    public void setCompletedByEmplId(String completedByEmplId) {
        this.completedByEmplId = completedByEmplId;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public Long getProspectId() {
        return prospectId;
    }

    public void setProspectId(Long prospectId) {
        this.prospectId = prospectId;
    }

    public Long getProfContactId() {
        return profContactId;
    }

    public void setProfContactId(Long profContactId) {
        this.profContactId = profContactId;
    }

    public Long getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(Long inquiryId) {
        this.inquiryId = inquiryId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public Long getParentRecId() {
        return parentRecId;
    }

    public void setParentRecId(Long parentRecId) {
        this.parentRecId = parentRecId;
    }

    public String getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType(String communicationType) {
        this.communicationType = communicationType;
    }

    public Date getDueByDate() {
        return dueByDate;
    }

    public void setDueByDate(Date dueByDate) {
        this.dueByDate = dueByDate;
    }

    public String getCreatedByEmplId() {
        return createdByEmplId;
    }

    public void setCreatedByEmplId(String createdByEmplId) {
        this.createdByEmplId = createdByEmplId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

}
