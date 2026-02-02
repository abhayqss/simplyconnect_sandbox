package com.scnsoft.eldermark.entity.serviceplan;

import javax.persistence.*;

@Entity
@Table(name = "ProgramType")
public class ProgramType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "need_type", nullable = false)
    private ServicePlanNeedType domain;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ServicePlanNeedType getDomain() {
        return domain;
    }

    public void setDomain(ServicePlanNeedType domain) {
        this.domain = domain;
    }
}
