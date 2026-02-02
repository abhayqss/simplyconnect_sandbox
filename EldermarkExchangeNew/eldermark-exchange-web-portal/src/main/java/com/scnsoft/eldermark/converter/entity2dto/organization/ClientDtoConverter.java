package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.ClientAssociatedContactDto;
import com.scnsoft.eldermark.dto.ClientDto;
import com.scnsoft.eldermark.dto.client.PrimaryContactDto;
import com.scnsoft.eldermark.dto.client.insurance.InsuranceAuthorizationDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.CommunityHieConsentPolicyService;
import com.scnsoft.eldermark.service.StateService;
import com.scnsoft.eldermark.service.client.SecuredClientProperty;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.service.security.TransportationSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class ClientDtoConverter implements Converter<Client, ClientDto> {

    @Autowired
    private Converter<Address, AddressDto> clientAddressDtoConverter;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private TransportationSecurityService transportationSecurityService;

    @Autowired
    private Converter<Client, ClientAssociatedContactDto> clientAssociatedContactDtoConverter;

    @Autowired
    private Converter<Client, PrimaryContactDto> clientPrimaryContactDtoConverter;

    @Autowired
    private CommunityHieConsentPolicyService communityHieConsentPolicyService;

    @Autowired
    private StateService stateService;

    @Override
    public ClientDto convert(Client source) {
        var accessibleSecuredProperties = clientSecurityService.getAccessibleSecuredProperties();

        ClientDto target = new ClientDto();
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setMiddleName(source.getMiddleName());
        target.setFullName(source.getFullName());
        target.setId(source.getId());
        if (accessibleSecuredProperties.contains(SecuredClientProperty.BIRTH_DATE)) {
            target.setBirthDate(DateTimeUtils.formatLocalDate(source.getBirthDate()));
        }
        if (accessibleSecuredProperties.contains(SecuredClientProperty.SSN_LAST_FOUR_DIGITS)) {
            target.setSsnLastFourDigits(source.getSsnLastFourDigits());
        }
        if (accessibleSecuredProperties.contains(SecuredClientProperty.SSN)) {
            target.setSsn(source.getSocialSecurity());
        }
        target.setIsActive(source.getActive());
        target.setOrganization(source.getOrganization() != null ? source.getOrganization().getName() : null);
        target.setOrganizationId(source.getOrganizationId());
        target.setOrganizationPhone(source.getOrganization().getAddressAndContacts() != null ?
                source.getOrganization().getAddressAndContacts().getPhone() : null);
        if (source.getAvatar() != null) {
            target.setAvatarId(source.getAvatar().getId());
            target.setAvatarName(source.getAvatar().getAvatarName());
        }
        if (source.getPerson() != null) {
            for (PersonTelecom telecom : source.getPerson().getTelecoms()) {
                if (PersonTelecomCode.EMAIL.name().equalsIgnoreCase(telecom.getUseCode())) {
                    target.setEmail(telecom.getValue());
                }
                if (PersonTelecomCode.HP.name().equalsIgnoreCase(telecom.getUseCode())) {
                    target.setPhone(telecom.getValue());
                }
                if (PersonTelecomCode.MC.name().equalsIgnoreCase(telecom.getUseCode())) {
                    target.setCellPhone(telecom.getValue());
                }
            }

            if (CollectionUtils.isNotEmpty(source.getPerson().getAddresses())) {
                target.setAddress(clientAddressDtoConverter.convert(source.getPerson().getAddresses().get(0)));
            }
        }
        if (source.getCommunity() != null) {
            /*
             * target.setPhone(source.getCommunity().getPhone()); target.setCellPhone(
             * source.getCommunity().getTelecom() != null ?
             * source.getCommunity().getTelecom().getUseCode() + "" : null);
             */

            target.setCommunityId(source.getCommunity().getId());
            target.setCommunity(source.getCommunity().getName());
            target.setCommunityPhone(source.getCommunity().getPhone());
        }
        if (source.getGender() != null) {
            target.setGenderId(source.getGender().getId());
            target.setGender(source.getGender().getDisplayName());
        }
        if (source.getRace() != null) {
            target.setRaceId(source.getRace().getId());
            target.setRace(source.getRace().getDisplayName());
        }
        if (source.getMaritalStatus() != null) {
            target.setMaritalStatusId(source.getMaritalStatus().getId());
            target.setMaritalStatus(source.getMaritalStatus().getDisplayName());
        }
        target.setInsuranceNetworkId(source.getInNetworkInsurance() != null ? source.getInNetworkInsurance().getId() : null);
        target.setInsurancePaymentPlan(source.getInsurancePlan());
        target.setMemberNumber(source.getMemberNumber());
        target.setGroupNumber(source.getGroupNumber());
        target.setMedicaidNumber(source.getMedicaidNumber());
        target.setMedicareNumber(source.getMedicareNumber());
        target.setRetained(source.getRetained());
        target.setPrimaryCarePhysicianFirstName(source.getPrimaryCarePhysicianFirstName());
        target.setPrimaryCarePhysicianLastName(source.getPrimaryCarePhysicianLastName());
        target.setIntakeDate(source.getIntakeDate() != null
                ? source.getIntakeDate().toEpochMilli()
                : null);
        target.setReferralSource(source.getReferralSource());
        target.setCurrentPharmacyName(source.getCurrentPharmacyName());
        target.setIsDataShareEnabled(source.getSharing());
        if (accessibleSecuredProperties.contains(SecuredClientProperty.RISK_SCORE)) {
            target.setRiskScore(source.getRiskScore());
        }
        boolean canEdit = clientSecurityService.canEdit(source.getId());
        target.setCanEdit(canEdit);
        target.setCanEditSsn(canEdit && clientSecurityService.canEditSsn(source.getId()));
        target.setCanRequestRide(transportationSecurityService.canRequestNewRide(source.getId()));
        target.setCanViewRideHistory(transportationSecurityService.canViewRideHistory(source.getId()));
        populateAdmittanceHistory(source, target);
        target.setAssociatedContact(clientAssociatedContactDtoConverter.convert(source));
        target.setUnit(source.getUnitNumber());
        target.setCreatedDate(source.getCreatedDate() != null ? source.getCreatedDate().toEpochMilli() : null);
        target.setDeactivatedDate(clientService.resolveDeactivatedDate(source)
                .map(DateTimeUtils::toEpochMilli)
                .orElse(null)
        );
        target.setManuallyCreated(clientService.wasManuallyCreated(source));
        target.setPharmacyPid(source.getPharmacyPid());
        target.setPrimaryContact(clientPrimaryContactDtoConverter.convert(source));
        populateInsuranceAuthorizations(source, target);
        populateHieConsentPolicyData(target, source);

        if (source.getPccPatientId() != null) {
            target.setPointClickCareMedicalRecordNumber(source.getMedicalRecordNumber());
        }

        return target;
    }

    private void populateHieConsentPolicyData(ClientDto target, Client source) {
        var policy = source.getHieConsentPolicyType();
        target.setHieConsentPolicyName(policy);
        target.setHieConsentPolicyObtainedBy(source.getHieConsentPolicyObtainedBy());
        target.setHieConsentPolicyTitle(policy.getDisplayName());
        target.setHieConsentPolicyObtainedDate(DateTimeUtils.toEpochMilli(source.getHieConsentPolicyUpdateDateTime()));
        target.setHieConsentPolicyObtainedFrom(source.getHieConsentPolicyObtainedFrom());
    }

    private void populateAdmittanceHistory(Client source, ClientDto target) {
        if (source.getCommunity() != null) {
            var admittanceHistory = clientService.findClientAdmittanceHistoryInCommunity(source.getId(), source.getCommunity().getId());
            TreeSet<Long> admitMillis = admittanceHistory.stream().map(AdmittanceHistory::getAdmitDate).filter(Objects::nonNull).map(DateTimeUtils::toEpochMilli).collect(Collectors.toCollection(TreeSet::new));
            TreeSet<Long> dischargeMillis = admittanceHistory.stream().map(AdmittanceHistory::getDischargeDate).filter(Objects::nonNull).map(DateTimeUtils::toEpochMilli).collect(Collectors.toCollection(TreeSet::new));
            Optional.ofNullable(source.getAdmitDate()).ifPresent(date -> admitMillis.add(date.toEpochMilli()));
            Optional.ofNullable(source.getDischargeDate()).ifPresent(date -> dischargeMillis.add(date.toEpochMilli()));
            target.setAdmitDates(admitMillis);
            target.setDischargeDates(dischargeMillis);
        }
    }

    private void populateInsuranceAuthorizations(Client source, ClientDto target) {
        if (CollectionUtils.isNotEmpty(source.getInsuranceAuthorizations())) {
            target.setInsuranceAuthorizations(
                    source.getInsuranceAuthorizations().stream()
                            .map(entity -> {
                                var dto = new InsuranceAuthorizationDto();
                                dto.setId(entity.getId());
                                dto.setEndDate(DateTimeUtils.toEpochMilli(entity.getEndDate()));
                                dto.setStartDate(DateTimeUtils.toEpochMilli(entity.getStartDate()));
                                dto.setNumber(entity.getNumber());
                                return dto;
                            })
                            .collect(Collectors.toList())
            );
        }
    }

}
