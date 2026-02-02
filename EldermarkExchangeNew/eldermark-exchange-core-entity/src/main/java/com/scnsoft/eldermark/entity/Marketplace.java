
package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.marketplace.ServiceCategory;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table
public class Marketplace implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Boolean discoverable;

    @Column(name = "summary")
    private String summary;

    @Deprecated
    @Column(name = "email")
    private String email;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id", nullable = false, insertable = false, updatable = false)
    private Organization organization;

    @Column(name = "database_id", nullable = false)
    private Long organizationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Community community;

    @Column(name = "organization_id")
    private Long communityId;

    @ManyToMany
    @JoinTable(name = "Marketplace_LanguageService", joinColumns = @JoinColumn(name = "marketplace_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "language_service_id", nullable = false))
    private List<LanguageService> languageServices;

    @OneToMany(mappedBy = "marketplace")
    private List<MarketplacePartnerNetwork> marketplacePartnerNetworks;

    @ManyToMany
    @JoinTable(name = "Marketplace_ServiceCategory", joinColumns = @JoinColumn(name = "marketplace_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "service_category_id", nullable = false))
    private List<ServiceCategory> serviceCategories;

    @ManyToMany
    @JoinTable(name = "Marketplace_ServiceType", joinColumns = @JoinColumn(name = "marketplace_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "service_type_id", nullable = false))
    private List<ServiceType> serviceTypes;

    @Transient
    private Double userLatitude;

    @Transient
    private Double userLongitude;

    @ElementCollection
    @CollectionTable(name = "MarketplaceReferralEmail", joinColumns = @JoinColumn(name = "marketplace_id"))
    @Column(name = "email")
    private List<String> referralEmails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public List<LanguageService> getLanguageServices() {
        return languageServices;
    }

    public void setLanguageServices(List<LanguageService> languageServices) {
        this.languageServices = languageServices;
    }

    public List<MarketplacePartnerNetwork> getMarketplacePartnerNetworks() {
        return marketplacePartnerNetworks;
    }

    public void setMarketplacePartnerNetworks(List<MarketplacePartnerNetwork> marketplacePartnerNetworks) {
        this.marketplacePartnerNetworks = marketplacePartnerNetworks;
    }

    @Transient
    public Double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(Double userLatitude) {
        this.userLatitude = userLatitude;
    }

    @Transient
    public Double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(Double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public List<String> getReferralEmails() {
        return referralEmails;
    }

    public void setReferralEmails(List<String> referralEmails) {
        this.referralEmails = referralEmails;
    }

    public List<ServiceCategory> getServiceCategories() {
        return serviceCategories;
    }

    public void setServiceCategories(List<ServiceCategory> serviceCategories) {
        this.serviceCategories = serviceCategories;
    }

    public List<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }
}
