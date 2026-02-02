package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "name")
public class Name extends StringLegacyTableAwareEntity implements LegacyTableAwareEntity {

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "use_code")
    private String nameUse;

    @Column(name = "given", columnDefinition = "nvarchar(100)")
    private String given;

    @Column(name = "middle", columnDefinition = "nvarchar(100)")
    private String middle;

    @Column(name = "family", columnDefinition = "nvarchar(100)")
    private String family;

    @Column(name = "full_name")
    private String fullName;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getNameUse() {
        return nameUse;
    }

    public void setNameUse(String nameUse) {
        this.nameUse = nameUse;
    }

    public String getGiven() {
        return given;
    }

    public void setGiven(String given) {
        this.given = given;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
