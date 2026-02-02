SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF (OBJECT_ID('[dbo].[find_linked_employees]')IS NOT NULL)
  DROP PROCEDURE [dbo].[find_linked_employees]
GO


CREATE PROCEDURE [dbo].[find_linked_employees]
	@EmployeeId bigint
AS
BEGIN
	SET NOCOUNT ON;

	declare @found_employees table (
		employee_id bigint
	)

	declare @current_step_employees_to_search table (
		employee_id bigint
	)

	declare @current_step_found_first_employees table (
		employee_id bigint
	)

	declare @current_step_found_second_employees table (
		employee_id bigint
	)

	insert into @found_employees(employee_id) values (@EmployeeId);
	insert into @current_step_employees_to_search(employee_id) values (@EmployeeId);

	declare @search_finished bit = 0;
	declare @found_employees_count_before_step int;
	declare @found_employees_count_after_step int;

	while @search_finished = 0
	begin
		delete from @current_step_found_first_employees;
		delete from @current_step_found_second_employees;
		select @found_employees_count_before_step = count(*) from @found_employees;
	
		insert into @current_step_found_first_employees(employee_id) 
			(select first_employee_id from dbo.LinkedEmployees where second_employee_id in (select employee_id from @current_step_employees_to_search) 
																	and first_employee_id not in (select employee_id from @found_employees));

		insert into @current_step_found_second_employees(employee_id)
			(select second_employee_id from dbo.LinkedEmployees where first_employee_id in (select employee_id from @current_step_employees_to_search)
																	and second_employee_id not in (select employee_id from @found_employees));

		insert into @found_employees(employee_id)
			(select employee_id from @current_step_found_first_employees r1 where not exists(select * from @found_employees r2 where r2.employee_id = r1.employee_id));
		insert into @found_employees(employee_id)
			(select employee_id from @current_step_found_second_employees r1 where not exists(select * from @found_employees r2 where r2.employee_id = r1.employee_id));

		select @found_employees_count_after_step = count(*) from @found_employees;

		delete from @current_step_employees_to_search;
		insert into @current_step_employees_to_search(employee_id)
			(select employee_id from @current_step_found_first_employees r1 where not exists(select * from @current_step_employees_to_search r2 where r2.employee_id = r1.employee_id));
		insert into @current_step_employees_to_search(employee_id)
			(select employee_id from @current_step_found_second_employees r1 where not exists(select * from @current_step_employees_to_search r2 where r2.employee_id = r1.employee_id));


		if (@found_employees_count_before_step = @found_employees_count_after_step)
		begin
			set @search_finished = 1;
		end
	end

	select employee_id from @found_employees

END


GO
