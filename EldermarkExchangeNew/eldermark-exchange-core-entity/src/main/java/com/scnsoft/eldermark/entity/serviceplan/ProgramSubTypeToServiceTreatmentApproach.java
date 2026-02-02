package com.scnsoft.eldermark.entity.serviceplan;

import com.scnsoft.eldermark.entity.ServicesTreatmentApproach;

import javax.persistence.*;

@Entity
@Table(name = "ProgramSubType_ServicesTreatmentApproach")
public class ProgramSubTypeToServiceTreatmentApproach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_sub_type_id", referencedColumnName = "id")
    private ProgramSubType programSubType;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    private ServicesTreatmentApproach servicesTreatmentApproach;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgramSubType getProgramSubType() {
        return programSubType;
    }

    public void setProgramSubType(ProgramSubType programSubType) {
        this.programSubType = programSubType;
    }

    public ServicesTreatmentApproach getServicesTreatmentApproach() {
        return servicesTreatmentApproach;
    }

    public void setServicesTreatmentApproach(ServicesTreatmentApproach servicesTreatmentApproach) {
        this.servicesTreatmentApproach = servicesTreatmentApproach;
    }
}
