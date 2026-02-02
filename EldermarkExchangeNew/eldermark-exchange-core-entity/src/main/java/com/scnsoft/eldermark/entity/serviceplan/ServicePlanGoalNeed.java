package com.scnsoft.eldermark.entity.serviceplan;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ServicePlanGoalNeed")
public class ServicePlanGoalNeed extends ServicePlanNeed {

	@Column(name = "need_opportunity", nullable = false)
	private String needOpportunity;

	@Column(name = "proficiency_graduation_criteria")
	private String proficiencyGraduationCriteria;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "need", orphanRemoval = true)
	private List<ServicePlanGoal> goals;

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

	public List<ServicePlanGoal> getGoals() {
		return goals;
	}

	public void setGoals(List<ServicePlanGoal> goals) {
		this.goals = goals;
	}

}
