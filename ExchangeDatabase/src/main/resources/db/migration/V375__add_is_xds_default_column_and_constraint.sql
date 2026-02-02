ALTER TABLE [dbo].[Organization] ADD [is_xds_default] [bit] NOT NULL DEFAULT (0)
GO

create function [dbo].[isOneXdsDefaultForOrganization](@dbId bigint, @orgId bigint, @xdsdefault bit)
  returns bit
as
  begin
  IF (@xdsdefault is null or @xdsdefault = 'false')
      return 'true';
    declare @count smallint;
    set @count = (select count(o.id)
                  from Organization o
                  where o.database_id = @dbId and is_xds_default = 1 and o.id != @orgId);
    if (@count = 0)
		return 'true';
	return 'false'
  end;
go

alter table Organization
  add constraint DF_One_Xds_Default_for_Organization check (dbo.isOneXdsDefaultForOrganization(database_id, id, is_xds_default) = 'true')
go
