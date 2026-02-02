SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[AutoCloseInterval](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](50) NOT NULL,
	[value] [bigint] NOT NULL,
 CONSTRAINT [PK_AutoCloseInterval] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

INSERT INTO [dbo].[AutoCloseInterval]([name],[value]) VALUES('5 min',300000);
INSERT INTO [dbo].[AutoCloseInterval]([name],[value]) VALUES('15 min',900000);
INSERT INTO [dbo].[AutoCloseInterval]([name],[value]) VALUES('30 min',1800000);
INSERT INTO [dbo].[AutoCloseInterval]([name],[value]) VALUES('1 hour',3600000);
INSERT INTO [dbo].[AutoCloseInterval]([name],[value]) VALUES('3 hour',10800000);
INSERT INTO [dbo].[AutoCloseInterval]([name],[value]) VALUES('5 hour',18000000);
INSERT INTO [dbo].[AutoCloseInterval]([name],[value]) VALUES('12 hour',43200000);
INSERT INTO [dbo].[AutoCloseInterval]([name],[value]) VALUES('1 day',86400000);
INSERT INTO [dbo].[AutoCloseInterval]([name],[value]) VALUES('Never',0);
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[DeviceType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[community_id] [bigint] NOT NULL,
	[auto_close_interval_id] [bigint] NOT NULL,
	[type] [varchar](200) NULL,
	[workflow] [varchar](20) NULL,
	[enabled] [bit] NULL,
 CONSTRAINT [PK_DeviceType] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[DeviceType]  WITH CHECK ADD  CONSTRAINT [FK_DeviceType_AutoCloseInterval] FOREIGN KEY([auto_close_interval_id])
REFERENCES [dbo].[AutoCloseInterval] ([id])
GO

ALTER TABLE [dbo].[DeviceType] CHECK CONSTRAINT [FK_DeviceType_AutoCloseInterval]
GO

ALTER TABLE [dbo].[DeviceType]  WITH CHECK ADD  CONSTRAINT [FK_DeviceType_Organization] FOREIGN KEY([community_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[DeviceType] CHECK CONSTRAINT [FK_DeviceType_Organization]
GO