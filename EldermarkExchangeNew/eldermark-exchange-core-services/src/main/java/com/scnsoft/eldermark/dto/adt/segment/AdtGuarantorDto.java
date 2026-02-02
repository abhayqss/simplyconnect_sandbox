package com.scnsoft.eldermark.dto.adt.segment;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.dto.adt.datatype.CXExtendedCompositeIdDto;
import com.scnsoft.eldermark.dto.adt.datatype.XPNDto;
import com.scnsoft.eldermark.dto.adt.datatype.XTNPhoneNumberDto;

import java.util.List;

public class AdtGuarantorDto {

    private String setId;
    List<CXExtendedCompositeIdDto> guarantorNumbers;
    private List<XPNDto> guarantorNames;
    private List<AddressDto> guarantorAddresses;
    private List<XTNPhoneNumberDto> guarantorHomePhones;
    private Long guarantorDatetimeOfBirth;
    private String guarantorAdministrativeSex;
    private String guarantorType;
    private CECodedElementDto guarantorRelationship;
    private String guarantorEmploymentStatus;
    private CECodedElementDto primaryLanguage;

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public List<CXExtendedCompositeIdDto> getGuarantorNumbers() {
        return guarantorNumbers;
    }

    public void setGuarantorNumbers(List<CXExtendedCompositeIdDto> guarantorNumbers) {
        this.guarantorNumbers = guarantorNumbers;
    }

    public List<XPNDto> getGuarantorNames() {
        return guarantorNames;
    }

    public void setGuarantorNames(List<XPNDto> guarantorNames) {
        this.guarantorNames = guarantorNames;
    }

    public List<AddressDto> getGuarantorAddresses() {
        return guarantorAddresses;
    }

    public void setGuarantorAddresses(List<AddressDto> guarantorAddresses) {
        this.guarantorAddresses = guarantorAddresses;
    }

    public List<XTNPhoneNumberDto> getGuarantorHomePhones() {
        return guarantorHomePhones;
    }

    public void setGuarantorHomePhones(List<XTNPhoneNumberDto> guarantorHomePhones) {
        this.guarantorHomePhones = guarantorHomePhones;
    }

    public Long getGuarantorDatetimeOfBirth() {
        return guarantorDatetimeOfBirth;
    }

    public void setGuarantorDatetimeOfBirth(Long guarantorDatetimeOfBirth) {
        this.guarantorDatetimeOfBirth = guarantorDatetimeOfBirth;
    }

    public String getGuarantorAdministrativeSex() {
        return guarantorAdministrativeSex;
    }

    public void setGuarantorAdministrativeSex(String guarantorAdministrativeSex) {
        this.guarantorAdministrativeSex = guarantorAdministrativeSex;
    }

    public String getGuarantorType() {
        return guarantorType;
    }

    public void setGuarantorType(String guarantorType) {
        this.guarantorType = guarantorType;
    }

    public CECodedElementDto getGuarantorRelationship() {
        return guarantorRelationship;
    }

    public void setGuarantorRelationship(CECodedElementDto guarantorRelationship) {
        this.guarantorRelationship = guarantorRelationship;
    }

    public String getGuarantorEmploymentStatus() {
        return guarantorEmploymentStatus;
    }

    public void setGuarantorEmploymentStatus(String guarantorEmploymentStatus) {
        this.guarantorEmploymentStatus = guarantorEmploymentStatus;
    }

    public CECodedElementDto getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(CECodedElementDto primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }
}
