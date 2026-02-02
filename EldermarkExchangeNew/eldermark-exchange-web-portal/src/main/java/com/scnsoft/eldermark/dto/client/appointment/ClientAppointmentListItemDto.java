package com.scnsoft.eldermark.dto.client.appointment;

import java.util.List;

public class ClientAppointmentListItemDto {

    private Long id;
    private String title;
    private String typeName;
    private String typeTitle;
    private String statusTitle;
    private String statusName;
    private Long clientId;
    private boolean canViewClient;
    private String clientName;
    private String clientGender;
    private String clientDOB;
    private String creator;
    private List<String> serviceProviders;
    private Long dateFrom;
    private Long dateTo;
    private String location;
    private String communityName;
    private String reasonForVisit;
    private String directionsInstructions;
    private String cancellationReason;
    private boolean canView;
    private boolean canEdit;
    private boolean canCancel;
    private boolean canDuplicate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public boolean getCanViewClient() {
        return canViewClient;
    }

    public void setCanViewClient(boolean canViewClient) {
        this.canViewClient = canViewClient;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientGender() {
        return clientGender;
    }

    public void setClientGender(String clientGender) {
        this.clientGender = clientGender;
    }

    public String getClientDOB() {
        return clientDOB;
    }

    public void setClientDOB(String clientDOB) {
        this.clientDOB = clientDOB;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(List<String> serviceProviders) {
        this.serviceProviders = serviceProviders;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public String getDirectionsInstructions() {
        return directionsInstructions;
    }

    public void setDirectionsInstructions(String directionsInstructions) {
        this.directionsInstructions = directionsInstructions;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public boolean getCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean getCanDuplicate() {
        return canDuplicate;
    }

    public void setCanDuplicate(boolean canDuplicate) {
        this.canDuplicate = canDuplicate;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
