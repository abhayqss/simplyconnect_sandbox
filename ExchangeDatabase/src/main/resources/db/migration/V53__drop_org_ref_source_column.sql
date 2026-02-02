SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[ProfessionalContact] DROP CONSTRAINT [FK_kf1fnxmgtmufya3dy6gow0u5p];
GO

ALTER TABLE [dbo].[ProfessionalContact] DROP COLUMN [org_ref_source_id];
GO

DROP TABLE [dbo].[OrgReferralSourceFacility];
GO

DROP TABLE [dbo].[OrgReferralSource];
GO
