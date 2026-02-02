DROP TABLE [dbo].[UserMobile]
GO


CREATE TABLE [dbo].[UserMobile](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[token_encoded] [varchar](500) NULL,
	[phr_patient] [bit] NULL,
	[resident_id] [bigint] NULL,
	[ssn] [varchar](9) NULL,
	[phone] [varchar](50) NULL,
	[email] [varchar](70) NULL,
	[active] [bit] NULL,
	[timezone_offset] [int] NULL,
	[registration_code] [int] NULL,
 CONSTRAINT [PK_UserMobile] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[UserMobile]  WITH CHECK ADD  CONSTRAINT [FK_UserMobile_resident_enc] FOREIGN KEY([resident_id])
REFERENCES [dbo].[resident_enc] ([id])
GO

ALTER TABLE [dbo].[UserMobile] CHECK CONSTRAINT [FK_UserMobile_resident_enc]
GO





CREATE TABLE [dbo].[UserResidentRecords](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
	[provider_id] [bigint] NULL,
	[is_current] [bit] NULL,
	[provider_name] [varchar](255) NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[UserResidentRecords]  WITH CHECK ADD  CONSTRAINT [FK_user_resident_records_Organization] FOREIGN KEY([provider_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[UserResidentRecords] CHECK CONSTRAINT [FK_user_resident_records_Organization]
GO

ALTER TABLE [dbo].[UserResidentRecords]  WITH CHECK ADD  CONSTRAINT [FK_user_resident_records_resident_enc] FOREIGN KEY([resident_id])
REFERENCES [dbo].[resident_enc] ([id])
GO

ALTER TABLE [dbo].[UserResidentRecords] CHECK CONSTRAINT [FK_user_resident_records_resident_enc]
GO

ALTER TABLE [dbo].[UserResidentRecords]  WITH CHECK ADD  CONSTRAINT [FK_user_resident_records_UserMobile] FOREIGN KEY([user_id])
REFERENCES [dbo].[UserMobile] ([id])
GO

ALTER TABLE [dbo].[UserResidentRecords] CHECK CONSTRAINT [FK_user_resident_records_UserMobile]
GO


