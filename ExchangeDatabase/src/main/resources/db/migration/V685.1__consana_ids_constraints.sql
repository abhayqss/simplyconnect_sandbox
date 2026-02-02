if EXISTS(SELECT *
          FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS
          WHERE CONSTRAINT_NAME = 'CHK_consana_ids_if_enabled_integration')
  alter table Organization
    drop constraint CHK_consana_ids_if_enabled_integration
GO

if OBJECT_ID('isConsanaXOwningIdPresent') is not null
  drop function [dbo].[isConsanaXOwningIdPresent]
GO


create function [dbo].[isConsanaXOwningIdPresent](
  @databaseId bigint
)
  returns bit
as
  begin
    if (@databaseId is null)
      return 'false';

    declare @count smallint;
    set @count = (select count(id)
                  from SourceDatabase
                  where id = @databaseId
                        and consana_xowning_id is not null and consana_xowning_id <> ''
    )

    if (@count > 0)
      return 'true'
    return 'false';
  end;
go

alter table Organization
  add constraint CHK_consana_ids_if_enabled_integration
check (is_consana_enabled = 0 and (is_consana_initial_sync is null or is_consana_initial_sync = 0) or
       (consana_org_id is not null and consana_org_id <> '' and dbo.isConsanaXOwningIdPresent(database_id) = 1))
go
