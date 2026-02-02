package com.scnsoft.eldermark.shared.carecoordination.adt;

import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XADPatientAddressDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XTNPhoneNumberDto;

import java.util.List;

public class GT1GuarantorSegmentDto implements SegmentDto {
    private String setId;
    private CECodedElementDto primaryLanguage;
    private List<String> guarantorNameList;
    private List<XADPatientAddressDto> guarantorAddressList;
    private List<XTNPhoneNumberDto> guarantorPhNumHomeList;

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public CECodedElementDto getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(CECodedElementDto primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public List<String> getGuarantorNameList() {
        return guarantorNameList;
    }

    public void setGuarantorNameList(List<String> guarantorNameList) {
        this.guarantorNameList = guarantorNameList;
    }

    public List<XADPatientAddressDto> getGuarantorAddressList() {
        return guarantorAddressList;
    }

    public void setGuarantorAddressList(List<XADPatientAddressDto> guarantorAddressList) {
        this.guarantorAddressList = guarantorAddressList;
    }

    public List<XTNPhoneNumberDto> getGuarantorPhNumHomeList() {
        return guarantorPhNumHomeList;
    }

    public void setGuarantorPhNumHomeList(List<XTNPhoneNumberDto> guarantorPhNumHomeList) {
        this.guarantorPhNumHomeList = guarantorPhNumHomeList;
    }
}
