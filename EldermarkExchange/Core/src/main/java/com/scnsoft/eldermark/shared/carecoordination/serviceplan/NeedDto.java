package com.scnsoft.eldermark.shared.carecoordination.serviceplan;

import java.util.Date;
import java.util.List;

public class NeedDto {

    private Long id;
    private String priority;
    private String type;

    //goal need fields
    private String needOpportunity;
    private String proficiencyGraduationCriteria;
    private List<GoalDto> goals;

    //education task fields
    private String activationOrEducationTask;
    private Date targetCompletionDate;
    private Date completionDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<GoalDto> getGoals() {
        return goals;
    }

    public void setGoals(List<GoalDto> goals) {
        this.goals = goals;
    }

    public String getNeedOpportunity() {
        return needOpportunity;
    }

    public void setNeedOpportunity(String needOpportunity) {
        this.needOpportunity = needOpportunity;
    }

    public String getProficiencyGraduationCriteria() {
        return proficiencyGraduationCriteria;
    }

    public void setProficiencyGraduationCriteria(String proficiencyGraduationCriteria) {
        this.proficiencyGraduationCriteria = proficiencyGraduationCriteria;
    }

    public String getActivationOrEducationTask() {
        return activationOrEducationTask;
    }

    public void setActivationOrEducationTask(String activationOrEducationTask) {
        this.activationOrEducationTask = activationOrEducationTask;
    }

    public Date getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(Date targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

}
