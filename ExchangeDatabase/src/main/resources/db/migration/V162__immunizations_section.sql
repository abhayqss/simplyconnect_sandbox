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

SET @table_name = N'Immunization'
SET @col_name = N'legacy_id'

SELECT @Command = 'ALTER TABLE ' + @table_name + ' DROP CONSTRAINT ' + d.name
    FROM sys.tables t
    JOIN sys.indexes d ON d.object_id = t.object_id  AND d.type=2 and d.is_unique=1
    JOIN sys.index_columns ic on d.index_id=ic.index_id and ic.object_id=t.object_id
    JOIN sys.columns c on ic.column_id = c.column_id  and c.object_id=t.object_id
    WHERE t.name = @table_name and c.name=@col_name

EXEC sp_executesql @Command;

alter table [dbo].[Immunization] alter column legacy_id varchar(32) NOT NULL;
alter table [dbo].[ReactionObservation] alter column legacy_id varchar(32) NOT NULL;
alter table [dbo].[ImmunizationMedicationInformation] add legacy_id varchar(32) NOT NULL;
alter table [dbo].[ImmunizationMedicationInformation] alter column [text] [varchar](MAX) NULL;

ALTER TABLE [dbo].[Immunization] ADD  CONSTRAINT [UQ_Immunization_db_legacy_id] UNIQUE NONCLUSTERED
(
	[database_id] ASC,
	[legacy_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

GO