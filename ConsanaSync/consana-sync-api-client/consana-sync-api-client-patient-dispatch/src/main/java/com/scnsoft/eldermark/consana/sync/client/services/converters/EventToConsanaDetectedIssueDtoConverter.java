package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.scnsoft.eldermark.consana.sync.client.model.ConsanaDetectedIssueDto;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaExtensionDto;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Event;
import com.scnsoft.eldermark.consana.sync.client.model.entities.EventType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Transactional
public class EventToConsanaDetectedIssueDtoConverter implements Converter<Event, List<ConsanaDetectedIssueDto>> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss'Z'").withZone(ZoneOffset.UTC);

    @Override
    public List<ConsanaDetectedIssueDto> convert(@NonNull Event event) {
        var detectedIssueDto = new ConsanaDetectedIssueDto();
        detectedIssueDto.setDate(DATE_TIME_FORMATTER.format(event.getEventDateTime()));
        detectedIssueDto.setDetail(convertDetail(event));
        detectedIssueDto.setExtension(convertConsanaExtensions(event));
        return Collections.singletonList(detectedIssueDto);
    }

    private String convertDetail(Event event) {
        var assessment = Optional.ofNullable(event.getAssessment()).filter(StringUtils::isNotEmpty).map(a -> "Assessment: " + a);
        var situation = Optional.ofNullable(event.getSituation()).filter(StringUtils::isNotEmpty).map(s -> "Situation: " + s);
        var background = Optional.ofNullable(event.getBackground()).filter(StringUtils::isNotEmpty).map(b -> "Background: " + b);
        return concatOptionalStings(assessment, situation, background);
    }

    private String concatOptionalStings(Optional... optionals) {
        return Arrays.stream(optionals)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Object::toString)
                .collect(Collectors.joining("\\n"));
    }

    private List<ConsanaExtensionDto> convertConsanaExtensions(Event event) {
        var extensions = Stream.of(
                Optional.ofNullable(event.getEventType()).map(EventType::getCode)
                        .flatMap(v -> createConsanaExtensionDto("http://app.simplyconnect.me/fhir-extensions/event-type-code", v)),

                Optional.ofNullable(event.getEventType()).map(EventType::getDescription)
                        .flatMap(v -> createConsanaExtensionDto("http://app.simplyconnect.me/fhir-extensions/event-type-display", v)),

                createConsanaExtensionDto("http://app.simplyconnect.me/fhir-extensions/event-location", event.getLocation()),

                createConsanaExtensionDto("http://app.simplyconnect.me/fhir-extensions/event-background", event.getBackground()),

                createConsanaExtensionDto("http://app.simplyconnect.me/fhir-extensions/emergency-room-visit", event.isErVisit()),

                createConsanaExtensionDto("http://app.simplyconnect.me/fhir-extensions/event-is-injury", event.isInjury()),

                createConsanaExtensionDto("http://app.simplyconnect.me/fhir-extensions/event-follow-up", event.getFollowup()),

                createConsanaExtensionDto("http://app.simplyconnect.me/fhir-extensions/event-overnight-in", event.isOvernightIn()),

                createConsanaExtensionDto("http://app.simplyconnect.me/fhir-extensions/event-situation", event.getSituation()),

                createConsanaExtensionDto("http://app.simplyconnect.me/fhir-extensions/event-assessment", event.getAssessment())
        )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return extensions;
    }

    private Optional<ConsanaExtensionDto> createConsanaExtensionDto(String url, String value) {
        if (StringUtils.isEmpty(value)) {
            return Optional.empty();
        }
        var extension = new ConsanaExtensionDto();
        extension.setUrl(url);
        extension.setValueString(value);
        return Optional.of(extension);
    }

    private Optional<ConsanaExtensionDto> createConsanaExtensionDto(String url, boolean value) {
        var extension = new ConsanaExtensionDto();
        extension.setUrl(url);
        extension.setValueBoolean(value);
        return Optional.of(extension);
    }
}
