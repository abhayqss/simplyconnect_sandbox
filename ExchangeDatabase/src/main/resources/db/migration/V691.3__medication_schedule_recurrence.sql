IF COL_LENGTH('Medication', 'prn_scheduled') IS NOT NULL
  BEGIN
    ALTER TABLE Medication
      DROP COLUMN prn_scheduled
  END
GO

ALTER TABLE Medication
  ADD [prn_scheduled] bit NULL
GO

IF COL_LENGTH('Medication', 'schedule') IS NOT NULL
  BEGIN
    ALTER TABLE [dbo].[Medication] 
      DROP COLUMN schedule;
  END
GO

ALTER TABLE [dbo].[Medication] 
   ADD schedule varchar(100) NULL
GO

IF COL_LENGTH('Medication', 'recurrence') IS NOT NULL
  BEGIN
    ALTER TABLE [dbo].[Medication] 
      DROP COLUMN recurrence;
  END
GO

ALTER TABLE [dbo].[Medication] 
   ADD recurrence varchar(MAX) NULL
GO

