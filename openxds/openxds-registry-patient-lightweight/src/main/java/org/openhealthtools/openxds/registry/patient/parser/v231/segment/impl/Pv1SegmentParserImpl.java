package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.model.v231.segment.PV1;
import org.openhealthtools.openxds.entity.hl7table.*;
import org.openhealthtools.openxds.entity.segment.PV1PatientVisitSegment;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.openhealthtools.openxds.registry.patient.parser.datatype.EmptyHL7Field231Service;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.Pv1SegmentParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Pv1SegmentParserImpl extends AbstractAdtSegmentParser<PV1PatientVisitSegment, PV1>
        implements Pv1SegmentParser {

    @Autowired
    private DataTypeService dataTypeService;

    @Autowired
    private EmptyHL7Field231Service emptyHL7Field231Service;

    @Override
    public PV1PatientVisitSegment doParse(PV1 segment) {
        final PV1PatientVisitSegment pv1 = new PV1PatientVisitSegment();

        pv1.setPatientClass(dataTypeService.createIS(segment.getPatientClass(), HL7CodeTable0004PatientClass.class));
        pv1.setAssignedPatientLocation(dataTypeService.createPL(segment.getAssignedPatientLocation()));
        pv1.setAdmissionType(dataTypeService.createIS(segment.getAdmissionType(), HL7CodeTable0007AdmissionType.class));
        pv1.setPriorPatientLocation(dataTypeService.createPL(segment.getPriorPatientLocation()));
        pv1.setAttendingDoctor(dataTypeService.createXCN(segment.getAttendingDoctor()));
        pv1.setRefferingDoctor(dataTypeService.createXCN(segment.getReferringDoctor()));
        pv1.setConsultingDoctor(dataTypeService.createXCN(segment.getConsultingDoctor()));
        pv1.setPreadmitTestIndicator(dataTypeService.getValue(segment.getPreadmitTestIndicator()));
        pv1.setReadmissionIndicator(dataTypeService.createIS(segment.getReAdmissionIndicator(), HL7CodeTable0092ReadmissionIndicator.class));
        pv1.setAdmitSource(dataTypeService.createIS(segment.getAdmitSource(), HL7CodeTable0023AdmitSource.class));
        pv1.setAmbulatoryStatuses(dataTypeService.createISList(segment.getAmbulatoryStatus(), HL7CodeTable0009AmbulatoryStatus.class));

        pv1.setDischargeDisposition(dataTypeService.getValue(segment.getDischargeDisposition()));
        pv1.setDischargedToLocation(dataTypeService.createDLD(segment.getDischargedToLocation()));
        pv1.setAdmitDatetime(dataTypeService.convertTsToDate(segment.getAdmitDateTime()));
        pv1.setDischargeDatetime(dataTypeService.convertTsToDate(segment.getDischargeDateTime()));
        pv1.setServicingFacility(dataTypeService.getValue(segment.getServicingFacility()));
        return pv1;
    }

    @Override
    public boolean isHl7SegmentEmpty(final PV1 hl7Segment) {
        return hl7Segment == null || emptyHL7Field231Service.isAbstractPrimitiveEmpty(hl7Segment.getPatientClass());
    }
}
