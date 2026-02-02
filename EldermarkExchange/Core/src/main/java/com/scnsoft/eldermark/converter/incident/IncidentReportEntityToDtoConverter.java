package com.scnsoft.eldermark.converter.incident;

import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.dto.IndividualDto;
import com.scnsoft.eldermark.dto.dictionary.TextDto;
import com.scnsoft.eldermark.entity.incident.IncidentReport;
import com.scnsoft.eldermark.entity.incident.IncidentReportIncidentPlaceTypeFreeText;
import com.scnsoft.eldermark.entity.incident.IncidentReportIncidentTypeFreeText;
import com.scnsoft.eldermark.entity.incident.Individual;
import com.scnsoft.eldermark.services.IncidentReportAdditionalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IncidentReportEntityToDtoConverter implements Converter<IncidentReport, IncidentReportDto> {

    @Autowired
    private IncidentReportAdditionalDataService incidentReportAdditionalDataService;

    @Override
    public IncidentReportDto convert(IncidentReport source) {
        IncidentReportDto target = new IncidentReportDto();

        target.setActiveMedications(getMedications(source));
        target.setAgencyAddress(source.getAgencyAddress());
        target.setAgencyName(source.getAgencyName());
        target.setAgencyResponseToIncident(source.getAgencyResponseToIncident());
        target.setCareManagerOrStaffEmail(source.getCareManagerOrStaffEmail());
        target.setCareManagerOrStaffPhone(source.getCareManagerOrStaffPhone());
        target.setCareManagerOrStaffWithPrimServRespAndTitle(source.getCareManagerOrStaffWithPrimServRespAndTitle());
        target.setClientBirthDate(source.getBirthDate() != null ? source.getBirthDate().getTime() : null);
        target.setClientClassMemberCurrentAddress(source.getClassMemberCurrentAddress());
        target.setClientName(source.getFirstName() + " " + source.getLastName());
        target.setClientRaceId(source.getRace().getId());
        target.setClientRIN(source.getRin());
        target.setClientTransitionToCommunityDate(
                source.getTransitionToCommunityDate() != null ? source.getTransitionToCommunityDate().getTime() : null);
        target.setCurrentDiagnoses(getCurrentDiagnoses(source));
        target.setId(source.getId());
        target.setIncidentDateTime(
                source.getIncidentDatetime() != null ? source.getIncidentDatetime().getTime() : null);
        target.setIncidentDiscoveredDate(
                source.getIncidentDiscoveredDate() != null ? source.getIncidentDiscoveredDate().getTime() : null);
        target.setIncidentPlaces(getIncidentPlaces(source));

        target.setMcoCareCoordinatorAndAgency(source.getMcoCareCoordinatorAndAgency());
        target.setMcoCareCoordinatorEmail(source.getMcoCareCoordinatorEmail());
        target.setMcoCareCoordinatorPhone(source.getMcoCareCoordinatorPhone());
        target.setQualityAdministrator(source.getQualityAdministrator());
        target.setReportAuthor(source.getReportAuthor());
        target.setReportCompletedDate(
                source.getReportCompletedDate() != null ? source.getReportCompletedDate().getTime() : null);
        target.setReportDate(source.getReportDate() != null ? source.getReportDate().getTime() : null);

        target.setIncidentInvolvedIndividuals(getIndividualList(source));

        target.setWasIncidentCausedBySubstance(source.getWasIncidentCausedBySubstance());
        target.setWasProviderPresentOrScheduled(source.getWasProviderPresentOrScheduled());

        target.setIncidentNarrative(source.getNarrative());

        List<TextDto> level1IncidentTypes = new ArrayList<>();
        List<TextDto> level2IncidentTypes = new ArrayList<>();
        List<TextDto> level3IncidentTypes = new ArrayList<>();

        filterIncidentTypes(level1IncidentTypes, level2IncidentTypes, level3IncidentTypes, source);

        target.setLevel1IncidentTypes(level1IncidentTypes);
        target.setLevel2IncidentTypes(level2IncidentTypes);
        target.setLevel3IncidentTypes(level3IncidentTypes);

        target.setClientClassMemberTypeId(source.getClassMemberType().getId());
        target.setClientGenderId(source.getGender().getId());


        return target;
    }

    private void filterIncidentTypes(List<TextDto> level1IncidentTypes, List<TextDto> level2IncidentTypes,
            List<TextDto> level3IncidentTypes, IncidentReport source) {
        for (IncidentReportIncidentTypeFreeText incidentType : source.getIncidentTypes()) {
            switch (incidentType.getIncidentType().getIncidentLevel()) {
                case 1:
                    level1IncidentTypes.add(convertIncidentType(incidentType));
                    break;
                case 2:
                    level2IncidentTypes.add(convertIncidentType(incidentType));
                    break;
                case 3:
                    level3IncidentTypes.add(convertIncidentType(incidentType));
                    break;
            }
        }
    }

    private TextDto convertIncidentType(IncidentReportIncidentTypeFreeText incidentType) {
        TextDto target = new TextDto();
        target.setId(incidentType.getIncidentType().getId());
        target.setText(incidentType.getFreeText() != null ? incidentType.getFreeText().getFreeText() : null);
        return target;
    }

    private List<IndividualDto> getIndividualList(IncidentReport source) {
        List<IndividualDto> individualList = new ArrayList<IndividualDto>();
        for (Individual individual : source.getIndividuals()) {
            individualList.add(convertIndividualEntityToDto(individual));
        }
        if (individualList.isEmpty()) {
            return null;
        }
        return individualList;
    }

    private List<TextDto> getIncidentPlaces(IncidentReport source) {
        List<TextDto> textDtoList = new ArrayList<TextDto>();
        for (IncidentReportIncidentPlaceTypeFreeText placeType : source.getIncidentPlaceTypes()) {
            TextDto freeTextTarget = new TextDto();
            freeTextTarget.setId(placeType.getIncidentPlaceType().getId());
            freeTextTarget.setText(placeType.getFreeText() != null ? placeType.getFreeText().getFreeText() : null);
            textDtoList.add(freeTextTarget);
        }
        if (textDtoList.isEmpty()) {
            return null;
        }
        return textDtoList;
    }

    private IndividualDto convertIndividualEntityToDto(Individual source) {
        IndividualDto target = new IndividualDto();
        // target.setId(source.getId());
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPhone(source.getPhone());
        target.setRelationship(source.getRelationship());
        return target;
    }

    private List<String> getMedications(IncidentReport incidentReport) {
        return incidentReportAdditionalDataService.listMedicationStrings(incidentReport);
    }

    private List<String> getCurrentDiagnoses(IncidentReport incidentReport) {
        return incidentReportAdditionalDataService.listProblemObservationStrings(incidentReport);
    }
}
