SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

DROP TABLE [dbo].[Marketplace_PrimaryFocus];
DROP TABLE [dbo].[Marketplace_LanguageService];
DROP TABLE [dbo].[Marketplace_AgeGroup];
DROP TABLE [dbo].[Marketplace_EmergencyService];
DROP TABLE [dbo].[Marketplace_CommunityType];
DROP TABLE [dbo].[Marketplace_LevelOfCare];
DROP TABLE [dbo].[Marketplace_ServicesTreatmentApproach];
DROP TABLE [dbo].[Marketplace_AncillaryService];
DROP TABLE [dbo].[Marketplace_InNetworkInsurance];
DROP TABLE [dbo].[Marketplace_InsurancePlan];
GO

DROP TABLE [dbo].[PrimaryFocus];
DROP TABLE [dbo].[LanguageService];
DROP TABLE [dbo].[AgeGroup];
DROP TABLE [dbo].[EmergencyService];
DROP TABLE [dbo].[CommunityType];
DROP TABLE [dbo].[LevelOfCare];
DROP TABLE [dbo].[ServicesTreatmentApproach];
DROP TABLE [dbo].[AncillaryService];
DROP TABLE [dbo].[InsurancePlan];
DROP TABLE [dbo].[Marketplace];
GO

DELETE FROM [dbo].[InNetworkInsurance]
WHERE [display_name] <> 'Aetna';
UPDATE [dbo].[InNetworkInsurance] SET [display_name] = 'Aetna Health Insurance' WHERE [display_name] = 'Aetna';
GO

DELETE FROM [dbo].[schema_version]
WHERE ([version] = 334 OR [version] = 340) AND [description] LIKE 'create tables for marketplace';
GO
