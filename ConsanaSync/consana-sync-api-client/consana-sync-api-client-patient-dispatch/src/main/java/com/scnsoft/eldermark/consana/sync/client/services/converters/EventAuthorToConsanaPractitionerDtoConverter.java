package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.scnsoft.eldermark.consana.sync.client.model.*;
import com.scnsoft.eldermark.consana.sync.client.model.entities.EventAuthor;
import com.scnsoft.eldermark.consana.sync.client.utils.ConsanaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Transactional
public class EventAuthorToConsanaPractitionerDtoConverter implements Converter<EventAuthor, List<ConsanaPractitionerDto>> {

    private final Converter<String, List<ConsanaIdentifierDto>> consanaIdentifierDtoConverter;
    private final Map<String, String> roles = Map.of("doctor", "Doctor",
            "nurse", "Nurse",
            "pharmacist", "Pharmacist",
            "researcher", "Researcher",
            "teacher", "Teacher/educator",
            "ict", "ICT professional");


    @Autowired
    public EventAuthorToConsanaPractitionerDtoConverter(Converter<String, List<ConsanaIdentifierDto>> consanaIdentifierDtoConverter) {
        this.consanaIdentifierDtoConverter = consanaIdentifierDtoConverter;
    }

    @Override
    public List<ConsanaPractitionerDto> convert(@NonNull EventAuthor eventAuthor) {
        var practitionerDto = new ConsanaPractitionerDto();
        practitionerDto.setIdentifier(consanaIdentifierDtoConverter.convert(eventAuthor.getId().toString()));
        practitionerDto.setName(convertEventAuthorName(eventAuthor));

        var role = convertPractitionerRole(eventAuthor.getRole());
        practitionerDto.setPractitionerRole(role.map(Collections::singletonList).orElse(Collections.emptyList()));
        return Collections.singletonList(practitionerDto);
    }

    private ConsanaHumanNameDto convertEventAuthorName(EventAuthor author) {
        return new ConsanaHumanNameDto(null, ConsanaUtils.getFullName(author.getFirstName(), author.getLastName()),
                Collections.singletonList(author.getLastName()),
                Collections.singletonList(author.getFirstName()));
    }

    private Optional<ConsanaPractitionerRoleDto> convertPractitionerRole(String role) {
        var code = role.toLowerCase();
        var display = roles.getOrDefault(code, null);
        if (display == null) {
            return Optional.empty();
        }
        var coding = new ConsanaCodingDto(null, code, display);
        var codeableConcept = new ConsanaCodeableConceptDto();
        codeableConcept.setCoding(Collections.singletonList(coding));
        return Optional.of(new ConsanaPractitionerRoleDto(codeableConcept));
    }
}
