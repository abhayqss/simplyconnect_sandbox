package com.scnsoft.eldermark.entity.serviceplan;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ServicePlanGoal")
public class ServicePlanGoal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chain_id")
    private Long chainId;

    @Column(name = "goal", nullable = false)
    private String goal;

    @Column(name = "barriers")
    private String barriers;

    @Column(name = "intervention_action")
    private String interventionAction;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "goal_completion")
    private Integer goalCompletion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "target_completion_date", nullable = false)
    private Date targetCompletionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completion_date")
    private Date completionDate;

    @JoinColumn(name = "service_plan_goal_need_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private ServicePlanGoalNeed need;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getBarriers() {
        return barriers;
    }

    public void setBarriers(String barriers) {
        this.barriers = barriers;
    }

    public String getInterventionAction() {
        return interventionAction;
    }

    public void setInterventionAction(String interventionAction) {
        this.interventionAction = interventionAction;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Integer getGoalCompletion() {
        return goalCompletion;
    }

    public void setGoalCompletion(Integer goalCompletion) {
        this.goalCompletion = goalCompletion;
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

    public ServicePlanGoalNeed getNeed() {
        return need;
    }

    public void setNeed(ServicePlanGoalNeed need) {
        this.need = need;
    }
}
