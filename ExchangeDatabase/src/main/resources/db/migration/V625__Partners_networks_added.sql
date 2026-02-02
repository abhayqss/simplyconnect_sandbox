if (object_id('drop_fk_constraints') is not null)
  drop procedure [dbo].[drop_fk_constraints]
go

CREATE PROCEDURE [dbo].[drop_fk_constraints]
    @table varchar(100)
AS
  BEGIN
    SET NOCOUNT ON;
    declare @sql nvarchar(max)

    select @sql = STUFF(
        (SELECT ' ALTER TABLE [' + OBJECT_SCHEMA_NAME(parent_object_id) +
                '].[' + OBJECT_NAME(parent_object_id) +
                '] DROP CONSTRAINT [' + name + '];'
         FROM sys.foreign_keys
         WHERE referenced_object_id = object_id(@table)
         FOR XML PATH ('')
        ), 1, 1, ''
    )
    FROM sys.foreign_keys
    group by referenced_object_id

    print @sql
    EXEC sp_executesql @sql;
  END
go

IF (OBJECT_ID('PartnerNetwork_SourceDatabase') is not null)
  drop table PartnerNetwork_SourceDatabase
go

IF (OBJECT_ID('PartnerNetwork_Organization') is not null)
  drop table PartnerNetwork_Organization
go

if (object_id('PartnerNetwork') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'PartnerNetwork'
    drop table PartnerNetwork
  end
go


create table PartnerNetwork (
  [id]          bigint       not null identity (1, 1),
  [name]        varchar(256) not null, --unique?
  [is_public]   bit          not null default (0),
  [description] varchar(max),

  constraint PK_PartnerNetwork primary key (id)
)
GO


--if user selected 'all' communities in organization
create table PartnerNetwork_SourceDatabase (
  [partner_network_id] bigint not null,
  constraint FK_PartnerNetwork_SourceDatabase_PartnerNetwork foreign key ([partner_network_id]) references PartnerNetwork ([id]),

  [database_id]        bigint not null,
  constraint FK_PartnerNetwork_SourceDatabase_SourceDatabase foreign key ([database_id]) references SourceDatabase ([id])
)
GO


create table PartnerNetwork_Organization (
  [partner_network_id] bigint not null,
  constraint FK_PartnerNetwork_Organization_PartnerNetwork foreign key ([partner_network_id]) references PartnerNetwork ([id]),

  [organization_id]    bigint not null,
  constraint FK_PartnerNetwork_Organization_Organization foreign key ([organization_id]) references Organization ([id])
)
GO
