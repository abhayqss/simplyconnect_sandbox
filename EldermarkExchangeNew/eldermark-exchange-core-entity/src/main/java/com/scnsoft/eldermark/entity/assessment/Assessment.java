package com.scnsoft.eldermark.entity.assessment;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.security.Permission;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Assessment")
public class Assessment implements Serializable, IdAware {
    private static final long serialVersionUID = 1L;
    public static final String GAD7 = "GAD-7";
    public static final String PHQ9 = "PHQ-9";
    public static final String COMPREHENSIVE = "Comprehensive Assessment";
    public static final String NOR_CAL_COMPREHENSIVE = "Nor Cal Comprehensive Assessment";
    public static final String IN_TUNE = "InTune Assessment";
    public static final String ARIZONA_SSM = "Arizona Self-sufficiency Matrix Assessment";
    public static final String HMIS_ADULT_CHILD_INTAKE = "HMIS Adult & Child Intake assessment";
    public static final String HMIS_ADULT_CHILD_INTAKE_REASESSMENT = "HMIS Adult & Child Reasessment";
    public static final String HMIS_ADULT_CHILD_INTAKE_EXIT = "HMIS Adult & Child Questionnaire - Exit";
    public static final String HOUSING = "Housing Assessment";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "short_name")
    private String shortName;

    @JoinColumn(name = "assessment_group_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private AssessmentGroup assessmentGroup;

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

    //    todo
    @Column(name = "type")
    @Deprecated
    private Boolean type;
//    todo

    @ManyToMany
    @JoinTable(name = "Assessment_SourceDatabase",
            joinColumns = @JoinColumn(name = "assessment_id", nullable = false, insertable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "database_id", nullable = false, insertable = false, updatable = false))
    private List<Organization> organizations;

    @ElementCollection
    @CollectionTable(name = "Assessment_SourceDatabase", joinColumns = @JoinColumn(name = "assessment_id", nullable = false))
    @Column(name = "database_id", nullable = false)
    private Set<Long> organizationIds;

    @ManyToMany
    @JoinTable(name = "Assessment_SourceDatabase_Disabled",
            joinColumns = @JoinColumn(name = "assessment_id", nullable = false, insertable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "database_id", nullable = false, insertable = false, updatable = false))
    private List<Organization> disabledOrganizations;

    @ElementCollection
    @CollectionTable(name = "Assessment_SourceDatabase_Disabled", joinColumns = @JoinColumn(name = "assessment_id", nullable = false))
    @Column(name = "database_id", nullable = false)
    private Set<Long> disabledOrganizationIds;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessment")
    private List<AssessmentScoringGroup> scoringGroups;

    @ElementCollection
    @CollectionTable(name = "AssessmentPermission", joinColumns = @JoinColumn(name = "assessment_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private List<Permission> allowedRoles;

    @Deprecated
    @Column(name = "send_event_enabled")
    private Boolean shouldSendEvents;

    @Column(name = "is_shared", nullable = false)
    private Boolean isShared;

    @Enumerated(EnumType.STRING)
    @Column(name = "events_preferences")
    private AssessmentEventsPreferences eventsPreferences;

    @Column(name = "editable", nullable = false)
    private Boolean editable;

    @Column(name = "draft_enabled", nullable = false)
    private Boolean draftEnabled;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organization) {
        this.organizations = organization;
    }

    public List<AssessmentScoringGroup> getScoringGroups() {
        return scoringGroups;
    }

    public void setScoringGroups(List<AssessmentScoringGroup> scoringGroups) {
        this.scoringGroups = scoringGroups;
    }

    public List<Permission> getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(List<Permission> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public Boolean getShouldSendEvents() {
        return shouldSendEvents;
    }

    public void setShouldSendEvents(Boolean shouldSendEvents) {
        this.shouldSendEvents = shouldSendEvents;
    }

    public Set<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(Set<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public List<Organization> getDisabledOrganizations() {
        return disabledOrganizations;
    }

    public void setDisabledOrganizations(List<Organization> disabledOrganizations) {
        this.disabledOrganizations = disabledOrganizations;
    }

    public Set<Long> getDisabledOrganizationIds() {
        return disabledOrganizationIds;
    }

    public void setDisabledOrganizationIds(Set<Long> disabledOrganizationIds) {
        this.disabledOrganizationIds = disabledOrganizationIds;
    }

    public Boolean getIsShared() {
        return isShared;
    }

    public void setIsShared(Boolean shared) {
        this.isShared = shared;
    }

    public AssessmentEventsPreferences getEventsPreferences() {
        return eventsPreferences;
    }

    public void setEventsPreferences(AssessmentEventsPreferences eventsPreferences) {
        this.eventsPreferences = eventsPreferences;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getDraftEnabled() {
        return draftEnabled;
    }

    public void setDraftEnabled(Boolean draftEnabled) {
        this.draftEnabled = draftEnabled;
    }
}
