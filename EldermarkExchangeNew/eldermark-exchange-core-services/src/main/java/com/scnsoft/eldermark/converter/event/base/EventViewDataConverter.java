package com.scnsoft.eldermark.converter.event.base;

import com.scnsoft.eldermark.converter.hl7.entity2dto.segment.*;
import com.scnsoft.eldermark.dao.AdtMessageDao;
import com.scnsoft.eldermark.dto.adt.segment.*;
import com.scnsoft.eldermark.dto.event.EventViewData;
import com.scnsoft.eldermark.dto.event.PatientVisitViewData;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.xds.message.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class EventViewDataConverter<E extends EventViewData> implements Converter<Event, E> {

    @Autowired
    private AdtMessageDao adtMessageDao; //todo add adt message as entity to event?

    @Autowired
    private InsuranceDtoConverter insuranceDtoConverter;

    @Autowired
    private GuarantorDtoConverter guarantorDtoConverter;

    @Autowired
    private ProcedureDtoConverter procedureDtoConverter;

    @Autowired
    private DiagnosisDtoConverter diagnosisDtoConverter;

    @Autowired
    private AdtAllergyDtoConverter allergyDtoConverter;

    @Override
    @SuppressWarnings("unchecked")
    public E convert(Event event) {
        var dto = create();
        fill(event, dto);
        return dto;
    }

    protected abstract E create();

    protected void fill(Event event, E dto) {
        dto.setClient(getClientSummaryViewDataConverter().convert(event));
        dto.setEssentials(getEventEssentialsViewDataConverter().convert(event));
        dto.setDescription(getEventDescriptionViewDataConverter().convert(event));
        dto.setTreatment(getTreatmentViewDataConverter().convert(event));
        dto.setResponsibleManager(getResponsibleManagerConverter().convert(event));
        dto.setRegisteredNurse(getRegisteredNurseConverter().convert(event));

        if (event.getAdtMsgId() != null) {
            var adt = adtMessageDao.findById(event.getAdtMsgId()).orElseThrow();

            dto.setPatientVisit(convertPatientVisit(adt));
            dto.setInsurances(convertInsurances(adt));
            dto.setGuarantors(convertGuarantors(adt));
            dto.setProcedures(convertProcedures(adt));
            dto.setDiagnoses(convertDiagnosis(adt));
            dto.setAllergies(convertAllergies(adt));
        }
    }


    protected abstract ClientSummaryViewDataConverter getClientSummaryViewDataConverter();

    protected abstract EventEssentialsViewDataConverter getEventEssentialsViewDataConverter();

    protected abstract EventDescriptionViewDataConverter getEventDescriptionViewDataConverter();

    protected abstract TreatmentViewDataConverter getTreatmentViewDataConverter();

    protected abstract ResponsibleManagerConverter getResponsibleManagerConverter();

    protected abstract RegisteredNurseConverter getRegisteredNurseConverter();

    protected abstract PatientVisitViewDataConverter getPatientVisitViewDataConverter();

    protected PatientVisitViewData convertPatientVisit(AdtMessage adt) {
        if (!(adt instanceof PV1SegmentContainingMessage) || ((PV1SegmentContainingMessage) adt).getPv1() == null) {
            return null;
        }

        return getPatientVisitViewDataConverter().convert(((PV1SegmentContainingMessage) adt).getPv1());
    }

    protected List<AdtInsuranceDto> convertInsurances(AdtMessage adt) {
        if ((adt instanceof IN1ListSegmentContainingMessage) && CollectionUtils.isNotEmpty(((IN1ListSegmentContainingMessage) adt).getIn1List())) {
            var insurances = ((IN1ListSegmentContainingMessage) adt).getIn1List();

            return insurances
                    .stream()
                    .map(insuranceDtoConverter::convert)
                    .collect(Collectors.toList());
        }
        return null;
    }


    protected List<AdtGuarantorDto> convertGuarantors(AdtMessage adt) {
        if (!(adt instanceof GT1ListSegmentContainingMessage)) {
            return null;
        }
        var gt1 = ((GT1ListSegmentContainingMessage) adt).getGt1List();
        return guarantorDtoConverter.convertList(gt1);
    }

    protected List<AdtProcedureDto> convertProcedures(AdtMessage adt) {
        if (!(adt instanceof PR1ListSegmentContaingMessage)) {
            return null;
        }
        var pr1 = ((PR1ListSegmentContaingMessage) adt).getPr1List();
        return procedureDtoConverter.convertList(pr1);
    }

    protected List<AdtDiagnosisDto> convertDiagnosis(AdtMessage adt) {
        if (!(adt instanceof DG1ListSegmentContainingMessage)) {
            return null;
        }
        var dg1List = ((DG1ListSegmentContainingMessage) adt).getDg1List();
        return diagnosisDtoConverter.convertList(dg1List);
    }

    protected List<AdtAllergyDto> convertAllergies(AdtMessage adt) {
        if (!(adt instanceof AL1ListSegmentContainingMessage)) {
            return null;
        }
        var al1List = ((AL1ListSegmentContainingMessage) adt).getAL1List();
        return allergyDtoConverter.convertList(al1List);
    }
}
