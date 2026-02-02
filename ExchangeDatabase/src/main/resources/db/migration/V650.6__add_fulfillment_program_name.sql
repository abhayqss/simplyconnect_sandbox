IF COL_LENGTH('SdohReportRowData', 'referral_fulfillment_program_name') IS NOT NULL
  BEGIN
    alter table SdohReportRowData
      drop column referral_fulfillment_program_name;
  END
GO

alter table SdohReportRowData add referral_fulfillment_program_name varchar(50)
