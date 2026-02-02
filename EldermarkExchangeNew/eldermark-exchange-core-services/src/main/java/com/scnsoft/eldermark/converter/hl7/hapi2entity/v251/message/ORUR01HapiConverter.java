package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.message;

import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.model.v251.segment.*;
import com.scnsoft.eldermark.entity.xds.message.ORUR01;
import com.scnsoft.eldermark.entity.xds.segment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ORUR01HapiConverter extends HL7MessageConverter<ORU_R01, ORUR01> {

    @Autowired
    private Converter<MSH, MSHMessageHeaderSegment> mshConverter;

    @Autowired
    private Converter<PID, PIDPatientIdentificationSegment> pidConverter;

//    @Autowired
//    private Converter<PV1, PV1ClientVisitSegment> pv1Converter;

    @Autowired
    private Converter<NTE, NTENotesAndComments> nteConveter;

    @Autowired
    private Converter<ORC, ORCCommonOrderSegment> orcConverter;

    @Autowired
    private Converter<OBX, OBXObservationResult> obxConverter;

    @Autowired
    private Converter<SPM, SPMSpecimen> spmConverter;

    @Override
    protected ORUR01 doConvert(ORU_R01 source) {
        var oru = new ORUR01();
        oru.setMsh(mshConverter.convert(source.getMSH()));

        var patient = source.getPATIENT_RESULT().getPATIENT();
        oru.setPid(pidConverter.convert(patient.getPID()));
        oru.setNteList(convertNteList(source));
//        oru.setPv1(pv1Converter.convert(patient.getVISIT().getPV1()));

        //apollo populates only one ORC
        var firstOrderObservation = source.getPATIENT_RESULT().getORDER_OBSERVATION();
        oru.setOrc(orcConverter.convert(firstOrderObservation.getORC()));
        oru.setObxList(convertObxList(source));

        //apollo populate only one SPM
        oru.setSpm(spmConverter.convert(firstOrderObservation.getSPECIMEN().getSPM()));

        return oru;
    }

    private List<NTENotesAndComments> convertNteList(ORU_R01 source) {
        var nteList = new ArrayList<NTENotesAndComments>();
        for (int i = 0; i < source.getPATIENT_RESULTReps(); i++) {
            var resultGroup = source.getPATIENT_RESULT(i);

            var patientGroup = resultGroup.getPATIENT();

            for (int j = 0; j < patientGroup.getNTEReps(); j++) {
                nteList.add(nteConveter.convert(patientGroup.getNTE(j)));
            }

            for (int j = 0; j < resultGroup.getORDER_OBSERVATIONReps(); j++) {
                var orderObservationGroup = resultGroup.getORDER_OBSERVATION(j);

                for (int k = 0; k < orderObservationGroup.getNTEReps(); k++) {
                    nteList.add(nteConveter.convert(orderObservationGroup.getNTE(k)));
                }

                for (int k = 0; k < orderObservationGroup.getOBSERVATIONReps(); k++) {
                    var observationGroup = orderObservationGroup.getOBSERVATION(k);

                    for (int l = 0; l < observationGroup.getNTEReps(); l++) {
                        nteList.add(nteConveter.convert(observationGroup.getNTE(l)));
                    }
                }
            }
        }

        return nteList;
    }

    private List<OBXObservationResult> convertObxList(ORU_R01 source) {
        var obxList = new ArrayList<OBXObservationResult>();
        for (int i = 0; i < source.getPATIENT_RESULTReps(); i++) {
            var resultGroup = source.getPATIENT_RESULT(i);
            for (int j = 0; j < resultGroup.getORDER_OBSERVATIONReps(); j++) {
                var orderObservationGroup = resultGroup.getORDER_OBSERVATION(j);

                for (int k = 0; k < orderObservationGroup.getOBSERVATIONReps(); k++) {
                    var observationGroup = orderObservationGroup.getOBSERVATION(k);
                    obxList.add(obxConverter.convert(observationGroup.getOBX()));
                }

                for (int k = 0; k < orderObservationGroup.getSPECIMENReps(); k++) {
                    var specimenGroup = orderObservationGroup.getSPECIMEN(k);
                    for (int m = 0; m < specimenGroup.getOBXReps(); m++ ) {
                        obxList.add(obxConverter.convert(specimenGroup.getOBX(m)));
                    }
                }
            }
        }
        return obxList;
    }
}
