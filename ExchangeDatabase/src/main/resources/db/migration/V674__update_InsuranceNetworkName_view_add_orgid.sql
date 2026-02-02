IF (OBJECT_ID('InsuranceNetworkName') IS NOT NULL)
DROP VIEW [dbo].[InsuranceNetworkName]
   GO

CREATE VIEW [dbo].[InsuranceNetworkName] AS
SELECT DISTINCT LTRIM(n.name) AS name, database_id
FROM (
        SELECT display_name AS name, null as database_id
        FROM InNetworkInsurance
        UNION ALL
        SELECT plan_name AS name, database_id
        FROM ResidentHealthPlan
        WHERE plan_name IS NOT NULL AND plan_name != ''
     ) n


GO