package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.entity.prospect.SecondOccupant;

import javax.persistence.*;

@Entity
@Table(name = "Avatar")
@Access(AccessType.FIELD)
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(mappedBy = "avatar")
    private Employee employee;

    @OneToOne(mappedBy = "avatar")
    private Client client;

    @OneToOne(mappedBy = "avatar")
    private Prospect prospect;

    @OneToOne(mappedBy = "avatar")
    private SecondOccupant secondOccupant;

    @Column(name = "avatar_name")
    private String avatarName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Prospect getProspect() {
        return prospect;
    }

    public void setProspect(Prospect prospect) {
        this.prospect = prospect;
    }

    public SecondOccupant getSecondOccupant() {
        return secondOccupant;
    }

    public void setSecondOccupant(SecondOccupant secondOccupant) {
        this.secondOccupant = secondOccupant;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }
}
