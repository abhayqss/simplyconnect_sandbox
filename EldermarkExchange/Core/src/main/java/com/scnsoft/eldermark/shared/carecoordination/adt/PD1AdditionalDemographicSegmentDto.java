package com.scnsoft.eldermark.shared.carecoordination.adt;

import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CXExtendedCompositeIdDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XCNExtendedCompositeIdNumberAndNameForPersonsDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XONExtendedCompositeNameAndIdForOrganizationsDto;

import java.util.List;

public class PD1AdditionalDemographicSegmentDto implements SegmentDto {
    private List<String> livingDependencyList;
    private String livingArrangement;
    private List<XONExtendedCompositeNameAndIdForOrganizationsDto> primaryFacilityList;
    private List<XCNExtendedCompositeIdNumberAndNameForPersonsDto> primaryCareProviderList;
    private String studentIndicator;
    private String handicap;
    private String livingWill;
    private String organDonor;
    private String separateBill;
    private List<CXExtendedCompositeIdDto> duplicatePatientList;
    private CECodedElementDto publicityCode;
    private String protectionIndicator;

    public List<String> getLivingDependencyList() {
        return livingDependencyList;
    }

    public void setLivingDependencyList(List<String> livingDependencyList) {
        this.livingDependencyList = livingDependencyList;
    }

    public String getLivingArrangement() {
        return livingArrangement;
    }

    public void setLivingArrangement(String livingArrangement) {
        this.livingArrangement = livingArrangement;
    }

    public List<XONExtendedCompositeNameAndIdForOrganizationsDto> getPrimaryFacilityList() {
        return primaryFacilityList;
    }

    public void setPrimaryFacilityList(List<XONExtendedCompositeNameAndIdForOrganizationsDto> primaryFacilityList) {
        this.primaryFacilityList = primaryFacilityList;
    }

    public List<XCNExtendedCompositeIdNumberAndNameForPersonsDto> getPrimaryCareProviderList() {
        return primaryCareProviderList;
    }

    public void setPrimaryCareProviderList(List<XCNExtendedCompositeIdNumberAndNameForPersonsDto> primaryCareProviderList) {
        this.primaryCareProviderList = primaryCareProviderList;
    }

    public String getStudentIndicator() {
        return studentIndicator;
    }

    public void setStudentIndicator(String studentIndicator) {
        this.studentIndicator = studentIndicator;
    }

    public String getHandicap() {
        return handicap;
    }

    public void setHandicap(String handicap) {
        this.handicap = handicap;
    }

    public String getLivingWill() {
        return livingWill;
    }

    public void setLivingWill(String livingWill) {
        this.livingWill = livingWill;
    }

    public String getOrganDonor() {
        return organDonor;
    }

    public void setOrganDonor(String organDonor) {
        this.organDonor = organDonor;
    }

    public String getSeparateBill() {
        return separateBill;
    }

    public void setSeparateBill(String separateBill) {
        this.separateBill = separateBill;
    }

    public List<CXExtendedCompositeIdDto> getDuplicatePatientList() {
        return duplicatePatientList;
    }

    public void setDuplicatePatientList(List<CXExtendedCompositeIdDto> duplicatePatientList) {
        this.duplicatePatientList = duplicatePatientList;
    }

    public CECodedElementDto getPublicityCode() {
        return publicityCode;
    }

    public void setPublicityCode(CECodedElementDto publicityCode) {
        this.publicityCode = publicityCode;
    }

    public String getProtectionIndicator() {
        return protectionIndicator;
    }

    public void setProtectionIndicator(String protectionIndicator) {
        this.protectionIndicator = protectionIndicator;
    }
}
