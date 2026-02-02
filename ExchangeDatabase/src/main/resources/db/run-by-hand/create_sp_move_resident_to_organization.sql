IF (OBJECT_ID('[dbo].[move_resident_to_organization]') IS NOT NULL)
  DROP PROCEDURE [dbo].[move_resident_to_organization];
GO

/*
### USAGE EXAMPLE ###

DECLARE @DatabaseId BIGINT = (SELECT [id] FROM [dbo].[SourceDatabase] WHERE [name] = 'Futurama');
DECLARE @ResidentId BIGINT;
SELECT @ResidentId = [id] FROM [resident] r WHERE r.[ssn] = '111111111' AND r.[database_id] = @DatabaseId;

EXEC [dbo].[move_resident_to_organization] @ResidentId, 'New Better Place';

*/
CREATE PROCEDURE [dbo].[move_resident_to_organization]
    @ResidentId       BIGINT,
    @OrganizationName VARCHAR(MAX)
AS
  BEGIN
    IF (@ResidentId IS NULL)
      RAISERROR ('The value for @ResidentId should not be NULL', 15, 1);
    IF (@OrganizationName IS NULL)
      RAISERROR ('The value for @OrganizationName should not be NULL', 15, 1);

    DECLARE @DatabaseId BIGINT, @PersonId BIGINT, @OrgId BIGINT;

    SELECT
      @DatabaseId = [database_id],
      @PersonId = [person_id]
    FROM [dbo].[resident_enc]
    WHERE [id] = @ResidentId;

    -- Check that resident exists
    IF (@DatabaseId IS NULL OR @PersonId IS NULL)
      RAISERROR ('Resident (ID=%s) doesn''t exist or has no associated Person record.', 15, 1, @ResidentId);

    SELECT @OrgId = [id]
    FROM [dbo].[Organization]
    WHERE [name] = @OrganizationName AND [database_id] = @DatabaseId;

    -- Check that resident exists
    IF (@OrgId IS NULL)
      RAISERROR ('Organization not found! Check that an organization with the specified @OrganizationName exists and belongs to the same SourceDatabase as the resident being moved.', 15, 1, @ResidentId);

    -- Check that this procedure won't create a duplicated resident in target organization
    -- TODO

    BEGIN TRANSACTION;

    UPDATE [dbo].[resident_enc] SET [facility_id] = @OrgId WHERE [id] = @ResidentId;

    COMMIT TRANSACTION;
  END;
GO
