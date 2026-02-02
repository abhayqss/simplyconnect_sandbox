SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF (OBJECT_ID('[dbo].[find_patients_and_merged_patients]') IS NOT NULL)
  DROP PROCEDURE [dbo].[find_patients_and_merged_patients];
GO

CREATE PROCEDURE [dbo].[find_patients_and_merged_patients]
    @DatabaseId BIGINT
AS
  BEGIN
    SET NOCOUNT ON;

    DECLARE @found_residents TABLE(
      resident_id BIGINT
    );

    INSERT INTO @found_residents(resident_id)
      SELECT mpi.[surviving_resident_id]
      FROM [dbo].[MPI_merged_residents] mpi join [dbo].[resident_enc] r on r.id = mpi.merged_resident_id
      WHERE r.[database_id] = @DatabaseId
      UNION
      SELECT mpi.[merged_resident_id]
      FROM [dbo].[MPI_merged_residents] mpi join [dbo].[resident_enc] r on r.id = mpi.surviving_resident_id
      WHERE r.[database_id] = @DatabaseId
      UNION
      SELECT r.[id] from [dbo].[resident_enc] r
	  WHERE r.[database_id] = @DatabaseId;

    SELECT distinct resident_id
    FROM @found_residents;
  END;
GO
