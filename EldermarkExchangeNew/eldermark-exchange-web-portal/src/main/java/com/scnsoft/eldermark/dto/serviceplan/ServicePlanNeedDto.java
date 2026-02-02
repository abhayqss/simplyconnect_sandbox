package com.scnsoft.eldermark.dto.serviceplan;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeedType;
import com.scnsoft.eldermark.validation.SpELAssert;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@SpELAssert.List(
        value = {
                @SpELAssert(
                        applyIf = "#findByDomainId(domainId).name().equals('EDUCATION_TASK')",
                        value = "#isNotEmpty(activationOrEducationTask)",
                        message = "activationOrEducationTask {javax.validation.constraints.NotEmpty.message}",
                        helpers = {StringUtils.class, ServicePlanNeedType.class}
                ),
                @SpELAssert(
                        applyIf = "#findByDomainId(domainId).name().equals('EDUCATION_TASK')",
                        value = "targetCompletionDate != null",
                        message = "targetCompletionDate {javax.validation.constraints.NotNull.message}",
                        helpers = {ServicePlanNeedType.class}
                ),
                @SpELAssert(
                        applyIf = "!#findByDomainId(domainId).name().equals('EDUCATION_TASK')",
                        value = "#isNotEmpty(needOpportunity)",
                        message = "needOpportunity {javax.validation.constraints.NotEmpty.message}",
                        helpers = {StringUtils.class, ServicePlanNeedType.class}
                )
        }
)
public class ServicePlanNeedDto extends BaseServicePlanNeedDto {
    private Long id;

    private String priorityName;

    @Size(max = 20000)
    private String activationOrEducationTask;

    @Size(max = 5000)
    private String proficiencyGraduationCriteria;

    @Valid
    private List<ServicePlanGoalItemDto> goals;

    private Long targetCompletionDate;
    private Long completionDate;

    private Long programTypeId;
    private String programTypeName;

    private Long programSubTypeId;
    private String programSubTypeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getActivationOrEducationTask() {
        return activationOrEducationTask;
    }

    public void setActivationOrEducationTask(String activationOrEducationTask) {
        this.activationOrEducationTask = activationOrEducationTask;
    }

    public String getProficiencyGraduationCriteria() {
        return proficiencyGraduationCriteria;
    }

    public void setProficiencyGraduationCriteria(String proficiencyGraduationCriteria) {
        this.proficiencyGraduationCriteria = proficiencyGraduationCriteria;
    }

    public List<ServicePlanGoalItemDto> getGoals() {
        return goals;
    }

    public void setGoals(List<ServicePlanGoalItemDto> goals) {
        this.goals = goals;
    }

    public Long getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(Long targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public Long getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Long completionDate) {
        this.completionDate = completionDate;
    }

    public Long getProgramTypeId() {
        return programTypeId;
    }

    public void setProgramTypeId(Long programTypeId) {
        this.programTypeId = programTypeId;
    }

    public String getProgramTypeName() {
        return programTypeName;
    }

    public void setProgramTypeName(String programTypeName) {
        this.programTypeName = programTypeName;
    }

    public Long getProgramSubTypeId() {
        return programSubTypeId;
    }

    public void setProgramSubTypeId(Long programSubTypeId) {
        this.programSubTypeId = programSubTypeId;
    }

    public String getProgramSubTypeName() {
        return programSubTypeName;
    }

    public void setProgramSubTypeName(String programSubTypeName) {
        this.programSubTypeName = programSubTypeName;
    }
}
