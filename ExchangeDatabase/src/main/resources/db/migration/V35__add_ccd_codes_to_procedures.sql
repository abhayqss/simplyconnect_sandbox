SET XACT_ABORT ON
GO

/* Procedure Activity */
ALTER TABLE [dbo].[ProcedureActivity] DROP [FK_jk8heiwios8jn5jwf9c9hyg35]
DROP TABLE ProcedureType
ALTER TABLE [dbo].[ProcedureActivity] DROP COLUMN [procedure_type_id]
ALTER TABLE [dbo].[ProcedureActivity] ADD [procedure_type_code_id] [bigint] NULL
ALTER TABLE [dbo].[ProcedureActivity] ADD FOREIGN KEY([procedure_type_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[ProcedureActivity] ADD [procedure_type_text] [varchar](max) NULL

/* ALTER TABLE [dbo].[ProcedureActivity] DROP COLUMN [status_code]
ALTER TABLE [dbo].[ProcedureActivity] ADD [status_code_id] [bigint] NULL
ALTER TABLE [dbo].[ProcedureActivity] ADD FOREIGN KEY([status_code_id]) REFERENCES [dbo].[CcdCode] ([id])*/

ALTER TABLE [dbo].[ProcedureActivity] DROP COLUMN [value]
ALTER TABLE [dbo].[ProcedureActivity] ADD [value_id] [bigint] NULL
ALTER TABLE [dbo].[ProcedureActivity] ADD FOREIGN KEY([value_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[ProcedureActivity] DROP COLUMN [priority_code]
ALTER TABLE [dbo].[ProcedureActivity] ADD [priority_code_id] [bigint] NULL
ALTER TABLE [dbo].[ProcedureActivity] ADD FOREIGN KEY([priority_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[ProcedureActivity] DROP COLUMN [method_code]
ALTER TABLE [dbo].[ProcedureActivity] ADD [method_code_id] [bigint] NULL
ALTER TABLE [dbo].[ProcedureActivity] ADD FOREIGN KEY([method_code_id]) REFERENCES [dbo].[CcdCode] ([id])

CREATE TABLE [dbo].[ProcedureActivity_BodySiteCode]
(
  [procedure_activity_id] [bigint] NOT NULL,
	[body_site_code_id] [bigint] NOT NULL,
  FOREIGN KEY ([procedure_activity_id]) REFERENCES [dbo].[ProcedureActivity] ([id]),
  FOREIGN KEY ([body_site_code_id]) REFERENCES [dbo].[CcdCode] ([id])
)

/* Product Instance */
ALTER TABLE [dbo].[ProductInstance] DROP COLUMN [device_code]
ALTER TABLE [dbo].[ProductInstance] ADD [device_code_id] [bigint] NULL
ALTER TABLE [dbo].[ProductInstance] ADD FOREIGN KEY([device_code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* Service Delivery Location*/
ALTER TABLE [dbo].[DeliveryLocation] DROP COLUMN [code]
ALTER TABLE [dbo].[DeliveryLocation] ADD [code_id] [bigint] NULL
ALTER TABLE [dbo].[DeliveryLocation] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])
