package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.scnsoft.eldermark.consana.sync.client.consana.ConsanaGateway;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaAddressDto;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaContactPointDto;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaIdentifierDto;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaOrganizationDto;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.model.entities.SourceDatabaseAddressAndContacts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class ResidentToConsanaOrganizationDtoConverter implements Converter<Resident, ConsanaOrganizationDto> {

    private final ConsanaGateway consanaGateway;

    @Autowired
    public ResidentToConsanaOrganizationDtoConverter(ConsanaGateway consanaGateway) {
        this.consanaGateway = consanaGateway;
    }

    @Override
    public ConsanaOrganizationDto convert(@NonNull Resident resident) {
        var database = resident.getDatabase();
        var organizationDto = new ConsanaOrganizationDto();
        var xclOrganizationId = consanaGateway.getXCLOrganizationId(database.getConsanaXOwningId());
        var identifiers = Collections.singletonList(
                new ConsanaIdentifierDto(
                        "http://xchangelabs.com/fhir/organization-id",
                        xclOrganizationId
                )
        );
        organizationDto.setIdentifier(identifiers);
        organizationDto.setActive(true);
        organizationDto.setName(database.getName());
        Optional.ofNullable(database.getAddressAndContacts())
                .ifPresent(c -> {
                    organizationDto.setTelecom(convertContacts(c));
                    organizationDto.setAddress(convertAddresses(c));
                });
        return organizationDto;
    }

    private List<ConsanaContactPointDto> convertContacts(SourceDatabaseAddressAndContacts contacts) {
        var dtos = new ArrayList<ConsanaContactPointDto>();
        if (StringUtils.isNotEmpty(contacts.getPhone())) {
            dtos.add(new ConsanaContactPointDto("phone", contacts.getPhone()));
        }
        if (StringUtils.isNotEmpty(contacts.getEmail())) {
            dtos.add(new ConsanaContactPointDto("email", contacts.getEmail()));
        }
        return dtos;
    }

    private List<ConsanaAddressDto> convertAddresses(SourceDatabaseAddressAndContacts contacts) {
        return Collections.singletonList(
                new ConsanaAddressDto(null,
                        StringUtils.isEmpty(contacts.getStreetAddress()) ? Collections.emptyList() :
                                Collections.singletonList(contacts.getStreetAddress()),
                        contacts.getCity(),
                        contacts.getState() != null ? contacts.getState().getAbbr() : null,
                        contacts.getPostalCode(),
                        "USA"));
    }
}
