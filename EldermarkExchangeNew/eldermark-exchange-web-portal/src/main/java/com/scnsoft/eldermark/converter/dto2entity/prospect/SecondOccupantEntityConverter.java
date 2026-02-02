package com.scnsoft.eldermark.converter.dto2entity.prospect;

import com.scnsoft.eldermark.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.dto.prospect.SecondOccupantDto;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.prospect.SecondOccupant;
import com.scnsoft.eldermark.service.CcdCodeService;
import com.scnsoft.eldermark.service.InsuranceNetworkService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.ProspectService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiFunction;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class SecondOccupantEntityConverter implements BiFunction<SecondOccupantDto, ProspectDto, SecondOccupant> {

    @Autowired
    private ProspectService prospectService;

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private InsuranceNetworkService inNetworkInsuranceService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private BiFunction<SecondOccupant, ProspectDto, Person> secondOccupantPersonEntityConverter;

    @Override
    public SecondOccupant apply(SecondOccupantDto secondOccupantDto, ProspectDto prospectDto) {
        if (secondOccupantDto != null) {
            var target = new SecondOccupant();
            if (prospectDto.getId() != null) {
                var prospect = prospectService.findById(prospectDto.getId());
                var secondOccupant = prospect.getSecondOccupant();
                if (secondOccupant != null) {
                    fillSecondOccupant(secondOccupantDto, prospectDto, secondOccupant);
                    return secondOccupant;
                }
            }
            fillSecondOccupant(secondOccupantDto, prospectDto, target);
            return target;
        }
        return null;
    }

    private void fillSecondOccupant(SecondOccupantDto secondOccupantDto, ProspectDto prospectDto, SecondOccupant target) {
        target.setPerson(secondOccupantPersonEntityConverter.apply(target, prospectDto));
        target.setBirthDate(DateTimeUtils.parseDateToLocalDate(secondOccupantDto.getBirthDate()));
        var ssn = CareCoordinationUtils.normalizePhone(
                StringUtils.isBlank(secondOccupantDto.getSsn()) ? null : secondOccupantDto.getSsn()
        );
        target.setSocialSecurity(ssn);
        target.setGender(secondOccupantDto.getGenderId() != null ? ccdCodeService.findById(secondOccupantDto.getGenderId()) : null);
        target.setRace(secondOccupantDto.getRaceId() != null ? ccdCodeService.findById(secondOccupantDto.getRaceId()) : null);
        target.setMaritalStatus(secondOccupantDto.getMaritalStatusId() != null ? ccdCodeService.findById(secondOccupantDto.getMaritalStatusId()) : null);
        target.setFirstName(secondOccupantDto.getFirstName());
        target.setLastName(secondOccupantDto.getLastName());
        target.setMiddleName(secondOccupantDto.getMiddleName());
        target.setInNetworkInsurance(secondOccupantDto.getInsuranceNetworkId() != null ?
                inNetworkInsuranceService.getById(secondOccupantDto.getInsuranceNetworkId()) : null);
        target.setInsurancePlan(secondOccupantDto.getInsurancePaymentPlan());
        target.setVeteran(secondOccupantDto.getVeteranStatusName());
        var organization = organizationService.findById(prospectDto.getOrganizationId());
        target.setOrganization(organization);
    }
}