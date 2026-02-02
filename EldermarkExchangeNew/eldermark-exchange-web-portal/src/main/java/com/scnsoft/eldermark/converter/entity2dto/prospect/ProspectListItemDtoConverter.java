package com.scnsoft.eldermark.converter.entity2dto.prospect;

import com.scnsoft.eldermark.beans.projection.ProspectListItemFieldsAware;
import com.scnsoft.eldermark.dto.prospect.ProspectListItemDto;
import com.scnsoft.eldermark.service.security.ProspectSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ProspectListItemDtoConverter implements Converter<ProspectListItemFieldsAware, ProspectListItemDto> {

    @Autowired
    private ProspectSecurityService prospectSecurityService;

    @Override
    public ProspectListItemDto convert(ProspectListItemFieldsAware source) {
        var target = new ProspectListItemDto();
        target.setId(source.getId());
        target.setAvatarId(source.getAvatarId());
        target.setFullName(source.getFullName());
        target.setGender(source.getGenderDisplayName());
        target.setBirthDate(DateTimeUtils.formatLocalDate(source.getBirthDate()));
        target.setDocumentReceived(0L);
        target.setDocumentSent(0L);
        target.setCommunityName(source.getCommunityName());
        target.setCreatedDate(DateTimeUtils.toEpochMilli(source.getCreatedDate()));
        target.setIsActive(source.getActive());
        target.setCanView(prospectSecurityService.canView(source.getId()));
        target.setCanEdit(prospectSecurityService.canEdit(source.getId()));
        target.setCanRequestSignature(false);
        return target;
    }
}
