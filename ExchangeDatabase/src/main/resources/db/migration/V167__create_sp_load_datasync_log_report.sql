/****** Object:  StoredProcedure [dbo].[load_datasync_log_report]   ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[load_datasync_log_report]
 @start_date_time datetime,
 @end_date_time datetime,
 @database_id int,
 @database_url varchar(255) 
AS		
	declare @temp_data_sync_log_data table
	(
	   id bigint,
	   date datetime2(7),
	   log_type varchar(255),
	   description varchar(MAX),
	   table_name varchar(255),
	   database_id bigint
	);
	insert into 
		@temp_data_sync_log_data
	select synclog.id, synclog.date, logtype.name, synclog.description, synclog.table_name, synclog.database_id
	from [dbo].[DataSyncLog] synclog join [dbo].[DataSyncLogType] logtype on synclog.type_id = logtype.id
	where synclog.database_id = @database_id and synclog.date between @start_date_time and @end_date_time;
	               
	with DataSyncLogView as 
			(select synclog.date, synclog.log_type, synclog.description, synclog.table_name, synclog.id
			 from @temp_data_sync_log_data synclog)
	select DataSyncLogView.date, DataSyncLogView.log_type, DataSyncLogView.description, DataSyncLogView.table_name, DataSyncLogView.id, @database_url as url
	from DataSyncLogView
	order by date desc;
GO