package com.scnsoft.eldermark.api.external.entity;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import com.scnsoft.eldermark.api.shared.entity.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Physician")
public class Physician extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_mobile_id")
    private MobileUser userMobile;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "fax")
    private String fax;

    @Column(name = "education")
    private String education;

    @Column(name = "board_of_certifications")
    private String boardOfCertifications;

    @Column(name = "professional_membership")
    private String professionalMembership;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Physician_InNetworkInsurance",
            joinColumns = @JoinColumn(name = "physician_id"),
            inverseJoinColumns = @JoinColumn(name = "in_network_insurance_id"))
    private Set<InNetworkInsurance> inNetworkInsurances;

    @Column(name = "npi")
    private String npi;

    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "professional_statement")
    private String professionalStatement;

    @Column(name = "verified")
    private Boolean verified;

    @Column(name = "discoverable")
    private Boolean discoverable;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "physician")
    private Set<PhysicianAttachment> attachments;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Physician_PhysicianCategory",
            joinColumns = @JoinColumn(name = "physician_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<PhysicianCategory> categories = new HashSet<PhysicianCategory>();


    /**
     * Physician builder
     */
    public static final class Builder {
        private MobileUser userMobile;
        private Employee employee;
        private Long id;
        private String fax;
        private String education;
        private String boardOfCertifications;
        private String professionalMembership;
        private Set<InNetworkInsurance> inNetworkInsurances;
        private String npi;
        private String hospitalName;
        private String professionalStatement;
        private Boolean verified;
        private Boolean discoverable;
        private Set<PhysicianAttachment> attachments;
        private Set<PhysicianCategory> categories;

        private Builder() {
        }

        public static Builder aPhysician() {
            return new Builder();
        }

        public Builder withUserMobile(MobileUser userMobile) {
            this.userMobile = userMobile;
            return this;
        }

        public Builder withEmployee(Employee employee) {
            this.employee = employee;
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
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

        public Builder withInNetworkInsurance(Set<InNetworkInsurance> inNetworkInsurances) {
            this.inNetworkInsurances = inNetworkInsurances;
            return this;
        }

        public Builder withNpi(String npi) {
            this.npi = npi;
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

        public Builder withVerified(Boolean verified) {
            this.verified = verified;
            return this;
        }

        public Builder withDiscoverable(Boolean discoverable) {
            this.discoverable = discoverable;
            return this;
        }

        public Builder withAttachments(Set<PhysicianAttachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder withCategories(Set<PhysicianCategory> categories) {
            this.categories = categories;
            return this;
        }

        public Physician build() {
            Physician physician = new Physician();
            physician.setUserMobile(userMobile);
            physician.setEmployee(employee);
            physician.setId(id);
            physician.setFax(fax);
            physician.setEducation(education);
            physician.setBoardOfCertifications(boardOfCertifications);
            physician.setProfessionalMembership(professionalMembership);
            physician.setInNetworkInsurances(inNetworkInsurances);
            physician.setNpi(npi);
            physician.setHospitalName(hospitalName);
            physician.setProfessionalStatement(professionalStatement);
            physician.setVerified(verified);
            physician.setDiscoverable(discoverable);
            physician.setAttachments(attachments);
            physician.setCategories(categories);
            return physician;
        }
    }

    public MobileUser getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(MobileUser userMobile) {
        this.userMobile = userMobile;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

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

    public Set<InNetworkInsurance> getInNetworkInsurances() {
        return inNetworkInsurances;
    }

    public void setInNetworkInsurances(Set<InNetworkInsurance> inNetworkInsurances) {
        this.inNetworkInsurances = inNetworkInsurances;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
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

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public Set<PhysicianAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<PhysicianAttachment> attachments) {
        this.attachments = attachments;
    }

    public Set<PhysicianCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<PhysicianCategory> categories) {
        this.categories = categories;
    }

}
