SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[Privilege] (
  [id]   BIGINT         NOT NULL IDENTITY PRIMARY KEY,
  [name] [VARCHAR](255) NOT NULL
);

CREATE TABLE [dbo].[CareTeamRole_Privilege] (
  [care_team_role_id] BIGINT NOT NULL
    CONSTRAINT [FK_Privilege_CareTeamRole_id] FOREIGN KEY REFERENCES [dbo].[CareTeamRole] ([id])
      ON DELETE CASCADE,
  [privilege_id]      BIGINT NOT NULL
    CONSTRAINT [FK_CareTeamRole_Privilege_id] FOREIGN KEY REFERENCES [dbo].[Privilege] ([id])
      ON DELETE CASCADE
);
GO

INSERT INTO [dbo].[Privilege] ([name]) VALUES ('CARE_TEAM_LIST_INVITE_FRIEND'), ('CARE_TEAM_LIST_INVITE_PHYSICIAN');

INSERT INTO [dbo].[CareTeamRole_Privilege] ([care_team_role_id], [privilege_id])
  (SELECT
     ctr.[id],
     1
   FROM CareTeamRole ctr
   WHERE ctr.[code] IN (
     'ROLE_CASE_MANAGER',
     'ROLE_CARE_COORDINATOR', 'ROLE_PARENT_GUARDIAN',
     'ROLE_PERSON_RECEIVING_SERVICES', 'ROLE_PRIMARY_PHYSICIAN',
     'ROLE_BEHAVIORAL_HEALTH', 'ROLE_SERVICE_PROVIDER',
     'ROLE_ADMINISTRATOR', 'ROLE_SUPER_ADMINISTRATOR',
     'ROLE_COMMUNITY_ADMINISTRATOR'));
-- Note the 'ROLE_COMMUNITY_MEMBERS' role excluded
GO
