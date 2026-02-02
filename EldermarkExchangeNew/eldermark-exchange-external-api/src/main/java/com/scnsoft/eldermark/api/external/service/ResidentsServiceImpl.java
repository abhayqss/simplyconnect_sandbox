package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.specification.ClientExtApiSpecifications;
import com.scnsoft.eldermark.api.external.utils.PersonUtils;
import com.scnsoft.eldermark.api.external.web.dto.ResidentDto;
import com.scnsoft.eldermark.api.external.web.dto.ResidentListItemDto;
import com.scnsoft.eldermark.api.external.web.dto.ResolveItiPatientIdentifierRequestDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.api.shared.exception.ValidationException;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.MPIDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.Organization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ResidentsServiceImpl implements ResidentsService {

    private static final Logger logger = LoggerFactory.getLogger(ResidentsServiceImpl.class);

    private final ClientDao clientDao;
    private final ClientExtApiSpecifications clientExtApiSpecifications;
    private final ClientSpecificationGenerator clientSpecifications;
    private final MPIDao mpiDao;
    private final PrivilegesService privilegesService;
    private final OrganizationDao organizationDao;

    @Autowired
    public ResidentsServiceImpl(ClientDao clientDao, ClientExtApiSpecifications clientExtApiSpecifications, ClientSpecificationGenerator clientSpecifications, MPIDao mpiDao, PrivilegesService privilegesService,
                                OrganizationDao organizationDao) {
        this.clientDao = clientDao;
        this.clientExtApiSpecifications = clientExtApiSpecifications;
        this.clientSpecifications = clientSpecifications;
        this.mpiDao = mpiDao;
        this.privilegesService = privilegesService;
        this.organizationDao = organizationDao;
    }

    @Override
    public Page<ResidentListItemDto> listByOrganization(Long orgId, Pageable pageable) {
        logger.info("Listing residents in organization [{}]", orgId);
        if (!privilegesService.canReadOrganization(orgId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        var byOrg = clientSpecifications.byOrganizationId(orgId);
        var visible = clientExtApiSpecifications.isVisible();
        var residents = clientDao.findAll(byOrg.and(visible), pageable);

        return convert(residents);
    }

    @Override
    public Page<ResidentListItemDto> listByCommunity(Long communityId, Pageable pageable) {
        logger.info("Listing residents in community [{}]", communityId);
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        var byCommunityId = clientSpecifications.byCommunityId(communityId);
        var visible = clientExtApiSpecifications.isVisible();
        var residents = clientDao.findAll(byCommunityId.and(visible), pageable);

        return convert(residents);
    }

//    @Override
//    public Page<ResidentListItemDto> listAllAccessible(Pageable pageable) {
//        final List<Long> ids = privilegesService.listOrganizationIdsWithReadAccess();
//        if (CollectionUtils.isNotEmpty(ids)) {
//            var byOrgs = clientSpecifications.byOrganizationIds(ids);
//            var visible = clientSpecifications.isVisible();
//            var residents = clientDao.findAll(byOrgs.and(visible), pageable);
//            return convert(residents);
//        }
//        return Page.empty();
//    }

    @Override
    public void checkAccessOrThrow(Long residentId) {
        final Long facilityId = clientDao.findById(residentId, CommunityIdAware.class)
                .map(CommunityIdAware::getCommunityId)
                .orElse(null);

        if (!privilegesService.canReadCommunity(facilityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        boolean allowInactive = privilegesService.hasConsanaAccess();
        var byId = clientSpecifications.byId(residentId);
        var visible = clientExtApiSpecifications.isVisible(allowInactive);
        if (clientDao.count(byId.and(visible)) == 0) {
            throw new PhrException(PhrExceptionType.RESIDENT_OPTED_OUT_OR_DEACTIVATED);
        }
    }

    @Override
    public ResidentDto get(Long residentId) {
        logger.info("Loading details of resident [{}]", residentId);
        checkAccessOrThrow(residentId);
        return convertDetailed(clientDao.getOne(residentId));
    }

    @Override
    public Client getEntity(Long residentId) {
        return clientDao.getOne(residentId);
    }

    @Override
    @Transactional(readOnly = false)
    public ResidentDto create(Long communityId, String phone, String email, String firstName, String lastName,
                              String ssn, String middleName, String nucleusUserId) {
        logger.info("Creating resident in community [{}]", communityId);
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        // TODO implement
        return new ResidentDto();
    }

    private static Page<ResidentListItemDto> convert(Page<Client> residents) {
        return residents.map(ResidentsServiceImpl::convert);
    }

    private static ResidentListItemDto convert(Client resident) {
        ResidentListItemDto dto = new ResidentListItemDto();
        dto.setId(resident.getId());
        dto.setFirstName(resident.getFirstName());
        dto.setLastName(resident.getLastName());
        dto.setOrgName(resident.getOrganization().getName());
        dto.setCommunityName(resident.getCommunity().getName());
        return dto;
    }

    private static ResidentDto convertDetailed(Client resident) {
        ResidentDto dto = new ResidentDto();
        fillBaseDetailsDto(dto, resident);
        return dto;
    }

    //todo create separate converters
    public static void fillBaseDetailsDto(ResidentDto dto, Client resident) {
        dto.setId(resident.getId());
        dto.setFirstName(resident.getFirstName());
        dto.setLastName(resident.getLastName());
        dto.setMiddleName(resident.getMiddleName());
        dto.setEmail(PersonUtils.getPersonEmailValue(resident.getPerson()));
        dto.setPhone(PersonUtils.getPersonPhoneValue(resident.getPerson()));
        dto.setCommunityId(resident.getCommunityId());
        dto.setOrgId(resident.getOrganizationId());
        dto.setCommunityName(resident.getCommunity().getName());
        dto.setOrgName(resident.getOrganization().getName());
    }

    @Override
    public Long resolveItiPatientIdentifier(
            ResolveItiPatientIdentifierRequestDto resolveItiPatientIdentifierRequestDto) {
        logger.info("Resolving resident id by ITI identifier resident [{}]", resolveItiPatientIdentifierRequestDto);
        if (resolveItiPatientIdentifierRequestDto.getAssigningAuthority() == null) {
            throw new ValidationException("Assigning authority must be specified");
        }
        if (StringUtils.isBlank(resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalID())) {
            throw new ValidationException("Assigning authority Universal Id must be specified");
        }
        if (StringUtils.isBlank(resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalIDType())) {
            throw new ValidationException("Assigning authority Universal Id Type must be specified");
        }
        final Organization organization = organizationDao.findFirstByOid(
                resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalID());
        if (organization == null || !privilegesService.canReadOrganization(organization.getId())) {
            throw new PhrException(PhrExceptionType.NOT_FOUND);
        }
        final MPI mpi;
        if (resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getNamespaceID() != null) {
            mpi = mpiDao
                    .findFirstByPatientIdAndAssigningAuthorityNamespaceAndAssigningAuthorityUniversalAndAssigningAuthorityUniversalType(
                            resolveItiPatientIdentifierRequestDto.getIdentifier(),
                            resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getNamespaceID(),
                            resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalID(),
                            resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalIDType());
        } else {
            mpi = mpiDao.findFirstByPatientIdAndAssigningAuthorityUniversalAndAndAssigningAuthorityUniversalType(
                    resolveItiPatientIdentifierRequestDto.getIdentifier(),
                    resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalID(),
                    resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalIDType());
        }
        if (mpi == null || mpi.getClientId() == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND);
        }
        checkAccessOrThrow(mpi.getClientId());
        return mpi.getClientId();
    }

}
