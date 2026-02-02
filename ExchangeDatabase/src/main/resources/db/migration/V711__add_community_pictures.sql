IF OBJECT_ID('CommunityPicture') IS NOT NULL
    DROP TABLE [dbo].[CommunityPicture];
GO

CREATE TABLE [dbo].[CommunityPicture]
(
    [id]                 [bigint] IDENTITY (1,1) NOT NULL,
    [original_file_name] [varchar](512)          NOT NULL,
    [file_name]          [varchar](512)          NOT NULL,
    [mime_type]          [varchar](256)          NULL,
    [community_id]       [bigint]                NOT NULL,
    CONSTRAINT [PK_CommunityPicture] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[CommunityPicture]
    WITH CHECK ADD CONSTRAINT [FK_CommunityPicture_Community] FOREIGN KEY ([community_id])
        REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[CommunityPicture]
    CHECK CONSTRAINT [FK_CommunityPicture_Community]
GO