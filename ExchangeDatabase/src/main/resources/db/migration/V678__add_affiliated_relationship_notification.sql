IF OBJECT_ID('AffiliatedRelationshipNotification') IS NOT NULL
DROP TABLE [dbo].[AffiliatedRelationshipNotification];
GO

SET ANSI_PADDING OFF
GO

CREATE TABLE AffiliatedRelationshipNotification (
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [primary_database_id] [bigint] NOT NULL,
  [affiliated_database_id] [bigint] NOT NULL,
  [created_datetime] [datetime2](7) NOT NULL,
  [sent_datetime] [datetime2](7) NULL,
  [author_id] [bigint] NOT NULL,
  [receiver_id] [bigint] NOT NULL,
  [destination] [varchar](256) NULL,
  [is_terminated] [bit] NOT NULL,
  CONSTRAINT [PK_AffiliatedRelationshipNotification] PRIMARY KEY CLUSTERED
(
  [id] ASC
) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[AffiliatedRelationshipNotification] WITH CHECK ADD CONSTRAINT [FK_AffiliatedRelationshipNotification_PrimaryDatabase] FOREIGN KEY ([primary_database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[AffiliatedRelationshipNotification] CHECK CONSTRAINT [FK_AffiliatedRelationshipNotification_PrimaryDatabase]
GO

ALTER TABLE [dbo].[AffiliatedRelationshipNotification] WITH CHECK ADD CONSTRAINT [FK_AffiliatedRelationshipNotification_AffiliatedDatabase] FOREIGN KEY ([affiliated_database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[AffiliatedRelationshipNotification] CHECK CONSTRAINT [FK_AffiliatedRelationshipNotification_AffiliatedDatabase]
GO

ALTER TABLE [dbo].[AffiliatedRelationshipNotification] WITH CHECK ADD CONSTRAINT [FK_AffiliatedRelationshipNotification_Author] FOREIGN KEY ([author_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[AffiliatedRelationshipNotification] CHECK CONSTRAINT [FK_AffiliatedRelationshipNotification_Author]
GO

ALTER TABLE [dbo].[AffiliatedRelationshipNotification] WITH CHECK ADD CONSTRAINT [FK_AffiliatedRelationshipNotification_Receiver] FOREIGN KEY ([receiver_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[AffiliatedRelationshipNotification] CHECK CONSTRAINT [FK_AffiliatedRelationshipNotification_Receiver]
GO