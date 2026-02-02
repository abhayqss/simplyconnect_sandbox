package com.scnsoft.eldermark.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * @author phomal
 * @author pzhurba
 */
@Entity
@Table(name = "EventAuthor")
public class EventAuthor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "first_name", length = 128, nullable = false)
    private String firstName;
    @Basic(optional = false)
    @Column(name = "last_name", length = 128, nullable = false)
    private String lastName;
    @Basic(optional = false)
    @Column(name = "role", length = 50, nullable = false)
    private String role;
    @Basic(optional = false)
    @Column(name = "organization", length = 128, nullable = false)
    private String organization;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

}
