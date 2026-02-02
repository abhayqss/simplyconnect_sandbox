-- --- Changes Description
--
-- (1) create abstract parent table AnyCcdCode for all CCD codes
-- (2) copy all existing IDs from CcdCode to AnyCcdCode
-- (3) change all foreign key references from CcdCode to AnyCcdCode
-- (4) alter CcdCode to reference AnyCcdCode as its parent
-- (5) rename CcdCode table to ConcreteCcdCode
-- (6) create two other tables (children of AnyCcdCode): UnknownCcdCode and InterpretiveCcdCode
-- (7) create a view CcdCode that unites code values from all these tables


CREATE PROCEDURE #alterAllForeignKeys  -- temp procedure
	@fromTable varchar(255),
	@toTable varchar(255)
AS
BEGIN
	DECLARE @references AS TABLE (
		PKTABLE_QUALIFIER sysname,
		PKTABLE_OWNER sysname,
		PKTABLE_NAME sysname,
		PKCOLUMN_NAME	sysname,
		FKTABLE_QUALIFIER	sysname,
		FKTABLE_OWNER	sysname,
		FKTABLE_NAME sysname,
		FKCOLUMN_NAME	sysname,
		KEY_SEQ	smallint,
		UPDATE_RULE	smallint,
		DELETE_RULE	smallint,
		FK_NAME	sysname,
		PK_NAME	sysname,
		DEFERRABILITY smallint
	);

	DECLARE @code AS TABLE (
		DROP_STMT varchar(max),
		CREATE_STMT varchar(max)
	);

	DECLARE @drop_stmt varchar(max);
	DECLARE @create_stmt varchar(max);

	-- query all foreign keys for a given table
	INSERT INTO @references EXEC sp_fkeys @fromTable;

	-- prepare DROP and ADD statements
	INSERT INTO @code (DROP_STMT, CREATE_STMT)
		SELECT
				'ALTER TABLE [dbo].[' + FKTABLE_NAME + '] DROP CONSTRAINT [' + FK_NAME + '];',
				'ALTER TABLE [dbo].[' + FKTABLE_NAME + '] WITH CHECK ADD CONSTRAINT [FK_' + left(FKTABLE_NAME + '_' + FKCOLUMN_NAME, 124) +
											'] FOREIGN KEY([' + FKCOLUMN_NAME + ']) REFERENCES [dbo].[' + @toTable + '] ([id]);'
		FROM @references
		WHERE FKTABLE_OWNER = 'dbo';

	-- iterate through statements and execute
	DECLARE cur CURSOR FOR SELECT DROP_STMT, CREATE_STMT FROM @code;
	OPEN cur;

	FETCH NEXT FROM cur INTO @drop_stmt, @create_stmt;
	WHILE @@FETCH_STATUS = 0 BEGIN
		EXEC (@drop_stmt);
		EXEC (@create_stmt);
		FETCH NEXT FROM cur INTO @drop_stmt, @create_stmt;
	END;

	CLOSE cur;
	DEALLOCATE cur;
END
GO

SET XACT_ABORT ON
GO

-- (1) create abstract parent table AnyCcdCode for all CCD codes
CREATE TABLE [dbo].[AnyCcdCode] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	PRIMARY KEY (id)
);
GO

-- (2) copy all existing IDs from CcdCode to AnyCcdCode
SET IDENTITY_INSERT [dbo].[AnyCcdCode] ON;
GO
INSERT INTO [dbo].[AnyCcdCode] ([id])
	SELECT [id]
	FROM [dbo].[CcdCode];
GO
SET IDENTITY_INSERT [dbo].[AnyCcdCode] OFF;
GO

-- (3) change all foreign key references from CcdCode to AnyCcdCode
EXEC #alterAllForeignKeys @fromTable = 'CcdCode', @toTable = 'AnyCcdCode';
GO

-- (4) alter CcdCode to reference AnyCcdCode as its parent
-- (4.1) create a duplicate column with a temporary name to store IDs
ALTER TABLE [dbo].[CcdCode] ADD tempId [bigint];
GO
UPDATE [dbo].[CcdCode] SET [dbo].[CcdCode].tempId = [id];
GO

-- (4.2) find the primary key constraint and drop it
DECLARE @pk_constraint sysname;
DECLARE @sql varchar(1024);

SELECT @pk_constraint = CONSTRAINT_NAME
	FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
	WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = 'CcdCode' AND CONSTRAINT_TYPE = 'PRIMARY KEY';

SELECT @sql = 'ALTER TABLE [dbo].[CcdCode] DROP CONSTRAINT ' + @pk_constraint;
EXEC (@sql);
GO

-- (4.3) recreate ID column (without IDENTITY)
ALTER TABLE [dbo].[CcdCode] DROP COLUMN [id];
GO
EXEC sp_rename 'dbo.CcdCode.tempId', 'id', 'COLUMN';
GO
ALTER TABLE [dbo].[CcdCode] ALTER COLUMN [id] [bigint] NOT NULL;
GO
ALTER TABLE [dbo].[CcdCode] ADD CONSTRAINT [PK_ConcreteCcdCode_id] PRIMARY KEY (id);
GO

-- (4.4) Finally, alter CcdCode to reference AnyCcdCode as its parent
ALTER TABLE [dbo].[CcdCode] WITH CHECK ADD CONSTRAINT [FK_ConcreteCcdCode_Parent] FOREIGN KEY([id]) REFERENCES [dbo].[AnyCcdCode] ([id]) ON DELETE CASCADE;
GO

-- (5) rename CcdCode table to ConcreteCcdCode
EXEC sp_rename 'CcdCode', 'ConcreteCcdCode';
GO

-- (6) create two other tables (children of AnyCcdCode): UnknownCcdCode and InterpretiveCcdCode
CREATE TABLE [dbo].[UnknownCcdCode] (
	[id] [bigint] NOT NULL,
	code VARCHAR (25) NOT NULL,
	code_system VARCHAR (40) NOT NULL,
	display_name [varchar] (MAX),
	code_system_name VARCHAR (255)
	PRIMARY KEY ([id]),
	CONSTRAINT [FK_UnknownCcdCode_Parent] FOREIGN KEY ([id]) REFERENCES [dbo].[AnyCcdCode] ([id]) ON DELETE CASCADE
);

CREATE TABLE [dbo].[InterpretiveCcdCode] (
	[id] [bigint] NOT NULL,
	referred_ccd_code [bigint] NOT NULL,
	display_name [varchar] (MAX) NOT NULL,
	PRIMARY KEY ([id]),
	CONSTRAINT [FK_InterpretiveCcdCode_Parent] FOREIGN KEY ([id]) REFERENCES [dbo].[AnyCcdCode] ([id]) ON DELETE CASCADE,
	CONSTRAINT [FK_InterpretiveCcdCode_Original] FOREIGN KEY ([referred_ccd_code]) REFERENCES [dbo].[ConcreteCcdCode] ([id])
);
GO

-- (7) create a view CcdCode that unites code values from all these tables
CREATE VIEW CcdCode
AS
	SELECT
		acc.id,
		ccc.value_set_name,
		ccc.value_set,
		ccc.code,
		ccc.code_system,
		ccc.display_name,
		ccc.inactive,
		ccc.code_system_name
	FROM AnyCcdCode acc
		INNER JOIN ConcreteCcdCode ccc ON acc.id = ccc.id
	UNION
	SELECT
		acc.id,
		NULL AS value_set_name,
		NULL AS value_set,
		ucc.code,
		ucc.code_system,
		ucc.display_name,
		0 AS inactive,
		ucc.code_system_name
	FROM AnyCcdCode acc
		INNER JOIN UnknownCcdCode ucc ON acc.id = ucc.id
	UNION
	SELECT
		acc.id,
		occ.value_set_name,
		occ.value_set,
		occ.code,
		occ.code_system,
		icc.display_name,
		occ.inactive,
		occ.code_system_name
	FROM AnyCcdCode acc
		INNER JOIN InterpretiveCcdCode icc ON acc.id = icc.id
		INNER JOIN ConcreteCcdCode occ ON icc.referred_ccd_code = occ.id;
GO