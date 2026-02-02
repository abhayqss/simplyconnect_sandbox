package com.scnsoft.eldermark.api.external.facade;

import com.scnsoft.eldermark.api.shared.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.ProspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProspectFacadeImpl implements ProspectFacade {

    @Autowired
    private Converter<ProspectDto, Prospect> prospectEntityConverter;

    @Autowired
    private ProspectService prospectService;

    @Autowired
    private CommunityService communityService;

    @Override
    public Long save(ProspectDto prospectDto) {
        var externalId = prospectDto.getIdentifier();
        var communityOid =prospectDto.getCommunityOID();
        var organizationOid = prospectDto.getOrganizationOID();
        var communityIdAwareOptional = communityService.findByOrganizationOidAndCommunityOid(organizationOid, communityOid);
        if (communityIdAwareOptional.isEmpty()) {
            throw new BusinessException("Prospect's communityis not found");
        } else {
            prospectDto.setCommunityId(communityIdAwareOptional.get().getId());
        }
        var prospectIdAwareOptional = prospectService.findByCommunityIdAndExternalId(communityIdAwareOptional.get().getId(), externalId);
        if (prospectIdAwareOptional.isPresent()) {
            prospectDto.setId(prospectIdAwareOptional.get().getId());
            prospectService.createHistoryRecord(prospectIdAwareOptional.get().getId());
        }
        var prospect = prospectEntityConverter.convert(prospectDto);
        return prospectService.save(prospect).getId();
    }
}
