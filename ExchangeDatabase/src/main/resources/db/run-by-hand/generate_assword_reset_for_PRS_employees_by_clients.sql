use exchange;
OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

DECLARE @TransactionName VARCHAR(30) = 'inserting_prs_reset_requests';  

BEGIN TRAN @TransactionName

DECLARE @employee_data TABLE
(id bigint,
 resident_id bigint,
 login_employee varchar(256),
 company_id varchar(256), 
 phone varchar(30))


DECLARE @generated_employees TABLE
(id bigint,
client_id bigint,
login_employee varchar(256),
company_id varchar(256),
phone varchar(30),
reset_link varchar(512)
);

INSERT INTO @employee_data
SELECT e.[id],
	   ear.resident_id,
	   e.login,
	   ss.login_company_id,
	   pt.value
  FROM [exchange].[dbo].[Employee] e 
  join [employee_associated_residents] ear on ear.employee_id = e.id 
  join SystemSetup ss on ss.database_id = e.database_id
  join Person p on  p.id = e.person_id
  left join PersonTelecom pt on pt.person_id = p.id and pt.[sync_qualifier] = 6
  where ear.resident_id in (SELECT [id] FROM [exchange].[dbo].[resident] where [facility_id] = 445406)


DECLARE @resident_id bigint = 0
DECLARE @database_id bigint = 0
DECLARE @person_id bigint = 0
DECLARE @name_id bigint = 0
DECLARE @organization_id bigint = 0
DECLARE @person_address_id bigint = 0
DECLARE @person_telecom_id bigint = 0
DECLARE @employee_id bigint = 0
DECLARE @company_id nvarchar(256)
DECLARE @first_name nvarchar(256)
DECLARE @last_name nvarchar(256)
DECLARE @phone varchar(30)
DECLARE @country nvarchar(128)
DECLARE @city nvarchar(256)
DECLARE @user_state nvarchar(8)
DECLARE @postal_code varchar(64)
DECLARE @street_address nvarchar(256)
DECLARE @uuid nvarchar(64)
DECLARE @reset_link nvarchar(512)
DECLARE @login_employee nvarchar(256)
WHILE(1 = 1)
BEGIN


  SELECT @employee_id = MIN(id)
  FROM @employee_data WHERE id > @employee_id
  IF @employee_id IS NULL BREAK

  SELECT @phone = phone, @company_id = company_id, @resident_id = resident_id, @login_employee = login_employee
  FROM @employee_data WHERE id = @employee_id
  
 

select @uuid = newid()

INSERT INTO [dbo].[EmployeeRequest]
           ([token]
           ,[created_date_time]
           ,[target_employee_id]
           ,[type]
           ,[created_employee_id]
           ,[created_resident_id])
     VALUES
           (@uuid
           ,getdate()
           ,@employee_id
           ,'RESET_PASSWORD'
           ,@employee_id
           ,null)



select @reset_link = 'https://app.simplyhie.com/web-portal/reset-password?token=' + @uuid +  '&organizationId=' + Cast(@database_id as varchar) + '&companyId=' + cast(@company_id as varchar) +'&email=' + @login_employee

insert into @generated_employees values(@employee_id, @resident_id, @login_employee, @company_id, @phone, @reset_link)

END

select * from @generated_employees

COMMIT TRAN @TransactionName
CLOSE SYMMETRIC KEY SymmetricKey1;
GO