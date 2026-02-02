package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Assessment")
public class Assessment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="short_name")
    private String shortName;

    @JoinColumn(name = "assessment_group_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private AssessmentGroup assessmentGroup;

    @Column(name="json_content")
    private String content;

    @Column(name = "scoring_enabled")
    private Boolean scoringEnabled;

    @Column(name = "severity_column_name")
    private String severityColumnName;

    @Column(name = "management_comment")
    private String managementComment;

    @Column(name = "has_numeration")
    private Boolean hasNumeration;

//    todo
    @Column(name = "type")
    private Boolean type;
//    todo

    @ManyToMany
    @JoinTable(name = "Assessment_SourceDatabase",
            joinColumns = @JoinColumn(name = "assessment_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "database_id", nullable = false))
    private List<Database> databases;

    @Column(name = "send_event_enabled")
    private Boolean isShouldSendEvents;

    @Column(name = "code")
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public AssessmentGroup getAssessmentGroup() {
        return assessmentGroup;
    }

    public void setAssessmentGroup(AssessmentGroup assessmentGroup) {
        this.assessmentGroup = assessmentGroup;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getScoringEnabled() {
        return scoringEnabled;
    }

    public void setScoringEnabled(Boolean scoringEnabled) {
        this.scoringEnabled = scoringEnabled;
    }

    public String getSeverityColumnName() {
        return severityColumnName;
    }

    public void setSeverityColumnName(String severityColumnName) {
        this.severityColumnName = severityColumnName;
    }

    public String getManagementComment() {
        return managementComment;
    }

    public void setManagementComment(String managementComment) {
        this.managementComment = managementComment;
    }

    public Boolean getHasNumeration() {
        return hasNumeration;
    }

    public void setHasNumeration(Boolean hasNumeration) {
        this.hasNumeration = hasNumeration;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }

    public Boolean getShouldSendEvents() {
        return isShouldSendEvents;
    }

    public void setShouldSendEvents(Boolean shouldSendEvents) {
        isShouldSendEvents = shouldSendEvents;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
