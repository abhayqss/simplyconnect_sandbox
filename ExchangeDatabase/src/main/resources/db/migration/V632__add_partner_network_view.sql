IF OBJECT_ID('MarketplacePartnerNetwork') IS NOT NULL
DROP VIEW [dbo].[MarketplacePartnerNetwork];
GO

CREATE VIEW MarketplacePartnerNetwork AS
  SELECT
    partner_network_id,
    m.id AS marketplace_id
  FROM PartnerNetwork pn
         JOIN PartnerNetwork_Organization pno ON pn.id = pno.partner_network_id
         JOIN Marketplace m ON m.organization_id = pno.organization_id
  UNION
  SELECT
    partner_network_id,
    m.id AS marketplace_id
  FROM PartnerNetwork pn
         JOIN PartnerNetwork_SourceDatabase pns ON pn.id = pns.partner_network_id
         JOIN Organization o ON pns.database_id = o.database_id
         JOIN Marketplace m ON m.organization_id = o.id
  GO

DECLARE @marketplaces TABLE (partnership_group_id bigint);
INSERT INTO @marketplaces (partnership_group_id)
SELECT DISTINCT partnership_group_id FROM MarketplacePartnersGroup;

DECLARE @ids TABLE(
  partner_network_id    bigint,
  partnership_group_id  bigint
);

WITH source AS (SELECT m.partnership_group_id FROM @marketplaces m)
MERGE INTO PartnerNetwork
USING source
ON 1 <> 1
WHEN NOT MATCHED THEN INSERT (name, is_public, description)
VALUES ('Network ' +  + CAST(source.partnership_group_id AS VARCHAR), 1, 'Description for network ' + CAST(source.partnership_group_id AS VARCHAR))
OUTPUT INSERTED.id, source.partnership_group_id INTO @ids;

DECLARE @insert TABLE (
  organization_id        bigint,
  partnership_group_id   bigint,
  partner_network_id     bigint
);

INSERT INTO @insert (organization_id, partnership_group_id, partner_network_id)
SELECT organization_id, mpg.partnership_group_id, t.partner_network_id FROM Marketplace
INNER JOIN MarketplacePartnersGroup mpg ON Marketplace.id = mpg.marketplace_id
INNER JOIN @ids t ON mpg.partnership_group_id = t.partnership_group_id;

WITH i AS (SELECT * FROM @insert)
MERGE INTO PartnerNetwork_Organization
USING i
ON 1 <> 1
WHEN NOT MATCHED THEN INSERT (partner_network_id, organization_id) VALUES (partner_network_id, organization_id);
GO

IF OBJECT_ID('MarketplacePartnersGroup') IS NOT NULL
DROP TABLE [dbo].[MarketplacePartnersGroup];