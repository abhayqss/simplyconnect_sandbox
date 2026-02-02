IF OBJECT_ID('LatestCareTeamMemberModified') IS NOT NULL
    DROP VIEW LatestCareTeamMemberModified;
GO

IF OBJECT_ID('last_ctm_update_read') IS NOT NULL
    DROP FUNCTION last_ctm_update_read;
GO

IF OBJECT_ID('CareTeamMemberModified_ListReadByEmployeeStatus') IS NOT NULL
    DROP table CareTeamMemberModified_ListReadByEmployeeStatus;
GO

IF OBJECT_ID('CareTeamMemberModified_ReadByEmployeeStatus') IS NOT NULL
    DROP table CareTeamMemberModified_ReadByEmployeeStatus;
GO

IF OBJECT_ID('CareTeamMemberModified') IS NOT NULL
    DROP table CareTeamMemberModified;
GO

create table CareTeamMemberModified
(
    id                       bigint       not null identity,
    constraint PK_CareTamMemberModified primary key (id),

    --intentionally no FK because can be deleted
    care_team_member_id      bigint       not null,

    ctm_employee_id          bigint       not null,
    constraint FK_CareTeamMemberModified_Employee_ctm_employee_id FOREIGN KEY (ctm_employee_id)
        references Employee_enc (id),

    resident_id              bigint       not null,
    constraint FK_CareTeamMemberModified_Resident_ctm_resident_id foreign key (resident_id)
        references resident_enc (id),

    modification_type        varchar(10)  not null,

    performed_by_employee_id bigint       not null,
    constraint FK_CareTeamMemberModified_Employee_ctm_performed_by_employee_id FOREIGN KEY (performed_by_employee_id)
        references Employee_enc (id),

    date_time                datetime2(7) not null
)
GO


create table CareTeamMemberModified_ReadByEmployeeStatus
(
    employee_id         bigint not null,
    constraint FK_CareTeamMemberModified_ReadByEmployeeStatus_Employee_employee_id FOREIGN KEY (employee_id)
        references Employee_enc (id),

    care_team_member_id bigint not null,

    constraint PK_CareTeamMemberModified_ReadByEmployeeStatus PRIMARY KEY (employee_id, care_team_member_id),

    last_read_update_id bigint not null,
    constraint FK_CareTeamMemberModified_ReadByEmployeeStatus_last_read_update_id FOREIGN KEY (last_read_update_id)
        references CareTeamMemberModified (id),
)
GO

create table CareTeamMemberModified_ListReadByEmployeeStatus
(
    employee_id         bigint not null,
    constraint FK_CareTeamMemberModified_ListReadByEmployeeStatus_Employee_employee_id FOREIGN KEY (employee_id)
        references Employee_enc (id),

    resident_id         bigint not null,
    constraint FK_CareTeamMemberModified_ListReadByEmployeeStatus_Resident_resident_id FOREIGN KEY (resident_id)
        references resident_enc (id),

    constraint PK_CareTeamMemberModified_ListReadByEmployeeStatus PRIMARY KEY (employee_id, resident_id),

    last_read_update_id bigint not null,
    constraint FK_CareTeamMemberModified_ListReadByEmployeeStatus_last_read_update_id FOREIGN KEY (last_read_update_id)
        references CareTeamMemberModified (id),
)
GO

create function last_ctm_update_read(
    @ctm_id bigint,
    @resident_id bigint,
    @read_by_employee_id bigint
    )
    RETURNS TABLE
        AS return
        select @read_by_employee_id as                     employee_id,
               @ctm_id              as                     care_team_member_id,
               (SELECT isnull(MAX(last_read_update_id), 0)
                FROM (select last_read_update_id
                      from CareTeamMemberModified_ReadByEmployeeStatus rr
                      where rr.employee_id = @read_by_employee_id
                        and rr.care_team_member_id = @ctm_id
                      union all
                      select last_read_update_id
                      from CareTeamMemberModified_ListReadByEmployeeStatus rrr
                               join MergedResidentsView mrv on rrr.resident_id = mrv.resident_id
                      where rrr.employee_id = @read_by_employee_id
                        and mrv.merged_resident_id = @resident_id)
                         AS AllReads(last_read_update_id)) last_read_update_id
GO

create view LatestCareTeamMemberModified as
select cc.id,
       cc.care_team_member_id,
       cc.ctm_employee_id,
       cc.resident_id,
       cc.modification_type,
       cc.performed_by_employee_id,
       cc.date_time,
       LatestUpdate.read_by_employee_id
from CareTeamMemberModified cc
         join (
    select max(ctmm.id) latest_update_id, lur.employee_id read_by_employee_id
    from CareTeamMemberModified ctmm
             cross join employee_enc e -- cross join is ok because we fetch data for one employee only
    -- so actual amount of data is 1 x CareTeamMemberModified. This is needed to pass employee_id down to
    -- last_ctm_update_read which will give either latest read ctm update or 0 as latest read update if
    -- no reads have occurred yet
             cross apply last_ctm_update_read(ctmm.care_team_member_id, ctmm.resident_id, e.id) lur
    where lur.last_read_update_id < ctmm.id
    group by ctmm.care_team_member_id, lur.employee_id) LatestUpdate
              on latest_update_id = cc.id
