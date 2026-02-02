WITH source AS
(
  SELECT
    mp.id     AS source_id,
    org.email AS source_email
  FROM Marketplace mp
  LEFT JOIN Organization org ON mp.organization_id = org.id
  WHERE org.email IS NOT NULL
)
merge INTO MarketplaceReferralEmail mpre
USING source
ON 1 <> 1
WHEN NOT matched THEN INSERT (marketplace_id, email) VALUES (source_id, source_email);
GO