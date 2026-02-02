SET XACT_ABORT ON
GO

UPDATE [dbo].[AccountType]
SET [type] = 'RECEIVER', [name] = 'Receiver'
WHERE [type] = 'PATIENT' AND [name] = 'Patient';
UPDATE [dbo].[AccountType]
SET [type] = 'PROVIDER', [name] = 'Provider'
WHERE [type] = 'GUARDIAN' AND [name] = 'Guardian';

GO
