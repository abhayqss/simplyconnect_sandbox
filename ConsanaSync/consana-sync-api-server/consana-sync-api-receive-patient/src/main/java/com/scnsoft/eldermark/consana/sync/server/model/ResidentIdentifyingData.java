package com.scnsoft.eldermark.consana.sync.server.model;

import java.util.Date;

public class ResidentIdentifyingData {

    private String consanaXrefId;

    private long communityId;

    private String firstName;
    private String lastName;
    private Date birthDate;
    private String ssn;

    public String getConsanaXrefId() {
        return consanaXrefId;
    }

    public void setConsanaXrefId(String consanaXrefId) {
        this.consanaXrefId = consanaXrefId;
    }

    public long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
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

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
}
