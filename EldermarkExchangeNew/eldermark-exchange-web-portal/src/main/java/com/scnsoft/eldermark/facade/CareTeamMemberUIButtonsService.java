package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;

/**
 * Service handles UI specific edit and delete buttons display logic. In some cases even if current user can
 * edit or delete care team member, corresponding buttons should not be displayed on UI
 *
 */
public interface CareTeamMemberUIButtonsService {

    boolean canViewList(Long clientId, Long communityId);

    boolean canAdd(Long clientId, Long communityId, CareTeamFilter.Affiliation affiliation);

    boolean canEdit(CareTeamMember careTeamMember, CareTeamFilter filter);

    boolean canDelete(CareTeamMember careTeamMember, CareTeamFilter filter);

}
