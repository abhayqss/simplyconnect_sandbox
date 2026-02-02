SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[MedProviderScheduleLog](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[date_time] [datetime2](7) NULL,
	[description] [varchar](max) NULL,
	[legacy_id] [varchar](255) NOT NULL,
	[more_data] [varchar](max) NULL,
	[more_tag] [varchar](255) NULL,
	[sequence] [bigint] NULL,
	[database_id] [bigint] NOT NULL,
	[employee_id] [bigint] NULL,
	[med_provider_schedule_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[MedProviderScheduleLog]  WITH CHECK ADD  CONSTRAINT [FK_8hjy2e3w686pyipmynnr3gg9d] FOREIGN KEY([med_provider_schedule_id])
REFERENCES [dbo].[MedProviderSchedule] ([id])
GO

ALTER TABLE [dbo].[MedProviderScheduleLog] CHECK CONSTRAINT [FK_8hjy2e3w686pyipmynnr3gg9d]
GO

ALTER TABLE [dbo].[MedProviderScheduleLog]  WITH CHECK ADD  CONSTRAINT [FK_9hhw82y4td3ykwc39myqjp5qy] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[MedProviderScheduleLog] CHECK CONSTRAINT [FK_9hhw82y4td3ykwc39myqjp5qy]
GO

ALTER TABLE [dbo].[MedProviderScheduleLog]  WITH CHECK ADD  CONSTRAINT [FK_jfi4tvi7pstyqff4hrxd9a5hu] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[MedProviderScheduleLog] CHECK CONSTRAINT [FK_jfi4tvi7pstyqff4hrxd9a5hu]
GO

