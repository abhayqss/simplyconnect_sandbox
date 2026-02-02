CREATE TABLE [dbo].[ReleaseNote](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[created_date] [datetime2](7) NOT NULL,
	[modified_date] [datetime2](7) NOT NULL,
	[title] [varchar](256) NOT NULL,
	[whats_new] [varchar](5000) NOT NULL,
	[bug_fixes] [varchar](5000) NULL,
	[description] [varchar](256) NULL,
	[file_name] [varchar](512) NOT NULL,
	[mime_type] [varchar](256) NULL,
	[email_notification_enabled] [bit] NOT NULL,
	[in_app_notification_enabled] [bit] NOT NULL,
 CONSTRAINT [PK_ReleaseNote] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO