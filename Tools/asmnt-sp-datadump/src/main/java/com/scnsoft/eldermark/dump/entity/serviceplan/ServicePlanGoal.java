package com.scnsoft.eldermark.dump.entity.serviceplan;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ServicePlanGoal")
public class ServicePlanGoal implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "goal", nullable = false)
    private String goal;

    @Column(name = "barriers")
    private String barriers;

    @Column(name = "intervention_action")
    private String interventionAction;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "target_completion_date", nullable = false)
    private LocalDate targetCompletionDate;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @JoinColumn(name = "service_plan_goal_need_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private ServicePlanGoalNeed need;

    @Column(name = "goal_completion")
    private Integer goalCompletion;

    public Long getId() {
        return id;
    }

    public Integer getGoalCompletion() {
        return goalCompletion;
    }

    public void setGoalCompletion(Integer goalCompletion) {
        this.goalCompletion = goalCompletion;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(LocalDate targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public ServicePlanGoalNeed getNeed() {
        return need;
    }

    public void setNeed(ServicePlanGoalNeed need) {
        this.need = need;
    }

}
