package com.scnsoft.eldermark.converter.dto2entity.organization;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.CommunityDto;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.service.StateService;

@Component
public class CommunityAddressEntityConverter implements ItemConverter<CommunityDto, CommunityAddress> {

    private static final String CARE_COORDINATION_LEGACY_TABLE = "COMPANY";

    @Autowired
    private StateService stateService;

    @Override
    public CommunityAddress convert(CommunityDto source) {
        var target = new CommunityAddress();
        setDefaults(target);
        convert(source, target);
        return target;
    }

    @Override
    public void convert(CommunityDto source, CommunityAddress entity) {
        entity.setCity(source.getCity());
        stateService.findById(source.getStateId()).ifPresent(s -> entity.setState(s.getAbbr()));
        entity.setStreetAddress(source.getStreet());
        entity.setPostalCode(source.getZipCode());
        entity.setOrganizationId(source.getOrganizationId());
    }

    private void setDefaults(CommunityAddress target) {
        target.setLegacyId(UUID.randomUUID().toString());
        target.setLegacyTable(CARE_COORDINATION_LEGACY_TABLE);
    }

}
