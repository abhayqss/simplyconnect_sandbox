package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.phr.JpaMPIDao;
import com.scnsoft.eldermark.dao.phr.ResidentDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.exception.ValidationException;
import com.scnsoft.eldermark.web.entity.ResidentDto;
import com.scnsoft.eldermark.web.entity.ResidentListItemDto;
import com.scnsoft.eldermark.web.entity.ResolveItiPatientIdentifierRequestDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author phomal Created on 1/30/2018.
 */
@Service
@Transactional(readOnly = true)
public class ResidentsService {

    private final ResidentDao residentDao;
    private final JpaMPIDao jpaMpiDao;
    private final PrivilegesService privilegesService;
    private final DatabasesDao databasesDao;

    @Autowired
    public ResidentsService(ResidentDao residentDao, JpaMPIDao jpaMpiDao, PrivilegesService privilegesService,
            DatabasesDao databasesDao) {
        this.residentDao = residentDao;
        this.jpaMpiDao = jpaMpiDao;
        this.privilegesService = privilegesService;
        this.databasesDao = databasesDao;
    }

    public Page<ResidentListItemDto> listByOrganization(Long orgId, Pageable pageable) {
        if (!privilegesService.canReadOrganization(orgId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        final Page<Resident> residents = residentDao.findVisibleByDatabaseId(orgId, pageable);

        return convert(residents);
    }

    public Page<ResidentListItemDto> listByCommunity(Long communityId, Pageable pageable) {
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        final Page<Resident> residents = residentDao.findVisibleByFacilityId(communityId, pageable);

        return convert(residents);
    }

    public Page<ResidentListItemDto> listAllAccessible(Pageable pageable) {
        final List<Long> ids = privilegesService.listOrganizationIdsWithReadAccess();
        if (CollectionUtils.isNotEmpty(ids)) {
            final Page<Resident> residents = residentDao.findVisibleByDatabaseIdIn(ids, pageable);
            return convert(residents);
        }
        return new PageImpl<>(Collections.<ResidentListItemDto>emptyList());
    }

    public void checkAccessOrThrow(Long residentId) {
        final Long facilityId = residentDao.getFacilityIdById(residentId);
        if (!privilegesService.canReadCommunity(facilityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        if (!residentDao.isVisible(residentId)) {
            throw new PhrException(PhrExceptionType.RESIDENT_OPTED_OUT_OR_DEACTIVATED);
        }
    }

    public ResidentDto get(Long residentId) {
        checkAccessOrThrow(residentId);
        return convertDetailed(residentDao.getOne(residentId));
    }

    Resident getEntity(Long residentId) {
        return residentDao.getOne(residentId);
    }

    @Transactional(readOnly = false)
    public ResidentDto create(Long communityId, String phone, String email, String firstName, String lastName,
            String ssn, String middleName, String nucleusUserId) {
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        // TODO implement
        return new ResidentDto();
    }

    private static Page<ResidentListItemDto> convert(Page<Resident> residents) {
        return residents.map(new Converter<Resident, ResidentListItemDto>() {
            @Override
            public ResidentListItemDto convert(Resident resident) {
                return ResidentsService.convert(resident);
            }
        });
    }

    private static ResidentListItemDto convert(Resident resident) {
        ResidentListItemDto dto = new ResidentListItemDto();
        dto.setId(resident.getId());
        dto.setFirstName(resident.getFirstName());
        dto.setLastName(resident.getLastName());
        dto.setOrgName(resident.getDatabase().getName());
        dto.setCommunityName(resident.getFacility().getName());
        return dto;
    }

    private static ResidentDto convertDetailed(Resident resident) {
        ResidentDto dto = new ResidentDto();
        dto.setId(resident.getId());
        dto.setFirstName(resident.getFirstName());
        dto.setLastName(resident.getLastName());
        dto.setMiddleName(resident.getMiddleName());
        dto.setEmail(PersonService.getPersonEmailValue(resident.getPerson()));
        dto.setPhone(PersonService.getPersonPhoneValue(resident.getPerson()));
        dto.setCommunityId(resident.getFacility().getId());
        dto.setOrgId(resident.getDatabaseId());
        dto.setCommunityName(resident.getFacility().getName());
        dto.setOrgName(resident.getDatabase().getName());

        return dto;
    }

    public Long resolveItiPatientIdentifier(
            ResolveItiPatientIdentifierRequestDto resolveItiPatientIdentifierRequestDto) {
        if (resolveItiPatientIdentifierRequestDto.getAssigningAuthority() == null) {
            throw new ValidationException("Assigning authority must be specified");
        }
        if (StringUtils.isBlank(resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalID())) {
            throw new ValidationException("Assigning authority Universal Id must be specified");
        }
        if (StringUtils.isBlank(resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalIDType())) {
            throw new ValidationException("Assigning authority Universal Id Type must be specified");
        }
        final Database database = databasesDao
                .getDatabaseByOid(resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalID());
        if (database == null || !privilegesService.canReadOrganization(database.getId())) {
            throw new PhrException(PhrExceptionType.NOT_FOUND);
        }
        final MPI mpi;
        if (resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getNamespaceID() != null) {
            mpi = jpaMpiDao
                    .findFirstByPatientIdAndAssigningAuthorityNamespaceAndAssigningAuthorityUniversalAndAssigningAuthorityUniversalType(
                            resolveItiPatientIdentifierRequestDto.getIdentifier(),
                            resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getNamespaceID(),
                            resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalID(),
                            resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalIDType());
        } else {
            mpi = jpaMpiDao.findFirstByPatientIdAndAssigningAuthorityUniversalAndAndAssigningAuthorityUniversalType(
                    resolveItiPatientIdentifierRequestDto.getIdentifier(),
                    resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalID(),
                    resolveItiPatientIdentifierRequestDto.getAssigningAuthority().getUniversalIDType());
        }
        if (mpi == null || mpi.getResidentId() == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND);
        }
        return mpi.getResidentId();
    }

}
