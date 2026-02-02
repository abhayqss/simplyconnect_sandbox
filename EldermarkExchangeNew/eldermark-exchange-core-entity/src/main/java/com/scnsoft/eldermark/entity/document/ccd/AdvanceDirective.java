package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.LegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.document.AdvanceDirectiveDocument;
import com.scnsoft.eldermark.entity.document.CcdCode;

/**
 * The Advance Directive observation is used to convey information about a
 * single advance directive which has been identified by the patient.
 */
@Entity
@AttributeOverride(name = "legacyTable", column = @Column(name = "legacy_table", nullable = false, length = 30))
public class AdvanceDirective extends LegacyTableAwareEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "advance_directive_type_id")
    private CcdCode type;

    @ManyToOne
    @JoinColumn(name = "advance_directive_value_id")
    private CcdCode value;

    @Column(name = "effective_time_low")
    private Date timeLow;

    @Column(name = "effective_time_high")
    private Date timeHigh;

    @Lob
    @Column(name = "text_type")
    private String textType;

    @Column(name = "text_value")
    private String textValue;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "AdvanceDirectivesVerifier", joinColumns = @JoinColumn(name = "advance_directive_id"), inverseJoinColumns = @JoinColumn(name = "verifier_id"))
    private List<Participant> verifiers;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "custodian_id")
    private Participant custodian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "advanceDirective", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdvanceDirectiveDocument> referenceDocuments;

    public CcdCode getType() {
        return type;
    }

    public void setType(CcdCode type) {
        this.type = type;
    }

    public Date getTimeLow() {
        return timeLow;
    }

    public void setTimeLow(Date timeLow) {
        this.timeLow = timeLow;
    }

    public Date getTimeHigh() {
        return timeHigh;
    }

    public void setTimeHigh(Date timeHigh) {
        this.timeHigh = timeHigh;
    }

    public List<Participant> getVerifiers() {
        return verifiers;
    }

    public void setVerifiers(List<Participant> verifiers) {
        this.verifiers = verifiers;
    }

    public Participant getCustodian() {
        return custodian;
    }

    public void setCustodian(Participant custodian) {
        this.custodian = custodian;
    }

    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<AdvanceDirectiveDocument> getReferenceDocuments() {
        return referenceDocuments;
    }

    public void setReferenceDocuments(List<AdvanceDirectiveDocument> referenceDocuments) {
        this.referenceDocuments = referenceDocuments;
    }

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

}
