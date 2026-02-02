package com.scnsoft.eldermark.entity.careteam.invitation;

import com.scnsoft.eldermark.beans.projection.ClientCareTeamInvitationCreatedAtAware;
import com.scnsoft.eldermark.beans.projection.ClientCareTeamInvitationStatusAware;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "ResidentCareTeamInvitation")
public class ClientCareTeamInvitation implements ClientCareTeamInvitationStatusAware, ClientCareTeamInvitationCreatedAtAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by_employee_id", nullable = false)
    private Long createdByEmployeeId;

    @JoinColumn(name = "created_by_employee_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne
    private Employee createdByEmployee;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ClientCareTeamInvitationStatus status;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "target_employee_id", nullable = false)
    private Long targetEmployeeId;

    @JoinColumn(name = "target_employee_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne
    private Employee targetEmployee;

    @Column(name = "resident_id", nullable = false)
    private Long clientId;

    @JoinColumn(name = "resident_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne
    private Client client;

    @Column(name = "family_app_resident_id")
    private Long familyAppClientId;

    @Column(name = "token")
    private String token;

    @Column(name = "is_hidden")
    private boolean isHidden;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "declined_at")
    private Instant declinedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @Column(name = "resent_from_invitation_id")
    private Long resentFromInvitationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedByEmployeeId() {
        return createdByEmployeeId;
    }

    public void setCreatedByEmployeeId(Long createdByEmployeeId) {
        this.createdByEmployeeId = createdByEmployeeId;
    }

    public Employee getCreatedByEmployee() {
        return createdByEmployee;
    }

    public void setCreatedByEmployee(Employee createdByEmployee) {
        this.createdByEmployee = createdByEmployee;
    }

    @Override
    public ClientCareTeamInvitationStatus getStatus() {
        return status;
    }

    public void setStatus(ClientCareTeamInvitationStatus status) {
        this.status = status;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getTargetEmployeeId() {
        return targetEmployeeId;
    }

    public void setTargetEmployeeId(Long targetEmployeeId) {
        this.targetEmployeeId = targetEmployeeId;
    }

    public Employee getTargetEmployee() {
        return targetEmployee;
    }

    public void setTargetEmployee(Employee targetEmployee) {
        this.targetEmployee = targetEmployee;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getFamilyAppClientId() {
        return familyAppClientId;
    }

    public void setFamilyAppClientId(Long familyAppResidentId) {
        this.familyAppClientId = familyAppResidentId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Instant getDeclinedAt() {
        return declinedAt;
    }

    public void setDeclinedAt(Instant declinedAt) {
        this.declinedAt = declinedAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Long getResentFromInvitationId() {
        return resentFromInvitationId;
    }

    public void setResentFromInvitationId(Long resentFromInvitationId) {
        this.resentFromInvitationId = resentFromInvitationId;
    }
}
