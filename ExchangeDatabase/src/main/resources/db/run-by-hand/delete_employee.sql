use [eldermark-clean3];

SET XACT_ABORT ON
GO

declare @employee_id bigint;
set @employee_id = 46969;
declare @person_id bigint;

set @person_id = (select top 1 person_id from Employee_enc where id = @employee_id);

delete top (1) from [name_enc] where person_id = @person_id;
delete top (1) from [PersonAddress_enc] where person_id = @person_id;
delete top (2) from [PersonTelecom_enc] where person_id = @person_id;
delete top (1) from [Person] where id = @person_id;

delete top (3) from [Employee_Role] where employee_id = @employee_id;
delete from [AuditLog_Residents] where audit_log_id IN (SELECT id from [AuditLog] where employee_id = @employee_id);
delete from [AuditLog] where employee_id = @employee_id;
delete top (1) from [EmployeeRequest] where target_employee_id = @employee_id;
delete top (1) from [EmployeePasswordSecurity] where employee_id = @employee_id;
update [resident_enc] set created_by_id = NULL where created_by_id = @employee_id;

delete top (1) from [Employee_enc] where id = @employee_id;

GO