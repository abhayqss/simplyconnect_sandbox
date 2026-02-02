if (OBJECT_ID('AffiliatedRelationship') IS NOT NULL)
  DROP VIEW AffiliatedRelationship
GO

CREATE VIEW AffiliatedRelationship as (
  select
    ao.id                     as id,
    ao.primary_database_id    as primary_database_id,
    primary_org.id            as primary_organization_id,
    ao.affiliated_database_id as affiliated_database_id,
    affiliated_org.id         as affiliated_organization_id
  from AffiliatedOrganizations ao
    join Organization primary_org on ao.primary_database_id = primary_org.database_id and
                                     (ao.primary_organization_id = primary_org.id or
                                      ao.primary_organization_id is null)
    join Organization affiliated_org on ao.affiliated_database_id = affiliated_org.database_id and
                                        (ao.affiliated_organization_id = affiliated_org.id or
                                         ao.affiliated_organization_id is null)
)
GO
