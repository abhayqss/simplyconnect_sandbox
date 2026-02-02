SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF (OBJECT_ID('[dbo].[find_merged_patients]') IS NOT NULL)
  DROP PROCEDURE [dbo].[find_merged_patients]
GO


CREATE PROCEDURE [dbo].[find_merged_patients]
	@ResidentId bigint
AS
BEGIN
	SET NOCOUNT ON;

	declare @found_residents table (
		resident_id bigint
	)

	declare @current_step_residents_to_search table (
		resident_id bigint
	)

	declare @current_step_found_surviving_residents table (
		resident_id bigint
	)

	declare @current_step_found_merged_residents table (
		resident_id bigint
	)

	insert into @found_residents(resident_id) values (@ResidentId);
	insert into @current_step_residents_to_search(resident_id) values (@ResidentId);

	declare @search_finished bit = 0;
	declare @found_residents_count_before_step int;
	declare @found_residents_count_after_step int;

	while @search_finished = 0
	begin
		delete from @current_step_found_surviving_residents;
		delete from @current_step_found_merged_residents;
		select @found_residents_count_before_step = count(*) from @found_residents;
	
		insert into @current_step_found_surviving_residents(resident_id) 
			(select surviving_resident_id from dbo.MPI_merged_residents where merged_resident_id in (select resident_id from @current_step_residents_to_search) 
																	and surviving_resident_id not in (select resident_id from @found_residents));
	
		insert into @current_step_found_merged_residents(resident_id) 
			(select merged_resident_id from dbo.MPI_merged_residents where surviving_resident_id in (select resident_id from @current_step_residents_to_search) 
																	and merged_resident_id not in (select resident_id from @found_residents)); 

		insert into @found_residents(resident_id) 
			(select resident_id from @current_step_found_surviving_residents r1 where not exists(select * from @found_residents r2 where r2.resident_id = r1.resident_id));
		insert into @found_residents(resident_id) 
			(select resident_id from @current_step_found_merged_residents r1 where not exists(select * from @found_residents r2 where r2.resident_id = r1.resident_id));

		select @found_residents_count_after_step = count(*) from @found_residents;

		delete from @current_step_residents_to_search;
		insert into @current_step_residents_to_search(resident_id) 
			(select resident_id from @current_step_found_surviving_residents r1 where not exists(select * from @current_step_residents_to_search r2 where r2.resident_id = r1.resident_id));
		insert into @current_step_residents_to_search(resident_id) 
			(select resident_id from @current_step_found_merged_residents r1 where not exists(select * from @current_step_residents_to_search r2 where r2.resident_id = r1.resident_id));

	
		if (@found_residents_count_before_step = @found_residents_count_after_step)
		begin
			set @search_finished = 1;
		end
	end

	select resident_id from @found_residents

END
GO