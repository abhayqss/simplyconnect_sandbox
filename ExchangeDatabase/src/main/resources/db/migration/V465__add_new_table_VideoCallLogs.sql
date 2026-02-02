
CREATE TABLE [dbo].[PhrOpenTokSessionDetail](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[opentok_session] [nvarchar](350) NOT NULL,
	[opentok_token] [nvarchar](max) NULL,
	[session_created_at] [datetime2](7) NULL,
	[session_created_by] [bigint] NULL,
	[is_session_active] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

ALTER TABLE [dbo].[PhrOpenTokSessionDetail]  WITH CHECK ADD FOREIGN KEY([session_created_by])
REFERENCES [dbo].[UserMobile] ([id])

CREATE TABLE [dbo].[PhrVideoCallParticipants](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[video_session_id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[is_user_active] [bit] NULL,
	[call_start_time] [datetime2](7) NULL,
	[call_duration] [bigint] NULL,
	[event] [varchar](20) NULL,
 CONSTRAINT [PK_PhrVideoCallParticipants] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

ALTER TABLE [dbo].[PhrVideoCallParticipants]  WITH CHECK ADD FOREIGN KEY([video_session_id])
REFERENCES [dbo].[PhrOpenTokSessionDetail] ([id])
		 
ALTER TABLE [dbo].[PhrVideoCallParticipants]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[UserMobile] ([id])

ALTER TABLE [dbo].[PushNotificationRegistration] ALTER COLUMN [service] varchar(9);
