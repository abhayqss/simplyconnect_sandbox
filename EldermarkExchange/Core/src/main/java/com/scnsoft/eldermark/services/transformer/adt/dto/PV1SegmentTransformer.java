package com.scnsoft.eldermark.services.transformer.adt.dto;

import com.scnsoft.eldermark.entity.xds.datatype.DLDDischargeLocation;
import com.scnsoft.eldermark.entity.xds.datatype.PLPatientLocation;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0009AmbulatoryStatus;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.segment.PV1PatientVisitSegment;
import com.scnsoft.eldermark.shared.carecoordination.adt.PV1PatientVisitSegmentDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.DLDDischargeLocationDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.PLPatientLocationDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PV1SegmentTransformer implements Converter<PV1PatientVisitSegment, PV1PatientVisitSegmentDto> {

    @Autowired
    private Converter<PLPatientLocation, PLPatientLocationDto> plPatientLocationTransformer;

    @Autowired
    private Converter<DLDDischargeLocation, DLDDischargeLocationDto> dldDischargeLocationTransformer;

    @Autowired
    private Converter<ISCodedValueForUserDefinedTables<? extends HL7UserDefinedCodeTable>, String> isCodedValueForUserDefinedTablesStringConverter;

    @Override
    public PV1PatientVisitSegmentDto convert(PV1PatientVisitSegment pv1) {
        if (pv1 == null) {
            return null;
        }
        PV1PatientVisitSegmentDto target = new PV1PatientVisitSegmentDto();
        target.setPatientClass(isCodedValueForUserDefinedTablesStringConverter.convert(pv1.getPatientClass()));
        target.setAssignedPatientLocation(plPatientLocationTransformer.convert(pv1.getAssignedPatientLocation()));
        target.setAdmissionType(isCodedValueForUserDefinedTablesStringConverter.convert(pv1.getAdmissionType()));
        target.setPriorPatientLocation(plPatientLocationTransformer.convert(pv1.getPriorPatientLocation()));
        if (pv1.getAttendingDoctor() != null) {
            target.setAttendingDoctors(Arrays.asList(CareCoordinationUtils.getFullName(pv1.getAttendingDoctor().getFirstName(),
                    pv1.getAttendingDoctor().getLastName())));
        }
        if (pv1.getRefferingDoctor() != null) {
            target.setReferringDoctors(Arrays.asList(CareCoordinationUtils.getFullName(pv1.getRefferingDoctor().getFirstName(),
                    pv1.getRefferingDoctor().getLastName())));
        }
        if (pv1.getConsultingDoctor() != null) {
            target.setConsultingDoctors(Arrays.asList(CareCoordinationUtils.getFullName(pv1.getConsultingDoctor().getFirstName(),
                    pv1.getConsultingDoctor().getLastName())));
        }
        target.setPreadmitTestIndicator(pv1.getPreadmitTestIndicator());
        target.setReadmissionIndicator(isCodedValueForUserDefinedTablesStringConverter.convert(pv1.getReadmissionIndicator()));
        target.setAdmitSource(isCodedValueForUserDefinedTablesStringConverter.convert(pv1.getAdmitSource()));

        if (CollectionUtils.isNotEmpty(pv1.getAmbulatoryStatuses())) {
            final List<String> abmulatoryStatuses = new ArrayList<>(pv1.getAmbulatoryStatuses().size());
            for (ISCodedValueForUserDefinedTables<HL7CodeTable0009AmbulatoryStatus> status: pv1.getAmbulatoryStatuses()) {
                abmulatoryStatuses.add(isCodedValueForUserDefinedTablesStringConverter.convert(status));
            }
            target.setAmbulatoryStatuses(abmulatoryStatuses);
        }

        target.setDischargeDisposition(pv1.getDischargeDisposition());
        if (pv1.getDischargedToLocation() != null) {
            target.setDischargedToLocation(dldDischargeLocationTransformer.convert(pv1.getDischargedToLocation()));
        }
        target.setAdmitDatetime(pv1.getAdmitDatetime());
        target.setDischargeDatetime(pv1.getDischargeDatetime());
        target.setServicingFacility(pv1.getServicingFacility());
        return target;
    }
}
