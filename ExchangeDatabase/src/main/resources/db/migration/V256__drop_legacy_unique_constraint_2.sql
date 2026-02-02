IF (OBJECT_ID('tempdb..#dropUniqueConstraint', 'P') IS NULL)
  EXEC ('CREATE PROCEDURE #dropUniqueConstraint AS BEGIN SET NOCOUNT ON; END')
GO

ALTER PROCEDURE #dropUniqueConstraint  -- temp procedure
    @table VARCHAR(255),
    @column VARCHAR(255)
AS
  BEGIN
    DECLARE @name VARCHAR(255);
    DECLARE @sql VARCHAR(1024);

    SELECT @name = CONSTRAINT_NAME
    FROM [INFORMATION_SCHEMA].[KEY_COLUMN_USAGE]
    WHERE TABLE_NAME = @table AND COLUMN_NAME = @column AND (CharIndex('UK_', CONSTRAINT_NAME) = 1);

    IF (@name IS NOT NULL) OR (LEN(@name) > 0)
      BEGIN
        PRINT N'DROP CONSTRAINT ' + @name;
        SELECT @sql = 'ALTER TABLE [' + @table + '] DROP CONSTRAINT ' + @name;
        EXEC (@sql);
      END;
  END;
GO

SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Author] DROP CONSTRAINT [UK_Author_legacy];
ALTER TABLE [dbo].[CoveragePlanDescription] DROP CONSTRAINT [UQ_CoveragePlanDescription_db_legacy_id];
ALTER TABLE [dbo].[Custodian] DROP CONSTRAINT [UQ_Custodian_db_legacy_id];
ALTER TABLE [dbo].[Guardian] DROP CONSTRAINT [UQ_Guardian_db_legacy_id];
ALTER TABLE [dbo].[Immunization] DROP CONSTRAINT [UQ_Immunization_db_legacy_id];
ALTER TABLE [dbo].[Participant] DROP CONSTRAINT [UK_Participant_db_legacy_table];
ALTER TABLE [dbo].[Payer] DROP CONSTRAINT [UQ_Payer_db_legacy_id];
ALTER TABLE [dbo].[PolicyActivity] DROP CONSTRAINT [UQ_PolicyActivity_db_legacy_id];
ALTER TABLE [dbo].[ProblemObservation] DROP CONSTRAINT [UQ_ProblemObservation_legacy];
ALTER TABLE [dbo].[VitalSignObservation] DROP CONSTRAINT [UQ_VitalSignObservation_db_legacy_type];

EXEC #dropUniqueConstraint 'Allergy', 'legacy_id';
EXEC #dropUniqueConstraint 'Encounter', 'legacy_id';
EXEC #dropUniqueConstraint 'Medication', 'legacy_id';
EXEC #dropUniqueConstraint 'MedicalEquipment', 'legacy_id';
EXEC #dropUniqueConstraint 'SocialHistory', 'legacy_id';
EXEC #dropUniqueConstraint 'VitalSign', 'legacy_id';

GO
