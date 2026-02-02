SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- add NPI column to Organization
ALTER TABLE [dbo].[Organization] ADD [provider_npi] [varchar](25) NULL;
GO

-- add legacy_id for Custodian
ALTER TABLE [dbo].[Custodian] ADD [legacy_id] [bigint] NOT NULL;
ALTER TABLE [dbo].[Custodian] ADD  CONSTRAINT [UQ_Custodian_db_legacy_id] UNIQUE NONCLUSTERED
(
	[database_id] ASC,
	[legacy_id] ASC
) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

ALTER TABLE [dbo].[Organization] ALTER COLUMN [name] [varchar](MAX) NULL;
GO

ALTER TABLE [dbo].[Resident] ALTER COLUMN [evacuation_status] [varchar](300) NULL;
GO

ALTER TABLE [dbo].[Guardian] ADD [legacy_id] [bigint] NOT NULL;
ALTER TABLE [dbo].[Guardian] ADD  CONSTRAINT [UQ_Guardian_db_legacy_id] UNIQUE NONCLUSTERED
(
	[database_id] ASC,
	[legacy_id] ASC
) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
