IF (OBJECT_ID('InsuranceNetworkName') IS NOT NULL)
DROP VIEW [dbo].[InsuranceNetworkName]
   GO

CREATE VIEW [dbo].[InsuranceNetworkName] AS
SELECT DISTINCT LTRIM(n.name) AS name
FROM (
        SELECT display_name AS name
        FROM InNetworkInsurance
        UNION ALL
        SELECT plan_name AS name
        FROM ResidentHealthPlan
        WHERE plan_name IS NOT NULL AND plan_name != ''
     ) n
GO