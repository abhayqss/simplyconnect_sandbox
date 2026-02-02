ALTER TABLE [dbo].[Person] ALTER COLUMN legacy_id varchar(25) NOT NULL;
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[Name]') AND name = N'UQ_Name_legacy')
ALTER TABLE [dbo].[Name] DROP CONSTRAINT [UQ_Name_legacy]
GO

ALTER TABLE [dbo].[Name] ALTER COLUMN legacy_id varchar(25) NOT NULL;
GO

ALTER TABLE [dbo].[Name] ADD  CONSTRAINT [UQ_Name_legacy] UNIQUE NONCLUSTERED
(
	[database_id] ASC,
	[legacy_table] ASC,
	[legacy_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[Name]') AND name = N'UQ_Name_legacy')
ALTER TABLE [dbo].[Name] DROP CONSTRAINT [UQ_Name_legacy]
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[PersonTelecom]') AND name = N'UQ_PersonTelecom_legacy')
ALTER TABLE [dbo].[PersonTelecom] DROP CONSTRAINT [UQ_PersonTelecom_legacy]
GO

ALTER TABLE [dbo].[PersonTelecom] ALTER COLUMN legacy_id varchar(25) NOT NULL;
GO

ALTER TABLE [dbo].[PersonTelecom] ADD  CONSTRAINT [UQ_PersonTelecom_legacy] UNIQUE NONCLUSTERED
(
	[database_id] ASC,
	[legacy_table] ASC,
	[legacy_id] ASC,
	[sync_qualifier] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[PersonAddress]') AND name = N'UQ_PersonAddress_legacy')
ALTER TABLE [dbo].[PersonAddress] DROP CONSTRAINT [UQ_PersonAddress_legacy]
GO

ALTER TABLE [dbo].[PersonAddress] ALTER COLUMN legacy_id varchar(25) NOT NULL;
GO

ALTER TABLE [dbo].[PersonAddress] ADD  CONSTRAINT [UQ_PersonAddress_legacy] UNIQUE NONCLUSTERED
(
	[database_id] ASC,
	[legacy_table] ASC,
	[legacy_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO