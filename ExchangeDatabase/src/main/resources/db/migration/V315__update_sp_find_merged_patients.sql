SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF (OBJECT_ID('[dbo].[find_merged_patients]') IS NOT NULL)
  DROP PROCEDURE [dbo].[find_merged_patients];
GO

CREATE PROCEDURE [dbo].[find_merged_patients]
    @ResidentId BIGINT
AS
  BEGIN
    SET NOCOUNT ON;

    DECLARE @found_residents TABLE(
      resident_id BIGINT
    );

    INSERT INTO @found_residents(resident_id)
      SELECT mpi.[surviving_resident_id]
      FROM [dbo].[MPI_merged_residents] mpi
      WHERE mpi.[merged_resident_id] = @ResidentId
      UNION
      SELECT mpi.[merged_resident_id]
      FROM [dbo].[MPI_merged_residents] mpi
      WHERE mpi.[surviving_resident_id] = @ResidentId
      UNION
      SELECT @ResidentId;

    SELECT resident_id
    FROM @found_residents;
  END;
GO
