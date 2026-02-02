SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[PolicyActivity] DROP COLUMN [health_insurance_type_code]
GO
ALTER TABLE [dbo].[PolicyActivity] ADD [health_insurance_type_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[PolicyActivity] ADD FOREIGN KEY([health_insurance_type_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[PolicyActivity] DROP COLUMN [payer_financially_responsible_party_code]
GO
ALTER TABLE [dbo].[PolicyActivity] ADD [payer_financially_responsible_party_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[PolicyActivity] ADD FOREIGN KEY([payer_financially_responsible_party_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

ALTER TABLE [dbo].[Participant] DROP COLUMN [role_code]
GO
ALTER TABLE [dbo].[Participant] ADD [role_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Participant] ADD FOREIGN KEY([role_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[Participant] DROP COLUMN [relationship_code]
GO
ALTER TABLE [dbo].[Participant] ADD [relationship_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Participant] ADD FOREIGN KEY([relationship_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

ALTER TABLE [dbo].[Person] DROP COLUMN [type_code]
GO
ALTER TABLE [dbo].[Person] ADD [type_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Person] ADD FOREIGN KEY([type_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
CREATE TABLE [dbo].[AuthorizationActivity_ClinicalStatement]
(
  [authorization_activity_id] [bigint] NOT NULL,
  [clinical_statement_id] [bigint] NOT NULL,
  FOREIGN KEY ([authorization_activity_id]) REFERENCES [dbo].[AuthorizationActivity] ([id]),
  FOREIGN KEY ([clinical_statement_id]) REFERENCES [dbo].[CcdCode] ([id])
)
GO