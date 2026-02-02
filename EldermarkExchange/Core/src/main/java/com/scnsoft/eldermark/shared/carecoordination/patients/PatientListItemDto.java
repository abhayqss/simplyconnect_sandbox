package com.scnsoft.eldermark.shared.carecoordination.patients;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Null;
import java.util.Date;

/**
 * Created by pzhurba on 16-Oct-15.
 */
public class PatientListItemDto {

    private long id;
    private String firstName;
    private String lastName;
    @Null
//    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date birthDate;
    private String gender;
    private String ssn;
    private long eventCount;
    private String community;
    private boolean active;
    private boolean hasMerged;
    private Date dateCreated;
    Long organizationId;
    String hashKey;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getEventCount() {
        return eventCount;
    }

    public void setEventCount(long eventCount) {
        this.eventCount = eventCount;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isHasMerged() {
        return hasMerged;
    }

    public void setHasMerged(boolean hasMerged) {
        this.hasMerged = hasMerged;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }
}
