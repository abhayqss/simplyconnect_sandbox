SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[MarcoIntegrationDocumentsLog]
  DROP CONSTRAINT FK_MarcoIntegrationDocumentsLog_Document
go

DROP TABLE [dbo].[MarcoIntegrationDocumentsLog]


CREATE TABLE [dbo].[MarcoIntegrationDocumentsLog] (
  [id]                     [bigint] IDENTITY (1, 1) NOT NULL,
  [received_timestamp]     [datetime2](7)           NOT NULL,
  [organization_name]      [varchar](255)           NULL,
  [first_name]             [varchar](150)           NULL,
  [last_name]              [varchar](150)           NULL,
  [date_of_birth]          [varchar](100)           NULL,
  [ssn]                    [varchar](100)           NULL, -- can be with dashes
  [file_title]             [varchar](255)           NULL,
  [author]                 [varchar](255)           NULL,
  [document_original_name] [varchar](255)           NULL,
  [document_id]            [bigint]                 NULL,
  [unassigned_reason]      [varchar](30)            NULL,
  PRIMARY KEY ([id])
)
GO

ALTER TABLE [dbo].[MarcoIntegrationDocumentsLog]
  WITH CHECK ADD CONSTRAINT [FK_MarcoIntegrationDocumentsLog_Document] FOREIGN KEY ([document_id])
REFERENCES [dbo].[Document] ([id])
GO

DECLARE @specialMarcoPrivilege bigint
select @specialMarcoPrivilege = id
from Privilege
where name = 'SPECIAL_MARCO'

DELETE FROM UserThirdPartyApplication_Privilege
where privilege_id = @specialMarcoPrivilege
DELETE FROM Privilege
where id = @specialMarcoPrivilege
GO

ALTER TABLE [dbo].[Document]
  ADD [marco_document_log_id] BIGINT NULL,
  CONSTRAINT [FK_Document_MarcoIntegrationDocumentsLog] FOREIGN KEY ([marco_document_log_id])
  REFERENCES [dbo].[MarcoIntegrationDocumentsLog] ([id])
GO