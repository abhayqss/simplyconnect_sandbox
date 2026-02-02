SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[SourceDatabase] ADD [is_service] [bit] NOT NULL DEFAULT(0)
GO
ALTER TABLE [dbo].[SourceDatabase] ALTER COLUMN [is_service] [bit] NULL
GO

INSERT INTO [dbo].[SourceDatabase]
           ([alternative_id],
            [name],
            [url],
            [is_service])
    VALUES
           ('service_datasource'
           ,'Service Datasource'
           ,'no_url',
           1)

INSERT INTO [dbo].[Role] ([name]) VALUES
('ROLE_SUPER_MANAGER');

GO