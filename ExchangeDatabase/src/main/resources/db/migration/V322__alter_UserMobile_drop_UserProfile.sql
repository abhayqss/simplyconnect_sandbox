SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[UserMobile]
  ADD [address_id] [BIGINT] NULL,
  [secondary_email] [VARCHAR](255) NULL,
  [secondary_phone] [VARCHAR](255) NULL;
GO

UPDATE [dbo].[UserMobile]
SET [secondary_email] = up.[secondary_email],
  [secondary_phone]   = up.[secondary_phone],
  [address_id]        = up.[address_id]
FROM [dbo].[UserProfile] up
WHERE up.[user_id] = [id];

DROP TABLE [dbo].[UserProfile];
GO


SET ANSI_PADDING OFF
GO