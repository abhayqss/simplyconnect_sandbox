package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.MedicationDto;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Indication;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.entity.medication.MedicationReport;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MedicationDtoConverter implements ListAndItemConverter<ClientMedication, MedicationDto>, MedicationConverter {

    private static final String TAKE_AS_NEEDED_FREQUENCY = "Take as needed";

    @Autowired
    private ListAndItemConverter<Indication, String> indicationDtoConverter;

    @Override
    public MedicationDto convert(ClientMedication source) {
        MedicationDto target = new MedicationDto();
        fill(source, target);
        target.setNdc(Optional.ofNullable(source.getMedicationInformation()).map(MedicationInformation::getProductNameCode)
                .map(CcdCode::getDisplayName).orElse(null));
        target.setStatusName(source.getStatus().name());
        target.setStatusTitle(source.getStatus().getTitle());
        target.setFrequency(BooleanUtils.isTrue(source.getPrnScheduled()) ? TAKE_AS_NEEDED_FREQUENCY : source.getSchedule());
        target.setRecurrence(source.getRecurrence());
        target.setOrganizationName(Optional.ofNullable(source.getOrganization()).map(Organization::getName).orElse(null));
        target.setCommunityName(Optional.ofNullable(source.getClient().getCommunity()).map(Community::getName).orElse(null));
        if (CollectionUtils.isNotEmpty(source.getIndications())) {
            target.setIndications(indicationDtoConverter.convertList(source.getIndications()));
        }
        target.setIndicatedFor(Optional.ofNullable(source.getMedicationReport()).map(MedicationReport::getIndicatedFor).orElse(null));
        target.setOrigin(Optional.ofNullable(source.getMedicationReport()).map(MedicationReport::getOrigin).orElse(null));
        target.setRefillDate(DateTimeUtils.toEpochMilli(source.getRefillDate()));
        target.setPharmacyOriginDate(DateTimeUtils.toEpochMilli(source.getPharmacyOriginDate()));
        target.setEndDateFuture(DateTimeUtils.toEpochMilli(source.getEndDateFuture()));
        target.setPharmRxid(source.getPharmRxId());
        if (source.getDispensingPharmacy() != null) {
            var dispensingPharmacy = source.getDispensingPharmacy();
            target.setDispensingPharmacyCode(dispensingPharmacy.getLegacyId());
            target.setDispensingPharmacyName(dispensingPharmacy.getName());
            target.setDispensingPharmacyPhone(dispensingPharmacy.getPhone());
        }
        target.setLastUpdate(source.getLastUpdate());
        target.setStopDeliveryAfterDate(DateTimeUtils.toEpochMilli(source.getStopDeliveryAfterDate()));
        if (source.getPharmacy() != null) {
            var pharmacy = source.getPharmacy();
            target.setPharmacyCode(pharmacy.getLegacyId());
            target.setPharmacyName(pharmacy.getName());
            target.setPharmacyPhone(pharmacy.getPhone());
        }
        populatePrescribedBy(source, target);

        Optional.ofNullable(source.getMedicationReport())
                .map(MedicationReport::getDosage)
                .ifPresent(target::setDosageQuantity);

        target.setRecordedDate(DateTimeUtils.toEpochMilli(source.getCreationDatetime()));
        target.setEditedDate(DateTimeUtils.toEpochMilli(source.getUpdateDatetime()));
        target.setComment(source.getComment());

        Optional.ofNullable(source.getUpdatedBy())
                .map(Employee::getFullName)
                .ifPresent(target::setEditedByName);

        Optional.ofNullable(source.getCreatedBy())
                .map(Employee::getFullName)
                .ifPresent(target::setRecordedByName);

        return target;
    }

    private void populatePrescribedBy(ClientMedication source, MedicationDto target) {
        var supplyOrder = source.getMedicationSupplyOrder();
        if (supplyOrder != null) {
            var author = supplyOrder.getAuthor();
            var medicalProfessional = supplyOrder.getMedicalProfessional();
            var prescribedBy = target.new PrescribedBy();
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
            target.setPrescribedDate(DateTimeUtils.toEpochMilli(supplyOrder.getTimeLow()));
            target.setPrescriptionQuantity(supplyOrder.getQuantity());
            target.setPrescriptionExpirationDate(DateTimeUtils.toEpochMilli(supplyOrder.getTimeHigh()));
        }
    }
}
