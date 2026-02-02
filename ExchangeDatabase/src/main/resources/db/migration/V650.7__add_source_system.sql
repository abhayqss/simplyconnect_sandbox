IF COL_LENGTH('SourceDatabase', 'sdoh_source_system') IS NOT NULL
  BEGIN
    alter table SourceDatabase
      drop column sdoh_source_system;
  END
GO

alter table SourceDatabase
  add [sdoh_source_system] varchar(10) null
GO

UPDATE SourceDatabase set sdoh_source_system = 'Genacross' where sdoh_submitter_name = 'Lutheran Services America'
go

IF COL_LENGTH('SdohReportRowData', 'source_system') IS NOT NULL
  BEGIN
    alter table SdohReportRowData
      drop column source_system;
  END
GO

alter table SdohReportRowData add source_system varchar(10)
