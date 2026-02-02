SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

INSERT INTO [dbo].[Privilege] ([name]) VALUES ('CARE_TEAM_EMERGENCY_WRITE');
GO

DECLARE @PrivilegeId BIGINT = (
  SELECT [id]
  FROM [dbo].[Privilege]
  WHERE [name] = 'CARE_TEAM_EMERGENCY_WRITE');

-- logged-in users with any role can mark care team member as an emergency contact
INSERT INTO [dbo].[CareTeamRole_Privilege] ([care_team_role_id], [privilege_id], [care_team_member_role_id])
  (SELECT
     ctr.[id],
     @PrivilegeId,
     NULL -- any CTM role
   FROM CareTeamRole ctr);

GO
