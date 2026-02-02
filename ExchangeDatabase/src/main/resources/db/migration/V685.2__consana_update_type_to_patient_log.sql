IF COL_LENGTH('ConsanaPatientLog', 'api_update_type') IS NOT NULL
  BEGIN
    alter table ConsanaPatientLog
      drop column api_update_type;
  END
GO


ALTER TABLE ConsanaPatientLog
  add [api_update_type] varchar(25)
GO

--drop not null constraints
ALTER TABLE ConsanaPatientLog alter column organization_id varchar(255)
GO

ALTER TABLE ConsanaPatientLog alter column community_id varchar(255)
GO

ALTER TABLE ConsanaEventDispatchLog alter column organization_id varchar(255)
GO

ALTER TABLE ConsanaEventDispatchLog alter column community_id varchar(255)
GO

ALTER TABLE ConsanaPatientDispatchLog alter column organization_id varchar(255)
GO

ALTER TABLE ConsanaPatientDispatchLog alter column community_id varchar(255)
GO
