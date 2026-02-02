SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[MedProviderSchedule](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[checked_out] [bit] NULL,
	[log] [varchar](max) NULL,
	[login_external_id] [varchar](255) NULL,
	[prepour_checked_out] [bit] NULL,
	[prepour_checked_out_emp_id] [varchar](10) NULL,
	[prepour_sm_login_id] [bigint] NULL,
	[provider_date] [varchar](20) NULL,
	[sm_login_id] [bigint] NULL,
	[start_date] [date] NULL,
	[database_id] [bigint] NOT NULL,
	[checked_out_by_emp_id] [bigint] NULL,
	[med_provider_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[MedProviderSchedule]  WITH CHECK ADD  CONSTRAINT [FK_19ddndja887adc1418k54ryhk] FOREIGN KEY([med_provider_id])
REFERENCES [dbo].[MedProvider] ([id])
GO

ALTER TABLE [dbo].[MedProviderSchedule] CHECK CONSTRAINT [FK_19ddndja887adc1418k54ryhk]
GO

ALTER TABLE [dbo].[MedProviderSchedule]  WITH CHECK ADD  CONSTRAINT [FK_3hsxr54v0k9kenxvo9hopqfm2] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[MedProviderSchedule] CHECK CONSTRAINT [FK_3hsxr54v0k9kenxvo9hopqfm2]
GO

ALTER TABLE [dbo].[MedProviderSchedule]  WITH CHECK ADD  CONSTRAINT [FK_m925gpd2oadqgqlxbyn6y5sxg] FOREIGN KEY([checked_out_by_emp_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[MedProviderSchedule] CHECK CONSTRAINT [FK_m925gpd2oadqgqlxbyn6y5sxg]
GO