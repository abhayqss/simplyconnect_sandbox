IF OBJECT_ID('ReferralAttachment') IS NOT NULL
    DROP TABLE [dbo].[ReferralAttachment];
GO

CREATE TABLE [dbo].[ReferralAttachment]
(
    [id]                 [bigint] IDENTITY (1,1) NOT NULL,
    [original_file_name] [varchar](512)          NOT NULL,
    [file_name]          [varchar](512)          NOT NULL,
    [mime_type]          [varchar](256)          NULL,
    [referral_id]        [bigint]                NOT NULL,
    CONSTRAINT [PK_ReferralAttachment] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[ReferralAttachment]
    WITH CHECK ADD CONSTRAINT [FK_ReferralAttachment_Referral] FOREIGN KEY ([referral_id])
        REFERENCES [dbo].[Referral] ([id])
GO

ALTER TABLE [dbo].[ReferralAttachment]
    CHECK CONSTRAINT [FK_ReferralAttachment_Referral]
GO