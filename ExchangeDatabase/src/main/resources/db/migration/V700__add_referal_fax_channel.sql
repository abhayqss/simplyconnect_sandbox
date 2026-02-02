ALTER TABLE [dbo].[ReferralRequest]
    ADD
        [shared_channel] [varchar](256) NULL,
        [shared_fax] [varchar](17) NULL,
        [shared_phone] [varchar](17) NULL,
        [shared_fax_comment] [varchar](1500) NULL,
        [timezone_id] varchar(20) NULL;
GO

ALTER TABLE [dbo].[ReferralRequestNotification]
    ADD
        [channel] [varchar](256) NOT NULL
        CONSTRAINT DF_ReferralRequestNotification_channel DEFAULT 'EMAIL'
GO

EXEC sp_rename 'ReferralRequestResponse.decline_comment', 'comment', 'COLUMN';