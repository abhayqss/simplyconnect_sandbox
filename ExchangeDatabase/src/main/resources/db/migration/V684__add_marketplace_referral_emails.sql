IF OBJECT_ID('MarketplaceReferralEmail') IS NOT NULL
DROP TABLE [dbo].[MarketplaceReferralEmail];
GO

SET ANSI_PADDING OFF
GO

CREATE TABLE [dbo].[MarketplaceReferralEmail](
	[marketplace_id] [bigint] NOT NULL,
	[email] [varchar](256) NOT NULL
)
GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[MarketplaceReferralEmail]  WITH CHECK ADD  CONSTRAINT [FK_MarketplaceReferralEmail_Marketplace] FOREIGN KEY([marketplace_id])
REFERENCES [dbo].[Marketplace] ([id])
GO

ALTER TABLE [dbo].[MarketplaceReferralEmail] CHECK CONSTRAINT [FK_MarketplaceReferralEmail_Marketplace]
GO

ALTER TABLE [dbo].[MarketplaceReferralEmail]
	ADD CONSTRAINT UQ_MarketplaceReferralEmail_Marketplace_Email UNIQUE (marketplace_id, email);
GO

CREATE CLUSTERED index IX_MarketplaceReferralEmail_Marketplace
	ON MarketplaceReferralEmail (marketplace_id)
GO