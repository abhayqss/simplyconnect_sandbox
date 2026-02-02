package com.scnsoft.eldermark.dump.entity.assessment;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AssessmentScoringValue")
public class AssessmentScoringValue implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Assessment assessment;

    @Column(name = "question_name")
    private String questionName;

    @Column(name = "answer_name")
    private String answerName;

    @Column(name = "result_group_name")
    private String resultGroupName;

    @Column(name = "value")
    private Long value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getAnswerName() {
        return answerName;
    }

    public void setAnswerName(String answerName) {
        this.answerName = answerName;
    }

    public String getResultGroupName() {
        return resultGroupName;
    }

    public void setResultGroupName(String resultGroupName) {
        this.resultGroupName = resultGroupName;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}