if col_length('CareTeamMemberModified', 'removed') is not null
    begin
        alter table CareTeamMemberModified
            drop constraint DF_CareTeamMemberModified_removed_0
        alter table CareTeamMemberModified
            drop column removed
    end
go


alter table CareTeamMemberModified
    add removed bit not null
        constraint DF_CareTeamMemberModified_removed_0 default 0
go

if OBJECT_ID('LatestCareTeamMemberModified') IS NOT NULL
    DROP VIEW LatestCareTeamMemberModified
GO

CREATE view LatestCareTeamMemberModified as
select cc.id,
       cc.care_team_member_id,
       cc.ctm_employee_id,
       cc.resident_id,
       cc.modification_type,
       cc.performed_by_employee_id,
       cc.date_time,
       LatestUpdate.read_by_employee_id
from CareTeamMemberModified cc
         join (select max(ctmm.id) latest_update_id, lur.employee_id read_by_employee_id
               from CareTeamMemberModified ctmm
                        cross join employee_enc e -- cross join is ok because we fetch data for one employee only
               -- so actual amount of data is 1 x CareTeamMemberModified. This is needed to pass employee_id down to
               -- last_ctm_update_read which will give either latest read ctm update or 0 as latest read update if
               -- no reads have occurred yet
                        cross apply last_ctm_update_read(ctmm.care_team_member_id, ctmm.resident_id, e.id) lur
               where lur.last_read_update_id < ctmm.id
                 AND ctmm.removed = 0
               group by ctmm.care_team_member_id, lur.employee_id) LatestUpdate
              on latest_update_id = cc.id
go

alter table CareTeamMemberModified
    alter column performed_by_employee_id bigint null
GO
