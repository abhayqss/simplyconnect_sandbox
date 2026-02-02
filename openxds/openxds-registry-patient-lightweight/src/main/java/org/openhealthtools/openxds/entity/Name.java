package org.openhealthtools.openxds.entity;

import org.hibernate.annotations.Index;

import javax.persistence.*;

@Entity
@org.hibernate.annotations.Table(appliesTo = "Name",
    indexes = {
            @Index(name="PersonId_Index", columnNames = "person_id"),
            @Index(name="Names_Index", columnNames = {"family_normalized", "middle_normalized", "given_normalized"}),
    })
public class Name extends StringLegacyIdAwareEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(length = 5, name = "use_code")
    private String nameUse;

    @Column(name = "prefix", columnDefinition = "nvarchar(50)")
    private String prefix;

    @Column
    private String degree;

    @Column(name = "given", columnDefinition = "nvarchar(100)")
    private String given;

    @Column(name = "given_normalized", columnDefinition = "nvarchar(100)")
    private String givenNormalized;

    @Column(name = "middle", columnDefinition = "nvarchar(100)")
    private String middle;

    @Column(name = "middle_normalized", columnDefinition = "nvarchar(100)")
    private String middleNormalized;

    @Column(name = "family", columnDefinition = "nvarchar(100)")
    private String family;

    @Column(name = "family_normalized", columnDefinition = "nvarchar(100)")
    private String familyNormalized;

    @Column(name = "suffix", columnDefinition = "nvarchar(50)")
    private String suffix;

    @Column(length=30, name = "prefix_qualifier")
    private String prefixQualifier;

    @Column(length=30, name = "given_qualifier")
    private String givenQualifier;

    @Column(length=30, name = "middle_qualifier")
    private String middleQualifier;

    @Column(length=30, name = "family_qualifier")
    private String familyQualifier;

    @Column(length=30, name = "suffix_qualifier")
    private String suffixQualifier;

    @Column(name = "call_me")
    private String preferredName;

    @Column(name = "legacy_table")
    private String legacyTable;

    @Column(name="name_representation_code")
    private String nameRepresentationCode;

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

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getGivenNormalized() {
        return givenNormalized;
    }

    public void setGivenNormalized(String givenNormalized) {
        this.givenNormalized = givenNormalized;
    }

    public String getMiddleNormalized() {
        return middleNormalized;
    }

    public void setMiddleNormalized(String middleNormalized) {
        this.middleNormalized = middleNormalized;
    }

    public String getFamilyNormalized() {
        return familyNormalized;
    }

    public void setFamilyNormalized(String familyNormalized) {
        this.familyNormalized = familyNormalized;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getNameRepresentationCode() {
        return nameRepresentationCode;
    }

    public void setNameRepresentationCode(String nameRepresentationCode) {
        this.nameRepresentationCode = nameRepresentationCode;
    }
}
