package com.scnsoft.eldermark.facade.prospect;

import com.scnsoft.eldermark.beans.projection.ProspectListItemFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.ProspectSecurityFieldsAware;
import com.scnsoft.eldermark.dto.ProspectSaveData;
import com.scnsoft.eldermark.dto.prospect.*;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.service.ProspectService;
import com.scnsoft.eldermark.service.security.ProspectSecurityService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProspectFacadeImpl implements ProspectFacade {

    @Autowired
    private ProspectService prospectService;

    @Autowired
    private ProspectSecurityService prospectSecurityService;

    @Autowired
    private Converter<ProspectDto, ProspectSaveData> prospectDataConverter;

    @Autowired
    private Converter<Prospect, ProspectDto> prospectEntityConverter;

    @Autowired
    private Converter<ProspectFilterDto, ProspectFilter> prospectFilterDtoConverter;

    @Autowired
    private Converter<ProspectListItemFieldsAware, ProspectListItemDto> prospectListItemConverter;

    @Override
    @Transactional(readOnly = true)
    public Page<ProspectListItemDto> find(ProspectFilterDto filter, Pageable pageable) {
        return prospectService.find(
                        prospectFilterDtoConverter.convert(filter),
                        PaginationUtils.applyEntitySort(pageable, ProspectListItemDto.class),
                        ProspectListItemFieldsAware.class
                )
                .map(prospectListItemConverter::convert);
    }

    @Override
    @Transactional
    @PreAuthorize("@prospectSecurityService.canAdd(#prospectDto)")
    public Long add(ProspectDto prospectDto) {
        return save(prospectDto);
    }

    @Override
    @Transactional
    @PreAuthorize("@prospectSecurityService.canAdd(#prospectDto)")
    public Long edit(ProspectDto prospectDto) {
        return save(prospectDto);
    }

    @Override
    @Transactional
    @PreAuthorize("@prospectSecurityService.canViewList()")
    public ProspectDto findById(Long prospectId) {
        return prospectEntityConverter.convert(prospectService.findById(prospectId));
    }

    @Override
    @Transactional
    @PreAuthorize("@prospectSecurityService.canEdit(#prospectId)")
    public void activate(Long prospectId, ProspectActivationDto activationDto) {
        prospectService.activateProspect(prospectId, activationDto);
    }

    @Override
    @Transactional
    @PreAuthorize("@prospectSecurityService.canEdit(#prospectId)")
    public void deactivate(Long prospectId, ProspectDeactivationDto deactivationDto) {
        prospectService.deactivateProspect(prospectId, deactivationDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canView() {
        return prospectSecurityService.canViewList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long organizationId) {
        return prospectSecurityService.canAdd(ProspectSecurityFieldsAware.of(
                organizationId,
                ProspectSecurityService.ANY_TARGET_COMMUNITY
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canEdit(Long prospectId) {
        return prospectSecurityService.canEdit(prospectId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#prospectId == null " +
            "? @prospectSecurityService.canAdd(T(com.scnsoft.eldermark.beans.security.projection.entity.ProspectSecurityFieldsAware).of(null, #communityId)) " +
            ": @prospectSecurityService.canEdit(#prospectId)")
    public ProspectCommunityUniquenessDto validateUniqueInCommunity(Long prospectId, Long communityId, String ssn) {
        Boolean ssnUnique = prospectService.isValidSsn(prospectId, communityId, ssn);
        return new ProspectCommunityUniquenessDto(ssnUnique);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#prospectId == null " +
            "? @prospectSecurityService.canAdd(T(com.scnsoft.eldermark.beans.security.projection.entity.ProspectSecurityFieldsAware).of(#organizationId, -1L)) " +
            ": @prospectSecurityService.canEdit(#prospectId)")
    public ProspectOrganizationUniquenessDto validateUniqueInOrganization(Long prospectId, Long organizationId, String email) {
        Boolean emailUnique = prospectService.isEmailUnique(prospectId, organizationId, email);
        return new ProspectOrganizationUniquenessDto(emailUnique);
    }

    private Long save(ProspectDto prospectDto) {
        if (prospectDto.getId() != null) {
            prospectService.createHistoryRecord(prospectDto.getId());
        }
        var prospectData = prospectDataConverter.convert(prospectDto);
        return prospectService.save(prospectData).getId();
    }
}
