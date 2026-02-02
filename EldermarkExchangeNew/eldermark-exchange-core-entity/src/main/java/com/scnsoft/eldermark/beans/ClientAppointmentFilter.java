package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentStatus;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentType;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class ClientAppointmentFilter {

    private Long organizationId;
    private Set<Long> communityIds;
    private Set<Long> clientIds;
    private Set<Long> creatorIds;
    private Set<Long> serviceProviderIds;
    private ClientStatus clientStatus;
    private Set<ClientAppointmentType> types;
    private Set<ClientAppointmentStatus> statuses;
    private Boolean includeTriaged;
    private Boolean includePlanned;
    private Boolean isExternalProviderServiceProvider;
    private Boolean hasNoServiceProviders;

    @NotNull
    private Long dateFrom;

    @NotNull
    private Long dateTo;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Set<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(Set<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public Set<Long> getClientIds() {
        return clientIds;
    }

    public void setClientIds(Set<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public Set<Long> getCreatorIds() {
        return creatorIds;
    }

    public void setCreatorIds(Set<Long> creatorIds) {
        this.creatorIds = creatorIds;
    }

    public Set<Long> getServiceProviderIds() {
        return serviceProviderIds;
    }

    public void setServiceProviderIds(Set<Long> serviceProviderIds) {
        this.serviceProviderIds = serviceProviderIds;
    }

    public ClientStatus getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    public Set<ClientAppointmentType> getTypes() {
        return types;
    }

    public void setTypes(Set<ClientAppointmentType> types) {
        this.types = types;
    }

    public Set<ClientAppointmentStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(Set<ClientAppointmentStatus> statuses) {
        this.statuses = statuses;
    }

    public Long getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Long dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Long getDateTo() {
        return dateTo;
    }

    public void setDateTo(Long dateTo) {
        this.dateTo = dateTo;
    }

    public Boolean getIncludeTriaged() {
        return includeTriaged;
    }

    public void setIncludeTriaged(Boolean includeTriaged) {
        this.includeTriaged = includeTriaged;
    }

    public Boolean getIncludePlanned() {
        return includePlanned;
    }

    public void setIncludePlanned(Boolean includePlanned) {
        this.includePlanned = includePlanned;
    }

    public Boolean getIsExternalProviderServiceProvider() {
        return isExternalProviderServiceProvider;
    }

    public void setIsExternalProviderServiceProvider(Boolean isExternalProviderServiceProvider) {
        this.isExternalProviderServiceProvider = isExternalProviderServiceProvider;
    }

    public Boolean getHasNoServiceProviders() {
        return hasNoServiceProviders;
    }

    public void setHasNoServiceProviders(Boolean hasNoServiceProviders) {
        this.hasNoServiceProviders = hasNoServiceProviders;
    }
}
