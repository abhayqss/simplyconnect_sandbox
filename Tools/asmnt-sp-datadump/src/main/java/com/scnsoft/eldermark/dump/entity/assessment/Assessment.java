package com.scnsoft.eldermark.dump.entity.assessment;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Assessment")
public class Assessment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "json_content")
    private String content;

    @Column(name = "scoring_enabled")
    private Boolean scoringEnabled;

    @Column(name = "severity_column_name")
    private String severityColumnName;

    @Column(name = "management_comment")
    private String managementComment;

    @Column(name = "has_numeration")
    private Boolean hasNumeration;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
