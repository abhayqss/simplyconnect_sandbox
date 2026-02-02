-- --- Changes Description
--
-- (1) change all foreign key references from UserMobileProvider to UserMobile
-- (2) rename the primary key (id -> user_id) and the table itself (UserMobileProvider -> UserProfile)
-- (3) create unique index on email
-- (4) rename RECEIVER -> CONSUMER

CREATE PROCEDURE #alterAllForeignKeys2  -- temp procedure
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

-- (1) change all foreign key references from UserMobileProvider to UserMobile
EXEC #alterAllForeignKeys2 @fromTable = 'UserMobileProvider', @toTable = 'UserMobile';
GO

-- (2) rename the primary key column and the table itself
EXEC sp_rename 'UserMobileProvider.id', 'user_id', 'COLUMN';
EXEC sp_rename 'UserMobileProvider', 'UserProfile';
GO

-- (3) create unique index on email
-- (3.1) get rid of duplicates
UPDATE UserMobile
SET email = CAST(id AS VARCHAR(5)) + email
WHERE email_normalized IN (
  SELECT [email_normalized]
  FROM UserMobile
  GROUP BY [email_normalized]
  HAVING COUNT(*) > 1) AND active <> 1;
GO

-- (3.2) recreate unique index
DROP INDEX [dbo].[UserMobile].[UQ_UserMobile_phone_email_normalized];
CREATE UNIQUE INDEX [UQ_UserMobile_email_normalized]
  ON [dbo].[UserMobile] ([email_normalized]);
GO

-- (4) rename RECEIVER -> CONSUMER
UPDATE [dbo].[AccountType]
SET [type] = 'CONSUMER', [name] = 'Consumer'
WHERE [type] = 'RECEIVER' AND [name] = 'Receiver';
GO
