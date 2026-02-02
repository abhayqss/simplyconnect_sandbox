package com.scnsoft.eldermark.entity.marketplace;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.entity.phr.MarketplaceInNetworkInsurancePlan;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id"}))
public class Marketplace implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "discoverable", nullable = false)
    private Boolean discoverable;

    @Column(name = "allow_appointments", nullable = false)
    private Boolean allowAppointments;

    @Column(name = "all_insurances_accepted", nullable = false)
    private Boolean allInsurancesAccepted;

    @Column(name = "summary", length = 512)
    private String summary;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "secure_email", length = 150)
    private String secureEmail;

    @OneToOne(optional = false)
    @JoinColumn(name = "database_id", nullable = false, insertable = false, updatable = false)
    private Database database;

    @Column(name = "database_id", nullable = false)
    private Long databaseId;

    @OneToOne
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    @Column(name = "organization_id")
    private Long organizationId;

    @ManyToMany
    @JoinTable(name = "Marketplace_AgeGroup",
            joinColumns = @JoinColumn(name = "marketplace_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "age_group_id", nullable = false))
    private Set<AgeGroup> acceptedAgeGroups;

    @ManyToMany
    @JoinTable(name = "Marketplace_AncillaryService",
            joinColumns = @JoinColumn(name = "marketplace_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "ancillary_service_id", nullable = false))
    private Set<AncillaryService> ancillaryServices;

    @ManyToMany
    @JoinTable(name = "Marketplace_CommunityType",
            joinColumns = @JoinColumn(name = "marketplace_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "community_type_id", nullable = false))
    private Set<CommunityType> communityTypes;

    @ManyToMany
    @JoinTable(name = "Marketplace_EmergencyService",
            joinColumns = @JoinColumn(name = "marketplace_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "emergency_service_id", nullable = false))
    private Set<EmergencyService> emergencyServices;

    @ManyToMany
    @JoinTable(name = "Marketplace_LanguageService",
            joinColumns = @JoinColumn(name = "marketplace_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "language_service_id", nullable = false))
    private Set<LanguageService> languageServices;

    @ManyToMany
    @JoinTable(name = "Marketplace_LevelOfCare",
            joinColumns = @JoinColumn(name = "marketplace_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "level_of_care_id", nullable = false))
    private Set<LevelOfCare> levelsOfCare;

    @ManyToMany
    @JoinTable(name = "Marketplace_PrimaryFocus",
            joinColumns = @JoinColumn(name = "marketplace_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "primary_focus_id", nullable = false))
    private Set<PrimaryFocus> primaryFocuses;

    @ManyToMany
    @JoinTable(name = "Marketplace_ServicesTreatmentApproach",
            joinColumns = @JoinColumn(name = "marketplace_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "services_treatment_approach_id", nullable = false))
    private Set<ServicesTreatmentApproach> servicesTreatmentApproaches;

    @ManyToMany
    @JoinTable(name = "Marketplace_InNetworkInsurance",
            joinColumns = @JoinColumn(name = "marketplace_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "in_network_insurance_id", nullable = false))
    private Set<InNetworkInsurance> inNetworkInsurances;

    @ManyToMany
    @JoinTable(name = "Marketplace_InsurancePlan",
            joinColumns = @JoinColumn(name = "marketplace_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "insurance_plan_id", nullable = false))
    private Set<InsurancePlan> insurancePlans;

    @Column(name = "prerequisite")
    private String prerequisite;

    @Column(name = "exclusion")
    private String exclusion;

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

    public Boolean getAllowAppointments() {
        return allowAppointments;
    }

    public void setAllowAppointments(Boolean allowAppointments) {
        this.allowAppointments = allowAppointments;
    }

    public Boolean getAllInsurancesAccepted() {
        return allInsurancesAccepted;
    }

    public void setAllInsurancesAccepted(Boolean allInsurancesAccepted) {
        this.allInsurancesAccepted = allInsurancesAccepted;
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

    public String getSecureEmail() {
        return secureEmail;
    }

    public void setSecureEmail(String secureEmail) {
        this.secureEmail = secureEmail;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
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

    public Set<AgeGroup> getAcceptedAgeGroups() {
        return acceptedAgeGroups;
    }

    public void setAcceptedAgeGroups(Set<AgeGroup> acceptedAgeGroups) {
        this.acceptedAgeGroups = acceptedAgeGroups;
    }

    public Set<AncillaryService> getAncillaryServices() {
        return ancillaryServices;
    }

    public void setAncillaryServices(Set<AncillaryService> ancillaryServices) {
        this.ancillaryServices = ancillaryServices;
    }

    public Set<CommunityType> getCommunityTypes() {
        return communityTypes;
    }

    public void setCommunityTypes(Set<CommunityType> communityTypes) {
        this.communityTypes = communityTypes;
    }

    public Set<EmergencyService> getEmergencyServices() {
        return emergencyServices;
    }

    public void setEmergencyServices(Set<EmergencyService> emergencyServices) {
        this.emergencyServices = emergencyServices;
    }

    public Set<LanguageService> getLanguageServices() {
        return languageServices;
    }

    public void setLanguageServices(Set<LanguageService> languageServices) {
        this.languageServices = languageServices;
    }

    public Set<LevelOfCare> getLevelsOfCare() {
        return levelsOfCare;
    }

    public void setLevelsOfCare(Set<LevelOfCare> levelsOfCare) {
        this.levelsOfCare = levelsOfCare;
    }

    public Set<PrimaryFocus> getPrimaryFocuses() {
        return primaryFocuses;
    }

    public void setPrimaryFocuses(Set<PrimaryFocus> primaryFocuses) {
        this.primaryFocuses = primaryFocuses;
    }

    public Set<ServicesTreatmentApproach> getServicesTreatmentApproaches() {
        return servicesTreatmentApproaches;
    }

    public void setServicesTreatmentApproaches(Set<ServicesTreatmentApproach> servicesTreatmentApproaches) {
        this.servicesTreatmentApproaches = servicesTreatmentApproaches;
    }

    public Set<InNetworkInsurance> getInNetworkInsurances() {
        return inNetworkInsurances;
    }

    public void setInNetworkInsurances(Set<InNetworkInsurance> inNetworkInsurances) {
        this.inNetworkInsurances = inNetworkInsurances;
    }

    public Set<InsurancePlan> getInsurancePlans() {
        return insurancePlans;
    }

    public void setInsurancePlans(Set<InsurancePlan> insurancePlans) {
        this.insurancePlans = insurancePlans;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
        this.prerequisite = prerequisite;
    }

    public String getExclusion() {
        return exclusion;
    }

    public void setExclusion(String exclusion) {
        this.exclusion = exclusion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Marketplace that = (Marketplace) o;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(getSummary(), that.getSummary())
                .append(getEmail(), that.getEmail())
                .append(getSecureEmail(), that.getSecureEmail())
                .append(getDatabase(), that.getDatabase())
                .append(getOrganization(), that.getOrganization())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .append(getSummary())
                .append(getEmail())
                .append(getSecureEmail())
                .append(getDatabase())
                .append(getOrganization())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Marketplace{" +
                "latitude=" + organization.getAddresses().get(0).getLatitude() +
                "longitude=" + organization.getAddresses().get(0).getLongitude() +
                '}';
    }
}
