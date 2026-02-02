SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[PlanOfCareActivity] ALTER COLUMN [mood_code] [varchar](50) NULL

ALTER TABLE [dbo].[PlanOfCareActivity] DROP COLUMN [code]
ALTER TABLE [dbo].[PlanOfCareActivity] ADD [code_id] [bigint] NULL
ALTER TABLE [dbo].[PlanOfCareActivity] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])