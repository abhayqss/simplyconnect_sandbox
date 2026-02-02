SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[InsurancePlan]
  ADD [is_popular] BIT NOT NULL DEFAULT 0;
ALTER TABLE [dbo].[InNetworkInsurance]
  ADD [is_popular] BIT NOT NULL DEFAULT 0;
GO

UPDATE [dbo].[InNetworkInsurance]
SET [is_popular] = 1
WHERE [display_name] IN ('Aetna', 'Cash or self-payment', 'Medicaid', 'Medicare');

GO