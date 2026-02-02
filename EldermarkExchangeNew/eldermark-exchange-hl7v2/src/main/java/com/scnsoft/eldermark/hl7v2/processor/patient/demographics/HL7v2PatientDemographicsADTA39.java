package com.scnsoft.eldermark.hl7v2.processor.patient.demographics;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.group.ADT_A39_PATIENT;
import ca.uhn.hl7v2.model.v251.message.ADT_A39;
import ca.uhn.hl7v2.model.v251.segment.*;
import com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.datatype.DataTypeConverter;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

class HL7v2PatientDemographicsADTA39 extends HL7v2PatientDemographicsDefault {

    private final ADT_A39 a39in;
    private final ADT_A39_PATIENT patientGroup;

    public HL7v2PatientDemographicsADTA39(Message in, MessageSource messageSource, DataTypeConverter dataTypeService,
                                          int patientGroupIdx) {
        super(in, messageSource, dataTypeService);
        this.a39in = (ADT_A39) in;
        if (a39in.getPATIENTReps() <= patientGroupIdx) {
            throw new ExceptionInInitializerError("No Patient Group with index " + patientGroupIdx);
        }
        this.patientGroup = a39in.getPATIENT(patientGroupIdx);
    }

    protected MSH getMSH() throws HL7Exception {
        return (MSH) in.get("MSH");
    }

    protected PID getPID() throws HL7Exception {
        return this.patientGroup.getPID();
    }

    protected PV1 getPV1() throws HL7Exception {
        return this.patientGroup.getPV1();
    }

    protected PV2 getPV2() throws HL7Exception {
        //todo validate if this segmets can be present
        return (PV2) this.patientGroup.get("PV2");
    }

    protected DG1 getDG1() throws HL7Exception {
        return (DG1) this.patientGroup.get("DG1");
    }

    protected PD1 getPD1() throws HL7Exception {
        return (PD1) this.patientGroup.get("PD1");
    }

    protected MRG getMRG() throws HL7Exception {
        return (MRG) this.patientGroup.get("MRG");
    }
}
