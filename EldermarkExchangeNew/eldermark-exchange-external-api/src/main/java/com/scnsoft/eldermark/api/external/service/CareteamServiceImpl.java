package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.dao.PhysicianDao;
import com.scnsoft.eldermark.api.external.specification.ClientCareTeamExtApiSpecifications;
import com.scnsoft.eldermark.api.external.utils.PersonUtils;
import com.scnsoft.eldermark.api.external.web.dto.CareteamMemberBriefDto;
import com.scnsoft.eldermark.api.external.web.dto.CareteamMemberDto;
import com.scnsoft.eldermark.api.shared.ccd.dto.NameDto;
import com.scnsoft.eldermark.api.shared.ccd.dto.PersonDto;
import com.scnsoft.eldermark.api.shared.entity.InvitationStatus;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.dao.ClientCareTeamMemberDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional(readOnly = true)
public class CareteamServiceImpl implements CareteamService {

    private final PrivilegesService privilegesService;
    private final ResidentsService residentsService;
    private final PhysicianDao physicianDao;
    private final ClientCareTeamMemberDao residentCareTeamMemberDao;
    private final ClientCareTeamExtApiSpecifications specifications;
    private DozerBeanMapper dozer;

    private static final Logger logger = LoggerFactory.getLogger(CareteamServiceImpl.class);

    @Autowired
    public CareteamServiceImpl(PrivilegesService privilegesService, ResidentsService residentsService, PhysicianDao physicianDao,
                               ClientCareTeamMemberDao residentCareTeamMemberDao, ClientCareTeamExtApiSpecifications specifications) {
        this.privilegesService = privilegesService;
        this.residentsService = residentsService;
        this.physicianDao = physicianDao;
        this.residentCareTeamMemberDao = residentCareTeamMemberDao;
        this.specifications = specifications;
    }

    @Override
    public Page<CareteamMemberBriefDto> listCommunityCTMs(Long communityId, Pageable pageable) {
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        // TODO implement
        return new PageImpl<>(new ArrayList<CareteamMemberBriefDto>());
    }

    @Override
    public Page<CareteamMemberBriefDto> listResidentCTMs(Long residentId, String directory, Pageable pageable) {
        logger.info("Listing resident CTM for resident [{}], directory is [{}]", residentId, directory);
        directory = StringUtils.trimToNull(directory);
        residentsService.checkAccessOrThrow(residentId);

        if (pageable == null) {
            pageable = Pageable.unpaged();
        }

        Page<ClientCareTeamMember> ctms;
        if ("FAMILY".equalsIgnoreCase(directory)) {
            ctms = residentCareTeamMemberDao.findAll(specifications.familyClientCtm(residentId), pageable);
        } else if ("CARE_PROVIDER".equalsIgnoreCase(directory)) {
            ctms = residentCareTeamMemberDao.findAll(specifications.careProviderClientCtm(residentId), pageable);
        } else if (null == directory) {
            ctms = residentCareTeamMemberDao.findAll(specifications.clientCtm(residentId), pageable);
        } else {
            ctms = Page.empty();
        }

        return ctms.map(this::convert);
    }

    @Override
    public CareteamMemberDto get(Long residentId, Long contactId) {
        logger.info("Get resident CTM details for resident [{}], contact [{}]", residentId, contactId);
        residentsService.checkAccessOrThrow(residentId);
        var ctm = residentCareTeamMemberDao.getOne(contactId);
        if (!residentId.equals(ctm.getClientId())) {
            throw new PhrException(PhrExceptionType.CTM_NOT_ASSOCIATED);
        }

        return convertDetailed(ctm);
    }

    private CareteamMemberBriefDto convert(ClientCareTeamMember ctm) {
        Validate.notNull(ctm);

        CareteamMemberBriefDto dto = new CareteamMemberBriefDto();
        dto.setId(ctm.getId());
        dto.setEmployeeId(ctm.getEmployee().getId());
        dto.setFullName(ctm.getEmployee().getFullName());
        dto.setEmergencyContact(Boolean.TRUE.equals(ctm.getEmergencyContact()));
        dto.setCareTeamRole(ctm.getCareTeamRole().getName());
        dto.setContactPhone(PersonUtils.getPersonPhoneValue(ctm.getEmployee().getPerson()));
        dto.setInvitationStatus(convert(ctm.getEmployee().getStatus()));

        return dto;
    }

    private CareteamMemberDto convertDetailed(ClientCareTeamMember ctm) {
        Validate.notNull(ctm);

        CareteamMemberDto dto = new CareteamMemberDto();
        dto.setId(ctm.getId());
        dto.setEmployeeId(ctm.getEmployee().getId());

        Long physicianId = physicianDao.getIdByEmployeeId(ctm.getEmployee().getId());
        dto.setPhysicianId(physicianId);
        dto.setInvitationStatus(convert(ctm.getEmployee().getStatus()));

        PersonDto personDto = dozer.map(ctm.getEmployee().getPerson(), PersonDto.class);

        String personPhone = PersonUtils.getPersonPhoneValue(ctm.getEmployee().getPerson());
        String personEmail = PersonUtils.getPersonTelecomValue(ctm.getEmployee().getPerson(), PersonTelecomCode.EMAIL);
        if (StringUtils.isNotBlank(personPhone)) {
            dto.setContactPhone(personPhone);
        }
        if (StringUtils.isNotBlank(personEmail)) {
            dto.setContactEmail(personEmail);
        }
        if (CollectionUtils.isEmpty(personDto.getNames())) {
            final NameDto name = transformName(ctm.getEmployee());
            if (name != null) {
                personDto.getNames().add(name);
            }
        }
        dto.setPerson(personDto);

        dto.setEmergencyContact(Boolean.TRUE.equals(ctm.getEmergencyContact()));

        if (ctm.getCareTeamRelation() != null) {
            dto.setRelation(ctm.getCareTeamRelation().getCode());
        }
        if (ctm.getCareTeamRelationship() != null) {
            dto.setRelationship(ctm.getCareTeamRelationship().getCode());
        }
        dto.setCareTeamRole(ctm.getCareTeamRole().getName());

        return dto;
    }

    private static InvitationStatus convert(EmployeeStatus status) {
        switch (status) {
            case ACTIVE:
                return InvitationStatus.ACTIVE;
            case PENDING:
                return InvitationStatus.PENDING;
            case EXPIRED:
                return InvitationStatus.EXPIRED;
            default:
                logger.warn("Unknown EmployeeStatus: " + status);
                return null;
        }
    }

    private NameDto transformName(Employee employee) {
        NameDto name = new NameDto();
        name.setFullName(employee.getFullName());
        name.setUseCode("L");

        return StringUtils.isNotBlank(name.getFullName()) ? name : null;
    }

    @Autowired
    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }
}
