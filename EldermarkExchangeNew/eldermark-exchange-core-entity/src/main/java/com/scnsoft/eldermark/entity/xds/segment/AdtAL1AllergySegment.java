package com.scnsoft.eldermark.entity.xds.segment;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0128AllergySeverity;

@Entity
@Table(name = "ADT_SGMNT_AL1_Allergy")
public class AdtAL1AllergySegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "allergen_type_id")
    private CECodedElement allergenType;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "allergy_code_id")
    private CECodedElement allergyCode;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "allergy_severity_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0128AllergySeverity> allergySeverity;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="ADT_FIELD_AL1_AllergyReaction_LIST", joinColumns=@JoinColumn(name="AL1_Id"))
    @Column(name="allergy_reaction")
    private List<String> allergyReactions;

    @Column(name = "identification_date")
    private Instant identificationDate;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(final String setId) {
        this.setId = setId;
    }

    public CECodedElement getAllergenType() {
        return allergenType;
    }

    public void setAllergenType(CECodedElement allergenType) {
        this.allergenType = allergenType;
    }

    public CECodedElement getAllergyCode() {
        return allergyCode;
    }

    public void setAllergyCode(final CECodedElement allergyCode) {
        this.allergyCode = allergyCode;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0128AllergySeverity> getAllergySeverity() {
        return allergySeverity;
    }

    public void setAllergySeverity(ISCodedValueForUserDefinedTables<HL7CodeTable0128AllergySeverity> allergySeverity) {
        this.allergySeverity = allergySeverity;
    }

    public List<String> getAllergyReactions() {
        return allergyReactions;
    }

    public void setAllergyReactions(final List<String> allergyReactions) {
        this.allergyReactions = allergyReactions;
    }

    public Instant getIdentificationDate() {
        return identificationDate;
    }

    public void setIdentificationDate(final Instant identificationDate) {
        this.identificationDate = identificationDate;
    }
}
