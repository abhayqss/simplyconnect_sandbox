package com.scnsoft.eldermark.converter.entity2dto.client;

import com.scnsoft.eldermark.beans.ClientFilter;
import com.scnsoft.eldermark.dto.filter.ClientFilterDto;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientFilterDtoConverter implements Converter<ClientFilterDto, ClientFilter> {

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public ClientFilter convert(ClientFilterDto source) {

        var target = new ClientFilter();

        target.setOrganizationId(source.getOrganizationId());
        target.setCommunityIds(source.getCommunityIds());
        target.setSsnLast4(source.getSsnLast4());
        target.setSsn(source.getSsn());
        target.setGenderId(source.getGenderId());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setBirthDate(source.getBirthDate());
        target.setRecordStatus(source.getRecordStatus());
        target.setProgramStatusIds(source.getProgramStatusIds());
        target.setPrimaryCarePhysician(source.getPrimaryCarePhysician());
        target.setInsuranceNetworkAggregatedName(source.getInsuranceNetworkAggregatedName());
        target.setPharmacyNames(source.getPharmacyNames());
        target.setHasNoPharmacies(source.getHasNoPharmacies());
        target.setIsAdmitted(source.getIsAdmitted());
        target.setUnit(source.getUnit());
        target.setMedicaidNumber(source.getMedicaidNumber());
        target.setMedicareNumber(source.getMedicareNumber());
        target.setWithAccessibleAppointments(source.getWithAccessibleAppointments());
        target.setHieConsentPolicyName(source.getHieConsentPolicyName());
        target.setClientAccessType(source.getClientAccessType());

        target.setPermissionFilter(permissionFilterService.createPermissionFilterForCurrentUser());

        return target;
    }
}
