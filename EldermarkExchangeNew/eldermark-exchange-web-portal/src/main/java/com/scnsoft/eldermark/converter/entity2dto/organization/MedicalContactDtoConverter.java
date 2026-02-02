package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.reports.model.ComprehensiveAssessment;
import com.scnsoft.eldermark.dto.FullNameDto;
import com.scnsoft.eldermark.dto.client.MedicalContactDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.document.ccd.DocumentationOf;
import com.scnsoft.eldermark.entity.document.facesheet.ContactWithRole;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MedicalContactDtoConverter {

    public List<MedicalContactDto> convert(Client client, ComprehensiveAssessment assessment, List<ContactWithRole> contactWithRoles) {
        var medicalContactDtos = new HashMap<FullNameDto, MedicalContactDto>();
        if (StringUtils.isNotEmpty(client.getPrimaryCarePhysicianFirstName()) || StringUtils.isNotEmpty(client.getPrimaryCarePhysicianLastName())) {
            var primaryPhysician = new MedicalContactDto();
            primaryPhysician.setRole("Primary care physician");
            primaryPhysician.setData(CareCoordinationUtils.concat(" ", client.getPrimaryCarePhysicianFirstName(), client.getPrimaryCarePhysicianLastName()));
            var fullName = new FullNameDto(client.getPrimaryCarePhysicianFirstName(), client.getPrimaryCarePhysicianLastName(), null);
            putIfPresent(medicalContactDtos, fullName, primaryPhysician);
        }

        if (assessment != null) {
            addPrimaryCarePhysician(assessment, medicalContactDtos);
            addSpecialityPhysician(assessment, medicalContactDtos);
        }

        for (var source : contactWithRoles) {
            if (CollectionUtils.isNotEmpty(source.getPersons())) {
                for (Person person : source.getPersons()) {
                    if (person != null && CollectionUtils.isNotEmpty(person.getNames())) {
                        var nameData = person.getNames().get(0);
                        var name = CareCoordinationUtils.concat(" ", nameData.getGiven(), nameData.getMiddle(), nameData.getFamily());
                        if (StringUtils.isEmpty(name)) {
                            continue;
                        }
                        var fullName = new FullNameDto(nameData.getGiven(), nameData.getFamily(), nameData.getMiddle());
                        MedicalContactDto dto = new MedicalContactDto();
                        if (Strings.isNotBlank(source.getRole())) {
                            dto.setRole(source.getRole());
                        } else if (source instanceof DocumentationOf) {
                            dto.setRole("Clinician");
                        }
                        String fullAddress = null;
                        if (CollectionUtils.isNotEmpty(person.getAddresses())) {
                            var address = person.getAddresses().get(0);
                            fullAddress = CareCoordinationUtils
                                    .concat(" ", address.getStreetAddress(), address.getCity(), address.getState(), address.getPostalCode());
                        }
                        String phones = CareCoordinationUtils.concat(", ", person.getTelecoms().stream()
                                .filter(t -> Strings.isNotBlank(t.getValue()))
                                .map(t -> CareCoordinationUtils.concat(": ", t.getUseCode(), t.getValue()))
                                .collect(Collectors.toList()));
                        dto.setData(CareCoordinationUtils.concat(", ", name, phones, fullAddress));
                        putIfPresent(medicalContactDtos, fullName, dto);
                    }
                }
            }
        }
        return medicalContactDtos.values().stream()
                .sorted(Comparator.comparing(MedicalContactDto::getRole).thenComparing(MedicalContactDto::getData))
                .collect(Collectors.toList());
    }

    private void addPrimaryCarePhysician(ComprehensiveAssessment assessment, Map<FullNameDto, MedicalContactDto> medicalContactDtos) {
        var fullName = new FullNameDto(assessment.getPrimaryCarePhysicianFirstName(), assessment.getPrimaryCarePhysicianLastName(), null);
        var data = CareCoordinationUtils.concat(", ",
                CareCoordinationUtils.concat(" ", assessment.getPrimaryCarePhysicianFirstName(), assessment.getPrimaryCarePhysicianLastName()),
                assessment.getPrimaryCarePhysicianPhoneNumber(),
                CareCoordinationUtils.concat(" ",
                        assessment.getPrimaryCarePhysicianAddressStreet(),
                        assessment.getPrimaryCarePhysicianAddressCity(),
                        assessment.getPrimaryCarePhysicianAddressState(),
                        assessment.getPrimaryCarePhysicianAddressZipCode()));
        var primaryPhysician = new MedicalContactDto();
        primaryPhysician.setRole("Primary care physician");
        primaryPhysician.setData(data);
        putIfPresent(medicalContactDtos, fullName, primaryPhysician);
    }

    private void addSpecialityPhysician(ComprehensiveAssessment assessment, Map<FullNameDto, MedicalContactDto> medicalContactDtos) {
        var fullName = new FullNameDto(assessment.getSpecialtyPhysicianFirstName(), assessment.getSpecialtyPhysicianLastName(), null);
        var data = CareCoordinationUtils.concat(", ",
                CareCoordinationUtils.concat(" ", assessment.getSpecialtyPhysicianFirstName(), assessment.getSpecialtyPhysicianLastName()),
                assessment.getSpecialtyPhysicianSpecialty(),
                assessment.getSpecialtyPhysicianPhoneNumber(),
                CareCoordinationUtils.concat(" ",
                        assessment.getSpecialtyPhysicianAddressStreet(),
                        assessment.getSpecialtyPhysicianAddressCity(),
                        assessment.getSpecialtyPhysicianAddressState(),
                        assessment.getSpecialtyPhysicianAddressZipCode()));
        var primaryPhysician = new MedicalContactDto();
        primaryPhysician.setRole("Specialty physician");
        primaryPhysician.setData(data);
        putIfPresent(medicalContactDtos, fullName, primaryPhysician);
    }

    void putIfPresent(Map<FullNameDto, MedicalContactDto> map, FullNameDto key, MedicalContactDto dto) {
        if (StringUtils.isNotBlank(dto.getData())) {
            map.put(key, dto);
        }
    }
}
