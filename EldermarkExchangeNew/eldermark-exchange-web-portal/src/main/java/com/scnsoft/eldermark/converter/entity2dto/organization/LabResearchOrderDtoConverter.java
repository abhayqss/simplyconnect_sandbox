package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;
import com.scnsoft.eldermark.dto.lab.*;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderObservationResult;
import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.segment.NTENotesAndComments;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.service.security.LabSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.DocumentUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class LabResearchOrderDtoConverter implements Converter<LabResearchOrder, LabResearchOrderDto> {

    @Autowired
    private LabSecurityService labSecurityService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private Converter<XADPatientAddress, AddressDto> xadConverter;

    @Autowired
    private Converter<CodedValueForHL7Table, String> codedValueStringConverter;

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementDtoConverter;

    @Override
    public LabResearchOrderDto convert(LabResearchOrder source) {
        var target = new LabResearchOrderDto();
        target.setId(source.getId());
        target.setRequisitionNumber(source.getRequisitionNumber());
        target.setStatusName(source.getStatus().name());
        target.setStatusTitle(source.getStatus().getDisplayName());
        target.setReason(source.getReason().getValue());
        target.setClinic(source.getClinic());
        target.setClinicAddress(source.getClinicAddress());
        target.setOrderDate(DateTimeUtils.toEpochMilli(source.getOrderDate()));
        target.setCreatedByName(source.getCreatedBy().getFullName());
        target.setNotes(source.getNotes());
        target.setProviderFirstName(source.getProviderFirstName());
        target.setProviderLastName(source.getProviderLastName());
        target.setProviderFullName(source.getProviderFirstName() + " " + source.getProviderLastName());
        target.setIcd10Codes(source.getIcd10Codes());
        target.setClient(fillClientInfo(source));
        target.setSpecimen(fillSpecimenInfo(source));
        if (labSecurityService.canViewResults(source.getId())) {
            target.setResult(fillResultInfo(source));
        }
        target.setCreatedDate(source.getCreatedDate().toEpochMilli());

        target.setCanReview(labSecurityService.canReview(source.getId()));
        return target;
    }

    private LabResearchResultDto fillResultInfo(LabResearchOrder source) {
        var result = new LabResearchResultDto();

        if (CollectionUtils.isNotEmpty(source.getDocuments())) {
            result.setDocuments(source.getDocuments().stream()
                    .map(document -> new LabResearchResultDocumentDto(document.getId(), document.getDocumentTitle(), DocumentUtils.resolveMimeType(document)))
                    .collect(Collectors.toList()));
        }
        result.setStatusName(source.getStatus().name());
        result.setStatusTitle(source.getStatus().getDisplayName());

        var observationResult = source.getObservationResults();

        result.setSource(
                observationResult.stream()
                        .map(LabResearchOrderObservationResult::getObservationSource)
                        .distinct()
                        .collect(Collectors.joining(", "))
        );

        result.setDates(
                observationResult.stream()
                        .map(LabResearchOrderObservationResult::getDatetimeOfObservation)
                        .filter(Objects::nonNull)
                        .map(Instant::toEpochMilli)
                        .distinct()
                        .collect(Collectors.toList())
        );

        result.setPerformerName(
                observationResult.stream()
                        .map(LabResearchOrderObservationResult::getPerformingOrgName)
                        .filter(StringUtils::isNotEmpty)
                        .distinct()
                        .collect(Collectors.joining(", "))
        );

        result.setPerformerAddress(
                observationResult.stream()
                        .map(LabResearchOrderObservationResult::getPerformingOrgAddr)
                        .map(xadConverter::convert)
                        .filter(Objects::nonNull)
                        .map(AddressDto::getDisplayAddress)
                        .filter(StringUtils::isNotEmpty)
                        .distinct()
                        .collect(Collectors.joining("; "))
        );


        result.setMedicalDirector(
                observationResult.stream()
                        .map(LabResearchOrderObservationResult::getPerformingOrgMedicalDirector)
                        .filter(Objects::nonNull)
                        .map(XCNExtendedCompositeIdNumberAndNameForPersons::getIdNumber)
                        .filter(StringUtils::isNotEmpty)
                        .distinct()
                        .collect(Collectors.joining(", "))
        );

        var oru = source.getOrderORU().getOru();

        result.setCommentSource(
                CollectionUtils.emptyIfNull(oru.getNteList()).stream()
                        .map(NTENotesAndComments::getSourceOfComment)
                        .map(codedValueStringConverter::convert)
                        .filter(StringUtils::isNotEmpty)
                        .distinct()
                        .collect(Collectors.joining(", "))
        );

        result.setComments(
                CollectionUtils.emptyIfNull(oru.getNteList()).stream()
                        .map(NTENotesAndComments::getComments)
                        .flatMap(List::stream)
                        .filter(StringUtils::isNotEmpty)
                        .distinct()
                        .collect(Collectors.toList())
        );

        if (oru.getSpm() != null) {
            result.setSpecimenType(
                    ceCodedElementDtoConverter.convert(oru.getSpm().getSpecimenType())
            );

            result.setSpecimenDate(
                    Optional.ofNullable(oru.getSpm().getSpecimenCollectionDatetime())
                            .map(DRDateRange::getRangeStartDatetime)
                            .map(DateTimeUtils::toEpochMilli)
                            .orElse(null)
            );

            result.setSpecimenReceivedDate(DateTimeUtils.toEpochMilli(oru.getSpm().getSpecimenReceivedDatetime()));
        }

        return result;
    }

    private ClientSummaryLabsAdaptDto fillClientInfo(LabResearchOrder source) {
        var target = new ClientSummaryLabsAdaptDto();
        Client client = source.getClient();
        target.setId(client.getId());
        target.setFullName(client.getFullName());
        target.setGenderTitle(source.getGender().getDisplayName());
        target.setGenderId(source.getGender().getId());
        if (source.getRace() != null) {
            target.setRaceTitle(source.getRace().getDisplayName());
            target.setRaceId(source.getRace().getId());
        }
        target.setBirthDate(DateTimeUtils.formatLocalDate(source.getBirthDate()));
        target.setSsn(client.getSsnLastFourDigits());
        target.setPhone(source.getPhone());
        target.setInsuranceNetwork(source.getInNetworkInsurance());
        target.setPolicyNumber(source.getPolicyNumber());
        target.setPolicyHolderRelationName(source.getPolicyHolder().getHL7Code());
        target.setPolicyHolderRelationTitle(source.getPolicyHolder().getDisplayName());
        target.setPolicyHolderDOB(DateTimeUtils.formatLocalDate(source.getPolicyHolderDOB()));
        target.setPolicyHolderName(source.getPolicyHolderName());
        target.setAddress(fillAddress(source));
        target.setCanView(clientSecurityService.canView(client.getId()));
        return target;

    }

    private LabOrderSpecimenDto fillSpecimenInfo(LabResearchOrder source) {
        var target = new LabOrderSpecimenDto();
        target.setTypes(source.getSpecimenTypes()
                .stream()
                .map(specimenType -> new IdentifiedNamedTitledEntityDto(specimenType.getId(), specimenType.getName(), specimenType.getTitle()))
                .collect(Collectors.toList()));
        target.setCollectorName(source.getCollectorsName());
        target.setDate(source.getSpecimenDate().toEpochMilli());
        target.setSite(source.getSite());
        return target;
    }

    private AddressDto fillAddress(LabResearchOrder source) {
        var target = new AddressDto();
        target.setCity(source.getCity());
        target.setStateAbbr(source.getState().getAbbr());
        target.setStateName(source.getState().getName());
        target.setStreet(source.getAddress());
        target.setZip(source.getZipCode());
        return target;
    }

}
