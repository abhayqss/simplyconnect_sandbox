IF OBJECT_ID('FK_ClientAppointmentNotification_Appointment') IS NOT NULL
	ALTER TABLE [dbo].[ClientAppointmentNotification] DROP CONSTRAINT [FK_ClientAppointmentNotification_Appointment]
GO

IF OBJECT_ID('FK_ClientAppointmentNotification_ResidentAppointment') IS NOT NULL
	ALTER TABLE [dbo].[ClientAppointmentNotification] DROP CONSTRAINT [FK_ClientAppointmentNotification_ResidentAppointment]
GO


IF OBJECT_ID('FK_DeferredAppointmentNotification_Appointment') IS NOT NULL
	ALTER TABLE [dbo].[DeferredAppointmentNotification] DROP CONSTRAINT [FK_DeferredAppointmentNotification_Appointment]
GO

IF OBJECT_ID('FK_DeferredAppointmentNotification_ResidentAppointment') IS NOT NULL
	ALTER TABLE [dbo].[DeferredAppointmentNotification] DROP CONSTRAINT [FK_DeferredAppointmentNotification_ResidentAppointment] 
GO

ALTER TABLE [dbo].[ClientAppointmentNotification]  WITH CHECK ADD  CONSTRAINT [FK_ClientAppointmentNotification_ResidentAppointment] FOREIGN KEY([appointment_id])
REFERENCES [dbo].[ResidentAppointment_enc] ([id])
GO

ALTER TABLE [dbo].[ClientAppointmentNotification] CHECK CONSTRAINT [FK_ClientAppointmentNotification_ResidentAppointment] 
GO

ALTER TABLE [dbo].[DeferredAppointmentNotification]  WITH CHECK ADD  CONSTRAINT [FK_DeferredAppointmentNotification_ResidentAppointment] FOREIGN KEY([appointment_id])
REFERENCES [dbo].[ResidentAppointment_enc] ([id])
GO

ALTER TABLE [dbo].[DeferredAppointmentNotification] CHECK CONSTRAINT [FK_DeferredAppointmentNotification_ResidentAppointment] 
GO
