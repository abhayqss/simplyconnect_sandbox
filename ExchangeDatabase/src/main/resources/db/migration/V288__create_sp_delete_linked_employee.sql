SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF (OBJECT_ID('[dbo].[delete_linked_employee]')IS NOT NULL)
  DROP PROCEDURE [dbo].[delete_linked_employee]
GO


CREATE PROCEDURE [dbo].[delete_linked_employee]
	@CurrentEmployeeId bigint,
	@LinkedEmployeeIdToRemove bigint
AS
BEGIN
	SET NOCOUNT ON;

	delete from dbo.LinkedEmployees where second_employee_id = @LinkedEmployeeIdToRemove 
	update dbo.LinkedEmployees set first_employee_id = @CurrentEmployeeId where first_employee_id = @LinkedEmployeeIdToRemove 
	delete from dbo.LinkedEmployees where second_employee_id = first_employee_id;

END


GO
