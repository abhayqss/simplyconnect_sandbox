IF (OBJECT_ID('[dbo].[print_resident_info]') IS NOT NULL)
  DROP PROCEDURE [dbo].[print_resident_info];
GO

CREATE PROCEDURE [dbo].[print_resident_info]
    @ResidentId       BIGINT
AS
  BEGIN
    IF (@ResidentId IS NULL)
      RAISERROR ('The value for @ResidentId should not be NULL', 15, 1);

    DECLARE @DatabaseId BIGINT, @PersonId BIGINT;

    SELECT
      @DatabaseId = [database_id],
      @PersonId = [person_id]
    FROM [dbo].[resident_enc]
    WHERE [id] = @ResidentId;

    -- Check that resident exists
    IF (@DatabaseId IS NULL OR @PersonId IS NULL)
      RAISERROR ('Resident (ID=%s) doesn''t exist or has no associated Person record.', 15, 1, @ResidentId);

    SELECT *
    FROM [dbo].[Participant]
    WHERE resident_id = @ResidentId;

    SELECT *
    FROM [dbo].[Allergy]
    WHERE resident_id = @ResidentId;

    SELECT *
    FROM [dbo].[Author]
    WHERE resident_id = @ResidentId;

    SELECT *
    FROM [dbo].[ResPharmacy]
    WHERE resident_id = @ResidentId;

    SELECT *
    FROM [dbo].[VitalSign]
    WHERE resident_id = @ResidentId;

    SELECT *
    FROM [dbo].[MedicationInformation] mi INNER JOIN Medication M2 ON mi.[id] = M2.[medication_information_id]
    WHERE m2.resident_id = @ResidentId;

    SELECT mp.*
    FROM [dbo].[MedicalProfessional] mp INNER JOIN ResMedProfessional RMP ON mp.[id] = RMP.[med_professional_id]
    WHERE RMP.resident_id = @ResidentId;

    SELECT c.*
    FROM [dbo].[Custodian] c INNER JOIN [dbo].[resident_enc] r ON c.[id] = r.[custodian_id]
    WHERE r.id = @ResidentId;

    SELECT * FROM [dbo].[resident] WHERE [id] = @ResidentId;
  END;
GO
