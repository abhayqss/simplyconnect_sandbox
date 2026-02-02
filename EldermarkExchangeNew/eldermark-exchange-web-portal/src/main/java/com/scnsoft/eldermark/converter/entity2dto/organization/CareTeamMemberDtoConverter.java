package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.CareTeamMemberDto;
import com.scnsoft.eldermark.dto.NotificationsPreferencesDto;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberNotificationPreferences;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.service.CareTeamMemberService;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import com.scnsoft.eldermark.service.security.CareTeamSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CareTeamMemberDtoConverter implements Converter<CareTeamMember, CareTeamMemberDto> {

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    @Autowired
    private CareTeamSecurityService careTeamSecurityService;

    @Autowired
    private ListAndItemConverter<List<CareTeamMemberNotificationPreferences>, NotificationsPreferencesDto> notificationsPreferencesDtoConverter;

    @Override
    public CareTeamMemberDto convert(CareTeamMember source) {
        CareTeamMemberDto target = new CareTeamMemberDto();
        target.setId(source.getId());
        if (source instanceof ClientCareTeamMember) {
            target.setClientId(((ClientCareTeamMember) source).getClient().getId());
            target.setIncludeInFaceSheet(((ClientCareTeamMember) source).getIncludeInFaceSheet());
        } else if (source instanceof CommunityCareTeamMember) {
            target.setCommunityId(((CommunityCareTeamMember) source).getCommunity().getId());
        }
        target.setEmployeeId(source.getEmployee().getId());
        target.setEmployeeName(source.getEmployee().getFullName());
        target.setEmployeeOrganizationName(source.getEmployee().getOrganization().getName());
        target.setRoleId(source.getCareTeamRole().getId());
        target.setRoleName(source.getCareTeamRole().getName());
        target.setDescription(source.getDescription());
        target.setCanChangeRole(careTeamSecurityService.canEdit(source.getId(), CareTeamRoleService.ANOTHER_TARGET_ROLE));

        var notificationsGroupedByEventType = source.getNotificationPreferences().stream().collect(Collectors.groupingBy(CareTeamMemberNotificationPreferences::getEventType));
        target.setNotificationsPreferences(notificationsPreferencesDtoConverter.convertList(new ArrayList<>(notificationsGroupedByEventType.values())));
        var defaultReponsibilities = careTeamMemberService.getResponsibilitiesForRole(source.getCareTeamRole().getId());
        target.getNotificationsPreferences().forEach(notificationsPreferencesDto -> {
            defaultReponsibilities.stream().filter(eventTypeCareTeamRoleXref -> eventTypeCareTeamRoleXref.getEventType().getId().equals(notificationsPreferencesDto.getEventTypeId()) &&
                                                    eventTypeCareTeamRoleXref.getResponsibility().name().equals(notificationsPreferencesDto.getResponsibilityName())).findFirst()
                    .ifPresentOrElse(eventTypeCareTeamRoleXref -> notificationsPreferencesDto.setCanEdit(eventTypeCareTeamRoleXref.getResponsibility().isChangeable()), () -> notificationsPreferencesDto.setCanEdit(true));
        });
        return target;
    }

}
