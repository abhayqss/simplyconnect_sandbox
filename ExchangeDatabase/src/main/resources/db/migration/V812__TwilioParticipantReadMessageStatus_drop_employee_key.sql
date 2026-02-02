IF OBJECT_ID('FK_TwilioParticipantReadMessageStatus_Employee_employee_id') IS NOT NULL
	ALTER TABLE [dbo].[TwilioParticipantReadMessageStatus] DROP CONSTRAINT [FK_TwilioParticipantReadMessageStatus_Employee_employee_id]
GO