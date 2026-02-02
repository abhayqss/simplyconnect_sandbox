package com.scnsoft.eldermark.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.PolicyActivity;

@Entity
public class AuthorizationActivity extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToMany
    @JoinTable(name = "AuthorizationActivity_ClinicalStatement",
            joinColumns = @JoinColumn( name="authorization_activity_id"),
            inverseJoinColumns = @JoinColumn( name="clinical_statement_id"))
    private List<CcdCode> clinicalStatements;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "policy_activity_id", nullable = false)
    private PolicyActivity policyActivity;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "AuthorizationActivity_Person",
            joinColumns = @JoinColumn( name="authorization_activity_id"),
            inverseJoinColumns = @JoinColumn( name="person_id") )
    private List<Person> performers;

    public List<CcdCode> getClinicalStatements() {
        return clinicalStatements;
    }

    public void setClinicalStatements(List<CcdCode> clinicalStatements) {
        this.clinicalStatements = clinicalStatements;
    }

    public PolicyActivity getPolicyActivity() {
        return policyActivity;
    }

    public void setPolicyActivity(PolicyActivity policyActivity) {
        this.policyActivity = policyActivity;
    }

    public List<Person> getPerformers() {
        return performers;
    }

    public void setPerformers(List<Person> performers) {
        this.performers = performers;
    }
}
