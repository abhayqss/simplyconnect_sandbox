IF COL_LENGTH('Medication', 'last_update') IS NOT NULL
  BEGIN
    ALTER TABLE Medication
      DROP COLUMN last_update
  END
GO

ALTER TABLE Medication
  ADD [last_update] varchar(50) NULL
GO

IF COL_LENGTH('Medication', 'stop_delivery_after_date') IS NOT NULL
  BEGIN
    ALTER TABLE [dbo].[Medication] 
      DROP COLUMN stop_delivery_after_date;
  END
GO

ALTER TABLE [dbo].[Medication] 
   ADD stop_delivery_after_date datetime2(7) NULL
GO

