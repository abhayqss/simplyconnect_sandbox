package org.openhealthtools.openxds.entity.segment;

import org.hibernate.annotations.Cascade;
import org.openhealthtools.openxds.entity.datatype.CECodedElement;
import org.openhealthtools.openxds.entity.datatype.XADPatientAddress;
import org.openhealthtools.openxds.entity.datatype.XPNPersonName;
import org.openhealthtools.openxds.entity.datatype.XTNPhoneNumber;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "ADT_SGMNT_GT1_Guarantor")
public class AdtGT1GuarantorSegment implements AdtBaseMessageSegment, Serializable {

    private static final long serialVersionUID = 1L;

    //attributes BEGIN
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "primary_language_id")
    private CECodedElement primaryLanguage;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_FIELD_GT1_GuarantorName",
            joinColumns = @JoinColumn(name = "GT1_Id"),
            inverseJoinColumns = @JoinColumn(name = "XPN_Id")
    )
    private List<XPNPersonName> guarantorNameList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_FIELD_GT1_GuarantorAddress",
            joinColumns = @JoinColumn(name = "GT1_Id"),
            inverseJoinColumns = @JoinColumn(name = "XAD_Id")
    )
    private List<XADPatientAddress> guarantorAddressList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_FIELD_GT1_GuarantorPhNumPhone",
            joinColumns = @JoinColumn(name = "GT1_Id"),
            inverseJoinColumns = @JoinColumn(name = "XTN_Id")
    )
    private List<XTNPhoneNumber> GuarantorPhNumHomeList;
    //attributes END

    //getters setters BEGIN
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public CECodedElement getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(CECodedElement primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public List<XPNPersonName> getGuarantorNameList() {
        return guarantorNameList;
    }

    public void setGuarantorNameList(List<XPNPersonName> guarantorNameList) {
        this.guarantorNameList = guarantorNameList;
    }

    public List<XADPatientAddress> getGuarantorAddressList() {
        return guarantorAddressList;
    }

    public void setGuarantorAddressList(List<XADPatientAddress> guarantorAddressList) {
        this.guarantorAddressList = guarantorAddressList;
    }

    public List<XTNPhoneNumber> getGuarantorPhNumHomeList() {
        return GuarantorPhNumHomeList;
    }

    public void setGuarantorPhNumHomeList(List<XTNPhoneNumber> guarantorPhNumHomeList) {
        GuarantorPhNumHomeList = guarantorPhNumHomeList;
    }
    //getters setters END
}
