SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[CareTeamRole_Privilege]
  ADD [care_team_member_role_id] BIGINT NULL
  CONSTRAINT [FK_Privilege_CareTeamRole_id_2] FOREIGN KEY REFERENCES [dbo].[CareTeamRole] ([id]);
GO

INSERT INTO [dbo].[Privilege] ([name]) VALUES ('CARE_TEAM_DELETE'), ('CARE_TEAM_DELETE_SELF');
GO

DECLARE @PrivilegeIdDelete BIGINT = (
  SELECT [id]
  FROM [dbo].[Privilege]
  WHERE [name] = 'CARE_TEAM_DELETE');
DECLARE @PrivilegeIdSelfDelete BIGINT = (
  SELECT [id]
  FROM [dbo].[Privilege]
  WHERE [name] = 'CARE_TEAM_DELETE_SELF');

-- logged-in users with any role can delete themselves from care team
INSERT INTO [dbo].[CareTeamRole_Privilege] ([care_team_role_id], [privilege_id], [care_team_member_role_id])
  (SELECT
     ctr.[id],
     @PrivilegeIdSelfDelete,
     NULL -- any CTM role
   FROM CareTeamRole ctr);

INSERT INTO [dbo].[CareTeamRole_Privilege] ([care_team_role_id], [privilege_id], [care_team_member_role_id])
  (SELECT
     ctr.[id],
     @PrivilegeIdDelete,
     ctmr.[id]
   FROM CareTeamRole ctmr
     -- logged-in users with these roles can delete CTMs with any role
     LEFT JOIN CareTeamRole ctr ON ctr.[code] IN (
       'ROLE_CASE_MANAGER',
       'ROLE_CARE_COORDINATOR',
       'ROLE_ADMINISTRATOR', 'ROLE_SUPER_ADMINISTRATOR',
       'ROLE_COMMUNITY_ADMINISTRATOR'));

INSERT INTO [dbo].[CareTeamRole_Privilege] ([care_team_role_id], [privilege_id], [care_team_member_role_id])
  (SELECT
     ctr.[id],
     @PrivilegeIdDelete,
     ctr.[id]
   FROM CareTeamRole ctr
   -- logged-in users with these roles can delete CTMs with the same role
   WHERE ctr.[code] IN ('ROLE_COMMUNITY_MEMBERS', 'ROLE_SERVICE_PROVIDER'));

INSERT INTO [dbo].[CareTeamRole_Privilege] ([care_team_role_id], [privilege_id], [care_team_member_role_id])
  (SELECT
     ctr.[id],
     @PrivilegeIdDelete,
     ctmr.[id]
   FROM CareTeamRole ctmr
     -- logged-in users with these roles
     LEFT JOIN CareTeamRole ctr ON ctr.[code] IN ('ROLE_PRIMARY_PHYSICIAN', 'ROLE_BEHAVIORAL_HEALTH', 'ROLE_SERVICE_PROVIDER')
   -- can delete CTMs with these roles
   WHERE ctmr.[code] IN ('ROLE_PRIMARY_PHYSICIAN', 'ROLE_BEHAVIORAL_HEALTH'));

INSERT INTO [dbo].[CareTeamRole_Privilege] ([care_team_role_id], [privilege_id], [care_team_member_role_id])
  (SELECT
     ctr.[id],
     @PrivilegeIdDelete,
     ctmr.[id]
   FROM CareTeamRole ctmr
     -- logged-in users with these roles
     LEFT JOIN CareTeamRole ctr ON ctr.[code] IN ('ROLE_PARENT_GUARDIAN', 'ROLE_PERSON_RECEIVING_SERVICES', 'ROLE_SERVICE_PROVIDER')
   -- can delete CTMs with these roles
   WHERE ctmr.[code] IN ('ROLE_PARENT_GUARDIAN', 'ROLE_PERSON_RECEIVING_SERVICES'));

GO
