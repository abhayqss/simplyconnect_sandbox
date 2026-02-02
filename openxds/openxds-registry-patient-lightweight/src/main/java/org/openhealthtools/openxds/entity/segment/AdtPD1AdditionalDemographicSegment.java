package org.openhealthtools.openxds.entity.segment;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.openhealthtools.openxds.entity.datatype.CECodedElement;
import org.openhealthtools.openxds.entity.datatype.CXExtendedCompositeId;
import org.openhealthtools.openxds.entity.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import org.openhealthtools.openxds.entity.datatype.XONExtendedCompositeNameAndIdForOrganizations;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "ADT_SGMNT_PD1_Patient_Additional_Demographic")
public class AdtPD1AdditionalDemographicSegment implements AdtBaseMessageSegment, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     *
     * [update hint] Usage @CollectionOfElements is a necessary measure as JPA 1.0 doesn't support
     * @ElementCollection (since JPA 2.0).
     *
     * Please use @ElementCollection after updating to JPA 2.0+
     */
    @CollectionOfElements
    @JoinTable(name="ADT_FIELD_PD1_LivingDependency_LIST", joinColumns=@JoinColumn(name="PD1_Id"))
    @Column(name="living_dependency")
    private List<String> livingDependencyList;

    @Column(name = "living_arrangement")
    private String livingArrangement;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_FIELD_PD1_PatientPrimaryFacility",
            joinColumns = @JoinColumn(name = "PD1_Id"),
            inverseJoinColumns = @JoinColumn(name = "XON_Id")
    )
    private List<XONExtendedCompositeNameAndIdForOrganizations> primaryFacilityList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_FIELD_PD1_CareProvider",
            joinColumns = @JoinColumn(name = "PD1_Id"),
            inverseJoinColumns = @JoinColumn(name = "XCN_Id")
    )
    private List<XCNExtendedCompositeIdNumberAndNameForPersons> primaryCareProviderList;

    @Column(name = "student_indicator")
    private String studentIndicator;

    @Column
    private String handicap;

    @Column(name = "living_will")
    private String livingWill;

    @Column(name = "organ_donor")
    private String organDonor;

    @Column(name = "separate_bill")
    private String separateBill;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_FIELD_PD1_DuplicatePatient",
            joinColumns = @JoinColumn(name = "PD1_Id"),
            inverseJoinColumns = @JoinColumn(name = "CX_Id")
    )
    private List<CXExtendedCompositeId> duplicatePatientList;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "publicity_code_id")
    private CECodedElement publicityCode;

    @Column(name = "protection_indicator")
    private String protectionIndicator;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public List<String> getLivingDependencyList() {
        return null;
    }

    public void setLivingDependencyList(final List<String> livingDependencyList) {
        this.livingDependencyList = livingDependencyList;
    }

    public String getLivingArrangement() {
        return livingArrangement;
    }

    public void setLivingArrangement(final String livingArrangement) {
        this.livingArrangement = livingArrangement;
    }

    public List<XONExtendedCompositeNameAndIdForOrganizations> getPrimaryFacilityList() {
        return primaryFacilityList;
    }

    public void setPrimaryFacilityList(final List<XONExtendedCompositeNameAndIdForOrganizations> primaryFacilityList) {
        this.primaryFacilityList = primaryFacilityList;
    }

    public List<XCNExtendedCompositeIdNumberAndNameForPersons> getPrimaryCareProviderList() {
        return primaryCareProviderList;
    }

    public void setPrimaryCareProviderList(final List<XCNExtendedCompositeIdNumberAndNameForPersons> primaryCareProviderList) {
        this.primaryCareProviderList = primaryCareProviderList;
    }

    public String getStudentIndicator() {
        return studentIndicator;
    }

    public void setStudentIndicator(final String studentIndicator) {
        this.studentIndicator = studentIndicator;
    }

    public String getHandicap() {
        return handicap;
    }

    public void setHandicap(final String handicap) {
        this.handicap = handicap;
    }

    public String getLivingWill() {
        return livingWill;
    }

    public void setLivingWill(final String livingWill) {
        this.livingWill = livingWill;
    }

    public String getOrganDonor() {
        return organDonor;
    }

    public void setOrganDonor(final String organDonor) {
        this.organDonor = organDonor;
    }

    public String getSeparateBill() {
        return separateBill;
    }

    public void setSeparateBill(final String separateBill) {
        this.separateBill = separateBill;
    }

    public CECodedElement getPublicityCode() {
        return publicityCode;
    }

    public void setPublicityCode(final CECodedElement publicityCode) {
        this.publicityCode = publicityCode;
    }

    public String getProtectionIndicator() {
        return protectionIndicator;
    }

    public void setProtectionIndicator(final String protectionIndicator) {
        this.protectionIndicator = protectionIndicator;
    }

    public List<CXExtendedCompositeId> getDuplicatePatientList() {
        return duplicatePatientList;
    }

    public void setDuplicatePatientList(final List<CXExtendedCompositeId> duplicatePatientList) {
        this.duplicatePatientList = duplicatePatientList;
    }
}
