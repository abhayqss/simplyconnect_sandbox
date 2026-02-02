package com.scnsoft.eldermark.mobile.converters.medication;

import com.scnsoft.eldermark.dto.medication.SaveMedicationRequest;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.MedicationDto;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SaveMedicationRequestConverter implements Converter<MedicationDto, SaveMedicationRequest> {

    @Autowired
    private ClientService clientService;

    @Override
    public SaveMedicationRequest convert(MedicationDto source) {

        var request = new SaveMedicationRequest();

        request.setId(source.getId());
        request.setClient(clientService.findById(source.getClientId()));
        request.setStartedDate(DateTimeUtils.toInstant(source.getStartedDate()));
        request.setStoppedDate(DateTimeUtils.toInstant(source.getStoppedDate()));
        request.setMediSpanId(source.getMediSpanId());
        request.setNdcCode(source.getNdcCode());
        request.setPrescribedBy(new SaveMedicationRequest.PrescribedBy());
        request.getPrescribedBy().setFirstName(source.getPrescribedBy().getFirstName());
        request.getPrescribedBy().setLastName(source.getPrescribedBy().getLastName());
        request.setPrescriptionQuantity(source.getPrescriptionQuantity());
        request.setPrescribedDate(DateTimeUtils.toInstant(source.getPrescribedDate()));
        request.setPrescriptionExpirationDate(DateTimeUtils.toInstant(source.getPrescriptionExpirationDate()));
        request.setFrequency(source.getFrequency());
        request.setDirections(source.getDirections());
        request.setIndicatedFor(source.getIndicatedFor());
        request.setStatus(source.getStatus());
        request.setComment(source.getComment());
        request.setDosageQuantity(source.getDosageQuantity());

        return request;
    }
}
