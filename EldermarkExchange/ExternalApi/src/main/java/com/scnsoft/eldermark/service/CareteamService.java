package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.OrganizationCareTeamMemberDao;
import com.scnsoft.eldermark.dao.phr.PhysicianDao;
import com.scnsoft.eldermark.dao.phr.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.shared.ccd.NameDto;
import com.scnsoft.eldermark.shared.ccd.PersonDto;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.CareteamMemberBriefDto;
import com.scnsoft.eldermark.web.entity.CareteamMemberDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * @author phomal
 * Created on 1/31/2018.
 */
@Service
@Transactional(readOnly = true)
public class CareteamService {

    private final PrivilegesService privilegesService;
    private final ResidentsService residentsService;
    private final PhysicianDao physicianDao;
    private final ResidentCareTeamMemberDao residentCareTeamMemberDao;
    private final OrganizationCareTeamMemberDao organizationCareTeamMemberDao;
    private DozerBeanMapper dozer;

    private final static Logger logger = Logger.getLogger(CareteamService.class.getName());

    public CareteamService(PrivilegesService privilegesService, ResidentsService residentsService, PhysicianDao physicianDao,
                           ResidentCareTeamMemberDao residentCareTeamMemberDao, OrganizationCareTeamMemberDao organizationCareTeamMemberDao) {
        this.privilegesService = privilegesService;
        this.residentsService = residentsService;
        this.physicianDao = physicianDao;
        this.residentCareTeamMemberDao = residentCareTeamMemberDao;
        this.organizationCareTeamMemberDao = organizationCareTeamMemberDao;
    }


    public Page<CareteamMemberBriefDto> listCommunityCTMs(Long communityId, Pageable pageable) {
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        // TODO implement
        return new PageImpl<>(new ArrayList<CareteamMemberBriefDto>());
    }

    public Page<CareteamMemberBriefDto> listResidentCTMs(Long residentId, String directory, Pageable pageable) {
        directory = StringUtils.trimToNull(directory);
        residentsService.checkAccessOrThrow(residentId);

        Page<ResidentCareTeamMember> ctms;
        if ("FAMILY".equalsIgnoreCase(directory)) {
            ctms = residentCareTeamMemberDao.findByResidentIdAndCareTeamRoleCode(residentId, CareTeamRoleCode.ROLE_PARENT_GUARDIAN, pageable);
        } else if ("CARE_PROVIDER".equalsIgnoreCase(directory)) {
            ctms = residentCareTeamMemberDao.findByResidentIdAndCareTeamRoleCodeNot(residentId, CareTeamRoleCode.ROLE_PARENT_GUARDIAN, pageable);
        } else if (null == directory) {
            ctms = residentCareTeamMemberDao.findByResidentId(residentId, pageable);
        } else {
            ctms = new PageImpl<>(Collections.<ResidentCareTeamMember>emptyList());
        }

        return convert(ctms);
    }

    public CareteamMemberDto get(Long residentId, Long contactId) {
        residentsService.checkAccessOrThrow(residentId);
        final ResidentCareTeamMember ctm = residentCareTeamMemberDao.getOne(contactId);
        if (!residentId.equals(ctm.getResidentId())) {
            throw new PhrException(PhrExceptionType.CTM_NOT_ASSOCIATED);
        }

        return convertDetailed(ctm);
    }

    private static Page<CareteamMemberBriefDto> convert(Page<ResidentCareTeamMember> ctms) {
        return ctms.map(new Converter<ResidentCareTeamMember, CareteamMemberBriefDto>() {
            @Override
            public CareteamMemberBriefDto convert(ResidentCareTeamMember source) {
                return CareteamService.convert(source);
            }
        });
    }

    private static CareteamMemberBriefDto convert(ResidentCareTeamMember ctm) {
        Validate.notNull(ctm);

        CareteamMemberBriefDto dto = new CareteamMemberBriefDto();
        dto.setId(ctm.getId());
        dto.setEmployeeId(ctm.getEmployee().getId());
        dto.setFullName(ctm.getEmployee().getFullName());
        dto.setEmergencyContact(Boolean.TRUE.equals(ctm.getEmergencyContact()));
        dto.setCareTeamRole(ctm.getCareTeamRole().getName());
        dto.setContactPhone(PersonService.getPersonPhoneValue(ctm.getEmployee().getPerson()));
        dto.setInvitationStatus(convert(ctm.getEmployee().getStatus()));

        return dto;
    }

    private CareteamMemberDto convertDetailed(ResidentCareTeamMember ctm) {
        Validate.notNull(ctm);

        CareteamMemberDto dto = new CareteamMemberDto();
        dto.setId(ctm.getId());
        dto.setEmployeeId(ctm.getEmployee().getId());

        Long physicianId = physicianDao.getIdByEmployeeId(ctm.getEmployee().getId());
        dto.setPhysicianId(physicianId);
        dto.setInvitationStatus(convert(ctm.getEmployee().getStatus()));

        PersonDto personDto = dozer.map(ctm.getEmployee().getPerson(), PersonDto.class);

        String personPhone = PersonService.getPersonPhoneValue(ctm.getEmployee().getPerson());
        String personEmail = PersonService.getPersonTelecomValue(ctm.getEmployee().getPerson(), PersonTelecomCode.EMAIL);
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
                logger.severe("Unknown EmployeeStatus: " + status);
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
