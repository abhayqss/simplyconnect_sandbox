CREATE TABLE [dbo].[DataSyncDeletedDataLog](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[deleted_record] [varchar](max) NOT NULL,
	[legacy_id] [varchar](250) NOT NULL,
	[deleted_date]  [datetime2](7) NULL,
	[target_table_name] [varchar](250) NOT NULL,
 CONSTRAINT [PK_DataSyncDeletedDataLog] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO