package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dao.CareTeamRoleDao;
import com.scnsoft.eldermark.dto.CareTeamMemberDto;
import com.scnsoft.eldermark.dto.NotificationsPreferencesDto;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.Responsibility;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberNotificationPreferences;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CareTeamMemberEntityConverter<T extends CareTeamMember> {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CareTeamRoleDao careTeamRoleDao;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ListAndItemConverter<NotificationsPreferencesDto, List<CareTeamMemberNotificationPreferences>> careTeamMemberNotificationsPreferencesEntityConverter;

    protected T setCommonFields(CareTeamMemberDto source, T target) {
        if (source.getId() == null) {
            target.setEmployee(employeeService.getEmployeeById(source.getEmployeeId()));
            target.setCreatedById(loggedUserService.getCurrentEmployee().getId());
        }
        if (target.getCareTeamRole() == null || !Objects.equals(target.getCareTeamRole().getId(), source.getRoleId())) {
            target.setCareTeamRole(careTeamRoleDao.getOne(source.getRoleId()));
        }
        target.setDescription(source.getDescription());
        var notificationPreferences = target.getNotificationPreferences();
        var newNotificationsPreferences = careTeamMemberNotificationsPreferencesEntityConverter.convertList(source.getNotificationsPreferences()).stream().flatMap(List::stream).collect(Collectors.toList());
        newNotificationsPreferences.forEach(careTeamMemberNotificationPreferences -> {
            careTeamMemberNotificationPreferences.setCareTeamMember(target);
        });
        if (CollectionUtils.isEmpty(notificationPreferences)) {
            target.setNotificationPreferences(newNotificationsPreferences);
        } else {
            target.setNotificationPreferences(mergeNotificationPreferences(notificationPreferences, newNotificationsPreferences));
        }
        return target;
    }

    private List<CareTeamMemberNotificationPreferences> mergeNotificationPreferences(List<CareTeamMemberNotificationPreferences> notificationPreferences, List<CareTeamMemberNotificationPreferences> newNotificationsPreferences) {
        Function<CareTeamMemberNotificationPreferences, Triple<Long, NotificationType, Responsibility>> mapPreferencesToKey = careTeamMemberNotificationPreferences -> Triple.of(careTeamMemberNotificationPreferences.getEventType().getId(), careTeamMemberNotificationPreferences.getNotificationType(), careTeamMemberNotificationPreferences.getResponsibility());
        var notificationPreferencesKeys = notificationPreferences.stream().map(mapPreferencesToKey).collect(Collectors.toSet());
        var groupedNewNotificationPreferences = newNotificationsPreferences.stream().collect(Collectors.toMap(mapPreferencesToKey, Function.identity()));
        notificationPreferences.addAll(groupedNewNotificationPreferences.entrySet().stream().filter(tripleListEntry -> !notificationPreferencesKeys.contains(tripleListEntry.getKey())).map(tripleListEntry -> tripleListEntry.getValue()).collect(Collectors.toList()));
        notificationPreferences.removeIf(careTeamMemberNotificationPreferences -> !groupedNewNotificationPreferences.containsKey(mapPreferencesToKey.apply(careTeamMemberNotificationPreferences)));
        return notificationPreferences;
    }

}
