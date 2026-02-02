package com.scnsoft.eldermark.services.inbound.therap.programenrollment;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.dao.DatabaseJpaDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapProgramEnrollmentCsv;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.CommunityCrudService;
import com.scnsoft.eldermark.services.inbound.therap.TherapInboundFilesServiceRunCondition;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityCreateDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityViewDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.ExchangeStringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Conditional(TherapInboundFilesServiceRunCondition.class)
public class TherapOrganizationServiceImpl implements TherapOrganizationService {

    private final OrganizationDao organizationDao;
    private final StateService stateService;
    private final DatabaseJpaDao databaseJpaDao;
    private final CommunityCrudService communityCrudService;

    @Autowired
    public TherapOrganizationServiceImpl(OrganizationDao organizationDao, StateService stateService, DatabaseJpaDao databaseJpaDao, CommunityCrudService communityCrudService) {
        this.organizationDao = organizationDao;
        this.stateService = stateService;
        this.databaseJpaDao = databaseJpaDao;
        this.communityCrudService = communityCrudService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Pair<Long, Boolean> findOrCreateOrganization(TherapProgramEnrollmentCsv enrollment) {
        final Optional<Organization> foundCommunity = find(enrollment);
        if (foundCommunity.isPresent()) {
            return new Pair<>(foundCommunity.get().getId(), true);
        } else {
            Long organizationId = create(enrollment);
            return new Pair<>(organizationId, false);
        }
    }

    private Optional<Organization> find(TherapProgramEnrollmentCsv enrollment) {
        List<Organization> commByNameAndOid = organizationDao.getOrganizationByOidAndNameAndDatabaseOid(enrollment.getPgmId(), enrollment.getPgmName(), enrollment.getProviderCode());
        if (CollectionUtils.isNotEmpty(commByNameAndOid)) {
            return Optional.of(commByNameAndOid.get(0));
        }

        List<Organization> commByName = organizationDao.getOrganizationByNameAndDatabaseOid(enrollment.getPgmName(), enrollment.getProviderCode());
        if (CollectionUtils.isNotEmpty(commByName)) {
            return Optional.of(commByName.get(0));
        }

        List<Organization> commByOid = organizationDao.getOrganizationByOidAndDatabaseOid(enrollment.getPgmId(), enrollment.getProviderCode());
        if (CollectionUtils.isNotEmpty(commByOid)) {
            return Optional.of(commByOid.get(0));
        }

        return Optional.absent();
    }

    private Long create(TherapProgramEnrollmentCsv enrollment) {
        final Database organization = databaseJpaDao.findByOid(enrollment.getProviderCode());
        final CommunityCreateDto communityCreateDto = toCommunityCreateDto(enrollment);
        CommunityViewDto communityViewDto = communityCrudService.create(organization.getId(), communityCreateDto, true);

        return communityViewDto.getId();
    }

    private CommunityCreateDto toCommunityCreateDto(TherapProgramEnrollmentCsv enrollment) {
        final CommunityCreateDto communityCreateDto = new CommunityCreateDto();

        communityCreateDto.setName(enrollment.getPgmName());
        communityCreateDto.setOid(enrollment.getPgmId());
        communityCreateDto.setPhone(enrollment.getSitePhone());
        communityCreateDto.setCity(enrollment.getSiteCity());
        communityCreateDto.setStreet(ExchangeStringUtils.joinNotEmpty(" ", enrollment.getSiteStreet1(), enrollment.getSiteStreet2()));
        communityCreateDto.setPostalCode(enrollment.getSiteZip());
        communityCreateDto.setStateId(stateService.findByAbbr(enrollment.getSiteState()).getId());

        return communityCreateDto;
    }
}
