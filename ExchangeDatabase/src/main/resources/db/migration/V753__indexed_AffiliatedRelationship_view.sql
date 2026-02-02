IF OBJECT_ID('AffiliatedRelationship') IS NOT NULL
    DROP VIEW AffiliatedRelationship
go

IF OBJECT_ID('AffiliatedRelationshipIntermediateIndexed') IS NOT NULL
    DROP VIEW AffiliatedRelationshipIntermediateIndexed
go

IF OBJECT_ID('Two') IS NOT NULL
    DROP TABLE Two
go

CREATE TABLE dbo.Two
(
    i INT
)
go

INSERT INTO dbo.Two
VALUES (1)
INSERT INTO dbo.Two
VALUES (2)
GO

CREATE VIEW dbo.AffiliatedRelationshipIntermediateIndexed
    WITH SCHEMABINDING
AS
select ao.id,
       ao.primary_database_id,
       ao.affiliated_database_id,

       i                       as org_type, --1 is primary, 2 is affiliated
       case
           when i = 1 and (ao.primary_organization_id is null and org.database_id = ao.primary_database_id or
                           ao.primary_organization_id = org.id)
               then org.id
           when i = 2 and (ao.affiliated_organization_id is null and org.database_id = ao.affiliated_database_id or
                           ao.affiliated_organization_id = org.id)
               then org.id end as org_id
from dbo.AffiliatedOrganizations ao
         cross join dbo.Two
         inner join dbo.Organization org on (
        (ao.primary_organization_id is null and org.database_id = ao.primary_database_id or
         ao.primary_organization_id = org.id) or
        (ao.affiliated_organization_id is null and org.database_id = ao.affiliated_database_id or
         ao.affiliated_organization_id = org.id))
WHERE CASE
          WHEN i = 1 and (ao.primary_organization_id is null and org.database_id = ao.primary_database_id or
                          ao.primary_organization_id = org.id) THEN org.id
          WHEN i = 2 and (ao.affiliated_organization_id is null and org.database_id = ao.affiliated_database_id or
                          ao.affiliated_organization_id = org.id) THEN org.id
          END IS NOT NULL
GO

CREATE UNIQUE CLUSTERED INDEX AffiliatedRelationshipIntermediateIndexed ON dbo.AffiliatedRelationshipIntermediateIndexed
    (org_type, id, primary_database_id, affiliated_database_id, org_id)
GO

CREATE VIEW dbo.AffiliatedRelationship
    WITH SCHEMABINDING
AS
SELECT prim.id,
       prim.primary_database_id,
       prim.org_id primary_organization_id,
       aff.affiliated_database_id,
       aff.org_id  affiliated_organization_id
from (
         SELECT id,
                primary_database_id,
                affiliated_database_id,
                org_id
         FROM dbo.AffiliatedRelationshipIntermediateIndexed WITH (NOEXPAND)
         where org_type = 1
     ) prim
         join
     (
         SELECT id,
                primary_database_id,
                affiliated_database_id,
                org_id
         FROM dbo.AffiliatedRelationshipIntermediateIndexed WITH (NOEXPAND)
         where org_type = 2
     ) aff
     on prim.id = aff.id
GO
