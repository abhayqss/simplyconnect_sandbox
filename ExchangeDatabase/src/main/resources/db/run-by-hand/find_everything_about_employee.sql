use exchange;

declare @employee_id bigint;
set @employee_id = 46969;
declare @person_id bigint;

set @person_id = (select top 1 person_id from Employee_enc where id = @employee_id);

select top 10 * from [name_enc] where person_id = @person_id;
select top 10 * from [PersonAddress_enc] where person_id = @person_id;
select top 10 * from [PersonTelecom_enc] where person_id = @person_id;

select top 10 * from [Employee_Groups] where employee_id = @employee_id;
select top 10 * from [Employee_Organization] where employee_id = @employee_id;
select top 10 * from [Employee_Role] where employee_id = @employee_id;

select top 1000 * from [AuditLog] where employee_id = @employee_id;
select top 1000 * from [AuditLog_Documents] where audit_log_id in (select top 1000 id from [AuditLog] where employee_id = @employee_id);
select top 1000 * from [AuditLog_Residents] where audit_log_id in (select top 1000 id from [AuditLog] where employee_id = @employee_id);

select top 10 * from [EmployeeRequest] where target_employee_id = @employee_id;
select top 10 * from [CareTeamMember] where employee_id = @employee_id;
select top 10 * from [CareTeamMember] where created_by_id = @employee_id;
select top 10 * from [EventNotification_enc] where employee_id = @employee_id;

select top 1000 * from [resident_enc] where person_id = @person_id;
select top 1000 * from [resident_enc] where mother_person_id = @person_id;
select top 10 * from [resident_enc] where created_by_id = @employee_id;

select top 1000 * from [Authenticator] where person_id = @person_id;
select top 1000 * from [Author] where person_id = @person_id;
select top 1000 * from [AuthorizationActivity_Person] where person_id = @person_id;
select top 10 * from [DataEnterer] where person_id = @person_id;
select top 1000 * from [DocumentationOf_Person] where person_id = @person_id;
select top 1000 * from [Encounter] where person_id = @person_id;
select top 1000 * from [Guardian] where person_id = @person_id;
select top 1000 * from [Immunization] where person_id = @person_id;
select top 1000 * from [Informant] where person_id = @person_id;
select top 1000 * from [InformationRecipient] where person_id = @person_id;
select top 1000 * from [LegalAuthenticator] where person_id = @person_id;
select top 1000 * from [MedicalProfessional] where person_id = @person_id;
select top 1000 * from [Medication] where person_id = @person_id;
select top 10 * from [Participant] where person_id = @person_id;
select top 1000 * from [PolicyActivity] where guarantor_person_id = @person_id;

select top 10 * from [MedDelivery] where given_or_recorded_person_id = @employee_id;
select top 10 * from [ResidentAdmittanceHistory] where sales_rep_employee_id = @employee_id;
