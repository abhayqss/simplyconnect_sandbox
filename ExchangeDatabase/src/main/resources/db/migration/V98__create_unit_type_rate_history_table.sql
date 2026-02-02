SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[UnitTypeRateHistory](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[daily_rate] [numeric](19, 2) NULL,
	[end_date] [date] NULL,
	[end_date_future] [date] NULL,
	[monthly_rate] [numeric](19, 2) NULL,
	[start_date] [date] NULL,
	[database_id] [bigint] NOT NULL,
	[unit_type_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[UnitTypeRateHistory]  WITH CHECK ADD  CONSTRAINT [FK_d9e19vw83ao7h1li4jdhqn030] FOREIGN KEY([unit_type_id])
REFERENCES [dbo].[UnitType] ([id])
GO

ALTER TABLE [dbo].[UnitTypeRateHistory] CHECK CONSTRAINT [FK_d9e19vw83ao7h1li4jdhqn030]
GO

ALTER TABLE [dbo].[UnitTypeRateHistory]  WITH CHECK ADD  CONSTRAINT [FK_oeb9ts1ruftl7cnm1skhdebt5] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[UnitTypeRateHistory] CHECK CONSTRAINT [FK_oeb9ts1ruftl7cnm1skhdebt5]
GO