package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.entity.CommunitySecurityAwareEntity;
import com.scnsoft.eldermark.entity.IncidentReportStatus;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.*;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@Service("incidentReportSecurityService")
@Transactional(readOnly = true)
public class IncidentReportSecurityServiceImpl extends BaseSecurityService implements IncidentReportSecurityService {

    private static final Set<Permission> MANAGE_IR_PERMISSIONS = EnumSet.of(
            Permission.IR_MANAGE_OPTED_IN_IF_QA,
            Permission.IR_MANAGE_IF_ASSOCIATED_ORGANIZATION_AND_QA,
            Permission.IR_MANAGE_IF_ASSOCIATED_COMMUNITY_AND_QA);

    @Autowired
    private EventSecurityService eventSecurityService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private IncidentPictureService incidentPictureService;

    @Override
    public boolean canViewList() {
        return eventSecurityService.canViewList() && canManageIr();
    }

    @Override
    public boolean canView(Long id) {
        var incidentReportSecurityAware = incidentReportService.findSecurityAwareEntity(id);
        return hasAccessByEventId(incidentReportSecurityAware.getEventId());
    }

    @Override
    public boolean hasAccessByEventId(Long eventId) {
        return hasAccessToIrByEventId(eventId) && eventSecurityService.canView(eventId);
    }

    @Override
    public boolean canViewByClient(Long clientId) {
        var clientSecurityAware = clientService.findSecurityAwareEntity(clientId);
        var communitySecurityAware = communityService.findById(clientSecurityAware.getCommunityId(),
                IrCommunitySecurityAwareEntity.class);
        return clientSecurityService.canView(clientId) && hasAccessToIrByCommunity(communitySecurityAware);
    }

    @Override
    public boolean canDelete(Long id) {
        var incidentReportSecurityAware = incidentReportService.findSecurityAwareEntity(id);
        var isDraft = Optional.ofNullable(incidentReportSecurityAware.getStatus())
                .orElseGet(() -> incidentReportSecurityAware.getSubmitted() ? IncidentReportStatus.SUBMITTED : IncidentReportStatus.DRAFT) == IncidentReportStatus.DRAFT;
        return isDraft && hasAccessByEventId(incidentReportSecurityAware.getEventId());
    }

    @Override
    public boolean canViewIncidentPicture(Long pictureId) {
        var incidentPictureSecurityAware = incidentPictureService.findSecurityAware(pictureId);
        return canView(incidentPictureSecurityAware.getIncidentReportId());
    }

    private boolean hasAccessToIrByEventId(Long eventId) {
        var event = eventService.findSecurityAwareEntity(eventId);
        var client = clientService.findSecurityAwareEntity(event.getClientId());
        var community = communityService.findById(client.getCommunityId(),
                IrCommunitySecurityAwareEntity.class);
        return hasAccessToIrByCommunity(community);
    }

    private boolean hasAccessToIrByCommunity(IrCommunitySecurityAwareEntity community) {
        if (BooleanUtils.isNotTrue(community.getIrEnabled())) {
            return false;
        }

        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(Permission.IR_MANAGE_OPTED_IN_IF_QA)) {
            return true;
        }

        if (permissionFilter.hasPermission(Permission.IR_MANAGE_IF_ASSOCIATED_ORGANIZATION_AND_QA)) {
            var employees = permissionFilter.getEmployees(Permission.IR_MANAGE_IF_ASSOCIATED_ORGANIZATION_AND_QA);
            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(Permission.IR_MANAGE_IF_ASSOCIATED_COMMUNITY_AND_QA)) {
            var employees = permissionFilter.getEmployees(Permission.IR_MANAGE_IF_ASSOCIATED_COMMUNITY_AND_QA);
            if (isAnyCreatedUnderCommunity(employees, community.getId())) {
                return true;
            }
        }

        return false;
    }

    private boolean canManageIr() {
        var permissionFilter = currentUserFilter();
        return permissionFilter.hasAnyPermission(MANAGE_IR_PERMISSIONS);
    }

    interface IrCommunitySecurityAwareEntity extends CommunitySecurityAwareEntity {
        Boolean getIrEnabled();
    }
}
