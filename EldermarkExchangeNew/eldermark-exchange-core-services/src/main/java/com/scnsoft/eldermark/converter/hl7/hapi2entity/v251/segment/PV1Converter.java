package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.segment;

import ca.uhn.hl7v2.model.v251.segment.PV1;
import com.scnsoft.eldermark.entity.xds.hl7table.*;
import com.scnsoft.eldermark.entity.xds.segment.PV1ClientVisitSegment;
import org.springframework.stereotype.Service;

@Service
public class PV1Converter extends HL7SegmentConverter<PV1, PV1ClientVisitSegment> {

    @Override
    public PV1ClientVisitSegment doConvert(PV1 segment) {
        var pv1 = new PV1ClientVisitSegment();

        //patient class is required, but Apollo don't send it
        pv1.setPatientClass(dataTypeService.createIS(segment.getPv12_PatientClass(), HL7CodeTable0004PatientClass.class));
        pv1.setAssignedPatientLocation(dataTypeService.createPL(segment.getPv13_AssignedPatientLocation()));
        pv1.setAdmissionType(dataTypeService.createIS(segment.getPv14_AdmissionType(), HL7CodeTable0007AdmissionType.class));
        pv1.setPriorPatientLocation(dataTypeService.createPL(segment.getPv16_PriorPatientLocation()));
        pv1.setAttendingDoctor(dataTypeService.createFirstPresentXCN(segment.getPv17_AttendingDoctor()));
        pv1.setRefferingDoctor(dataTypeService.createFirstPresentXCN(segment.getPv18_ReferringDoctor()));
        pv1.setConsultingDoctor(dataTypeService.createFirstPresentXCN(segment.getPv19_ConsultingDoctor()));
        pv1.setPreadmitTestIndicator(dataTypeService.getValue(segment.getPv112_PreadmitTestIndicator()));
        pv1.setReadmissionIndicator(dataTypeService.createIS(segment.getPv113_ReAdmissionIndicator(), HL7CodeTable0092ReadmissionIndicator.class));
        pv1.setAdmitSource(dataTypeService.createIS(segment.getPv114_AdmitSource(), HL7CodeTable0023AdmitSource.class));
        pv1.setAmbulatoryStatuses(dataTypeService.createISList(segment.getPv115_AmbulatoryStatus(), HL7CodeTable0009AmbulatoryStatus.class));
        pv1.setDischargeDisposition(dataTypeService.getValue(segment.getPv136_DischargeDisposition()));
        pv1.setDischargedToLocation(dataTypeService.createDLD(segment.getPv137_DischargedToLocation()));
        pv1.setAdmitDatetime(dataTypeService.convertTS(segment.getPv144_AdmitDateTime()));
        if (segment.getPv145_DischargeDateTimeReps() > 0) {
            pv1.setDischargeDatetime(dataTypeService.convertTS(segment.getPv145_DischargeDateTime(0)));
        }
        pv1.setServicingFacility(dataTypeService.getValue(segment.getPv139_ServicingFacility()));
        return pv1;
    }
}
