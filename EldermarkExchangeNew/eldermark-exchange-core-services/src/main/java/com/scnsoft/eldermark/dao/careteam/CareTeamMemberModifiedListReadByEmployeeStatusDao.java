package com.scnsoft.eldermark.dao.careteam;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModifiedListReadByEmployeeStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CareTeamMemberModifiedListReadByEmployeeStatusDao
        extends AppJpaRepository<CareTeamMemberModifiedListReadByEmployeeStatus, Long> {

    @Modifying
    @Query(nativeQuery = true,
            value = "merge into CareTeamMemberModified_ListReadByEmployeeStatus source " +
                    "using (select :employeeId as employee_id, " +
                    "              :clientId   as resident_id) target " +
                    "on source.employee_id = target.employee_id and source.resident_id = target.resident_id " +
                    "when matched then " +
                    "    update " +
                    "    set last_read_update_id = (select max(id) from CareTeamMemberModified) " +
                    "when not matched then " +
                    "    insert (employee_id, resident_id, last_read_update_id) " +
                    "    values (target.employee_id, target.resident_id, (select max(id) from CareTeamMemberModified));")
    void careTeamMemberListViewed(
            @Param("employeeId") Long currentEmployeeId,
            @Param("clientId") Long clientId

    );
}
