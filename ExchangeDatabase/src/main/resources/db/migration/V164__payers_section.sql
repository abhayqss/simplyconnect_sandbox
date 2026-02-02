SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

ALTER TABLE [dbo].[Participant] ADD [legacy_table] [varchar](30) NULL;
GO

ALTER TABLE [dbo].[Payer] ALTER COLUMN [coverage_activity_id] [varchar](MAX) NULL;
GO


INSERT INTO [dbo].[CcdCode]
           ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
     VALUES
           ('CoverageRoleType', '2.16.840.1.113883.1.11.18877', 'SELF', '2.16.840.1.113883.5.111', 'SELF', 0, 'RoleCode')
GO

ALTER TABLE [dbo].[Payer] ADD [legacy_id] [bigint] NOT NULL;
ALTER TABLE [dbo].[Payer] ADD  CONSTRAINT [UQ_Payer_db_legacy_id] UNIQUE NONCLUSTERED
(
	[database_id] ASC,
	[legacy_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

ALTER TABLE [dbo].[CoveragePlanDescription] ADD [legacy_id] [bigint] NOT NULL;
ALTER TABLE [dbo].[CoveragePlanDescription] ADD  CONSTRAINT [UQ_CoveragePlanDescription_db_legacy_id] UNIQUE NONCLUSTERED
(
	[database_id] ASC,
	[legacy_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

ALTER TABLE [dbo].[PolicyActivity] ADD [legacy_id] [bigint] NOT NULL;
ALTER TABLE [dbo].[PolicyActivity] ADD  CONSTRAINT [UQ_PolicyActivity_db_legacy_id] UNIQUE NONCLUSTERED
(
	[database_id] ASC,
	[legacy_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

ALTER TABLE [dbo].[PolicyActivity] ADD [participant_member_id] [varchar](MAX) NULL;

CREATE TABLE [dbo].[ResidentHealthPlan](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
	[resident_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[plan_name] [varchar](MAX) NULL,
	[plan_policy_number] [varchar](MAX) NULL,
	[plan_group_number] [varchar](MAX) NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([resident_id]) REFERENCES [dbo].[Resident] ([id]),
	FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id])
);

ALTER TABLE [dbo].[Resident] DROP COLUMN [health_plan];
GO


-- Drop Participant.legacy_id constraint to be able to alter the column

DECLARE @table_name nvarchar(256)
DECLARE @col_name nvarchar(256)
DECLARE @Command  nvarchar(1000)

SET @table_name = N'Participant'
SET @col_name = N'legacy_id'

SELECT @Command = 'ALTER TABLE ' + @table_name + ' DROP CONSTRAINT ' + d.name
    FROM sys.tables t
    JOIN sys.indexes d ON d.object_id = t.object_id  AND d.type=2 and d.is_unique=1
    JOIN sys.index_columns ic on d.index_id=ic.index_id and ic.object_id=t.object_id
    JOIN sys.columns c on ic.column_id = c.column_id  and c.object_id=t.object_id
    WHERE t.name = @table_name and c.name=@col_name

EXEC sp_executesql @Command;

ALTER TABLE [dbo].[Participant] ADD  CONSTRAINT [UK_Participant_db_legacy_table] UNIQUE NONCLUSTERED
(
	[legacy_id] ASC,
	[database_id] ASC,
	[legacy_table] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

UPDATE Participant SET legacy_table = 'Contact' where legacy_table is null
GO

-- alter ccd stored procedures

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[load_ccd_payer_providers]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['payer_providers_insurance_info'|'payer_providers_insurance_member_id'|'payer_providers_time_heigh'|'payer_providers_time_low']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int
AS
BEGIN

	SET NOCOUNT ON;
	
	-- validate input params
	IF @Offset IS NULL OR @Offset < 0 
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN 
		SET @SortBy = 'payer_providers_time_heigh'
		SET @SortDir = 'DESC'
	END
	
	
	DECLARE @T TABLE (
		payer_providers_id bigint,
		payer_providers_insurance_info nvarchar(255),
		payer_providers_insurance_member_id varchar(MAX),
		payer_providers_time_low datetime2(7),			
		payer_providers_time_heigh datetime2(7)
	)

	-- select data
	INSERT INTO @T 
		SELECT pr.id, o.name, pa.participant_member_id, prt.effective_time_high, prt.effective_time_low
	FROM Payer pr
	LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id 
	LEFT OUTER JOIN Organization o ON pa.payer_org_id = o.id                  
	LEFT OUTER JOIN Participant prt ON pa.participant_id = prt.id
		WHERE pr.resident_id = @ResidentId

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.payer_providers_id, 
		t.payer_providers_insurance_info, 
		t.payer_providers_insurance_member_id, 
		t.payer_providers_time_low, 
		t.payer_providers_time_heigh,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'payer_providers_insurance_info' AND @SortDir = 'ASC'  THEN t.payer_providers_insurance_info END ASC,
					CASE WHEN @SortBy = 'payer_providers_insurance_info' AND @SortDir = 'DESC' THEN t.payer_providers_insurance_info END DESC,
					CASE WHEN @SortBy = 'payer_providers_insurance_member_id' AND @SortDir = 'ASC'  THEN t.payer_providers_insurance_member_id END ASC,
					CASE WHEN @SortBy = 'payer_providers_insurance_member_id' AND @SortDir = 'DESC' THEN t.payer_providers_insurance_member_id END DESC,
					CASE WHEN @SortBy = 'payer_providers_time_low' AND @SortDir = 'ASC'  THEN t.payer_providers_time_low END ASC,
					CASE WHEN @SortBy = 'payer_providers_time_low' AND @SortDir = 'DESC' THEN t.payer_providers_time_low END DESC,
					CASE WHEN @SortBy = 'payer_providers_time_heigh' AND @SortDir = 'ASC'  THEN t.payer_providers_time_heigh END ASC,
					CASE WHEN @SortBy = 'payer_providers_time_heigh' AND @SortDir = 'DESC' THEN t.payer_providers_time_heigh END DESC
			) AS RowNum
		FROM @T AS t
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, payer_providers_insurance_info, payer_providers_insurance_member_id, payer_providers_time_low, payer_providers_time_heigh 
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 
END
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[load_ccd_payer_providers_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;
	
	DECLARE @T TABLE (
		payer_providers_id bigint,
		payer_providers_insurance_info nvarchar(255),
		payer_providers_insurance_member_id varchar(MAX),
		payer_providers_time_low datetime2(7),			
		payer_providers_time_heigh datetime2(7)
	)

	-- select data
	INSERT INTO @T 
		SELECT pr.id, o.name, pa.participant_member_id, prt.effective_time_high, prt.effective_time_low
	FROM Payer pr
	LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id 
	LEFT OUTER JOIN Organization o ON pa.payer_org_id = o.id                 
	LEFT OUTER JOIN Participant prt ON pa.participant_id = prt.id
		WHERE pr.resident_id = @ResidentId

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.payer_providers_id, 
		t.payer_providers_insurance_info, 
		t.payer_providers_insurance_member_id, 
		t.payer_providers_time_low, 
		t.payer_providers_time_heigh
		FROM @T AS t
	)
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable		
END
GO
