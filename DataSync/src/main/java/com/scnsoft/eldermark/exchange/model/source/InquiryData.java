package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.util.Date;

@Table(InquiryData.TABLE_NAME)
public class InquiryData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "inquiry";
    public static final String INQUIRY_ID = "Inquiry_ID";

    @Id
    @Column(INQUIRY_ID)
    private long id;

    @Column("Date")
    private Date date;

    @Column("First_Name")
    private String firstName;

    @Column("Last_Name")
    private String lastName;

    @Column("Is_Prospect")
    private Boolean isProspect;

    @Column("Is_Related_Party")
    private Boolean isRelatedParty;

    @Column("No_Longer_Active")
    private Boolean noLongerActive;

    @Column("SalesRep_Employee_ID")
    private String salesRepEmployeeId;

    @Column("Converted")
    private Boolean converted;

    @Column("Facility")
    private String facility;

    @Column("Reason_No_Longer_Active")
    private String reasonNoLongerActive;

    @Column("Phones")
    private String phones;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getProspect() {
        return isProspect;
    }

    public void setProspect(Boolean prospect) {
        isProspect = prospect;
    }

    public Boolean getRelatedParty() {
        return isRelatedParty;
    }

    public void setRelatedParty(Boolean relatedParty) {
        isRelatedParty = relatedParty;
    }

    public Boolean getNoLongerActive() {
        return noLongerActive;
    }

    public void setNoLongerActive(Boolean noLongerActive) {
        this.noLongerActive = noLongerActive;
    }

    public String getSalesRepEmployeeId() {
        return salesRepEmployeeId;
    }

    public void setSalesRepEmployeeId(String salesRepEmployeeId) {
        this.salesRepEmployeeId = salesRepEmployeeId;
    }

    public Boolean getConverted() {
        return converted;
    }

    public void setConverted(Boolean converted) {
        this.converted = converted;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getReasonNoLongerActive() {
        return reasonNoLongerActive;
    }

    public void setReasonNoLongerActive(String reasonNoLongerActive) {
        this.reasonNoLongerActive = reasonNoLongerActive;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

}
