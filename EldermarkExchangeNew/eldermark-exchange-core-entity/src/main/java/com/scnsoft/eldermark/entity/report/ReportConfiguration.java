package com.scnsoft.eldermark.entity.report;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.entity.assessment.Assessment;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "ReportConfiguration")
public class ReportConfiguration {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType type;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "shared", nullable = false)
    private boolean isShared;

    @ManyToOne
    @JoinColumn(name = "depends_on_assessment_id")
    private Assessment dependsOnAssessment;

    @ElementCollection
    @CollectionTable(
            name = "ReportConfiguration_SourceDatabase_Enabled",
            joinColumns = @JoinColumn(name = "report_type", nullable = false)
    )
    @Column(name = "database_id", nullable = false)
    private Set<Long> enabledOrganizationIds;

    @ElementCollection
    @CollectionTable(
            name = "ReportConfiguration_SourceDatabase_Disabled",
            joinColumns = @JoinColumn(name = "report_type", nullable = false)
    )
    @Column(name = "database_id", nullable = false)
    private Set<Long> disabledOrganizationIds;

    @ElementCollection
    @CollectionTable(
            name = "ReportConfiguration_Organization_Enabled",
            joinColumns = @JoinColumn(name = "report_type", nullable = false)
    )
    @Column(name = "organization_id", nullable = false)
    private Set<Long> enabledCommunityIds;

    @ElementCollection
    @CollectionTable(
            name = "ReportConfiguration_Organization_Disabled",
            joinColumns = @JoinColumn(name = "report_type", nullable = false)
    )
    @Column(name = "organization_id", nullable = false)
    private Set<Long> disabledCommunityIds;

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    public Assessment getDependsOnAssessment() {
        return dependsOnAssessment;
    }

    public void setDependsOnAssessment(Assessment assessment) {
        this.dependsOnAssessment = assessment;
    }

    public Set<Long> getEnabledOrganizationIds() {
        return enabledOrganizationIds;
    }

    public void setEnabledOrganizationIds(Set<Long> enabledOrganizationIds) {
        this.enabledOrganizationIds = enabledOrganizationIds;
    }

    public Set<Long> getDisabledOrganizationIds() {
        return disabledOrganizationIds;
    }

    public void setDisabledOrganizationIds(Set<Long> disabledOrganizationIds) {
        this.disabledOrganizationIds = disabledOrganizationIds;
    }

    public Set<Long> getEnabledCommunityIds() {
        return enabledCommunityIds;
    }

    public void setEnabledCommunityIds(Set<Long> enabledCommunityIds) {
        this.enabledCommunityIds = enabledCommunityIds;
    }

    public Set<Long> getDisabledCommunityIds() {
        return disabledCommunityIds;
    }

    public void setDisabledCommunityIds(Set<Long> disabledCommunityIds) {
        this.disabledCommunityIds = disabledCommunityIds;
    }
}
