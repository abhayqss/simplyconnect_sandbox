ALTER TABLE [dbo].[Referral]
  ADD [is_marketplace] BIT NOT NULL CONSTRAINT [DF_Referral_is_marketplace] DEFAULT 0
GO