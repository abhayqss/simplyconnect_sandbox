IF OBJECT_ID('PartnerNetworkCommunity') IS NOT NULL
  DROP VIEW [dbo].[PartnerNetworkCommunity];
GO

create view PartnerNetworkCommunity as
  SELECT
    partner_network_id,
    pno.organization_id as organization_id
  FROM PartnerNetwork pn
    JOIN PartnerNetwork_Organization pno ON pn.id = pno.partner_network_id
  UNION
  SELECT
    partner_network_id,
    o.id AS organization_id
  FROM PartnerNetwork pn
    JOIN PartnerNetwork_SourceDatabase pns ON pn.id = pns.partner_network_id
    JOIN Organization o ON pns.database_id = o.database_id
GO

IF OBJECT_ID('MarketplacePartnerNetwork') is not null
  DROP VIEW MarketplacePartnerNetwork
GO

create view MarketplacePartnerNetwork as
  SELECT
    partner_network_id,
    m.id AS marketplace_id
  FROM PartnerNetworkCommunity pnc
    JOIN Marketplace m ON m.organization_id = pnc.organization_id
GO