package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.basic.StringLegacyTableAwareEntity;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "PersonId_Index", columnList = "person_id"),
        @Index(name = "IX_name_given_hash", columnList = "given_hash"),
        @Index(name = "IX_name_family_hash", columnList = "family_hash")
}, name = "name")
public class Name extends StringLegacyTableAwareEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

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

    @Column(name = "given_normalized", columnDefinition = "nvarchar(256)", insertable = false, updatable = false)
    @Nationalized
    private String givenNormalized;

    @Column(name = "middle", columnDefinition = "nvarchar(256)")
    @Nationalized
    private String middle;

    @Column(name = "middle_hash", insertable = false, updatable = false, columnDefinition = "int")
    private Long middleHash;

    @Column(name = "middle_normalized", columnDefinition = "nvarchar(256)", insertable = false, updatable = false)
    @Nationalized
    private String middleNormalized;

    @Column(name = "family", columnDefinition = "nvarchar(256)")
    @Nationalized
    private String family;

    @Column(name = "family_hash", insertable = false, updatable = false, columnDefinition = "int")
    private Long familyHash;

    @Column(name = "family_normalized", columnDefinition = "nvarchar(256)", insertable = false, updatable = false)
    @Nationalized
    private String familyNormalized;

    @Column(name = "suffix", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String suffix;

    @Column(length = 30, name = "prefix_qualifier")
    private String prefixQualifier;

    @Column(length = 30, name = "given_qualifier")
    private String givenQualifier;

    @Column(length = 30, name = "middle_qualifier")
    private String middleQualifier;

    @Column(length = 30, name = "family_qualifier")
    private String familyQualifier;

    @Column(length = 30, name = "suffix_qualifier")
    private String suffixQualifier;

    @Column(length = 35, name = "call_me")
    private String preferredName;

    @Column(length = 100, name = "degree")
    private String degree;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "name_representation_code")
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

    public String getDegree() {
        return degree;
    }

    // Is degree (from openpixpdq PersonName) the same as Academic title in CCD?
    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getGivenHash() {
        return givenHash;
    }

    public void setGivenHash(Long givenHash) {
        this.givenHash = givenHash;
    }

    public Long getMiddleHash() {
        return middleHash;
    }

    public void setMiddleHash(Long middleHash) {
        this.middleHash = middleHash;
    }

    public Long getFamilyHash() {
        return familyHash;
    }

    public void setFamilyHash(Long familyHash) {
        this.familyHash = familyHash;
    }

    public String getNameRepresentationCode() {
        return nameRepresentationCode;
    }

    public void setNameRepresentationCode(String nameRepresentationCode) {
        this.nameRepresentationCode = nameRepresentationCode;
    }
}
