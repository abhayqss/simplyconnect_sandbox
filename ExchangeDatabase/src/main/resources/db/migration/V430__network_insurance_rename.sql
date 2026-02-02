SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

UPDATE [dbo].[InNetworkInsurance]
   SET [display_name] = 'Cash or self-payment'
 WHERE code = 'CASH_OR_SELF_PAYMENT'
GO