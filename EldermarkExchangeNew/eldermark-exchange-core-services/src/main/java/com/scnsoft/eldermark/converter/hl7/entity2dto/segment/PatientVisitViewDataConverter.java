package com.scnsoft.eldermark.converter.hl7.entity2dto.segment;

import com.scnsoft.eldermark.converter.hl7.entity2dto.datatype.CodedValueForHL7TableConverter;
import com.scnsoft.eldermark.converter.hl7.entity2dto.datatype.DischargeLocationDtoConverter;
import com.scnsoft.eldermark.converter.hl7.entity2dto.datatype.PatientLocationDtoConverter;
import com.scnsoft.eldermark.dto.adt.datatype.XCNDto;
import com.scnsoft.eldermark.dto.event.PatientVisitViewData;
import com.scnsoft.eldermark.entity.xds.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import com.scnsoft.eldermark.entity.xds.segment.PV1ClientVisitSegment;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public abstract class PatientVisitViewDataConverter<PV extends PatientVisitViewData> implements Converter<PV1ClientVisitSegment, PV> {

    @Autowired
    private CodedValueForHL7TableConverter isCodedValueConverter;

    @Autowired
    private DischargeLocationDtoConverter dldConverter;

    @Autowired
    private PatientLocationDtoConverter patientLocationDtoConverter;

    @Autowired
    private Converter<XCNExtendedCompositeIdNumberAndNameForPersons, XCNDto> xcnConverter;

    @Override
    public PV convert(PV1ClientVisitSegment pv1) {
        var visit = create();

        visit.setPatientClass(isCodedValueConverter.convert(pv1.getPatientClass()));
        visit.setAssignedPatientLocation(patientLocationDtoConverter.convert(pv1.getAssignedPatientLocation()));
        visit.setAdmissionType(isCodedValueConverter.convert(pv1.getAdmissionType()));
        visit.setPriorPatientLocation(patientLocationDtoConverter.convert(pv1.getPriorPatientLocation()));

        if (pv1.getAttendingDoctor() != null) {
            visit.setAttendingDoctors(Collections.singletonList(xcnConverter.convert(pv1.getAttendingDoctor())));
        }

        if (pv1.getRefferingDoctor() != null) {
            visit.setReferringDoctors(Collections.singletonList(xcnConverter.convert(pv1.getRefferingDoctor())));
        }

        if (pv1.getConsultingDoctor() != null) {
            visit.setConsultingDoctors(Collections.singletonList(xcnConverter.convert(pv1.getConsultingDoctor())));
        }
        visit.setPreadmitTestIndicator(pv1.getPreadmitTestIndicator());
        visit.setReadmissionIndicator(isCodedValueConverter.convert(pv1.getReadmissionIndicator()));
        visit.setAdmitSource(isCodedValueConverter.convert(pv1.getAdmitSource()));

        if (CollectionUtils.isNotEmpty(pv1.getAmbulatoryStatuses())) {
            var statuses = pv1.getAmbulatoryStatuses().stream()
                    .map(isCodedValueConverter::convert)
                    .collect(Collectors.toList());
            visit.setAmbulatoryStatuses(statuses);
        }

        if (CollectionUtils.isNotEmpty(pv1.getAdmittingDoctors())) {
            visit.setAdmittingDoctors(pv1.getAdmittingDoctors().stream()
                    .map(xcnConverter::convert).collect(Collectors.toList()));
        }

        visit.setDischargeDisposition(pv1.getDischargeDisposition());
        visit.setDischargedToLocation(dldConverter.convert(pv1.getDischargedToLocation()));

        visit.setServicingFacility(pv1.getServicingFacility());

        visit.setAdmitDate(DateTimeUtils.toEpochMilli(pv1.getAdmitDatetime()));
        visit.setDischargeDate(DateTimeUtils.toEpochMilli(pv1.getDischargeDatetime()));

        if (CollectionUtils.isNotEmpty(pv1.getOtherHealthcareProviders())) {
            visit.setOtherHealthcareProviders(pv1.getOtherHealthcareProviders().stream()
                    .map(xcnConverter::convert).collect(Collectors.toList()));
        }

        return visit;
    }

    protected abstract PV create();
}
