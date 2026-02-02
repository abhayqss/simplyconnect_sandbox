IF EXISTS ( SELECT * 
			FROM   sysobjects 
			WHERE  
			id = object_id(N'[dbo].[get_services_sync_stats]') 
			and OBJECTPROPERTY(id, N'IsProcedure') = 1 )
BEGIN
	drop PROCEDURE [dbo].[get_services_sync_stats]
END
GO

CREATE PROCEDURE [dbo].[get_services_sync_stats] 
@database_id bigint 

AS
-- Step1: - finding last record of any service which are successfully sync
select DSS.* 
into #DataSyncStats_Success 
from DataSyncStats DSS with (nolock)
inner join (
	select sync_service_name, max(id) LastSuccessSyncID
	from DataSyncStats  with (nolock) WHERE database_id  = @database_id AND sync_service_name IS not NULL and completed is not null
	group by sync_service_name
) DSS1 on DSS.ID = DSS1.LastSuccessSyncID


-- Step2: - finding last record of any service which might have failed ever during the Initial Sync
select DSS.* 
into #DataSyncStats_FailedLast
from DataSyncStats DSS  with (nolock)
inner join (
	select sync_service_name, max(id) LastSuccessSyncID
	from DataSyncStats  with (nolock) WHERE database_id  = @database_id AND sync_service_name IS not NULL and completed is null
	group by sync_service_name
) DSS1 on DSS.ID = DSS1.LastSuccessSyncID
left join #DataSyncStats_Success Success on DSS.sync_service_name = Success.sync_service_name
where Success.ID is null

--step3: combining Success + failed
select * from #DataSyncStats_FailedLast
union
select * from #DataSyncStats_Success
--End--