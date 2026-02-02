ALTER TABLE [dbo].[CareTeamRole] ADD	[position] [int] NOT NULL DEFAULT 0;
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 1 WHERE [code] = 'ROLE_CASE_MANAGER';
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 2 WHERE [code] = 'ROLE_CARE_COORDINATOR';
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 3 WHERE [code] = 'ROLE_PARENT_GUARDIAN';
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 4 WHERE [code] = 'ROLE_PERSON_RECEIVING_SERVICES';
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 5 WHERE [code] = 'ROLE_PRIMARY_PHYSICIAN';
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 6 WHERE [code] = 'ROLE_BEHAVIORAL_HEALTH';
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 7 WHERE [code] = 'ROLE_COMMUNITY_MEMBERS';
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 8 WHERE [code] = 'ROLE_SERVICE_PROVIDER';
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 9 WHERE [code] = 'ROLE_COMMUNITY_ADMINISTRATOR';
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 10 WHERE [code] = 'ROLE_ADMINISTRATOR';
GO
UPDATE [dbo].[CareTeamRole] SET [position] = 11 WHERE [code] = 'ROLE_SUPER_ADMINISTRATOR';
GO