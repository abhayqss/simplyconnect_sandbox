SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO

-- create a view AdmitIntakeResidentDates that unites fetching admit and intake dates of resident
CREATE VIEW AdmitIntakeResidentDate
  AS
    SELECT
      rah.id,
      rah.resident_id,
      rah.admit_date as admit_intake_date
    FROM ResidentAdmittanceHistory rah
    WHERE rah.admit_date IS NOT NULL
    UNION ALL
    SELECT
      0             as id,
      r.id          as resident_id,
      r.intake_date as admit_intake_date
    FROM Resident r
    where r.intake_date IS NOT NULL
GO