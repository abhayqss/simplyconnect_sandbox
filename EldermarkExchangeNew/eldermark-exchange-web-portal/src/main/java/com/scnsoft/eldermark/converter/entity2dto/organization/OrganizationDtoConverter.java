package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.OrganizationBaseDto;
import com.scnsoft.eldermark.dto.OrganizationDto;
import com.scnsoft.eldermark.entity.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OrganizationDtoConverter implements Converter<Organization, OrganizationDto> {

    @Autowired
    private ItemConverter<Organization, OrganizationBaseDto> organizationBasicDtoItemConverter;

    @Override
    public OrganizationDto convert(Organization source) {
        OrganizationDto target = new OrganizationDto();
        organizationBasicDtoItemConverter.convert(source, target);
        target.setCopyEventNotificationsForPatients(source.getCopyEventNotificationsForPatients());
        target.setAllowExternalInboundReferrals(source.isReceiveNonNetworkReferrals());
        return target;
    }
}
