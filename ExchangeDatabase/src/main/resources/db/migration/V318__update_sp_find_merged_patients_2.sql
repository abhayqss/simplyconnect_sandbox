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
        -- filter out residents not eligible for discovery
        INNER JOIN resident r ON r.[id] = mpi.[surviving_resident_id] AND r.[opt_out] <> 1
        -- filter out residents from organizations not eligible for discovery by Exchange user
        INNER JOIN Organization o ON o.[id] = r.[facility_id] AND o.[testing_training] <> 1 AND o.[inactive] <> 1 AND o.[module_hie] = 1 AND o.[legacy_table] = 'Company'
      WHERE mpi.[merged_resident_id] = @ResidentId AND mpi.[merged] = 1
      UNION
      SELECT mpi.[merged_resident_id]
      FROM [dbo].[MPI_merged_residents] mpi
        -- filter out residents not eligible for discovery
        INNER JOIN resident r ON r.[id] = mpi.[merged_resident_id] AND r.[opt_out] <> 1
        -- filter out residents from organizations not eligible for discovery by Exchange user
        INNER JOIN Organization o ON o.[id] = r.[facility_id] AND o.[testing_training] <> 1 AND o.[inactive] <> 1 AND o.[module_hie] = 1 AND o.[legacy_table] = 'Company'
      WHERE mpi.[surviving_resident_id] = @ResidentId AND mpi.[merged] = 1
      UNION
      SELECT @ResidentId;

    SELECT resident_id
    FROM @found_residents;
  END;
GO
