SET XACT_ABORT ON
GO

INSERT INTO [dbo].[Role] ([name]) VALUES
('ROLE_DIRECT_MANAGER');
GO

CREATE TABLE [dbo].[DirectConfiguration] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[pin] [varchar](255) NULL,
	[keystore_file] [varchar](255) NULL,
	[is_configured] [bit] NULL DEFAULT (0),
  PRIMARY KEY ([id])
);
GO

ALTER TABLE [dbo].[SourceDatabase] ADD [direct_config_id] bigint NULL;
ALTER TABLE [dbo].[SourceDatabase] ADD FOREIGN KEY([direct_config_id]) REFERENCES [dbo].[DirectConfiguration] ([id]);
GO

