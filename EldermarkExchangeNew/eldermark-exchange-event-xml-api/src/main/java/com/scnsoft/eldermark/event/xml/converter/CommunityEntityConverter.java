package com.scnsoft.eldermark.event.xml.converter;

import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.event.xml.schema.Community;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Component
public class CommunityEntityConverter implements Converter<Community, com.scnsoft.eldermark.entity.community.Community> {

    @Override
    public com.scnsoft.eldermark.entity.community.Community convert(Community source) {
        var target = new com.scnsoft.eldermark.entity.community.Community();
        target.setInactive(false);
        target.setCreatedAutomatically(true);
        target.setModuleCloudStorage(false);
        target.setModuleHie(true);
        target.setTestingTraining(false);
        target.setLegacyId(UUID.randomUUID().toString());
        target.setLegacyTable("COMPANY");
        target.setName(source.getName());
        target.setOid(source.getID());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        var address = new CommunityAddress();
        address.setOrganization(target.getOrganization());
        address.setCommunity(target);
        address.setLegacyTable("COMPANY");
        address.setLegacyId(UUID.randomUUID().toString());
        var addresses = new ArrayList<CommunityAddress>();
        addresses.add(address);
        target.setAddresses(addresses);
        target.setLastModified(Instant.now());
        return target;
    }
}
