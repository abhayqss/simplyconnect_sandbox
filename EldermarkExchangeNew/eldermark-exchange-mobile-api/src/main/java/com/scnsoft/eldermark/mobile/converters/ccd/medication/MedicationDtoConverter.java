package com.scnsoft.eldermark.mobile.converters.ccd.medication;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.entity.medication.MedicationReport;
import com.scnsoft.eldermark.mobile.dto.ccd.CcdDtoConverterUtils;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.MedicationDto;
import com.scnsoft.eldermark.service.medispan.MedicationSearchService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.NdcUtils;
import com.scnsoft.eldermark.util.cda.EldermarkMedicationRecurrence;
import com.scnsoft.eldermark.util.cda.EldermarkRecurrenceParseException;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class MedicationDtoConverter implements Converter<ClientMedication, MedicationDto>,
        BaseMedicationDtoConverter {
    private static final DateTimeFormatter LAST_UPDATED_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("America/New_York"));
    private static final String TAKE_AS_NEEDED_FREQUENCY = "Take as needed";

    @Autowired
    private MedicationSearchService medicationSearchService;

    @Override
    public MedicationDto convert(ClientMedication source) {
        var target = new MedicationDto();
        fill(source, target);

        Stream.ofNullable(source.getMedicationInformation())
                .flatMap(info -> Stream.concat(
                        Stream.ofNullable(info.getProductNameCode()),
                        info.getTranslationProductCodes().stream()
                ))
                .filter(it -> CodeSystem.NDC.getOid().equals(it.getCodeSystem()))
                .findFirst()
                .map(CcdCode::getCode)
                .map(NdcUtils::toDisplayValue)
                .ifPresent(target::setNdcCode);

        target.setNdcName(Optional.ofNullable(source.getMedicationInformation()).map(MedicationInformation::getProductNameCode)
                .map(CcdCode::getDisplayName).orElse(null));
        target.setStatus(source.getStatus());
        target.setStatusName(source.getStatus().name());
        target.setStatusTitle(source.getStatus().getTitle());
        target.setFrequency(BooleanUtils.isTrue(source.getPrnScheduled()) ? TAKE_AS_NEEDED_FREQUENCY : source.getSchedule());

        if (StringUtils.isNotEmpty(source.getRecurrence())) {
            try {
                var recurrence = new EldermarkMedicationRecurrence(source.getRecurrence());
                target.setRecurrence(recurrence.toString());
            } catch (EldermarkRecurrenceParseException e) {
                target.setRecurrence(source.getRecurrence());
            }
        } else {
            target.setRecurrence(source.getRecurrence());
        }

        target.setDirections(source.getFreeTextSig());
        Optional.ofNullable(source.getMedicationReport())
                .map(MedicationReport::getDosage)
                .ifPresent(target::setDosageQuantity);
        target.setIndicatedFor(Optional.ofNullable(source.getMedicationReport()).map(MedicationReport::getIndicatedFor).orElse(null));
        target.setOrigin(Optional.ofNullable(source.getMedicationReport()).map(MedicationReport::getOrigin).orElse(null));
        target.setRefillDate(DateTimeUtils.toEpochMilli(source.getRefillDate()));
        target.setPharmacyOriginDate(DateTimeUtils.toEpochMilli(source.getPharmacyOriginDate()));
        target.setPharmRxId(source.getPharmRxId());
        if (source.getDispensingPharmacy() != null) {
            target.setDispensingPharmacy(createPharmacy(source.getDispensingPharmacy()));
        }
        if (source.getPharmacy() != null) {
            target.setPharmacy(createPharmacy(source.getPharmacy()));
        }
        target.setLastUpdateStr(source.getLastUpdate());
        if (StringUtils.isNotEmpty(source.getLastUpdate())) {
            try {
                target.setLastUpdate(Instant.from(LAST_UPDATED_PATTERN.parse(source.getLastUpdate())).toEpochMilli());
            } catch (Exception e) {
                //do nothing
            }
        }
        target.setStopDeliveryAfterDate(DateTimeUtils.toEpochMilli(source.getStopDeliveryAfterDate()));

        populatePrescriptionInfo(source, target);

        target.setDataSource(CcdDtoConverterUtils.createDataSource(source.getClient()));
        target.setComment(source.getComment());
        if (source.getMediSpanId() != null) {
            target.setMediSpanId(source.getMediSpanId());
            medicationSearchService.findByMediSpanId(source.getMediSpanId())
                    .ifPresent(drug -> {
                        target.setStrength(drug.getStrength());
                        target.setDoseForm(drug.getDoseForm());
                        target.setRoute(drug.getRoute());
                    });
        }

        target.setRecordedDate(DateTimeUtils.toEpochMilli(source.getCreationDatetime()));
        target.setEditedDate(DateTimeUtils.toEpochMilli(source.getUpdateDatetime()));

        Optional.ofNullable(source.getUpdatedBy())
                .map(Employee::getFullName)
                .ifPresent(target::setEditedByName);

        Optional.ofNullable(source.getCreatedBy())
                .map(Employee::getFullName)
                .ifPresent(target::setRecordedByName);

        return target;
    }

    private void populatePrescriptionInfo(ClientMedication source, MedicationDto target) {
        var supplyOrder = source.getMedicationSupplyOrder();
        if (supplyOrder != null) {
            var author = supplyOrder.getAuthor();
            var medicalProfessional = supplyOrder.getMedicalProfessional();
            var prescribedBy = new MedicationDto.PrescribedBy();
            if (author != null && author.getPerson() != null) {
                var person = author.getPerson();
                prescribedBy.setCode(Optional.ofNullable(person.getCode()).map(CcdCode::getDisplayName).orElse(null));
                if (CollectionUtils.isNotEmpty(person.getNames())) {
                    var name = person.getNames().get(0);
                    prescribedBy.setFirstName(name.getGiven());
                    prescribedBy.setLastName(name.getFamily());
                }
                prescribedBy.setWorkPhone(PersonTelecomUtils.findValue(person, PersonTelecomCode.WP).orElse(null));
                prescribedBy.setEmail(PersonTelecomUtils.findValue(person, PersonTelecomCode.EMAIL).orElse(null));
                if (CollectionUtils.isNotEmpty(person.getAddresses())) {
                    prescribedBy.setAddress(person.getAddresses().get(0).getFullAddress());
                }
            }
            if (medicalProfessional != null) {
                prescribedBy.setSpeciality(medicalProfessional.getSpeciality());
                prescribedBy.setOrganizationName(medicalProfessional.getOrganizationName());
                prescribedBy.setCommunityName(Optional.ofNullable(medicalProfessional.getCommunity()).map(Community::getName).orElse(null));
                prescribedBy.setExtPharmacyId(medicalProfessional.getExtPharmacyId());
                prescribedBy.setNpi(medicalProfessional.getNpi());
            }
            target.setPrescribedBy(prescribedBy);
            target.setPrescriptionQuantity(supplyOrder.getQuantity());
            target.setPrescribedDate(DateTimeUtils.toEpochMilli(supplyOrder.getTimeLow()));
            target.setPrescriptionExpirationDate(DateTimeUtils.toEpochMilli(supplyOrder.getTimeHigh()));
        }
    }

    private MedicationDto.Pharmacy createPharmacy(Community source) {
        var pharmacy = new MedicationDto.Pharmacy();
        pharmacy.setCode(source.getLegacyId());
        pharmacy.setName(source.getName());
        pharmacy.setPhone(source.getPhone());
        return pharmacy;
    }
}
