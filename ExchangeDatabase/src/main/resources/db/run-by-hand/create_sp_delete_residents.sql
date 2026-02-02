IF (OBJECT_ID('[dbo].[delete_residents]') IS NOT NULL)
  DROP PROCEDURE [dbo].[delete_residents];
GO

CREATE TYPE [dbo].[ID_LIST_TABLE_TYPE] AS TABLE(
  [id] BIGINT UNIQUE NOT NULL
);
GO

CREATE PROCEDURE [dbo].[delete_residents]
    @ResidentIds [dbo].[ID_LIST_TABLE_TYPE] READONLY
AS
  SET NOCOUNT ON;
  BEGIN
    BEGIN TRANSACTION;
    DECLARE @res_id BIGINT;
    DECLARE cur CURSOR FOR SELECT [id]
                           FROM @ResidentIds;
    OPEN cur;

    FETCH NEXT FROM cur
    INTO @res_id;
    PRINT 'deleting residents';
    WHILE @@FETCH_STATUS = 0 BEGIN
      PRINT @res_id;
      EXEC [dbo].delete_resident @res_id;
      FETCH NEXT FROM cur
      INTO @res_id;
    END;
    CLOSE cur;
    DEALLOCATE cur;

    IF @@TRANCOUNT > 0
      COMMIT TRANSACTION;
  END;
GO
