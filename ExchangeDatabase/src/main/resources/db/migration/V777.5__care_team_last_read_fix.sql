alter table CareTeamMemberModified_ListReadByEmployeeStatus
    alter column last_read_update_id bigint
GO

alter table CareTeamMemberModified_ReadByEmployeeStatus
    alter column last_read_update_id bigint
GO
