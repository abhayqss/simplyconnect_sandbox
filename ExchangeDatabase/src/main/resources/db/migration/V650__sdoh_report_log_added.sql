IF OBJECT_ID('SdohReportLog') is not null
  drop table SdohReportLog
GO

create table SdohReportLog (
  [id]                   bigint       not null identity (1, 1),
  constraint PK_SdohReportLog primary key ([id]),

  [database_id]          bigint       not null,
  constraint FK_SdohReportLog_SourceDatabase_database_id foreign key ([database_id]) references SourceDatabase ([id]),

  [period_start]         datetime2(7) not null,
  [period_end]           datetime2(7) not null,

  [excel_file]           varchar(50),
  [uhc_zip_file]         varchar(50),

  [sent_to_uhc_datetime] datetime2(7)
)
GO


IF COL_LENGTH('SourceDatabase', 'sdoh_reports_enabled') IS NOT NULL
  BEGIN
    alter table SourceDatabase
      drop constraint DF_SourceDatabase_sdoh_reports_enabled;
    alter table SourceDatabase
      drop column sdoh_reports_enabled;
  END
GO

alter table SourceDatabase
  add [sdoh_reports_enabled] bit not null
  CONSTRAINT DF_SourceDatabase_sdoh_reports_enabled DEFAULT (0)
GO

IF COL_LENGTH('SourceDatabase', 'sdoh_submitter_name') IS NOT NULL
  BEGIN
    alter table SourceDatabase
      drop column sdoh_submitter_name;
  END
GO

alter table SourceDatabase
  add [sdoh_submitter_name] varchar(100) null
GO

IF COL_LENGTH('SourceDatabase', 'sdoh_zoneId') IS NOT NULL
  BEGIN
    alter table SourceDatabase
      drop column sdoh_zoneId;
  END
GO

alter table SourceDatabase
  add [sdoh_zoneId] varchar(20);
GO
