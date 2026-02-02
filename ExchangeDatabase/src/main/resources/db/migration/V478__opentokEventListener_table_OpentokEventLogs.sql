
CREATE TABLE [dbo].[OpentokEventLogs](
	[id] [bigint] NOT NULL,
	[opentok_session_id] [bigint] NOT NULL,
	[project_id] [varchar](150) NOT NULL,
	[event] [varchar](100) NULL,
	[timestamp] [bigint] NULL,
	[video_type] [varchar](150) NULL,
	[user_id] [varchar](50) NULL,
	[reason] [varchar](100) NULL,
	[created_at] [datetime2](7) NULL,
 CONSTRAINT [PK_opentokEventLogs] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]


ALTER TABLE [dbo].[opentokEventLogs]  WITH CHECK ADD  CONSTRAINT [FK_opentokEventLogs_PhrOpenTokSessionDetail] FOREIGN KEY([opentok_session_id])
REFERENCES [dbo].[PhrOpenTokSessionDetail] ([id])

ALTER TABLE [dbo].[opentokEventLogs] CHECK CONSTRAINT [FK_opentokEventLogs_PhrOpenTokSessionDetail]


