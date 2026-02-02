package com.scnsoft.eldermark.shared;

import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Resident")
public class ResidentDto implements Serializable {
    @XmlElement(required = true, name = "id")
    private String id;

    @XmlElement(required = true, name = "firstName")
    private String firstName;

    @XmlElement(required = true, name = "lastName")
    private String lastName;

    @XmlElement(required = true, name = "gender", nillable = true)
    private Gender gender;

    @XmlElement(required = true, name = "middleName", nillable = true)
    private String middleName;

    @XmlElement(required = true, name = "ssn", nillable = true)
    private String ssn;

    @XmlElement(required = true, name = "birthPlace", nillable = true)
    private String birthPlace;

    @XmlElement(name = "dateOfBirth")
    @XmlSchemaType(name = "date")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date dateOfBirth;

    @XmlElement(required = true, name = "phone", nillable = true)
    private String phone;

    @XmlElement(required = true, name = "streetAddress", nillable = true)
    private String streetAddress;

    @XmlElement(required = true, name = "city", nillable = true)
    private String city;

    @XmlElement(required = true, name = "state", nillable = true)
    private String state;

    @XmlElement(required = true, name = "postalCode", nillable = true)
    private String postalCode;

    @XmlTransient //Hide country from web services contract
    private String country;

    @XmlElement(required = true, name = "email", nillable = true)
    private String email;

    @XmlElement(required = true, name = "facilityId", nillable = true)
    private String organizationId;

    @XmlElement(required = true, name = "facilityName", nillable = true)
    private String organizationName;

    @XmlElement(required = true, name = "sourceOrganizationId", nillable = true)
    private String databaseId;

    @XmlElement(required = true, name = "sourceOrganizationName", nillable = true)
    private String databaseName;

    @XmlElement(required = true, name = "idInSourceOrganization", nillable = true)
    private String residentNumber;

    @XmlElement(required = true, name = "hashKey", nillable = false)
    private String hashKey;

    @XmlTransient //Hide from web services contract
    private SearchScope searchScope;

    @XmlTransient
    private Boolean matchedAutomatically;

    @XmlTransient
    private Boolean select;

    @XmlTransient
    private Long mergedId;

    @XmlTransient
    private Long probablyMatchedId;

    @XmlTransient
    private boolean hasMerged;

    @XmlTransient
    private boolean actions; //TODO this field added to prevent strange grid warning. The cause of this warning should be found and field should be removed.

    @XmlTransient
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date dateCreated;

    public String getResidentNumber() {
        return residentNumber;
    }

    public void setResidentNumber(String residentNumber) {
        this.residentNumber = residentNumber;
    }

    public void setResidentNumber(Long residentNumber) {
        if (residentNumber != null)
            this.residentNumber = residentNumber.toString();
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

    public String getFullName() {
        return firstName + ' ' + lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        if (databaseId != null)
            this.databaseId = databaseId.toString();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getOrganization() {
        return organizationId + " - " + organizationName;
    }

    public String getDatabase() {
        return databaseId + " - " + databaseName;
    }

    public String getCityStateAndPostalCode() {
        String cityDelimeter = StringUtils.isNotBlank(city) && (StringUtils.isNotBlank(state) ||  StringUtils.isNotBlank(postalCode)) ? ", " : "";

        return StringUtils.defaultIfBlank(city, "") + cityDelimeter +
                StringUtils.defaultIfBlank(state, "") + " " +
                StringUtils.defaultIfBlank(postalCode, "");
    }

    public String getGenderDisplayName() {
        String result = "";
        if (gender != null) {
            switch (gender) {
                case MALE:
                    result = "Male";
                    break;
                case FEMALE:
                    result = "Female";
                    break;
                case UNDIFFERENTIATED:
                    result = "Undifferentiated";
                    break;
                default:
                    throw new IllegalArgumentException("Unknown gender type");
            }
        }
        return result;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public SearchScope getSearchScope() {
        return searchScope;
    }

    public void setSearchScope(SearchScope searchScope) {
        this.searchScope = searchScope;
    }

    public Long getMergedId() {
        return mergedId;
    }

    public void setMergedId(Long mergedId) {
        this.mergedId = mergedId;
    }

    public Long getProbablyMatchedId() {
        return probablyMatchedId;
    }

    public void setProbablyMatchedId(Long probablyMatchedId) {
        this.probablyMatchedId = probablyMatchedId;
    }

    public Boolean getMatchedAutomatically() {
        return matchedAutomatically;
    }

    public void setMatchedAutomatically(Boolean matchedAutomatically) {
        this.matchedAutomatically = matchedAutomatically;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }

    public boolean isHasMerged() {
        return hasMerged;
    }

    public void setHasMerged(boolean hasMerged) {
        this.hasMerged = hasMerged;
    }

    public boolean isActions() {
        return actions;
    }

    public void setActions(boolean actions) {
        this.actions = actions;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
