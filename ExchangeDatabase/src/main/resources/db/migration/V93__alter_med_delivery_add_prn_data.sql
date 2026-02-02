SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[MedDelivery] ADD
	[prn_reason_given] [varchar](80) NULL,
	[prn_results] [varchar](80) NULL,
	[given_or_recorded_date] [datetime2](7) NULL;
GO
