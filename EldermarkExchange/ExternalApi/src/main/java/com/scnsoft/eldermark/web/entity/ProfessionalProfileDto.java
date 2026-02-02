package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import javax.annotation.Generated;
import java.util.List;

/**
 * This DTO is intended to represent professional profile. It's a map of physician's professional data (field name / field value approach).
 */
@ApiModel(description = "This DTO is intended to represent professional profile. It's a map of physician's professional data (field name / field value approach).")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-06T18:36:31.035+03:00")
public class ProfessionalProfileDto {

    @JsonProperty("fax")
    private String fax;

    @JsonProperty("Education")
    private String education;

    @JsonProperty("Board of Certifications")
    private String boardOfCertifications;

    @JsonProperty("Professional Memberships")
    private String professionalMembership;

    @JsonProperty("In-Network Insurances")
    private List<String> inNetworkInsurances;

    @JsonIgnore
    private List<Long> inNetworkInsurancesIds;

    @JsonProperty("Specialities")
    private List<String> specialities;

    @JsonIgnore
    private List<Long> specialitiesIds;

    @JsonProperty("Hospital")
    private String hospitalName;

    @JsonProperty("Professional Statement")
    private String professionalStatement;

    @JsonIgnore
    private String npi;

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getBoardOfCertifications() {
        return boardOfCertifications;
    }

    public void setBoardOfCertifications(String boardOfCertifications) {
        this.boardOfCertifications = boardOfCertifications;
    }

    public String getProfessionalMembership() {
        return professionalMembership;
    }

    public void setProfessionalMembership(String professionalMembership) {
        this.professionalMembership = professionalMembership;
    }

    public List<String> getInNetworkInsurances() {
        return inNetworkInsurances;
    }

    public void setInNetworkInsurances(List<String> inNetworkInsurances) {
        this.inNetworkInsurances = inNetworkInsurances;
    }

    public List<String> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(List<String> specialities) {
        this.specialities = specialities;
    }

    public List<Long> getInNetworkInsurancesIds() {
        return inNetworkInsurancesIds;
    }

    public void setInNetworkInsurancesIds(List<Long> inNetworkInsurancesIds) {
        this.inNetworkInsurancesIds = inNetworkInsurancesIds;
    }

    public List<Long> getSpecialitiesIds() {
        return specialitiesIds;
    }

    public void setSpecialitiesIds(List<Long> specialitiesIds) {
        this.specialitiesIds = specialitiesIds;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getProfessionalStatement() {
        return professionalStatement;
    }

    public void setProfessionalStatement(String professionalStatement) {
        this.professionalStatement = professionalStatement;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    /**
     * Professional profile builder
     */
    public static final class Builder {
        private String fax;
        private String education;
        private String boardOfCertifications;
        private String professionalMembership;
        private List<String> inNetworkInsurances;
        private List<Long> inNetworkInsurancesIds;
        private List<String> specialities;
        private List<Long> specialitiesIds;
        private String hospitalName;
        private String professionalStatement;
        private String npi;

        private Builder() {
        }

        public static Builder aProfessionalProfileDto() {
            return new Builder();
        }

        public Builder withFax(String fax) {
            this.fax = fax;
            return this;
        }

        public Builder withEducation(String education) {
            this.education = education;
            return this;
        }

        public Builder withBoardOfCertifications(String boardOfCertifications) {
            this.boardOfCertifications = boardOfCertifications;
            return this;
        }

        public Builder withProfessionalMembership(String professionalMembership) {
            this.professionalMembership = professionalMembership;
            return this;
        }

        public Builder withInNetworkInsurances(List<String> inNetworkInsurances) {
            this.inNetworkInsurances = inNetworkInsurances;
            return this;
        }

        public Builder withInNetworkInsurancesIds(List<Long> inNetworkInsurancesIds) {
            this.inNetworkInsurancesIds = inNetworkInsurancesIds;
            return this;
        }

        public Builder withSpecialities(List<String> specialities) {
            this.specialities = specialities;
            return this;
        }

        public Builder withSpecialitiesIds(List<Long> specialitiesIds) {
            this.specialitiesIds = specialitiesIds;
            return this;
        }

        public Builder withHospitalName(String hospitalName) {
            this.hospitalName = hospitalName;
            return this;
        }

        public Builder withProfessionalStatement(String professionalStatement) {
            this.professionalStatement = professionalStatement;
            return this;
        }

        public Builder withNpi(String npi) {
            this.npi = npi;
            return this;
        }

        public ProfessionalProfileDto build() {
            ProfessionalProfileDto professionalProfileDto = new ProfessionalProfileDto();
            professionalProfileDto.setFax(fax);
            professionalProfileDto.setEducation(education);
            professionalProfileDto.setBoardOfCertifications(boardOfCertifications);
            professionalProfileDto.setProfessionalMembership(professionalMembership);
            professionalProfileDto.setInNetworkInsurances(inNetworkInsurances);
            professionalProfileDto.setInNetworkInsurancesIds(inNetworkInsurancesIds);
            professionalProfileDto.setSpecialities(specialities);
            professionalProfileDto.setSpecialitiesIds(specialitiesIds);
            professionalProfileDto.setHospitalName(hospitalName);
            professionalProfileDto.setProfessionalStatement(professionalStatement);
            professionalProfileDto.setNpi(npi);
            return professionalProfileDto;
        }
    }

}

