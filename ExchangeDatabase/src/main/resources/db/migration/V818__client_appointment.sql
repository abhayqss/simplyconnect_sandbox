IF (OBJECT_ID('ResidentAppointment') IS NOT NULL)
	DROP VIEW [dbo].[ResidentAppointment]
GO

IF (OBJECT_ID('ResidentAppointment_ServiceProvider') IS NOT NULL)
  DROP TABLE ResidentAppointment_ServiceProvider
GO

IF (OBJECT_ID('ResidentAppointment_Reminder') IS NOT NULL)
  DROP TABLE ResidentAppointment_Reminder
GO

IF (OBJECT_ID('ResidentAppointment_NotificationMethod') IS NOT NULL)
  DROP TABLE ResidentAppointment_NotificationMethod
GO

IF (OBJECT_ID('ResidentAppointment_enc') IS NOT NULL)
  DROP TABLE ResidentAppointment_enc
GO


CREATE TABLE [dbo].[ResidentAppointment_enc](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[title] [varbinary](max) NOT NULL,
	[appointment_status] [varchar](50) NOT NULL,
	[is_public] [bit] NULL,
	[location] [varbinary](max) NOT NULL,
	[appointment_type] [varchar](50) NOT NULL,
	[service_category] [varchar](50) NULL,
	[referral_source] [varbinary](max) NULL,
	[reason_for_visit] [varbinary](max) NULL,
	[directions_instructions] [varbinary](max) NULL,
	[notes] [varbinary](max) NULL,
	[resident_id] [bigint] NOT NULL,
	[creator_id] [bigint] NOT NULL,
	[date_from] [datetime2](7) NOT NULL,
	[date_to] [datetime2](7) NOT NULL,
	[email] [varbinary](max) NULL,
	[phone] [varbinary](max) NULL,
	[chain_id] [bigint] NULL,
	[last_modified_date] [datetime2](7) NOT NULL,
	[status] [varchar](50) NOT NULL,
	[archived] [bit] NOT NULL,
	[cancellation_reason] [varbinary](max) NULL,
 CONSTRAINT [PK_ResidentAppointment_enc] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ResidentAppointment_enc]  WITH CHECK ADD  CONSTRAINT [FK_ResidentAppointment_enc_Employee_enc] FOREIGN KEY([creator_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[ResidentAppointment_enc] CHECK CONSTRAINT [FK_ResidentAppointment_enc_Employee_enc]
GO

ALTER TABLE [dbo].[ResidentAppointment_enc]  WITH CHECK ADD  CONSTRAINT [FK_ResidentAppointment_enc_resident_enc] FOREIGN KEY([resident_id])
REFERENCES [dbo].[resident_enc] ([id])
GO

ALTER TABLE [dbo].[ResidentAppointment_enc] CHECK CONSTRAINT [FK_ResidentAppointment_enc_resident_enc]
GO


CREATE TABLE [dbo].[ResidentAppointment_NotificationMethod](
	[resident_appointment_id] [bigint] NOT NULL,
	[notification_method] [varchar](10) NOT NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ResidentAppointment_NotificationMethod]  WITH CHECK ADD  CONSTRAINT [FK_ResidentAppointment_NotificationMethod_ResidentAppointment_enc] FOREIGN KEY([resident_appointment_id])
REFERENCES [dbo].[ResidentAppointment_enc] ([id])
GO

ALTER TABLE [dbo].[ResidentAppointment_NotificationMethod] CHECK CONSTRAINT [FK_ResidentAppointment_NotificationMethod_ResidentAppointment_enc]
GO


CREATE TABLE [dbo].[ResidentAppointment_Reminder](
	[resident_appointment_id] [bigint] NOT NULL,
	[reminder] [varchar](20) NOT NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ResidentAppointment_Reminder]  WITH CHECK ADD  CONSTRAINT [FK_ResidentAppointment_Reminder_ResidentAppointment_enc] FOREIGN KEY([resident_appointment_id])
REFERENCES [dbo].[ResidentAppointment_enc] ([id])
GO

ALTER TABLE [dbo].[ResidentAppointment_Reminder] CHECK CONSTRAINT [FK_ResidentAppointment_Reminder_ResidentAppointment_enc]
GO

CREATE TABLE [dbo].[ResidentAppointment_ServiceProvider](
	[resident_appointment_id] [bigint] NOT NULL,
	[employee_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[ResidentAppointment_ServiceProvider]  WITH CHECK ADD  CONSTRAINT [FK_ResidentAppointment_ServiceProvider_Employee_enc] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[ResidentAppointment_ServiceProvider] CHECK CONSTRAINT [FK_ResidentAppointment_ServiceProvider_Employee_enc]
GO

ALTER TABLE [dbo].[ResidentAppointment_ServiceProvider]  WITH CHECK ADD  CONSTRAINT [FK_ResidentAppointment_ServiceProvider_ResidentAppointment_enc] FOREIGN KEY([resident_appointment_id])
REFERENCES [dbo].[ResidentAppointment_enc] ([id])
GO

ALTER TABLE [dbo].[ResidentAppointment_ServiceProvider] CHECK CONSTRAINT [FK_ResidentAppointment_ServiceProvider_ResidentAppointment_enc]
GO

CREATE VIEW [dbo].[ResidentAppointment]
AS
SELECT [id]
      ,CONVERT(VARCHAR(256), DecryptByKey([title]))  [title]
      ,[appointment_status]
      ,[is_public]
      ,CONVERT(VARCHAR(256), DecryptByKey([location]))  [location]
      ,[appointment_type]
      ,[service_category]
      ,CONVERT(VARCHAR(256), DecryptByKey([referral_source]))  [referral_source]
      ,CONVERT(VARCHAR(MAX), DecryptByKey([reason_for_visit])) [reason_for_visit]
      ,CONVERT(VARCHAR(MAX), DecryptByKey([directions_instructions])) [directions_instructions]
      ,CONVERT(VARCHAR(MAX), DecryptByKey([notes])) [notes]
      ,[resident_id]
      ,[creator_id]
      ,[date_from]
      ,[date_to]
      ,CONVERT(VARCHAR(256), DecryptByKey([email]))[email]
      ,CONVERT(VARCHAR(16), DecryptByKey([phone]))[phone]
      ,[chain_id]
      ,[last_modified_date]
      ,[status]
      ,[archived]
	  ,CONVERT(VARCHAR(MAX), DecryptByKey([cancellation_reason]))[cancellation_reason]
  FROM [dbo].[ResidentAppointment_enc]
GO


CREATE TRIGGER [dbo].[ResidentAppointmentInsert]
    ON [dbo].[ResidentAppointment]
    INSTEAD OF INSERT
    AS
BEGIN
INSERT INTO [dbo].[ResidentAppointment_enc]
           ([title]
           ,[appointment_status]
           ,[is_public]
           ,[location]
           ,[appointment_type]
           ,[service_category]
           ,[referral_source]
           ,[reason_for_visit]
           ,[directions_instructions]
           ,[notes]
           ,[resident_id]
           ,[creator_id]
           ,[date_from]
           ,[date_to]
           ,[email]
           ,[phone]
           ,[chain_id]
           ,[last_modified_date]
           ,[status]
           ,[archived]
		   ,[cancellation_reason])
	SELECT  EncryptByKey(Key_GUID('SymmetricKey1'), [title])   [title]
           ,[appointment_status]
           ,[is_public]
           ,EncryptByKey(Key_GUID('SymmetricKey1'), [location])   [location]
           ,[appointment_type]
           ,[service_category]
           ,EncryptByKey(Key_GUID('SymmetricKey1'), [referral_source]) [referral_source]
           ,EncryptByKey(Key_GUID('SymmetricKey1'), [reason_for_visit]) [reason_for_visit]
           ,EncryptByKey(Key_GUID('SymmetricKey1'), [directions_instructions]) [directions_instructions]
           ,EncryptByKey(Key_GUID('SymmetricKey1'), [notes]) [notes]
           ,[resident_id]
           ,[creator_id]
           ,[date_from]
           ,[date_to]
           ,EncryptByKey(Key_GUID('SymmetricKey1'), [email]) [email]
           ,EncryptByKey(Key_GUID('SymmetricKey1'), [phone]) [phone]
           ,[chain_id]
           ,[last_modified_date]
           ,[status]
           ,[archived]
		   ,EncryptByKey(Key_GUID('SymmetricKey1'), [cancellation_reason]) [cancellation_reason]
    FROM inserted
    SELECT @@IDENTITY;
END
GO


CREATE TRIGGER [dbo].[ResidentAppointmentUpdate]
    ON [dbo].[ResidentAppointment]
    INSTEAD OF UPDATE
    AS
BEGIN
    UPDATE ResidentAppointment_enc
	SET [title] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[title])
      ,[appointment_status] = i.[appointment_status]
      ,[is_public] = i.[is_public]
      ,[location] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[location])
      ,[appointment_type] = i.[appointment_type]
      ,[service_category] = i.[service_category]
      ,[referral_source] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[referral_source])
      ,[reason_for_visit] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[reason_for_visit])
      ,[directions_instructions] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[directions_instructions])
      ,[notes] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[notes])
      ,[resident_id] = i.[resident_id]
      ,[creator_id] = i.[creator_id]
      ,[date_from] = i.[date_from]
      ,[date_to] = i.[date_to]
      ,[email] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[email])
      ,[phone] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[phone])
      ,[chain_id] = i.[chain_id]
      ,[last_modified_date] = i.[last_modified_date]
      ,[status] = i.[status]
      ,[archived] = i.[archived]
	  ,[cancellation_reason] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[cancellation_reason])
    FROM inserted i
    WHERE ResidentAppointment_enc.id = i.id;
END
GO