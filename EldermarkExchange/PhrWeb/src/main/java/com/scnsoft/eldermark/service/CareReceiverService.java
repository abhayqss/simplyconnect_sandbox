package com.scnsoft.eldermark.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.dao.phr.chat.PhrChatUserDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.chat.PhrChatUser;
import com.scnsoft.eldermark.service.chat.PhrChatService;
import com.scnsoft.eldermark.service.internal.MostSuitableUserSelector;
import com.scnsoft.eldermark.service.palatiumcare.LocationService;
import com.scnsoft.eldermark.service.validation.CareTeamValidator;
import com.scnsoft.eldermark.services.phr.AccessRightsService;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.ccd.NameDto;
import com.scnsoft.eldermark.shared.ccd.PersonDto;
import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;
import com.scnsoft.eldermark.web.entity.CareReceiverDto;
import com.scnsoft.eldermark.web.entity.DataSourceDto;
import com.scnsoft.eldermark.web.entity.PhrChatThreadDto;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author phomal
 * Created on 6/2/2017
 */
@Service
@Transactional
public class CareReceiverService extends BasePhrService {

    private static final Logger logger = LoggerFactory.getLogger(CareReceiverService.class);
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    private HealthProviderService healthProviderService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private PrivilegesService privilegesService;

    @Autowired
    private LocationService locationService;

    private DozerBeanMapper dozer;
    
    @Autowired
    PhrChatService phrChatService;

    @Autowired
    PhrChatUserDao phrChatUserDao;

    @Autowired
    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }

    public CareReceiverDto getCareReceiverByResidentId(Long userId, Long residentId) {
        final Long employeeId = getEmployeeIdOrThrow(userId);
        ResidentCareTeamMember careTeamMember = residentCareTeamMemberDao.getResidentCareTeamMemberByEmployeeIdAndResidentId(employeeId, residentId);
        User userPatient = getUserPatientByCtm(careTeamMember);
        final Boolean canInviteFriend = privilegesService.canInviteFriendToCareTeam();
        NotifyLocationDto locationDto = locationService.getLocationByResidentId(residentId);
        return transform(careTeamMember, userPatient, canInviteFriend, locationDto, null);
    }

    public CareReceiverDto getCareReceiver(Long userId, Long receiverId) {
        // TODO: ease restrictions?
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        final ResidentCareTeamMember careTeamMember = getCareReceiverOrThrow(userId, receiverId);
        User userPatient = getUserPatientByCtm(careTeamMember);
        NotifyLocationDto locationDto = locationService.getLocationByResidentId(careTeamMember.getResidentId());
        final Boolean canInviteFriend = privilegesService.canInviteFriendToCareTeam();

        return transform(careTeamMember, userPatient, canInviteFriend, locationDto, null);
    }

    ResidentCareTeamMember getCareReceiverOrThrow(Long userId, Long receiverId) {
        final Long employeeId = getEmployeeIdOrThrow(userId);
        final ResidentCareTeamMember careTeamMember = residentCareTeamMemberDao.get(receiverId);
        CareTeamValidator.validateCareReceiverAssociationOrThrow(employeeId, careTeamMember);
        return careTeamMember;
    }

    User getUserPatientByCtm(ResidentCareTeamMember careTeamMember) {
        final Set<User> users = healthProviderService.getUsersByResidentId(careTeamMember.getResidentId());
        final User user = MostSuitableUserSelector.selectBySimilarity(users, careTeamMember.getResident());
        // kostyl' #2
        return user == null ? createUserPatientFor(careTeamMember.getResident()) : user;
    }

    /**
     * Automatically create User record for patients (residents) that are not registered in PHR mobile
     * @return New user
     */
    private User createUserPatientFor(Resident resident) {
        User consumer = User.Builder.anUser()
                .withResident(resident)
                .withDatabase(resident.getDatabase())
                .withAutocreated(Boolean.TRUE)
                .withPhrPatient(Boolean.FALSE)
                .build();
        if (consumer.getEmail() == null) {
            consumer.setEmail("");
        }
        if (consumer.getPhone() == null) {
            consumer.setPhone("");
        }

        final User user = userDao.saveAndFlush(consumer);
        healthProviderService.updateUserResidentRecords(user);

        return user;
    }

    public List<CareReceiverDto> getCareReceivers(Long userId, Pageable pageable) {
        return getCareReceivers(userId, pageable, null);
    }

    public List<CareReceiverDto> getCareReceivers(Long userId, Pageable pageable, String phrChatToken) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);

        final Sort sort = new Sort(
                new Sort.Order(Sort.Direction.ASC, "resident.firstName"),
                new Sort.Order(Sort.Direction.ASC, "resident.lastName"));
        final Pageable pageableWithSort;
        if (pageable == null) {
            pageableWithSort = new PageRequest(0, Integer.MAX_VALUE, sort);
        } else {
            pageableWithSort = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        final Long employeeId = getEmployeeIdOrThrow(userId);
        final List<ResidentCareTeamMember> careTeamMembers = residentCareTeamMemberDao.getCareTeamMembersByEmployeeIds(
                Collections.singleton(employeeId), pageableWithSort);
        List<CareReceiverDto> dtos = new ArrayList<>(careTeamMembers.size());
        String phrChatResponse = "";
        if (phrChatToken != null) {
            phrChatResponse = phrChatService.getMessageThread(userId, phrChatToken);
        }
        final Boolean canInviteFriend = privilegesService.canInviteFriendToCareTeam();
        for (ResidentCareTeamMember careTeamMember : careTeamMembers) {
            User userPatient = getUserPatientByCtm(careTeamMember);
            NotifyLocationDto locationDto = careTeamMember != null ?
                    locationService.getLocationByResidentId(careTeamMember.getResidentId()) : null;
            CareReceiverDto dto = transform(careTeamMember, userPatient, canInviteFriend, locationDto, phrChatResponse);
            dtos.add(dto);
        }

        return dtos;
    }

    @Transactional(readOnly = true)
    public Long countCareReceivers(Long userId) {
        final Long employeeId = getEmployeeIdOrThrow(userId);
        return residentCareTeamMemberDao.getCareTeamMembersCountByEmployeeIds(Collections.singleton(employeeId));
    }

    /**
     * @param ctm Care Team Member; not null
     * @param userPatient User; not null
     */
    private CareReceiverDto transform(ResidentCareTeamMember ctm, User userPatient, Boolean canInviteFriend, NotifyLocationDto locationDto, String phrChatResponse) {
        CareReceiverDto dto = new CareReceiverDto();
        dto.setId(ctm.getId());

        dto.setAccessRights(AccessRightsService.getAccessRights(ctm));
        dto.setCareTeamRole(ctm.getCareTeamRole().getCode());
        if (ctm.getCareTeamRelation() != null) {
            dto.setRelation(ctm.getCareTeamRelation().getCode());
        }

        final Resident ctmResident = ctm.getResident();
        dto.setAge(ctmResident.getAge());
        dto.setSsnLastFourDigits(ctmResident.getSsnLastFourDigits());
        final Organization facility = ctmResident.getFacility();
        if (facility != null) {
            dto.setCommunity(facility.getName());
            dto.setCommunityId(facility.getId());
        }

        dto.setUserId(userPatient.getId());
        dto.setPhotoUrl(avatarService.getPhotoUrl(userPatient.getId()));
        final String email = userPatient.getResidentEmailLegacy();
        if (StringUtils.isNotBlank(email)) {
            dto.setContactEmail(email);
        }
        final String phone = userPatient.getResidentPhoneLegacy();
        if (StringUtils.isNotBlank(phone)) {
            dto.setContactPhone(phone);
        }

        if (ctmResident.getGender() != null) {
            dto.setGender(Gender.getGenderByCode(ctmResident.getGender().getCode()));
        }
        final PersonDto personDto = dozer.map(ctmResident.getPerson(), PersonDto.class);
        dto.setPerson(personDto);

        //TODO revise dto, temporary solution to get resident names from resident entity instead of person entity
        //no need to use complex dtos from CCD section
        if (CollectionUtils.isNotEmpty(dto.getPerson().getNames())) {
            NameDto nameDto = dto.getPerson().getNames().get(0);
            if (StringUtils.isBlank(nameDto.getFullName())) {
                nameDto.setFullName(ctmResident.getFullName());
            }
        } else {
            NameDto nameDto = new NameDto();
            nameDto.setFullName(ctmResident.getFullName());
            List<NameDto> nameDtos = new ArrayList<>();
            nameDtos.add(nameDto);
            dto.getPerson().setNames(nameDtos);
        }

        dto.setCanInviteFriend(canInviteFriend);

        final DataSourceDto dataSourceDto = DataSourceService.transform(ctmResident.getDatabase(), ctmResident.getId());
        dto.setDataSource(dataSourceDto);

        dto.setLocationDto(locationDto);
	
	Long userCtmId = userPatient.getId();
        
        if (userCtmId != null && phrChatUserDao != null) {
        	PhrChatUser chatUser = phrChatUserDao.findByNotifyUserId(userCtmId);
        	if(chatUser!=null) {
        		dto.setChatUserId(chatUser.getId());
                if (StringUtils.isNotEmpty(phrChatResponse)) {
                    try {
                        String responseData = mapper.writeValueAsString(mapper.readValue(phrChatResponse, HashMap.class));
                        String threadsData = mapper.writeValueAsString(mapper.readValue(responseData, HashMap.class).get("threads"));
                        
                        List<PhrChatThreadDto>  listOfThreads = mapper.readValue(threadsData,new TypeReference<List<PhrChatThreadDto>>() {});
                        for (PhrChatThreadDto item : listOfThreads) {
                        	if(userCtmId.equals(item.getNotifyUserId())) {
                        		String response = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(item);
                        		dto.setChatThread(response);
                        	}
                        }
                    } catch (Exception e) {
                    	logger.error("Node chat data {}",e);
                    }
                }
        	}
                        
        }

        return dto;
    }

}