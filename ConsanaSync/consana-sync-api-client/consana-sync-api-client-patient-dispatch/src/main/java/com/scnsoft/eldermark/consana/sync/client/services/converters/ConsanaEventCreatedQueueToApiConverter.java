package com.scnsoft.eldermark.consana.sync.client.services.converters;


import com.scnsoft.eldermark.consana.sync.client.model.*;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Event;
import com.scnsoft.eldermark.consana.sync.client.model.entities.EventAuthor;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import org.hl7.fhir.instance.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class ConsanaEventCreatedQueueToApiConverter implements Converter<Pair<Event, Pair<Resident, Patient>>, ConsanaEventCreatedApiDto> {

    private final Converter<Resident, ConsanaOrganizationDto> consanaOrganizationDtoConverter;
    private final Converter<Pair<Resident, Patient>, ConsanaPatientDto> consanaPatientDtoConverter;
    private final Converter<EventAuthor, List<ConsanaPractitionerDto>> consanaPractitionerDtoConverter;
    private final Converter<Event, List<ConsanaDetectedIssueDto>> consanaDetectedIssueDtoConverter;

    @Autowired
    public ConsanaEventCreatedQueueToApiConverter(Converter<Resident, ConsanaOrganizationDto> consanaOrganizationDtoConverter, Converter<Pair<Resident, Patient>, ConsanaPatientDto> consanaPatientDtoConverter, Converter<EventAuthor, List<ConsanaPractitionerDto>> consanaPractitionerDtoConverter, Converter<Event, List<ConsanaDetectedIssueDto>> consanaDetectedIssueDtoConverter) {
        this.consanaOrganizationDtoConverter = consanaOrganizationDtoConverter;
        this.consanaPatientDtoConverter = consanaPatientDtoConverter;
        this.consanaPractitionerDtoConverter = consanaPractitionerDtoConverter;
        this.consanaDetectedIssueDtoConverter = consanaDetectedIssueDtoConverter;
    }

    @Override
    public ConsanaEventCreatedApiDto convert(@NonNull Pair<Event, Pair<Resident, Patient>> source) {
        var event = source.getFirst();
        var resident = source.getSecond().getFirst();

        var consanaEntry = new ConsanaEntryDto();
        var consanaResourceDto = new ConsanaResourceDto();
        consanaResourceDto.setOrganization(consanaOrganizationDtoConverter.convert(resident));
        consanaResourceDto.setPatient(consanaPatientDtoConverter.convert(source.getSecond()));

        consanaResourceDto.setPractitioner(Optional.ofNullable(event.getEventAuthor()).map(consanaPractitionerDtoConverter::convert).orElse(null));
        consanaResourceDto.setDetectedIssue(consanaDetectedIssueDtoConverter.convert(event));

        consanaEntry.setResource(consanaResourceDto);
        var target = new ConsanaEventCreatedApiDto();
        target.setEntry(Collections.singletonList(consanaEntry));
        return target;
    }
}
