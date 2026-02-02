package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0128AllergySeverity;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ADT_SGMNT_AL1_Allergy")
public class AdtAL1AllergySegment implements AdtBaseMessageSegment, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "allergen_type_id")
    private CECodedElement allergenType;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "allergy_code_id")
    private CECodedElement allergyCode;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "allergy_severity_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0128AllergySeverity> allergySeverity;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="ADT_FIELD_AL1_AllergyReaction_LIST", joinColumns=@JoinColumn(name="AL1_Id"))
    @Column(name="allergy_reaction")
    private List<String> allergyReactionList;

    @Column(name = "identification_date")
    private Date identificationDate;

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

    public List<String> getAllergyReactionList() {
        return allergyReactionList;
    }

    public void setAllergyReactionList(final List<String> allergyReactionList) {
        this.allergyReactionList = allergyReactionList;
    }

    public Date getIdentificationDate() {
        return identificationDate;
    }

    public void setIdentificationDate(final Date identificationDate) {
        this.identificationDate = identificationDate;
    }
}
