SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[MedDelivery](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[attempts_last_when] [bigint] NULL,
	[given] [bit] NULL,
	[not_given_reason] [varchar](255) NULL,
	[on_hold] [bit] NULL,
	[poured_when] [bigint] NULL,
	[prn] [bit] NULL,
	[scheduled_date] [datetime2](7) NULL,
	[scheduled_earliest_when] [datetime2](7) NULL,
	[scheduled_latest_when] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[medication_id] [bigint] NULL,
	[organization_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[MedDelivery]  WITH CHECK ADD  CONSTRAINT [FK_1lv7mp7ss1jbb8ue2sbg1ohp3] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])
GO

ALTER TABLE [dbo].[MedDelivery] CHECK CONSTRAINT [FK_1lv7mp7ss1jbb8ue2sbg1ohp3]
GO

ALTER TABLE [dbo].[MedDelivery]  WITH CHECK ADD  CONSTRAINT [FK_4kpbc4h2l7985p9e0lhy7txxw] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[MedDelivery] CHECK CONSTRAINT [FK_4kpbc4h2l7985p9e0lhy7txxw]
GO

ALTER TABLE [dbo].[MedDelivery]  WITH CHECK ADD  CONSTRAINT [FK_d22vgsq2nkdbyyk8fcn4n0lg9] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[MedDelivery] CHECK CONSTRAINT [FK_d22vgsq2nkdbyyk8fcn4n0lg9]
GO

ALTER TABLE [dbo].[MedDelivery]  WITH CHECK ADD  CONSTRAINT [FK_j4qhwdqxwwu9722d8ag19deqs] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[MedDelivery] CHECK CONSTRAINT [FK_j4qhwdqxwwu9722d8ag19deqs]
GO


