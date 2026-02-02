package com.scnsoft.eldermark.entity.referral;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Referral")
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "modified_date", nullable = false)
    private Instant modifiedDate;

    @Column(name = "request_datetime")
    private Instant requestDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "referral_status")
    private ReferralStatus referralStatus;

    @ManyToOne
    @JoinColumn(name = "priority_id")
    private ReferralPriority priority;

    @ManyToOne
    @JoinColumn(name = "intent_id")
    private ReferralIntent intent;

    @ManyToMany
    @JoinTable(name = "Referral_ReferralCategory", joinColumns = @JoinColumn(name = "referral_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<ReferralCategory> categories;

    @Column(name = "category_other_text")
    private String categoryOtherText;

    @ManyToMany
    @JoinTable(
            name = "Referral_ServiceType",
            joinColumns = @JoinColumn(name = "referral_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "service_type_id", nullable = false)
    )
    private List<ServiceType> services;

    @Column(name = "service_name")
    private String serviceName;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Client client;

    @Column(name = "resident_id", insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "client_location")
    private String clientLocation;

    @Column(name = "location_phone")
    private String locationPhone;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "in_network_insurance")
    private String inNetworkInsurance;

    @Column(name = "referring_individual")
    private String referringIndividual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_employee_id")
    private Employee requestingEmployee;

    @Column(name = "requesting_employee_id", insertable = false, updatable = false)
    private Long requestingEmployeeId;

    @Column(name = "requesting_organization_phone")
    private String requestingOrganizationPhone;

    @Column(name = "requesting_organization_email")
    private String requestingOrganizationEmail;

    @ManyToMany
    @JoinTable(name = "Referral_CcdCode_Reason",
            joinColumns = {@JoinColumn(name = "referral_id")},
            inverseJoinColumns = {@JoinColumn(name = "ccd_code_id")})
    private List<CcdCode> referralReasons;

    @Column(name = "referral_instructions")
    private String referralInstructions;

    @Column(name = "is_facesheet_shared")
    private boolean isFacesheetShared;

    @Column(name = "is_ccd_shared")
    private boolean isCcdShared;

    @Column(name = "is_service_plan_shared")
    private boolean isServicePlanShared;

    @OneToMany(mappedBy = "referral", cascade = CascadeType.ALL)
    private List<ReferralRequest> referralRequests;

    @ElementCollection
    @CollectionTable(name = "ReferralRequest", joinColumns = @JoinColumn(name = "referral_id"))
    @Column(name = "id", insertable = false, updatable = false)
    private Set<Long> referralRequestIds;

    @ManyToOne
    @JoinColumn(name = "updated_by_response_id")
    private ReferralRequestResponse updatedByResponse;

    @Column(name = "updated_by_response_id", insertable = false, updatable = false)
    private Long updatedByResponseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by")
    private Employee cancelledBy;

    @OneToMany(mappedBy = "referral", cascade = CascadeType.ALL)
    private List<ReferralHistory> referralHistories;

    @Column(name = "is_marketplace")
    private boolean isMarketplace;

    @OneToMany(mappedBy = "referral", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReferralAttachment> attachments;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requesting_organization_id")
    private Community requestingCommunity;

    @Column(name = "requesting_organization_id", insertable = false, updatable = false, nullable = false)
    private Long requestingCommunityId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Instant getRequestDatetime() {
        return requestDatetime;
    }

    public void setRequestDatetime(Instant requestDatetime) {
        this.requestDatetime = requestDatetime;
    }

    public ReferralStatus getReferralStatus() {
        return referralStatus;
    }

    public void setReferralStatus(ReferralStatus referralStatus) {
        this.referralStatus = referralStatus;
    }

    public ReferralPriority getPriority() {
        return priority;
    }

    public void setPriority(ReferralPriority priority) {
        this.priority = priority;
    }

    public ReferralIntent getIntent() {
        return intent;
    }

    public void setIntent(ReferralIntent intent) {
        this.intent = intent;
    }

    public List<ReferralCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ReferralCategory> categories) {
        this.categories = categories;
    }

    public String getCategoryOtherText() {
        return categoryOtherText;
    }

    public void setCategoryOtherText(String categoryOtherText) {
        this.categoryOtherText = categoryOtherText;
    }

    public List<ServiceType> getServices() {
        return services;
    }

    public void setServices(List<ServiceType> services) {
        this.services = services;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientLocation() {
        return clientLocation;
    }

    public void setClientLocation(String clientLocation) {
        this.clientLocation = clientLocation;
    }

    public String getLocationPhone() {
        return locationPhone;
    }

    public void setLocationPhone(String locationPhone) {
        this.locationPhone = locationPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(String inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
    }

    public String getReferringIndividual() {
        return referringIndividual;
    }

    public void setReferringIndividual(String referringIndividual) {
        this.referringIndividual = referringIndividual;
    }

    public Employee getRequestingEmployee() {
        return requestingEmployee;
    }

    public void setRequestingEmployee(Employee requestingEmployee) {
        this.requestingEmployee = requestingEmployee;
    }

    public Long getRequestingEmployeeId() {
        return requestingEmployeeId;
    }

    public void setRequestingEmployeeId(Long requestingEmployeeId) {
        this.requestingEmployeeId = requestingEmployeeId;
    }

    public String getRequestingOrganizationPhone() {
        return requestingOrganizationPhone;
    }

    public void setRequestingOrganizationPhone(String requestingOrganizationPhone) {
        this.requestingOrganizationPhone = requestingOrganizationPhone;
    }

    public String getRequestingOrganizationEmail() {
        return requestingOrganizationEmail;
    }

    public void setRequestingOrganizationEmail(String requestingOrganizationEmail) {
        this.requestingOrganizationEmail = requestingOrganizationEmail;
    }

    public List<CcdCode> getReferralReasons() {
        return referralReasons;
    }

    public void setReferralReasons(List<CcdCode> referralReasons) {
        this.referralReasons = referralReasons;
    }

    public String getReferralInstructions() {
        return referralInstructions;
    }

    public void setReferralInstructions(String referralInstructions) {
        this.referralInstructions = referralInstructions;
    }

    public boolean isFacesheetShared() {
        return isFacesheetShared;
    }

    public void setFacesheetShared(boolean facesheetShared) {
        isFacesheetShared = facesheetShared;
    }

    public boolean isCcdShared() {
        return isCcdShared;
    }

    public void setCcdShared(boolean ccdShared) {
        isCcdShared = ccdShared;
    }

    public boolean isServicePlanShared() {
        return isServicePlanShared;
    }

    public void setServicePlanShared(boolean servicePlanShared) {
        isServicePlanShared = servicePlanShared;
    }

    public List<ReferralRequest> getReferralRequests() {
        return referralRequests;
    }

    public void setReferralRequests(List<ReferralRequest> referralRequests) {
        this.referralRequests = referralRequests;
    }

    public Set<Long> getReferralRequestIds() {
        return referralRequestIds;
    }

    public void setReferralRequestIds(Set<Long> referralRequestIds) {
        this.referralRequestIds = referralRequestIds;
    }

    public ReferralRequestResponse getUpdatedByResponse() {
        return updatedByResponse;
    }

    public void setUpdatedByResponse(ReferralRequestResponse updatedByResponse) {
        this.updatedByResponse = updatedByResponse;
    }

    public Long getUpdatedByResponseId() {
        return updatedByResponseId;
    }

    public void setUpdatedByResponseId(Long updatedByResponseId) {
        this.updatedByResponseId = updatedByResponseId;
    }

    public Employee getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(Employee cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public List<ReferralHistory> getReferralHistories() {
        return referralHistories;
    }

    public void setReferralHistories(List<ReferralHistory> referralHistories) {
        this.referralHistories = referralHistories;
    }

    public boolean isMarketplace() {
        return isMarketplace;
    }

    public void setMarketplace(boolean isMarketplace) {
        this.isMarketplace = isMarketplace;
    }

    public List<ReferralAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ReferralAttachment> attachments) {
        this.attachments = attachments;
    }

    public Community getRequestingCommunity() {
        return requestingCommunity;
    }

    public void setRequestingCommunity(Community referrignCommunity) {
        this.requestingCommunity = referrignCommunity;
    }

    public Long getRequestingCommunityId() {
        return requestingCommunityId;
    }

    public void setRequestingCommunityId(Long referringCommunityId) {
        this.requestingCommunityId = referringCommunityId;
    }
}
