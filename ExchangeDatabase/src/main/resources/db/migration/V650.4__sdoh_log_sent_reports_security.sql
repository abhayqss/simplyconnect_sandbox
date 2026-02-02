IF EXISTS(SELECT *
          FROM sys.indexes
          WHERE name = 'IX_SdohReportRowData_service_plan_id' AND object_id = OBJECT_ID('dbo.SdohReportRowData'))
  drop index IX_SdohReportRowData_service_plan_id on SdohReportRowData
GO

create index IX_SdohReportRowData_service_plan_id
  on SdohReportRowData (service_plan_id)
go

IF COL_LENGTH('SdohReportLog', 'last_zip_download_at') IS NOT NULL
  BEGIN
    alter table SdohReportLog
      drop column last_zip_download_at;
  END
GO

IF COL_LENGTH('SdohReportLog', 'last_zip_download_submitter_name') IS NOT NULL
  BEGIN
    alter table SdohReportLog
      drop column last_zip_download_submitter_name;
  END
GO

alter table SdohReportLog
  add last_zip_download_at datetime2(7),
  last_zip_download_submitter_name varchar(100)
go

declare @new_youk_offset int, @app_offset int;
set @new_youk_offset = -4 --at the moment new york is the only zone used for SDOH
set @app_offset = (select datepart(tz, SYSDATETIMEOFFSET()) / 60)

update SdohReportLog
set last_zip_download_at = DATEADD(hh, @app_offset - @new_youk_offset,
                                   CONVERT(datetime2(7), STUFF(
                                       STUFF(
                                           STUFF(
                                               STUFF(
                                                   substring(
                                                       uhc_zip_file,
                                                       len(uhc_zip_file) - 3 - 15,
                                                       15)
                                                   , 14, 0, ':')
                                               , 12, 0, ':')
                                           , 7, 0, '-')
                                       , 5, 0, '-'
                                   ), 126)),
  last_zip_download_submitter_name = substring(uhc_zip_file, 0, charindex('_', uhc_zip_file))
  where uhc_zip_file is not null
go
