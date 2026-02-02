package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.GroupedAffiliatedOrganizationDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author averazub
 * @author knetkachou
 * @author mradzivonenka
 * @author phomal
 * Created by averazub on 3/21/2016.
 */
@Transactional
public interface OrganizationService {

    @Transactional(readOnly = true)
    OrganizationDto getOrganization(Long id);
    @Transactional(readOnly = true)
    OrganizationDto getOrganizationWithAffiliatedDetails(Long id);
    @Transactional(readOnly = true)
    Page<OrganizationListItemDto> list(OrganizationFilterDto organizationFilterDto, Pageable pageRequest);
    @Transactional(readOnly = true)
    List<Pair<Long, String>> listBrief();
    Boolean isSingleOrganizationAccessible();
    OrganizationDto create(final OrganizationDto organization, Boolean createdAutomatically);
    OrganizationDto update(Long organizationId, final OrganizationDto organization);
    void deleteOrganization(Long organizationId);
    @Transactional(readOnly = true)
    Boolean checkIfUnique(OrganizationDto data);
    Long getOrCreateOrganizationFromSchema(com.scnsoft.eldermark.schema.Organization source);
    @Transactional(readOnly = true)
    List<GroupedAffiliatedOrganizationDto> getAffiliatedOrganizationsInfo(Long primaryDatabaseId);
    @Transactional(readOnly = true)
    List<GroupedAffiliatedOrganizationDto> getPrimaryOrganizationsInfo(Long affiliatedDatabaseId);
    boolean checkDatabaseAccess(long databaseId, Set<Long> employeeDbIds);
    @Transactional(readOnly = true)
    Set<Long> getPrimaryOrgIds(Long affiliatedOrgId);
    @Transactional(readOnly = true)
    Set<Long> getAffiliatedOrgIds(Long primaryOrgId);

    void setCurrentOrganization(Long databaseId);
}
