SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[ResidentAssessmentResult] 
	ADD [status] [varchar](50) NULL,
		[last_modified_date] [datetime2](7) NULL,
		[event_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[ResidentAssessmentResult]  WITH CHECK ADD  CONSTRAINT [FK_ResidentAssessmentResult_Event_enc] FOREIGN KEY([event_id])
REFERENCES [dbo].[Event_enc] ([id])
GO

ALTER TABLE [dbo].[ResidentAssessmentResult] CHECK CONSTRAINT [FK_ResidentAssessmentResult_Event_enc]
GO