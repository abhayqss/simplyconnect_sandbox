package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.entity.externalapi.NucleusInfo;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.externalapi.NucleusInfoService;
import com.scnsoft.eldermark.web.entity.CalleeInfoDto;
import com.scnsoft.eldermark.web.entity.NucleusInfoDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author phomal
 * Created on 4/18/2018
 */
@Service
@Transactional(readOnly = true)
public class VideoCallNucleusService extends BasePhrService {

    private final CareReceiverService careReceiverService;
    private final CareTeamService careTeamService;
    private final CareTeamSecurityUtils careTeamSecurityUtils;
    private final NucleusInfoService nucleusInfoService;
    private final DozerBeanMapper dozer;

    @Autowired
    public VideoCallNucleusService(CareReceiverService careReceiverService, CareTeamService careTeamService,
                                   CareTeamSecurityUtils careTeamSecurityUtils, NucleusInfoService nucleusInfoService, DozerBeanMapper dozer) {
        this.careReceiverService = careReceiverService;
        this.careTeamService = careTeamService;
        this.careTeamSecurityUtils = careTeamSecurityUtils;
        this.nucleusInfoService = nucleusInfoService;
        this.dozer = dozer;
    }

    public List<NucleusInfoDto> listNucleusInfo(AccountType.Type accountType) {
        final Long currentUserId = PhrSecurityUtils.getCurrentUserId();

        final List<NucleusInfoDto> dtos;
        switch (accountType) {
            case CONSUMER:
                final Collection<Long> residentIds = getResidentIdsOrThrow(currentUserId);
                final List<NucleusInfo> infos = nucleusInfoService.findByResidentIds(residentIds);
                dtos = convert(infos);
                break;
            case PROVIDER:
                final Long employeeId = getEmployeeIdOrThrow(currentUserId);
                final String nucleusUserId = nucleusInfoService.findByEmployeeId(employeeId);
                dtos = newDtoListOf(nucleusUserId, employeeId, null);
                break;
            default:
                dtos = Collections.emptyList();
        }

        return dtos;
    }

    public List<NucleusInfoDto> listNucleusInfoForCareReceiver(Long userId, Long receiverId) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        final ResidentCareTeamMember careTeamMember = careReceiverService.getCareReceiverOrThrow(userId, receiverId);
        final String nucleusUserId = nucleusInfoService.findByResidentId(careTeamMember.getResidentId());
        return newDtoListOf(nucleusUserId, null, careTeamMember.getResidentId());
    }

    public List<NucleusInfoDto> listNucleusInfoForCareTeamMember(Long userId, Long contactId, AccountType.Type accountType) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        final ResidentCareTeamMember careTeamMember = careTeamService.getResidentCareTeamMemberOrThrow(userId, contactId);
        final String nucleusUserId = nucleusInfoService.findByEmployeeId(careTeamMember.getEmployee().getId());
        return newDtoListOf(nucleusUserId, careTeamMember.getEmployee().getId(), null);
    }

    public CalleeInfoDto getCalleeInfoByNucleusId(String from, AccountType.Type accountType) {
        // search user by employee
        final List<Long> employeeIds = nucleusInfoService.findEmployeeIdsByNucleusId(from);
        if (CollectionUtils.isNotEmpty(employeeIds)) {
            final List<User> users = userDao.getAllByAutocreatedIsFalseAndEmployeeIdIn(employeeIds);
            // pick any user - test how it works
            if (CollectionUtils.isNotEmpty(users)) {
                return convert(users.get(0));
            }
        }

        // search user by resident
        final List<Long> residentIds = nucleusInfoService.findResidentIdsByNucleusId(from);
        if (CollectionUtils.isNotEmpty(residentIds)) {
            final List<User> users = getUsersByResidentIds(residentIds);
            // pick any user - test how it works
            if (CollectionUtils.isNotEmpty(users)) {
                return convert(users.get(0));
            }
        }

        return null;
    }

    private List<User> getUsersByResidentIds(Collection<Long> residentIds) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }
        final List<Long> userIds = userResidentRecordsDao.getAllUserIdsByResidentIdIn(residentIds);
        return userDao.getAllByAutocreatedIsFalseAndIdIn(userIds);
    }

    private static List<NucleusInfoDto> newDtoListOf(String nucleusUserId, Long employeeId, Long residentId) {
        if (StringUtils.isEmpty(nucleusUserId)) {
            return Collections.emptyList();
        }
        final NucleusInfoDto dto = new NucleusInfoDto();
        dto.setEmployeeId(employeeId);
        dto.setResidentId(residentId);
        dto.setNucleusUserId(nucleusUserId);
        return Arrays.asList(dto);
    }

    private List<NucleusInfoDto> convert(List<NucleusInfo> infos) {
        final List<NucleusInfoDto> dtos = new ArrayList<>();
        for (NucleusInfo info : infos) {
            final NucleusInfoDto dto = dozer.map(info, NucleusInfoDto.class);
            dtos.add(dto);
        }
        return dtos;
    }

    private CalleeInfoDto convert(User user) {
        // residentId is user's main resident ID here, not necessary an ID of Nucleus-synchronized resident
        final CalleeInfoDto dto = dozer.map(user, CalleeInfoDto.class);
        dto.setUserId(user.getId());
        return dto;
    }

}
