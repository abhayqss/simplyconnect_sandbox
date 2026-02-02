ALTER TABLE [dbo].[CareTeamRole] ADD	[display_name] [varchar](50);
GO
UPDATE [dbo].[CareTeamRole] SET [display_name] = 'Provider' WHERE [code] = 'ROLE_CASE_MANAGER';
GO
UPDATE [dbo].[CareTeamRole] SET [display_name] = 'Provider' WHERE [code] = 'ROLE_CARE_COORDINATOR';
GO
UPDATE [dbo].[CareTeamRole] SET [display_name] = 'Provider' WHERE [code] = 'ROLE_PRIMARY_PHYSICIAN';
GO
UPDATE [dbo].[CareTeamRole] SET [display_name] = 'Provider' WHERE [code] = 'ROLE_BEHAVIORAL_HEALTH';
GO
UPDATE [dbo].[CareTeamRole] SET [display_name] = 'Provider' WHERE [code] = 'ROLE_COMMUNITY_MEMBERS';
GO
UPDATE [dbo].[CareTeamRole] SET [display_name] = 'Provider' WHERE [code] = 'ROLE_SERVICE_PROVIDER';
GO
UPDATE [dbo].[CareTeamRole] SET [display_name] = 'Community Administrator' WHERE [code] = 'ROLE_COMMUNITY_ADMINISTRATOR';
GO
UPDATE [dbo].[CareTeamRole] SET [display_name] = 'Administrator' WHERE [code] = 'ROLE_ADMINISTRATOR';
GO
UPDATE [dbo].[CareTeamRole] SET [display_name] = 'Super Administrator' WHERE [code] = 'ROLE_SUPER_ADMINISTRATOR';
GO