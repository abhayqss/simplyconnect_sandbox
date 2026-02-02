IF COL_LENGTH('SdohReportLog', 'excel_file') IS NOT NULL
  BEGIN
    alter table SdohReportLog
      drop column excel_file;
  END
GO

IF COL_LENGTH('SdohReportLog', 'uhc_zip_file') IS NOT NULL
  BEGIN
    alter table SdohReportLog
      drop column uhc_zip_file;
  END
GO