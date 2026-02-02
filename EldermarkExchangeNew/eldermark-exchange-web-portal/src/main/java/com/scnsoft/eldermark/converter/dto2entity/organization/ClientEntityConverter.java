package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.ClientDto;
import com.scnsoft.eldermark.dto.client.insurance.InsuranceAuthorizationDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContact;
import com.scnsoft.eldermark.entity.client.insurance.ClientInsuranceAuthorization;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.basic.CareCoordinationConstants;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class ClientEntityConverter extends ClientEssentialsEntityConverter<ClientDto> implements Converter<ClientDto, Client> {

    @Autowired
    private Converter<ClientDto, Person> clientPersonEntityConverter;

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private InsuranceNetworkService inNetworkInsuranceService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private Converter<ClientDto, ClientPrimaryContact> clientPrimaryContactEntityConverter;

    protected Client getClientEntityWithNonEditable(ClientDto source) {
        Client target;
        if (source.getId() != null)
            target = clientService.findById(source.getId());
        else {
            target = new Client();
            target.setCommunity(communityService.findById(source.getCommunityId()));
            target.setOrganization(organizationService.findById(source.getOrganizationId()));
            target.setOrganizationId(source.getOrganizationId());

            CareCoordinationConstants.setLegacyId(target);
            target.setLegacyTable(CareCoordinationConstants.CCN_MANUAL_LEGACY_TABLE);
            target.setCreatedBy(loggedUserService.getCurrentEmployee());
        }

        return target;
    }

    @Override
    protected void updateEditable(ClientDto source, Client target) {
        super.updateEditable(source, target);

        var ssn = CareCoordinationUtils.normalizePhone(
                StringUtils.isBlank(source.getSsn()) ? null : source.getSsn()
        );
        target.setSocialSecurity(ssn);
        target.setSsnLastFourDigits(StringUtils.right(ssn, 4));

        target.setPerson(clientPersonEntityConverter.convert(source));
        target.setGender(ccdCodeService.findById(source.getGenderId()));
        target.setRace(source.getRaceId() != null ? ccdCodeService.findById(source.getRaceId()) : null);
        target.setMaritalStatus(source.getMaritalStatusId() != null ? ccdCodeService.findById(source.getMaritalStatusId()) : null);
        target.setGroupNumber(source.getGroupNumber());
        target.setMemberNumber(source.getMemberNumber());
        target.setMedicareNumber(source.getMedicareNumber());
        target.setMedicaidNumber(source.getMedicaidNumber());
        target.setRetained(source.getRetained());
        target.setPrimaryCarePhysicianFirstName(source.getPrimaryCarePhysicianFirstName());
        target.setPrimaryCarePhysicianLastName(source.getPrimaryCarePhysicianLastName());
        target.setIntakeDate(DateTimeUtils.toInstant(source.getIntakeDate()));
        target.setReferralSource(source.getReferralSource());
        target.setCurrentPharmacyName(source.getCurrentPharmacyName());
        target.setSharing(source.getIsDataShareEnabled());
        target.setCreatedDate(Instant.now());
        target.setMultipartFile(source.getAvatar());
        target.setShouldRemoveAvatar(source.getShouldRemoveAvatar());
        target.setInNetworkInsurance(source.getInsuranceNetworkId() != null ?
                inNetworkInsuranceService.getById(source.getInsuranceNetworkId()) : null);
        target.setInsurancePlan(source.getInsurancePaymentPlan());
        target.setRiskScore(source.getRiskScore());
        target.setUnitNumber(source.getUnit());
        target.setPrimaryContact(clientPrimaryContactEntityConverter.convert(source));
        populateInsuranceAuthorizations(source, target);
    }

    private void populateInsuranceAuthorizations(ClientDto source, Client target) {
        if (target.getInsuranceAuthorizations() == null) {
            target.setInsuranceAuthorizations(new ArrayList<>());
        }
        var existingIds = target.getInsuranceAuthorizations().stream()
                .map(ClientInsuranceAuthorization::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        var oldIds = Optional.ofNullable(source.getInsuranceAuthorizations()).stream()
                .flatMap(Collection::stream)
                .map(InsuranceAuthorizationDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!CollectionUtils.isEqualCollection(existingIds, oldIds)) {
            throw new ValidationException("Invalid insurance authorization list");
        }

        Optional.ofNullable(source.getInsuranceAuthorizations()).stream()
                .flatMap(Collection::stream)
                .filter(it -> it.getId() == null)
                .map(dto -> {
                    var entity = new ClientInsuranceAuthorization();
                    entity.setClient(target);
                    entity.setStartDate(DateTimeUtils.toInstant(dto.getStartDate()));
                    entity.setEndDate(DateTimeUtils.toInstant(dto.getEndDate()));
                    entity.setNumber(dto.getNumber());
                    entity.setCreatedBy(loggedUserService.getCurrentEmployee());
                    entity.setCreatedDate(Instant.now());
                    return entity;
                })
                .forEach(target.getInsuranceAuthorizations()::add);
    }
}
