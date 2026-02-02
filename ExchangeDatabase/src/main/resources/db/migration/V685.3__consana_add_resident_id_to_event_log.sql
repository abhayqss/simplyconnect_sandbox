IF COL_LENGTH('ConsanaEventDispatchLog', 'resident_id') IS NOT NULL
  BEGIN
    alter table ConsanaPatientLog
      drop column resident_id;
  END
GO

ALTER TABLE ConsanaEventDispatchLog
  add [resident_id] bigint
GO
