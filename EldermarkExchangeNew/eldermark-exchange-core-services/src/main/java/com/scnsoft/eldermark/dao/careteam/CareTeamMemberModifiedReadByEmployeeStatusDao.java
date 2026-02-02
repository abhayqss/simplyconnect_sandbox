package com.scnsoft.eldermark.dao.careteam;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModifiedReadByEmployeeStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CareTeamMemberModifiedReadByEmployeeStatusDao
        extends AppJpaRepository<CareTeamMemberModifiedReadByEmployeeStatus, CareTeamMemberModifiedReadByEmployeeStatus.Id> {

    //setting just max(id) CareTeamMemberModified instead of for max(id) for given care team member
    //because when reading updates it is important that read id is just less that new modification update id
    //this also saves us from where condition
    @Modifying
    @Query(nativeQuery = true,
            value = "merge into CareTeamMemberModified_ReadByEmployeeStatus source " +
                    "using (select :employeeId       as employee_id, " +
                    "              :careTeamMemberId as care_team_member_id) target " +
                    "on source.employee_id = target.employee_id and source.care_team_member_id = target.care_team_member_id " +
                    "when matched then " +
                    "    update " +
                    "    set last_read_update_id = (select max(id) from CareTeamMemberModified) " +
                    "when not matched then " +
                    "    insert (employee_id, care_team_member_id, last_read_update_id) " +
                    "    values (target.employee_id, target.care_team_member_id, (select max(id) from CareTeamMemberModified));")
    void careTeamMemberViewed(
            @Param("careTeamMemberId") Long careTeamMemberId,
            @Param("employeeId") Long currentEmployeeId
    );
}
