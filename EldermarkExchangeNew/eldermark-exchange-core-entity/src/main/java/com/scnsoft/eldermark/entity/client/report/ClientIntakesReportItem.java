package com.scnsoft.eldermark.entity.client.report;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.beans.projection.IdActiveCreatedLastUpdatedAware;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ClientIntakesReportItem extends ClientNameAndCommunity implements IdActiveCreatedLastUpdatedAware {

    private Instant intakeDate;
    private Instant lastUpdated;
    private Boolean isActive;
    private LocalDate birthDate;
    private Instant createdDate;
    private Instant exitDate;
    private Instant activationDate;
    private Instant deactivationDate;
    private String comment;
    private String exitComment;
    private ClientDeactivationReason deactivationReason;
    private String gender;
    private String race;
    private String city;
    private String insuranceNetwork;
    private String insurancePlan;
    private List<String> healthPlans;

    public ClientIntakesReportItem(Long id, String firstName, String lastName, Long communityId, String communityName,
                                   Instant intakeDate, Instant lastUpdated, Boolean isActive, LocalDate birthDate, Instant createdDate,
                                   String gender, String race, String city, String insuranceNetwork, String insurancePlan,
                                   Instant exitDate, Instant activationDate, Instant deactivationDate, String comment,
                                   String exitComment, ClientDeactivationReason deactivationReason) {
        super(id, firstName, lastName, communityId, communityName);
        this.intakeDate = intakeDate;
        this.lastUpdated = lastUpdated;
        this.isActive = isActive;
        this.birthDate = birthDate;
        this.createdDate = createdDate;
        this.gender = gender;
        this.race = race;
        this.city = city;
        this.insuranceNetwork = insuranceNetwork;
        this.insurancePlan = insurancePlan;
        this.exitDate = exitDate;
        this.deactivationDate = deactivationDate;
        this.activationDate = activationDate;
        this.comment = comment;
        this.exitComment = exitComment;
        this.deactivationReason = deactivationReason;
    }

    public List<String> getHealthPlans() {
        return healthPlans;
    }

    public void setHealthPlans(List<String> healthPlans) {
        this.healthPlans = healthPlans;
    }

    public String getInsuranceNetwork() {
        return insuranceNetwork;
    }

    public void setInsuranceNetwork(String insuranceNetwork) {
        this.insuranceNetwork = insuranceNetwork;
    }

    public String getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public Instant getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Instant intakeDate) {
        this.intakeDate = intakeDate;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Instant getExitDate() {
        return exitDate;
    }

    public void setExitDate(final Instant exitDate) {
        this.exitDate = exitDate;
    }

    public Instant getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(final Instant activationDate) {
        this.activationDate = activationDate;
    }

    public Instant getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(final Instant deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public ClientDeactivationReason getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(final ClientDeactivationReason deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public String getExitComment() {
        return exitComment;
    }

    public void setExitComment(final String exitComment) {
        this.exitComment = exitComment;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientIntakesReportItem)) return false;
        if (!super.equals(o)) return false;

        final ClientIntakesReportItem that = (ClientIntakesReportItem) o;

        if (getIntakeDate() != null ? !getIntakeDate().equals(that.getIntakeDate()) : that.getIntakeDate() != null)
            return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;
        if (getExitDate() != null ? !getExitDate().equals(that.getExitDate()) : that.getExitDate() != null)
            return false;
        if (getActivationDate() != null ? !getActivationDate().equals(that.getActivationDate()) : that.getActivationDate() != null)
            return false;
        if (getDeactivationDate() != null ? !getDeactivationDate().equals(that.getDeactivationDate()) : that.getDeactivationDate() != null)
            return false;
        if (getComment() != null ? !getComment().equals(that.getComment()) : that.getComment() != null) return false;
        if (getExitComment() != null ? !getExitComment().equals(that.getExitComment()) : that.getExitComment() != null)
            return false;
        return getDeactivationReason() == that.getDeactivationReason();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getIntakeDate() != null ? getIntakeDate().hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        result = 31 * result + (getExitDate() != null ? getExitDate().hashCode() : 0);
        result = 31 * result + (getActivationDate() != null ? getActivationDate().hashCode() : 0);
        result = 31 * result + (getDeactivationDate() != null ? getDeactivationDate().hashCode() : 0);
        result = 31 * result + (getComment() != null ? getComment().hashCode() : 0);
        result = 31 * result + (getExitComment() != null ? getExitComment().hashCode() : 0);
        result = 31 * result + (getDeactivationReason() != null ? getDeactivationReason().hashCode() : 0);
        return result;
    }
}
