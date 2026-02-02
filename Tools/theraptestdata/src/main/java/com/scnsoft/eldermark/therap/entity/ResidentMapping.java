package com.scnsoft.eldermark.therap.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ResidentMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String sourcePatientId;

    @Column
    private String sourceFirstName;

    @Column
    private String sourceLastName;

    @Column
    private String sourceSsn;

    @Column
    private String sourceDateOfBirth;

    @Column
    private String newPatientId;

    @Column
    private String newFirstName;

    @Column
    private String newLastName;

    @Column
    private String newSsn;

    @Column
    private String newDateOfBirth;

    @Column
    private String className;

    @Column
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourcePatientId() {
        return sourcePatientId;
    }

    public void setSourcePatientId(String sourcePatientId) {
        this.sourcePatientId = sourcePatientId;
    }

    public String getSourceFirstName() {
        return sourceFirstName;
    }

    public void setSourceFirstName(String sourceFirstName) {
        this.sourceFirstName = sourceFirstName;
    }

    public String getSourceLastName() {
        return sourceLastName;
    }

    public void setSourceLastName(String sourceLastName) {
        this.sourceLastName = sourceLastName;
    }

    public String getSourceSsn() {
        return sourceSsn;
    }

    public void setSourceSsn(String sourceSsn) {
        this.sourceSsn = sourceSsn;
    }

    public String getSourceDateOfBirth() {
        return sourceDateOfBirth;
    }

    public void setSourceDateOfBirth(String sourceDateOfBirth) {
        this.sourceDateOfBirth = sourceDateOfBirth;
    }

    public String getNewPatientId() {
        return newPatientId;
    }

    public void setNewPatientId(String newPatientId) {
        this.newPatientId = newPatientId;
    }

    public String getNewFirstName() {
        return newFirstName;
    }

    public void setNewFirstName(String newFirstName) {
        this.newFirstName = newFirstName;
    }

    public String getNewLastName() {
        return newLastName;
    }

    public void setNewLastName(String newLastName) {
        this.newLastName = newLastName;
    }

    public String getNewSsn() {
        return newSsn;
    }

    public void setNewSsn(String newSsn) {
        this.newSsn = newSsn;
    }

    public String getNewDateOfBirth() {
        return newDateOfBirth;
    }

    public void setNewDateOfBirth(String newDateOfBirth) {
        this.newDateOfBirth = newDateOfBirth;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}


