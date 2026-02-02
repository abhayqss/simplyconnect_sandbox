package com.scnsoft.eldermark.service.assessment.arizona;

import com.scnsoft.eldermark.beans.projection.ClientCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.beans.reports.constants.ArizonaMatrixConstants;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.security.projection.PersonTelecomDataAware;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.dao.ClientCareTeamMemberDao;
import com.scnsoft.eldermark.dao.CommunityCareTeamMemberDao;
import com.scnsoft.eldermark.dao.PersonTelecomDao;
import com.scnsoft.eldermark.dao.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientCareTeamMemberSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.CommunityCareTeamMemberSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.PersonTelecomSpecificationGenerator;
import com.scnsoft.eldermark.dto.notification.ArizonaMatrixMonthlyNotificationDto;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ArizonaMatrixMonthlyNotificationServiceImpl implements ArizonaMatrixMonthlyNotificationService {

    private static final Pattern FOLLOW_UP_FIELD_PATTERN = Pattern.compile(
        "\"" + ArizonaMatrixConstants.SURVEY_FREQUENCY + "\"" +
            ":\"" + ArizonaMatrixConstants.FOLLOW_UP_PATTERN.pattern() + "\""
    );
    public static final List<CareTeamRoleCode> NOTIFIABLE_ROLES = List.of(
        CareTeamRoleCode.ROLE_CARE_COORDINATOR,
        CareTeamRoleCode.ROLE_CASE_MANAGER,
        CareTeamRoleCode.ROLE_COMMUNITY_MEMBERS,
        CareTeamRoleCode.ROLE_COMMUNITY_ADMINISTRATOR,
        CareTeamRoleCode.ROLE_ADMINISTRATOR,
        CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR
    );

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Autowired
    private ClientAssessmentResultSpecificationGenerator clientAssessmentResultSpecificationGenerator;

    @Autowired
    private ClientCareTeamMemberDao clientCareTeamMemberDao;

    @Autowired
    private ClientCareTeamMemberSpecificationGenerator clientCareTeamMemberSpecificationGenerator;

    @Autowired
    private CommunityCareTeamMemberDao communityCareTeamMemberDao;

    @Autowired
    private CommunityCareTeamMemberSpecificationGenerator communityCareTeamMemberSpecificationGenerator;

    @Autowired
    private PersonTelecomDao personTelecomDao;

    @Autowired
    private PersonTelecomSpecificationGenerator personTelecomSpecificationGenerator;

    @Value("${portal.url}")
    private String portalUrl;

    @Override
    @Transactional(readOnly = true)
    public List<ArizonaMatrixMonthlyNotificationDto> generateNotifications(Long organizationId, Instant fromDate, Instant toDate) {

        var clients = getClientsWithFollowUpDateInRange(organizationId, toDate);
        var employeeIdToCommunityIdsMap = getEmployeeIdToCommunityIdsMap(clients);

        var employeeIds = employeeIdToCommunityIdsMap.keySet();

        var employeeIdToNameMap = new HashMap<Long, String>();
        var employeeIdToEmailMap = new HashMap<Long, String>();

        getEmployeeData(employeeIds).forEach(it -> {
            var employeeId = it.getPersonEmployeeId();
            var firstName = it.getPersonEmployeeFirstName();
            var lastName = it.getPersonEmployeeLastName();
            var email = it.getValue();
            employeeIdToNameMap.putIfAbsent(employeeId, CareCoordinationUtils.getFullName(firstName, lastName));
            employeeIdToEmailMap.putIfAbsent(employeeId, email);
        });

        return employeeIdToEmailMap.keySet().stream()
            .map(employeeId -> {
                var email = employeeIdToEmailMap.get(employeeId);
                var name = employeeIdToNameMap.get(employeeId);
                var communityIds = employeeIdToCommunityIdsMap.get(employeeId);
                return new ArizonaMatrixMonthlyNotificationDto(
                    name,
                    email,
                    generateLink(organizationId, communityIds, fromDate, toDate)
                );
            })
            .collect(Collectors.toList());
    }

    private List<PersonTelecomDataAware> getEmployeeData(Set<Long> employeeIds) {
        return personTelecomDao.findAll(
            personTelecomSpecificationGenerator.byEmployeeIdIn(employeeIds)
                .and(personTelecomSpecificationGenerator.byCode(PersonTelecomCode.EMAIL)),
            PersonTelecomDataAware.class
        );
    }

    private String generateLink(Long organizationId, Collection<Long> communityIds, Instant from, Instant to) {
        return portalUrl + "reports"
            + "?organizationId=" + organizationId
            // comma at the end is necessary for correct parsing on front-end
            + "&communityIds=" + communityIds.stream().map(it -> it + ",").collect(Collectors.joining())
            + "&reportType=" + ReportType.ARIZONA_MATRIX_MONTHLY.name()
            + "&fromDate=" + DateTimeUtils.toEpochMilli(from)
            + "&toDate=" + DateTimeUtils.toEpochMilli(to)
            + "&export=true";
    }

    private Map<Long, Set<Long>> getEmployeeIdToCommunityIdsMap(List<ClientData> clients) {

        var employeeCommunitiesMap = new HashMap<Long, Set<Long>>();

        var clientIds = clients.stream()
            .map(ClientIdAware::getClientId)
            .collect(Collectors.toSet());

        var clientCareTeamMembers = getClientCareTeamMembers(clientIds);
        clientCareTeamMembers.forEach(ctm ->
            employeeCommunitiesMap.computeIfAbsent(ctm.getEmployeeId(), (k) -> new HashSet<>())
                .add(ctm.getClientCommunityId())
        );

        var clientIdsWithClientCareTeam = clientCareTeamMembers.stream()
            .map(ClientIdAware::getClientId)
            .collect(Collectors.toSet());

        var communityIds = clients.stream()
            .filter(it -> !clientIdsWithClientCareTeam.contains(it.getClientId()))
            .map(ClientCommunityIdAware::getClientCommunityId)
            .collect(Collectors.toSet());

        var communityCareTeamMembers = getCommunityCareTeamMembers(communityIds);
        communityCareTeamMembers.forEach(ctm ->
            employeeCommunitiesMap.computeIfAbsent(ctm.getEmployeeId(), (k) -> new HashSet<>())
                .add(ctm.getCommunityId())
        );

        return employeeCommunitiesMap;
    }

    private List<CommunityCtmData> getCommunityCareTeamMembers(Set<Long> communityIds) {
        var byCommunityId = communityCareTeamMemberSpecificationGenerator.byCommunityIdIn(communityIds);
        var isEmployeeActive = communityCareTeamMemberSpecificationGenerator.isEmployeeActive();
        var byRole = communityCareTeamMemberSpecificationGenerator.byCareTeamRoleCodeIn(NOTIFIABLE_ROLES);

        return communityCareTeamMemberDao.findAll(byCommunityId.and(isEmployeeActive.and(byRole)), CommunityCtmData.class);
    }

    private List<ClientCtmData> getClientCareTeamMembers(Set<Long> clientIds) {
        var ofClients = clientCareTeamMemberSpecificationGenerator.byClientIdIn(clientIds);
        var employeeActive = clientCareTeamMemberSpecificationGenerator.isEmployeeActive();
        var byRole = clientCareTeamMemberSpecificationGenerator.byCareTeamRoleCodeIn(NOTIFIABLE_ROLES);

        return clientCareTeamMemberDao.findAll(ofClients.and(employeeActive.and(byRole)), ClientCtmData.class);
    }

    private List<ClientData> getClientsWithFollowUpDateInRange(Long organizationId, Instant untilDate) {
        var completed = clientAssessmentResultSpecificationGenerator.completed();
        var ofOrganization = clientAssessmentResultSpecificationGenerator.ofOrganization(organizationId);
        var byType = clientAssessmentResultSpecificationGenerator.byType(Assessment.ARIZONA_SSM);
        var unarchived = clientAssessmentResultSpecificationGenerator.isUnarchived();
        var withMaxStartDatePerClient = clientAssessmentResultSpecificationGenerator
            .byMaxDateStartedPerClient(byType.and(completed.and(unarchived)));

        var spec = ofOrganization.and(completed.and(byType.and(unarchived.and(withMaxStartDatePerClient))));
        return clientAssessmentDao.findAll(spec, AssessmentData.class).stream()
            .filter(assessment -> {
                var result = assessment.getResult();
                var matcher = FOLLOW_UP_FIELD_PATTERN.matcher(result);
                if (matcher.find()) {
                    var followUpMonthCount = Integer.parseInt(matcher.group(1));
                    var followUpDate = DateTimeUtils.plusMonths(assessment.getDateStarted(), followUpMonthCount);
                    return followUpDate.isBefore(untilDate);
                } else {
                    return false;
                }
            })
            .collect(Collectors.toList());
    }

    private interface AssessmentData extends ClientData {
        Instant getDateStarted();
        String getResult();
    }

    private interface ClientCtmData extends ClientIdAware, ClientCommunityIdAware, EmployeeIdAware {
    }

    private interface CommunityCtmData extends CommunityIdAware, EmployeeIdAware {
    }

    private interface ClientData extends ClientIdAware, ClientCommunityIdAware {
    }

}
