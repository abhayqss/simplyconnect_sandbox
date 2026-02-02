
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
	[registration_code] [int] NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[UserMobile]  WITH CHECK ADD  CONSTRAINT [FK_UserMobile_resident_enc] FOREIGN KEY([resident_id])
REFERENCES [dbo].[resident_enc] ([id])
GO

ALTER TABLE [dbo].[UserMobile] CHECK CONSTRAINT [FK_UserMobile_resident_enc]
GO


