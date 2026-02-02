package com.scnsoft.eldermark.entity.document.ccd;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ClientAllergy")
public class ClientAllergy {

    @Id
    @Column(name = "allergy_observation_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergy_observation_id", insertable = false, updatable = false)
    private AllergyObservation observation;

    @Column(name = "allergy_id")
    private Long allergyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergy_id", insertable = false, updatable = false)
    private Allergy allergy;

    @Column(name = "client_id")
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;

    @Column(name = "product_text")
    private String productText;

    @Column(name = "type_text")
    private String typeText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_code_id")
    private CcdCode typeCode;

    @Column(name = "severity_text")
    private String severityText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "severity_code_id")
    private CcdCode severityCode;

    @Column(name = "combined_reactions_texts", columnDefinition = "nvarchar")
    @Nationalized
    private String combinedReactionTexts;

    @Column(name = "effective_time_low")
    private Instant effectiveTimeLow;

    @Column(name = "effective_time_high")
    private Instant effectiveTimeHigh;

    @Column(name = "observation_status_code_id")
    private Long observationStatusCodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observation_status_code_id", insertable = false, updatable = false)
    private CcdCode observationStatusCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ClientAllergyStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AllergyObservation getObservation() {
        return observation;
    }

    public void setObservation(AllergyObservation observation) {
        this.observation = observation;
    }

    public Long getAllergyId() {
        return allergyId;
    }

    public void setAllergyId(Long allergyId) {
        this.allergyId = allergyId;
    }

    public Allergy getAllergy() {
        return allergy;
    }

    public void setAllergy(Allergy allergy) {
        this.allergy = allergy;
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

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public CcdCode getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(CcdCode typeCode) {
        this.typeCode = typeCode;
    }

    public String getSeverityText() {
        return severityText;
    }

    public void setSeverityText(String severityText) {
        this.severityText = severityText;
    }

    public CcdCode getSeverityCode() {
        return severityCode;
    }

    public void setSeverityCode(CcdCode severityCode) {
        this.severityCode = severityCode;
    }

    public String getCombinedReactionTexts() {
        return combinedReactionTexts;
    }

    public void setCombinedReactionTexts(String combinedReactionTexts) {
        this.combinedReactionTexts = combinedReactionTexts;
    }

    public Instant getEffectiveTimeLow() {
        return effectiveTimeLow;
    }

    public void setEffectiveTimeLow(Instant effectiveTimeLow) {
        this.effectiveTimeLow = effectiveTimeLow;
    }

    public Instant getEffectiveTimeHigh() {
        return effectiveTimeHigh;
    }

    public void setEffectiveTimeHigh(Instant effectiveTimeHigh) {
        this.effectiveTimeHigh = effectiveTimeHigh;
    }

    public Long getObservationStatusCodeId() {
        return observationStatusCodeId;
    }

    public void setObservationStatusCodeId(Long observationStatusCodeId) {
        this.observationStatusCodeId = observationStatusCodeId;
    }

    public CcdCode getObservationStatusCode() {
        return observationStatusCode;
    }

    public void setObservationStatusCode(CcdCode observationStatusCode) {
        this.observationStatusCode = observationStatusCode;
    }

    public ClientAllergyStatus getStatus() {
        return status;
    }

    public void setStatus(ClientAllergyStatus status) {
        this.status = status;
    }
}
