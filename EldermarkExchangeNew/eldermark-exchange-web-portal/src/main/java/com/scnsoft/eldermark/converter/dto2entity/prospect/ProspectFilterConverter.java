package com.scnsoft.eldermark.converter.dto2entity.prospect;

import com.scnsoft.eldermark.dto.prospect.ProspectFilter;
import com.scnsoft.eldermark.dto.prospect.ProspectFilterDto;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ProspectFilterConverter implements Converter<ProspectFilterDto, ProspectFilter> {

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public ProspectFilter convert(ProspectFilterDto source) {
        var target = new ProspectFilter();
        target.setCommunityIds(source.getCommunityIds());
        target.setOrganizationId(source.getOrganizationId());
        target.setBirthDate(DateTimeUtils.parseDateToLocalDate(source.getBirthDate()));
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setProspectStatus(source.getProspectStatus());
        target.setGenderId(source.getGenderId());
        target.setPermissionFilter(permissionFilterService.createPermissionFilterForCurrentUser());
        return target;
    }
}
