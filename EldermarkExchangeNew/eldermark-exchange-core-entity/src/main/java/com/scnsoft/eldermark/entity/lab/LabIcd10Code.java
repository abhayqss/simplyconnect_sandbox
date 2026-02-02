package com.scnsoft.eldermark.entity.lab;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "LabIcd10Code")
@Immutable
public class LabIcd10Code {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "code")
    private String code;
    @Column(name = "item_order")
    private Long order;

    @ManyToOne
    @JoinColumn(name = "lab_icd10_group_id")
    private LabIcd10Group groupId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LabIcd10Group getGroupId() {
        return groupId;
    }

    public void setGroupId(LabIcd10Group groupId) {
        this.groupId = groupId;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
}
