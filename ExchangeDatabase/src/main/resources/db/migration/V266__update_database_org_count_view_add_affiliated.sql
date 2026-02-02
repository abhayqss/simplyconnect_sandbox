
SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

ALTER view [dbo].[database_org_count] as
select database_id, count(*) AS org_count, sum(case when module_hie=1 then 1 else 0 end) as org_hie_count, 
	sum(case when module_cloud_storage=1 then 1 else 0 end)  as org_cloud_count, 
	sum(case when (module_cloud_storage=1 or module_hie=1) then 1 else 0 end)  as org_hie_or_cloud_count,
	(select count(distinct ao.affiliated_database_id) from dbo.AffiliatedOrganizations ao where ao.primary_database_id = database_id) as affiliated_org_count
from dbo.Organization
where legacy_table='Company' and (testing_training IS NULL or testing_training=0) and (inactive IS NULL or inactive=0)
group by database_id;

GO