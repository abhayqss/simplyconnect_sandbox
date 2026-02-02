use exchange;
OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

DECLARE @TransactionName VARCHAR(30) = 'inserting_prs_employees';  

BEGIN TRAN @TransactionName

DECLARE @residents_employee_fields TABLE
(id bigint,
first_name varchar(256),
last_name varchar(256),
organization_id bigint,
company_id varchar(256),
database_id bigint,
phone varchar(30),
country varchar(128),
city varchar(256),
user_state varchar(8),
postal_code varchar(64),
street_address varchar(256)
);

DECLARE @generated_employees TABLE
(id bigint,
client_id bigint,
login_employee varchar(256),
company_id varchar(256),
phone varchar(30),
reset_link varchar(512)
);

DECLARE @trigger_ids TABLE (Id bigint);


INSERT INTO @residents_employee_fields
SELECT r.[id]
	  ,r.[first_name]
      ,r.[last_name]
	  ,r.[facility_id]
	  ,ss.login_company_id
      ,r.[database_id]
	  ,pt.value as phone
	  ,pa.[country]
	  ,pa.[city]
      ,pa.[state]
      ,pa.[postal_code]
      ,pa.[street_address]
     
  FROM [dbo].[resident] r join Person p on  p.id = r.person_id join PersonAddress pa on pa.person_id = p.id join SystemSetup ss on ss.database_id = r.database_id left join PersonTelecom pt on pt.person_id = p.id and pt.[sync_qualifier] = 6 where r.id = 15237 and /*facility_id= 445406 and*/ active = 1 order by r.id asc


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
  SELECT @resident_id = MIN(id)
  FROM @residents_employee_fields WHERE id > @resident_id
  IF @resident_id IS NULL BREAK

  SELECT @database_id = database_id, @first_name = first_name, @last_name = last_name, @phone = phone, @organization_id = organization_id, @company_id = company_id,
  @country = country, @city = city, @user_state = user_state, @postal_code = postal_code, @street_address = street_address
  FROM @residents_employee_fields WHERE id = @resident_id
  
  INSERT INTO [dbo].[Person]
           ([legacy_id]
           ,[legacy_table]
           ,[database_id])
     VALUES
           (N'CCN'
           ,N'CCN_MANUAL'
           ,@database_id);

select @person_id = SCOPE_IDENTITY()

UPDATE [dbo].[Person]
   SET [legacy_id] = N'CCN_' + cast(@person_id as varchar)
 WHERE id = @person_id

INSERT INTO [dbo].[name_enc]
           ([use_code]
           ,[database_id]
           ,[person_id]
           ,[family]
           ,[given]
           ,[legacy_id]
           ,[legacy_table]
           ,[family_hash]
           ,[given_hash])
     VALUES
           ('L'
           ,@database_id
           ,@person_id
           ,EncryptByKey(Key_GUID('SymmetricKey1'), @last_name) 
           ,EncryptByKey(Key_GUID('SymmetricKey1'), @first_name) 
           ,EncryptByKey(Key_GUID('SymmetricKey1'), 'CCN') 
           ,EncryptByKey(Key_GUID('SymmetricKey1'), 'RBA_Name') 
           ,[dbo].[hash_string](@last_name, default)   
           ,[dbo].[hash_string](@first_name, default)  )


select @name_id = @@IDENTITY

UPDATE [dbo].[name_enc]
   SET [legacy_id] = EncryptByKey(Key_GUID('SymmetricKey1'),  'CCN_' + cast(@person_id as varchar) + '_' + cast(@name_id as varchar))
 WHERE id = @name_id

 INSERT INTO [dbo].[PersonAddress_enc]
           ([database_id]
           ,[person_id]
           ,[legacy_id]
           ,[legacy_table]
           ,[city]
           ,[country]
           ,[use_code]
           ,[state]
           ,[postal_code]
           ,[street_address])
     VALUES
           (@database_id
           ,@person_id
           ,'CCN'
           ,'CCN_MANUAL'
           ,EncryptByKey (Key_GUID('SymmetricKey1'),@city)
           ,EncryptByKey (Key_GUID('SymmetricKey1'),@country)
           ,EncryptByKey (Key_GUID('SymmetricKey1'),'HP')
           ,EncryptByKey (Key_GUID('SymmetricKey1'),@user_state)
           ,EncryptByKey (Key_GUID('SymmetricKey1'),@postal_code)
           ,EncryptByKey (Key_GUID('SymmetricKey1'),@street_address))

select @person_address_id = @@IDENTITY

UPDATE [dbo].[PersonAddress_enc]
   SET [legacy_id] = 'CCN_' + cast(@person_id as varchar) + '_' + cast(@person_address_id as varchar)
 WHERE id = @person_address_id


 INSERT INTO [dbo].[PersonTelecom_enc]
           ([sync_qualifier]
           ,[database_id]
           ,[person_id]
           ,[legacy_id]
           ,[legacy_table]
           ,[use_code]
           ,[value]
           ,[value_normalized_hash])
     VALUES
           (6
           ,@database_id
           ,@person_id
           ,'CCN'
           ,'RBA_PersonTelecom'
           ,EncryptByKey(Key_GUID('SymmetricKey1'), 'MC') 
           ,EncryptByKey(Key_GUID('SymmetricKey1'), @phone)
           ,[dbo].[hash_string]([dbo].[normalize_phone](@phone), default))

select @person_telecom_id = @@IDENTITY


UPDATE [dbo].[PersonTelecom_enc]
   SET [legacy_id] = 'CCN_' + cast(@person_id as varchar) + '_' + cast(@person_telecom_id as varchar)
 WHERE id = @person_telecom_id

select @login_employee = lower(left(@first_name,1)) + lower(@last_name)

INSERT INTO [dbo].[Employee_enc]
           ([inactive]
           ,[legacy_id]
           ,[password]
           ,[database_id]
           ,[person_id]
           ,[care_team_role_id]
           ,[created_automatically]
           ,[secure_email_active]
           ,[secure_email_error]
           ,[modified_timestamp]
           ,[contact_4d]
           ,[first_name]
           ,[last_name]
           ,[login]
           ,[secure_email]
           ,[ccn_company]
           ,[ccn_community_id]
           ,[first_name_hash]
           ,[last_name_hash]
           ,[login_hash]
           ,[qa_incident_reports]
           ,[creator_id]
           ,[labs_coordinator]
           ,[is_incident_report_reviewer]
           ,[twilio_user_sid])
     VALUES
           (0
           ,'CCN'
           ,'password'
		   ,@database_id
           --,10386
           ,@person_id
           ,4
           ,0
           ,0
           ,NULL
           ,0
           ,0
           ,EncryptByKey(Key_GUID('SymmetricKey1'), @first_name) 
           ,EncryptByKey(Key_GUID('SymmetricKey1'), @last_name)
           ,EncryptByKey(Key_GUID('SymmetricKey1'), @login_employee)
           ,NULL
           ,EncryptByKey(Key_GUID('SymmetricKey1'), @company_id)
           ,@organization_id
           ,[dbo].[hash_string](@first_name, 150) 
           ,[dbo].[hash_string](@last_name, 150) 
           ,[dbo].[hash_string](@login_employee, 150) 
           ,0
           ,NULL
           ,0
           ,0
           ,NULL)

select @employee_id = @@IDENTITY

UPDATE [dbo].[Employee_enc]
   SET [legacy_id] = 'CCN_' + cast(@employee_id as varchar)
 WHERE id = @employee_id




INSERT INTO [dbo].[employee_associated_residents]
           ([resident_id]
           ,[employee_id])
     VALUES
           (@resident_id
           ,@employee_id)

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

INSERT INTO [dbo].[EmployeePasswordSecurity]
           ([employee_id]
           ,[locked]
           ,[locked_time]
           ,[failed_logons]
           ,[change_password_time])
     VALUES
           (@employee_id
           ,0
           ,NULL
           ,0
           ,getdate())

select @reset_link = 'https://app.simplyhie.com/web-portal/reset-password?token=' + @uuid +  '&organizationId=' + Cast(@database_id as varchar) + '&companyId=' + cast(@company_id as varchar) +'&email=' + @login_employee

insert into @generated_employees values(@employee_id, @resident_id, @login_employee, @company_id, @phone, @reset_link)

END

select * from @generated_employees

COMMIT TRAN @TransactionName
CLOSE SYMMETRIC KEY SymmetricKey1;
GO