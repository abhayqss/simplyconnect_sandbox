SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- Drop Immunization.legacy_id constraint to be able to alter the column

DECLARE @table_name nvarchar(256)
DECLARE @col_name nvarchar(256)
DECLARE @Command  nvarchar(1000)

SET @table_name = N'VitalSignObservation'
SET @col_name = N'legacy_id'

SELECT @Command = 'ALTER TABLE ' + @table_name + ' DROP CONSTRAINT ' + d.name
    FROM sys.tables t
    JOIN sys.indexes d ON d.object_id = t.object_id  AND d.type=2 and d.is_unique=1
    JOIN sys.index_columns ic on d.index_id=ic.index_id and ic.object_id=t.object_id
    JOIN sys.columns c on ic.column_id = c.column_id  and c.object_id=t.object_id
    WHERE t.name = @table_name and c.name=@col_name

EXEC sp_executesql @Command;

alter table [dbo].[VitalSignObservation] alter column [value] float NULL;

ALTER TABLE [dbo].[VitalSignObservation] ADD  CONSTRAINT [UQ_VitalSignObservation_db_legacy_type] UNIQUE NONCLUSTERED
(
	[database_id] ASC,
	[legacy_id] ASC,
	[result_type_code_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

ALTER TABLE [dbo].[VitalSign] ADD [organization_id] [bigint] NULL;
ALTER TABLE [dbo].[VitalSign] ADD CONSTRAINT [FK_VitalSign_organization_id] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id]);
GO

-- Smooth migration to new Vital Sign Mapping

DECLARE @weight_res_type_ccd_id nvarchar(256)
SET @weight_res_type_ccd_id = (SELECT TOP 1 id FROM dbo.CcdCode WHERE code = '3141-9' AND [code_system] = '2.16.840.1.113883.6.1')
UPDATE [VitalSignObservation] SET unit = 'lb_av', [result_type_code_id] = @weight_res_type_ccd_id