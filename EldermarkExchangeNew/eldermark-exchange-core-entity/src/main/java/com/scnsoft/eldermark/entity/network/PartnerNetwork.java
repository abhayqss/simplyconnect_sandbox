package com.scnsoft.eldermark.entity.network;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "PartnerNetwork")
public class PartnerNetwork implements IdAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @Column(name = "is_public", length = 256, nullable = false)
    private boolean isPublic;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(name = "PartnerNetwork_SourceDatabase",
            joinColumns = @JoinColumn(name = "partner_network_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "database_id", nullable = false))
    private List<Organization> organizations;

    @ManyToMany
    @JoinTable(name = "PartnerNetwork_Organization",
            joinColumns = @JoinColumn(name = "partner_network_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "organization_id", nullable = false))
    private List<Community> communities;

    public PartnerNetwork() {
    }

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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public List<Community> getCommunities() {
        return communities;
    }

    public void setCommunities(List<Community> communities) {
        this.communities = communities;
    }
}
