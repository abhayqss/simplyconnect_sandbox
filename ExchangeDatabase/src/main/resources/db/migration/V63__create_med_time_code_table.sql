SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[MedTimeCode](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[inactive] [bit] NULL,
	[name] [varchar](255) NULL,
	[prn] [bit] NULL,
	[time_range_begin] [time](7) NULL,
	[time_range_begin_alpha] [varchar](255) NULL,
	[time_range_end] [time](7) NULL,
	[time_range_end_alpha] [varchar](255) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[MedTimeCode]  WITH CHECK ADD  CONSTRAINT [FK_7k8ub6g4cae343v68io0v15o2] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[MedTimeCode] CHECK CONSTRAINT [FK_7k8ub6g4cae343v68io0v15o2]
GO

ALTER TABLE [dbo].[MedTimeCode]  WITH CHECK ADD  CONSTRAINT [FK_ms1tp1ga7heobdsnns0282vml] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[MedTimeCode] CHECK CONSTRAINT [FK_ms1tp1ga7heobdsnns0282vml]
GO
