package com.scnsoft.eldermark.entity.xds.datatype;

import org.hibernate.annotations.Nationalized;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "XPN_PersonName")
public class XPNPersonName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "last_name", columnDefinition = "nvarchar(80)")
    @Nationalized
    private String lastName;

    @Column(name = "first_name", columnDefinition = "nvarchar(80)")
    @Nationalized
    private String firstName;

    @Column(name = "middle_name", columnDefinition = "nvarchar(80)")
    @Nationalized
    private String middleName;

    @Column(name = "suffix", columnDefinition = "nvarchar(20)")
    @Nationalized
    private String suffix;

    @Column(name = "prefix", columnDefinition = "nvarchar(20)")
    @Nationalized
    private String prefix;

    @Column(name = "degree", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String degree;

    @Column(name = "name_type_code", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String nameTypeCode;

    @Column(name = "name_representation_code", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String nameRepresentationCode;

    public XPNPersonName() {
    }

    public XPNPersonName(String lastName, String firstName, String middleName) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
    }

    public XPNPersonName(String lastName, String firstName, String middleName, String suffix, String prefix, String degree, String nameTypeCode, String nameRepresentationCode) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.suffix = suffix;
        this.prefix = prefix;
        this.degree = degree;
        this.nameTypeCode = nameTypeCode;
        this.nameRepresentationCode = nameRepresentationCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getNameTypeCode() {
        return nameTypeCode;
    }

    public void setNameTypeCode(String nameTypeCode) {
        this.nameTypeCode = nameTypeCode;
    }

    public String getNameRepresentationCode() {
        return nameRepresentationCode;
    }

    public void setNameRepresentationCode(String nameRepresentationCode) {
        this.nameRepresentationCode = nameRepresentationCode;
    }
}
