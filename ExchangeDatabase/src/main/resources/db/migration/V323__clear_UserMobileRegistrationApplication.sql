SET ANSI_PADDING ON
GO

DELETE FROM [dbo].[UserMobileRegistrationApplication]
WHERE registration_type IS NOT NULL;

ALTER TABLE [dbo].[UserMobile]
  DROP COLUMN [address_id];
GO

SET ANSI_PADDING OFF
GO
