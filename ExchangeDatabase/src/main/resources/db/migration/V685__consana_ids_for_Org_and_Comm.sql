IF COL_LENGTH('SourceDatabase', 'consana_xowning_id') IS NOT NULL
  BEGIN
    alter table SourceDatabase
      drop column consana_xowning_id;
  END
GO

ALTER TABLE SourceDatabase add [consana_xowning_id] varchar (256) null
go

IF COL_LENGTH('Organization', 'consana_org_id') IS NOT NULL
  BEGIN
    alter table Organization
      drop column consana_org_id;
  END
GO

alter table Organization add [consana_org_id] varchar (256)
go

IF COL_LENGTH('Organization', 'is_consana_initial_sync') IS NOT NULL
  BEGIN
    alter table Organization
      drop column is_consana_initial_sync;
  END
GO

alter table Organization add [is_consana_initial_sync] bit null
GO


EXEC sp_rename 'ConsanaPatientDispatchLog.organization_oid', 'organization_id', 'COLUMN';
EXEC sp_rename 'ConsanaPatientDispatchLog.community_oid', 'community_id', 'COLUMN';

EXEC sp_rename 'ConsanaEventDispatchLog.organization_oid', 'organization_id', 'COLUMN';
EXEC sp_rename 'ConsanaEventDispatchLog.community_oid', 'community_id', 'COLUMN';

EXEC sp_rename 'ConsanaPatientLog.organization_oid', 'organization_id', 'COLUMN';
EXEC sp_rename 'ConsanaPatientLog.community_oid', 'community_id', 'COLUMN';
