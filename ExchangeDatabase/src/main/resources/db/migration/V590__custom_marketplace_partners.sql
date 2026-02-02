create table MarketplacePartnersGroup (
  id                   [bigint] not null identity (1, 1),
  marketplace_id       [bigint] not null,
  partnership_group_id [bigint] not null,
  PRIMARY KEY ([id]),
  FOREIGN KEY ([marketplace_id]) REFERENCES [dbo].[Marketplace] ([id]),
)
GO
