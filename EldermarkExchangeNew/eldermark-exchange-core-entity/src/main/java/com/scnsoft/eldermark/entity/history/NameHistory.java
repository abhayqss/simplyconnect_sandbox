package com.scnsoft.eldermark.entity.history;

import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "name_History")
public class NameHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    //updated on db level
    @Column(name = "updated_datetime", insertable = false, updatable = false)
    private Instant updatedDatetime;

    //updated on db level
    @Column(name = "deleted_datetime", insertable = false, updatable = false)
    private Instant deletedDatetime;

    @Column(name = "name_id")
    private Long nameId;

    @Column(name = "legacy_table")
    private String legacyTable;

    @Column(name = "legacy_id")
    private String legacyId;

    @Column(name = "database_id")
    private Long organizationId;

    @Column(name = "person_id")
    private Long personId;

    @Column(length = 5, name = "use_code")
    private String nameUse;

    @Column(name = "prefix", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String prefix;

    @Column(name = "given", columnDefinition = "nvarchar(256)")
    @Nationalized
    private String given;

    @Column(name = "given_hash", insertable = false, updatable = false, columnDefinition = "int")
    private Long givenHash;

    @Column(name = "given_normalized", insertable = false, updatable = false, columnDefinition = "nvarchar(256)")
    @Nationalized
    private String givenNormalized;

    @Column(name = "middle", columnDefinition = "nvarchar(255)")
    @Nationalized
    private String middle;

    @Column(name = "middle_hash", insertable = false, updatable = false, columnDefinition = "int")
    private Long middleHash;

    @Column(name = "middle_normalized", insertable = false, updatable = false, columnDefinition = "nvarchar(256)")
    @Nationalized
    private String middleNormalized;

    @Column(name = "family", columnDefinition = "nvarchar(256)")
    @Nationalized
    private String family;

    @Column(name = "family_hash", insertable = false, updatable = false, columnDefinition = "int")
    private Long familyHash;

    @Column(name = "family_normalized", insertable = false, updatable = false, columnDefinition = "nvarchar(256)")
    @Nationalized
    private String familyNormalized;

    @Column(name = "suffix", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String suffix;

    @Column(name = "prefix_qualifier")
    private String prefixQualifier;

    @Column(name = "given_qualifier")
    private String givenQualifier;

    @Column(name = "middle_qualifier")
    private String middleQualifier;

    @Column(name = "family_qualifier")
    private String familyQualifier;

    @Column(name = "suffix_qualifier")
    private String suffixQualifier;

    @Column(name = "call_me")
    private String preferredName;

    @Column(name = "degree")
    private String degree;

    @Column(name = "full_name")
    private String fullName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Instant updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public Instant getDeletedDatetime() {
        return deletedDatetime;
    }

    public void setDeletedDatetime(Instant deletedDatetime) {
        this.deletedDatetime = deletedDatetime;
    }

    public Long getNameId() {
        return nameId;
    }

    public void setNameId(Long nameId) {
        this.nameId = nameId;
    }

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getNameUse() {
        return nameUse;
    }

    public void setNameUse(String nameUse) {
        this.nameUse = nameUse;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getGiven() {
        return given;
    }

    public void setGiven(String given) {
        this.given = given;
    }

    public Long getGivenHash() {
        return givenHash;
    }

    public void setGivenHash(Long givenHash) {
        this.givenHash = givenHash;
    }

    public String getGivenNormalized() {
        return givenNormalized;
    }

    public void setGivenNormalized(String givenNormalized) {
        this.givenNormalized = givenNormalized;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public Long getMiddleHash() {
        return middleHash;
    }

    public void setMiddleHash(Long middleHash) {
        this.middleHash = middleHash;
    }

    public String getMiddleNormalized() {
        return middleNormalized;
    }

    public void setMiddleNormalized(String middleNormalized) {
        this.middleNormalized = middleNormalized;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Long getFamilyHash() {
        return familyHash;
    }

    public void setFamilyHash(Long familyHash) {
        this.familyHash = familyHash;
    }

    public String getFamilyNormalized() {
        return familyNormalized;
    }

    public void setFamilyNormalized(String familyNormalized) {
        this.familyNormalized = familyNormalized;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefixQualifier() {
        return prefixQualifier;
    }

    public void setPrefixQualifier(String prefixQualifier) {
        this.prefixQualifier = prefixQualifier;
    }

    public String getGivenQualifier() {
        return givenQualifier;
    }

    public void setGivenQualifier(String givenQualifier) {
        this.givenQualifier = givenQualifier;
    }

    public String getMiddleQualifier() {
        return middleQualifier;
    }

    public void setMiddleQualifier(String middleQualifier) {
        this.middleQualifier = middleQualifier;
    }

    public String getFamilyQualifier() {
        return familyQualifier;
    }

    public void setFamilyQualifier(String familyQualifier) {
        this.familyQualifier = familyQualifier;
    }

    public String getSuffixQualifier() {
        return suffixQualifier;
    }

    public void setSuffixQualifier(String suffixQualifier) {
        this.suffixQualifier = suffixQualifier;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
