package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "State")
public class State {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
    @Column(name = "name", length = 30)
    private String name;
    @Column(name = "abbr", length = 10)
    private String abbr;

    @Enumerated(EnumType.STRING)
    @Column(name = "hie_consent_policy_type")
    private HieConsentPolicyType hieConsentPolicy;

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

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public HieConsentPolicyType getHieConsentPolicy() {
        return hieConsentPolicy;
    }

    public void setHieConsentPolicy(HieConsentPolicyType hieConsentPolicy) {
        this.hieConsentPolicy = hieConsentPolicy;
    }
}
