IF COL_LENGTH('ResidentCareTeamMember', 'on_hold') IS NOT NULL
    BEGIN
        alter table ResidentCareTeamMember
            drop constraint DF_ResidentCareTeamMember_on_hold_0;
        alter table ResidentCareTeamMember
            drop column on_hold;
    END
GO

alter table ResidentCareTeamMember add on_hold bit not null
    constraint DF_ResidentCareTeamMember_on_hold_0 default 0
GO
