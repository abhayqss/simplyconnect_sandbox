SET XACT_ABORT ON
GO

DECLARE @ConstraintName VARCHAR(256)
SET @ConstraintName = (
     SELECT             obj.name
     FROM               sys.columns col

     LEFT OUTER JOIN    sys.objects obj
     ON                 obj.object_id = col.default_object_id
     AND                obj.type = 'D'

     WHERE              col.object_id = OBJECT_ID('Resident')
     AND                obj.name IS NOT NULL
     AND                col.name = 'is_archived'
)

IF(@ConstraintName IS NOT NULL)
BEGIN
    EXEC ('ALTER TABLE [dbo].[Resident] DROP CONSTRAINT ['+@ConstraintName+']')
END

ALTER TABLE [dbo].[Resident] DROP COLUMN is_archived;

GO
