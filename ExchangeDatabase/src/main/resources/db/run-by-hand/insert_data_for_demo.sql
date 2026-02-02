USE eldermark_demo

SET XACT_ABORT ON
GO

DECLARE @resident_given_name nvarchar(100)
DECLARE @resident_family_name nvarchar(100)
DECLARE @source_database_url varchar(255)

DECLARE @resident_id_var INT
DECLARE @database_id_var INT
DECLARE @legacy_id_prefix INT

/*

This script prepares data of specified resident for demo.

Script arguments:
    1. Resident family name,
    2. Resident given name,
    3. Url of source database,
    4. Legacy_id prefix.

Please, ensure that pair [Legacy_id prefix, Url of source database] is unique in each run of script.

*/

SET @resident_given_name = 'Tanya' --'Maxine'
SET @resident_family_name = 'Beckman' --'Allburg'
SET @source_database_url = 'jdbc:odbc:EMTest_21250'
SET @legacy_id_prefix = 3333000


if 1 != (select count([id]) from [SourceDatabase] where [url] = @source_database_url)
	begin
		raiserror('There is no source database with specified name.', 10, 1) with log;
		return;
	end

SET @database_id_var = (select [id] from [SourceDatabase] where [url] = @source_database_url)

if 1 != (select count([Resident].[id])
			from [Resident]
				join [Name] on [Resident].[person_id] = [Name].[person_id]
			where
			    [Name].[family] = @resident_family_name and
			    [Name].[given] = @resident_given_name and
			    [Resident].[database_id] = @database_id_var)
	begin
		raiserror('There are no residents or more than one resident with specified name.', 10, 1) with log;
		return;
	end

SET @resident_id_var = (select [Resident].[id]
							from [Resident]
								join [Name] on [Resident].[person_id] = [Name].[person_id]
							where
								[Name].[family] = @resident_family_name and
								[Name].[given] = @resident_given_name and
								[Resident].[database_id] = @database_id_var)


BEGIN TRANSACTION;

/* Header */
INSERT INTO [Person]
           ([legacy_id]
           ,[type_code_id]
           ,[legacy_table]
           ,[database_id])
     VALUES
           (@legacy_id_prefix + 1
           ,(select id from CcdCode where code  = '208D00000X' and code_system = '2.16.840.1.113883.6.101')
           ,'Medical_Professionals'
           ,@database_id_var)

INSERT INTO [Name]
           ([family]
           ,[family_normalized]
           ,[family_qualifier]
           ,[given]
           ,[given_normalized]
           ,[given_qualifier]
           ,[middle]
           ,[middle_normalized]
           ,[middle_qualifier]
           ,[use_code]
           ,[prefix]
           ,[prefix_qualifier]
           ,[suffix]
           ,[suffix_qualifier]
           ,[person_id]
           ,[legacy_table]
           ,[legacy_id]
           ,[database_id])
     VALUES
           ('Hostler'
           ,'hostler'
           ,NULL
           ,'Jane'
           ,'jane'
           ,NULL
           ,NULL
           ,NULL
           ,NULL
           ,'L'
           ,'Mrs.'
           ,NULL
           ,NULL
           ,NULL
           ,(SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 1)
           ,'Medical_Professionals'
           ,@legacy_id_prefix + 1
           ,@database_id_var)

INSERT INTO [PersonTelecom]
           ([use_code]
           ,[value]
           ,[value_normalized]
           ,[person_id]
           ,[sync_qualifier]
           ,[legacy_table]
           ,[legacy_id]
           ,[database_id])
     VALUES
           ('HP',
           '(432) 846-1234' ,
           '4328461234'
           ,(SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 1)
		       ,1
		        ,'Medical_Professionals'
				,@legacy_id_prefix + 1
		       ,@database_id_var)

INSERT INTO [PersonAddress]
           ([city]
           ,[country]
           ,[use_code]
           ,[postal_code]
           ,[state]
           ,[street_address]
           ,[person_id]
           ,[database_id]
            ,[legacy_table]
           ,[legacy_id])
     VALUES
           ('Minnetonka'
           ,'USA'
           ,'HP'
           ,'12345'
           ,'MN'
           ,'115-7 Orange Grove Rd'
           ,(SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 1)
           ,@database_id_var
		   ,'Medical_Professionals'
		   ,@legacy_id_prefix + 1           
           )

INSERT INTO [Person]
           ([legacy_id]
           ,[type_code_id]
           ,[legacy_table]
           ,[database_id])
     VALUES
           (@legacy_id_prefix + 2
           ,(select id from CcdCode where code  = '208D00000X' and code_system = '2.16.840.1.113883.6.101')
           ,'Medical_Professionals'
           ,@database_id_var)

INSERT INTO [Name]
           ([family]
           ,[family_normalized]
           ,[family_qualifier]
           ,[given]
           ,[given_normalized]
           ,[given_qualifier]
           ,[middle]
           ,[middle_normalized]
           ,[middle_qualifier]
           ,[use_code]
           ,[prefix]
           ,[prefix_qualifier]
           ,[suffix]
           ,[suffix_qualifier]
           ,[person_id]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           ('Train'
           ,'train'
           ,NULL
           ,'Thomas'
           ,'tomas'
           ,NULL
           ,NULL
           ,NULL
           ,NULL
           ,'L'
           ,'Mr.'
           ,NULL
           ,NULL
           ,NULL
           ,(SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 2)
           ,@database_id_var
		   ,'Medical_Professionals'
		   ,@legacy_id_prefix + 2           
           )

INSERT INTO [PersonTelecom]
           ([use_code]
           ,[value]
           ,[value_normalized]
           ,[person_id]
           ,[sync_qualifier]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           ('HP',
           '(422) 645-1777' ,
           '4226451777'
           ,(SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 2)
		       ,1
		       ,@database_id_var
		   ,'Medical_Professionals'
		   ,@legacy_id_prefix + 2  		       
		       )

INSERT INTO [PersonAddress]
           ([city]
           ,[country]
           ,[use_code]
           ,[postal_code]
           ,[state]
           ,[street_address]
           ,[person_id]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           ('Orlando'
           ,'USA'
           ,'HP'
           ,'34285'
           ,'FL'
           ,'5874 Mighican Ave NW, Unit# 102'
           ,(SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 2)
           ,@database_id_var
		   ,'Medical_Professionals'
		   ,@legacy_id_prefix + 2             
           )

INSERT INTO [Organization]
           ([legacy_id]
           ,[legacy_table]
           ,[logo_pict_id]
           ,[name]
           ,[sales_region]
           ,[database_id])
     VALUES
           (@legacy_id_prefix + 1
           ,'Company'
           ,null
           ,'Seattle Assisted Living'
           ,'North'
           ,@database_id_var)

INSERT INTO [OrganizationTelecom]
           ([use_code]
           ,[value]
           ,[organization_id]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           ('WP'
           ,'(401) 123-8745'
           ,(SELECT TOP 1 id FROM [Organization] WHERE [legacy_id] = @legacy_id_prefix + 1)
           ,@database_id_var
           ,'Company'
           ,@legacy_id_prefix + 1
           )

INSERT INTO [OrganizationAddress]
           ([city]
           ,[country]
           ,[use_code]
           ,[postal_code]
           ,[state]
           ,[street_address]
           ,[org_id]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           ('Chicago'
           ,'US'
           ,null
           ,'23541'
           ,'IL'
           ,'578 Shady Lane'
           ,(SELECT TOP 1 id FROM [Organization] WHERE [legacy_id] = @legacy_id_prefix + 1)
           ,@database_id_var
           ,'Company'
           ,@legacy_id_prefix + 1)

/*Participants are imported via DataSync*/

/*Authors are imported via DataSync*/

/*Guardians*/
INSERT INTO [Guardian]
           ([relationship_code_id]
           ,[person_id]
           ,[resident_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = 'GRFTH')
           ,(SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 2)
           ,@resident_id_var
           ,@database_id_var)

/*Data Enterers*/
INSERT INTO [DataEnterer]
           ([person_id]
           ,[database_id])
     VALUES
           ((SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 2)
           ,@database_id_var)

/*Custodians*/
INSERT INTO [Custodian]
           ([organization_id]
           ,[database_id])
     VALUES
           ((SELECT TOP 1 id FROM [Organization] WHERE [legacy_id] = @legacy_id_prefix + 1)
           ,@database_id_var)

/*Informants*/
INSERT INTO [Informant]
           ([person_id]
           ,[resident_id]
           ,[database_id])
     VALUES
           ((SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 2)
           ,@resident_id_var
           ,@database_id_var)

/*InformationRecipients*/
INSERT INTO [InformationRecipient]
           ([organization_id]
           ,[person_id]
           ,[resident_id]
           ,[database_id])
     VALUES
           ((SELECT TOP 1 id FROM [Organization] WHERE [legacy_id] = @legacy_id_prefix + 1)
           ,(SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 2)
           ,@resident_id_var
           ,@database_id_var)

/*Authenticators*/
INSERT INTO [Authenticator]
           ([time]
           ,[person_id]
           ,[resident_id]
           ,[database_id])
     VALUES
           (CURRENT_TIMESTAMP
           ,(SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 1)
           ,@resident_id_var
           ,@database_id_var)

/*LegalAuthenticators*/
INSERT INTO [LegalAuthenticator]
           ([time]
           ,[person_id]
           ,[database_id])
     VALUES
           (CURRENT_TIMESTAMP
           ,(SELECT TOP 1 id FROM [Person] WHERE [legacy_id] = @legacy_id_prefix + 1)
           ,@database_id_var)

/*Healthcare Providers*/

INSERT INTO [DocumentationOf]
           ([effective_time_high]
           ,[effective_time_low]
           ,[resident_id]
           ,[database_id])
     VALUES
           (CURRENT_TIMESTAMP
           ,'19970603'
           ,@resident_id_var
           ,@database_id_var)

INSERT INTO [Person]
           ([legacy_id]
           ,[type_code_id]
           ,[legacy_table]
           ,[database_id])
     VALUES
           (@legacy_id_prefix + 3
           ,(select id from CcdCode where code  = '208D00000X' and code_system = '2.16.840.1.113883.6.101')
           ,'Medical_Professionals'
           ,@database_id_var)

INSERT INTO [DocumentationOf_Person]
           ([documentation_of_id]
           ,[person_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [DocumentationOf])
           ,(SELECT MAX(id) FROM [Person])
           ,@database_id_var)

INSERT INTO [Name]
           ([family]
           ,[family_normalized]
           ,[family_qualifier]
           ,[given]
           ,[given_normalized]
           ,[given_qualifier]
           ,[middle]
           ,[middle_normalized]
           ,[middle_qualifier]
           ,[use_code]
           ,[prefix]
           ,[prefix_qualifier]
           ,[suffix]
           ,[suffix_qualifier]
           ,[person_id]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           ('Clurkson'
           ,'clurkson'
           ,NULL
           ,'Alex'
           ,'alex'
           ,NULL
           ,NULL
           ,NULL
           ,NULL
           ,'L'
           ,'Dr.'
           ,NULL
           ,NULL
           ,NULL
           ,(SELECT MAX(id) FROM [Person])
           ,@database_id_var
           ,'Medical_Professionals'
           ,@legacy_id_prefix + 3)

INSERT INTO [PersonTelecom]
           ([use_code]
           ,[value]
           ,[value_normalized]
           ,[person_id]
           ,[sync_qualifier]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           ('HP',
           '(705) 846-2355' ,
           '7058462355'
		       ,(SELECT MAX(id) FROM [Person])
		       ,1
		       ,@database_id_var
		       ,'Medical_Professionals'
		       , @legacy_id_prefix + 3
		       )

INSERT INTO [PersonTelecom]
           ([use_code]
           ,[value]
           ,[value_normalized]
           ,[person_id]
           ,[sync_qualifier]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           (NULL
           ,'(567) 894-5655'
           ,'5678945655'
           ,(SELECT MAX(id) FROM [Person])
           ,2
           ,@database_id_var
		   ,'Medical_Professionals'
		   , @legacy_id_prefix + 3           
           )

INSERT INTO [PersonTelecom]
           ([use_code]
           ,[value]
           ,[value_normalized]
           ,[person_id]
           ,[sync_qualifier]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           ('EMAIL'
           ,'FatherDave@Eldermark.com'
           ,NULL
           ,(SELECT MAX(id) FROM [Person])
           ,0
           ,@database_id_var
		   ,'Medical_Professionals'
		   , @legacy_id_prefix + 3           
           )

INSERT INTO [Person]
           ([legacy_id]
           ,[type_code_id]
           ,[legacy_table]
           ,[database_id])
     VALUES
           (@legacy_id_prefix + 4
           ,(select id from CcdCode where code  = '208D00000X' and code_system = '2.16.840.1.113883.6.101')
           ,'Medical_Professionals'
           ,@database_id_var)

INSERT INTO [DocumentationOf_Person]
           ([documentation_of_id]
           ,[person_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [DocumentationOf])
           ,(SELECT MAX(id) FROM [Person])
           ,@database_id_var)

INSERT INTO [Name]
           ([family]
           ,[family_normalized]
           ,[family_qualifier]
           ,[given]
           ,[given_normalized]
           ,[given_qualifier]
           ,[middle]
           ,[middle_normalized]
           ,[middle_qualifier]
           ,[use_code]
           ,[prefix]
           ,[prefix_qualifier]
           ,[suffix]
           ,[suffix_qualifier]
           ,[person_id]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           ('Edisson'
           ,'edisson'
           ,NULL
           ,'Mark'
           ,'mark'
           ,NULL
           ,NULL
           ,NULL
           ,NULL
           ,'L'
           ,'Dr.'
           ,NULL
           ,NULL
           ,NULL
           ,(SELECT MAX(id) FROM [Person])
           ,@database_id_var
           ,'Medical_Professionals'
           ,@legacy_id_prefix + 4)

/*Allergies are imported via DataSync*/

/*Problems are imported via DataSync*/

/*Procedures*/
INSERT INTO [ResidentProcedure]
           ([legacy_id]
           ,[database_id]
           ,[resident_id])
     VALUES
           (@legacy_id_prefix + 1
           ,@database_id_var
           ,@resident_id_var)

/*INSERT INTO [ProcedureType]
           ([legacy_id]
           ,[procedure_type_code]
           ,[procedure_type_code_system]
           ,[procedure_type_text]
           ,[database_id])
     VALUES
           (@legacy_id_prefix + 1
           ,'274025005'
           ,'2.16.840.1.113883.6.96'
           ,'Colonic polypectomy'
           ,@database_id_var)
*/
INSERT INTO [ProcedureActivity]
           ([method_code_id]
           ,[mood_code]
           ,[priority_code_id]
           ,[procedure_started]
           ,[procedure_stopped]
           ,[status_code]
           ,[value_id]
           ,[instructions_id]
           ,[medication_id]
           ,[procedure_type_code_id]
           ,[legacy_id]
           ,[database_id])
     VALUES
           (NULL
           ,'EVN'
           ,(select id from CcdCode where code  = 'A' and code_system = '2.16.840.1.113883.5.7')
           ,CURRENT_TIMESTAMP
           ,CURRENT_TIMESTAMP
           ,'completed'
           ,null
           ,(SELECT MAX(id) FROM [Instructions])
           ,(SELECT TOP 1 id FROM [Medication] WHERE [legacy_id] = @legacy_id_prefix + 1)
           ,(select id from CcdCode where code  = '80146002' and code_system = '2.16.840.1.113883.6.96')
           ,@legacy_id_prefix + 1
           ,@database_id_var)

INSERT INTO [Procedure_ActivityProcedure]
           ([procedure_id]
           ,[procedure_activity_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [ResidentProcedure])
           ,(SELECT MAX(id) FROM [ProcedureActivity])
           ,@database_id_var)
/*
INSERT INTO [ProcedureType]
           ([legacy_id]
           ,[procedure_type_code]
           ,[procedure_type_code_system]
           ,[procedure_type_text]
           ,[database_id])
     VALUES
           (@legacy_id_prefix + 2
           ,'80146002'
           ,'2.16.840.1.113883.6.96'
           ,'Appendectomy'
           ,@database_id_var)
*/
INSERT INTO [ProcedureActivity]
            ([method_code_id]
           ,[mood_code]
           ,[priority_code_id]
           ,[procedure_started]
           ,[procedure_stopped]
           ,[status_code]
           ,[value_id]
           ,[instructions_id]
           ,[medication_id]
           ,[procedure_type_code_id]
           ,[legacy_id]
           ,[database_id])
     VALUES
           (NULL
           ,'EVN'
           ,(select id from CcdCode where code  = 'CSP' and code_system = '2.16.840.1.113883.5.7')
           ,CURRENT_TIMESTAMP
           ,CURRENT_TIMESTAMP
           ,'completed'
           ,null
           ,(SELECT MAX(id) FROM [Instructions])
           ,(SELECT TOP 1 id FROM [Medication] WHERE [legacy_id] = @legacy_id_prefix + 2)
           ,(select id from CcdCode where code  = '397394009' and code_system = '2.16.840.1.113883.6.96')
           ,@legacy_id_prefix + 2
           ,@database_id_var)

INSERT INTO [Procedure_ActivityProcedure]
           ([procedure_id]
           ,[procedure_activity_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [ResidentProcedure])
           ,(SELECT MAX(id) FROM [ProcedureActivity])
           ,@database_id_var)

INSERT INTO [ProcedureActivity]
            ([method_code_id]
           ,[mood_code]
           ,[priority_code_id]
           ,[procedure_started]
           ,[procedure_stopped]
           ,[status_code]
           ,[value_id]
           ,[instructions_id]
           ,[medication_id]
           ,[procedure_type_code_id]
           ,[legacy_id]
           ,[database_id])
     VALUES
           (NULL
           ,'EVN'
           ,(select TOP 1 id from CcdCode where code  = 'CR' and code_system = '2.16.840.1.113883.5.7')
           ,CURRENT_TIMESTAMP
           ,CURRENT_TIMESTAMP
           ,'completed'
           ,null
           ,(SELECT MAX(id) FROM [Instructions])
           ,(SELECT TOP 1 id FROM [Medication] WHERE [legacy_id] = @legacy_id_prefix + 3)
           ,(select id from CcdCode where code  = '274025005'  and code_system = '2.16.840.1.113883.6.96')
           ,@legacy_id_prefix + 3
           ,@database_id_var)

INSERT INTO [Procedure_ActivityProcedure]
           ([procedure_id]
           ,[procedure_activity_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [ResidentProcedure])
           ,(SELECT MAX(id) FROM [ProcedureActivity])
           ,@database_id_var)

/*Results*/

INSERT INTO [Result]
           ([legacy_id]
           ,[class_code]
           ,[code_id]
           ,[status_code]
           ,[database_id]
           ,[resident_id])
     VALUES
           (@legacy_id_prefix + 1
           ,'CLUSTER'
           ,(select id from CcdCode where code  = '57021-8' and code_system = '2.16.840.1.113883.6.1')
           ,'Completed'
           ,@database_id_var
           ,@resident_id_var)

INSERT INTO [ResultObservation]
           ([effective_time]
           ,[method_code_id]
           ,[result_type_code_id]
           ,[status_code]
           ,[site_code_id]
           ,[result_text]
           ,[result_value]
           ,[result_value_unit]
           ,[author_id]
           ,[database_id])
     VALUES
           (CURRENT_TIMESTAMP
           ,NULL
           ,(select id from CcdCode where code  = '30313-1' and code_system = '2.16.840.1.113883.6.1')
           ,'Completed'
           ,NULL
           ,'HGB'
           ,'100'
           ,'10+3/ul'
           ,(SELECT MAX(id) from [Author] where [resident_id] = @resident_id_var)
           ,@database_id_var)

INSERT INTO [ResultObservationRange]
           ([result_observation_id]
           ,[result_range]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [ResultObservation])
           ,'4.3-10.8 10+3/ul'
           ,@database_id_var)

if 1 != (select id from CcdCode where code  = 'N' and code_system = '2.16.840.1.113883.5.83')
 begin
	INSERT INTO [ResultObservationInterpretationCode]
           ([result_observation_id]
           ,[interpretation_code_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [ResultObservation])
           ,(select id from CcdCode where code  = 'N' and code_system = '2.16.840.1.113883.5.83')
           ,@database_id_var)
 end

INSERT INTO [Result_ResultObservation]
           ([result_id]
           ,[result_observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [Result])
           ,(SELECT MAX(id) from [ResultObservation])
           ,@database_id_var)

INSERT INTO [ResultObservation]
           ([effective_time]
           ,[method_code_id]
           ,[result_type_code_id]
           ,[status_code]
           ,[site_code_id]
           ,[result_text]
           ,[result_value]
           ,[result_value_unit]
           ,[author_id]
           ,[database_id])
     VALUES
           (CURRENT_TIMESTAMP
           ,NULL
           ,(select id from CcdCode where code  = '30313-1'  and code_system = '2.16.840.1.113883.6.1')
           ,'Active'
           ,NULL
           ,'ALT (SGPT)'
           ,'50'
           ,'mg/dl'
           ,(SELECT MAX(id) from [Author] where [resident_id] = @resident_id_var)
           ,@database_id_var)

INSERT INTO [ResultObservationRange]
           ([result_observation_id]
           ,[result_range]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [ResultObservation])
           ,'50-100 mg/dl'
           ,@database_id_var)

INSERT INTO [Result_ResultObservation]
           ([result_id]
           ,[result_observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [Result])
           ,(SELECT MAX(id) from [ResultObservation])
           ,@database_id_var)

INSERT INTO [ResultObservation]
           ([effective_time]
           ,[method_code_id]
           ,[result_type_code_id]
           ,[status_code]
           ,[site_code_id]
           ,[result_text]
           ,[result_value]
           ,[result_value_unit]
           ,[author_id]
           ,[database_id])
     VALUES
           (CURRENT_TIMESTAMP
           ,NULL
           ,(select id from CcdCode where code  = '33765-9' and code_system = '2.16.840.1.113883.6.1')
           ,'Completed'
           ,NULL
           ,'WBC'
           ,'13'
           ,'meq/l'
           ,(SELECT MAX(id) from [Author] where [resident_id] = @resident_id_var)
           ,@database_id_var)

INSERT INTO [ResultObservationRange]
           ([result_observation_id]
           ,[result_range]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [ResultObservation])
           ,'135-145 meq/l'
           ,@database_id_var)

if 1 != (select id from CcdCode where code  = 'L' and code_system = '2.16.840.1.113883.5.83')
 begin
	INSERT INTO [ResultObservationInterpretationCode]
			   ([result_observation_id]
			   ,[interpretation_code_id]
			   ,[database_id])
		 VALUES
			   ((SELECT MAX(id) from [ResultObservation])
			   ,(select id from CcdCode where code  = 'L' and code_system = '2.16.840.1.113883.5.83')
			   ,@database_id_var)
end			   

INSERT INTO [Result_ResultObservation]
           ([result_id]
           ,[result_observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [Result])
           ,(SELECT MAX(id) from [ResultObservation])
           ,@database_id_var)

INSERT INTO [ResultObservation]
           ([effective_time]
           ,[method_code_id]
           ,[result_type_code_id]
           ,[status_code]
           ,[site_code_id]
           ,[result_text]
           ,[result_value]
           ,[result_value_unit]
           ,[author_id]
           ,[database_id])
     VALUES
           (CURRENT_TIMESTAMP
           ,NULL
           ,(select id from CcdCode where code  = '26515-7' and code_system = '2.16.840.1.113883.6.1')
           ,'Suspended'
           ,NULL
           ,'PLT'
           ,'50'
           ,'g/dl'
           ,null
           ,@database_id_var)

INSERT INTO [ResultObservationRange]
           ([result_observation_id]
           ,[result_range]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [ResultObservation])
           ,'M 13-18 g/dl; F 12-16 g/dl'
           ,@database_id_var)

if 1 != (select id from CcdCode where code  = 'L' and code_system = '2.16.840.1.113883.5.83')
begin
INSERT INTO [ResultObservationInterpretationCode]
           ([result_observation_id]
           ,[interpretation_code_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [ResultObservation])
           ,(select id from CcdCode where code  = 'L')
           ,@database_id_var)
end

INSERT INTO [Result_ResultObservation]
           ([result_id]
           ,[result_observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [Result])
           ,(SELECT MAX(id) from [ResultObservation])
           ,@database_id_var)

/*Encounters*/

INSERT INTO [Encounter]
           ([legacy_id]
           ,[disposition_code_id]
           ,[effective_time]
           ,[encounter_type_code_id]
           ,[encounter_type_text]
           ,[database_id]
           ,[problem_observation_id]
           ,[resident_id])
     VALUES
           (@legacy_id_prefix + 1
           ,NULL
           ,'20130909'
           ,(select id from CcdCode where code  = '99241' and code_system = '2.16.840.1.113883.6.12')
           ,'Examination'
           ,@database_id_var
           ,(SELECT TOP 1 [ProblemObservation].[id]
                 FROM [ProblemObservation]
                 INNER JOIN [Problem]
                 ON [ProblemObservation].[problem_id] = [Problem].[id]
                 WHERE [Problem].[legacy_id] = @legacy_id_prefix + 1)
           ,@resident_id_var)

if 1 != (select id from CcdCode where code  = '59058001' and code_system = '2.16.840.1.113883.6.96')
 begin
	INSERT INTO [EncounterProviderCode]
           ([encounter_id]
           ,[provider_code_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [Encounter])
           ,(select id from CcdCode where code  = '59058001' and code_system = '2.16.840.1.113883.6.96')
           ,@database_id_var)
end

INSERT INTO [Indication]
           ([code_id]
           ,[effective_time_high]
           ,[effective_time_low]
           ,[value_code_id]
           ,[database_id]
           ,[legacy_table]
           ,[legacy_id])
     VALUES
           ((select id from CcdCode where code  = '404684003' and code_system = '2.16.840.1.113883.6.96')
           ,NULL
           ,NULL
           ,(select id from CcdCode where code  = '32398004' and code_system = '2.16.840.1.113883.6.96')
           ,@database_id_var
           ,'SomeLegacyTable'
           ,@legacy_id_prefix + 1)

INSERT INTO [Encounter_Indication]
           ([encounter_id]
           ,[indication_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [Encounter])
           ,(SELECT MAX(id) from [Indication])
           ,@database_id_var)

INSERT INTO [DeliveryLocation]
           ([code_id]
           ,[name]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = 'GACH' and code_system = '2.16.840.1.113883.5.111')
           ,'Good Health Clinic'
           ,@database_id_var)

INSERT INTO [DeliveryLocation_OrganizationAddress]
           ([delivery_location_id]
           ,[address_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [DeliveryLocation])
           ,(SELECT MAX(id) from [OrganizationAddress])
           ,@database_id_var)

INSERT INTO [DeliveryLocation_OrganizationTelecom]
           ([delivery_location_id]
           ,[telecom_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [DeliveryLocation])
           ,(SELECT MAX(id) from [OrganizationTelecom])
           ,@database_id_var)

INSERT INTO [Encounter_DeliveryLocation]
           ([encounter_id]
           ,[location_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [Encounter])
           ,(SELECT MAX(id) from [DeliveryLocation])
           ,@database_id_var)

INSERT INTO [Encounter]
           ([legacy_id]
           ,[disposition_code_id]
           ,[effective_time]
           ,[encounter_type_code_id]
           ,[encounter_type_text]
           ,[database_id]
           ,[problem_observation_id]
           ,[resident_id])
     VALUES
           (@legacy_id_prefix + 2
           ,NULL
           ,'20120608'
           ,(select id from CcdCode where code  = '99301' and code_system = '2.16.840.1.113883.6.12')
           ,'Nursing Home Care'
           ,@database_id_var
           ,(SELECT TOP 1 [ProblemObservation].[id]
                 FROM [ProblemObservation]
                 INNER JOIN [Problem]
                 ON [ProblemObservation].[problem_id] = [Problem].[id]
                 WHERE [Problem].[legacy_id] = @legacy_id_prefix + 1)
           ,@resident_id_var)

INSERT INTO [Encounter]
            ([legacy_id]
           ,[disposition_code_id]
           ,[effective_time]
           ,[encounter_type_code_id]
           ,[encounter_type_text]
           ,[database_id]
           ,[problem_observation_id]
           ,[resident_id])
     VALUES
           (@legacy_id_prefix + 3
           ,NULL
           ,'20100214'
           ,(select id from CcdCode where code  = '99241'  and code_system = '2.16.840.1.113883.6.12')
           ,'Office consultation'
           ,@database_id_var
           ,(SELECT TOP 1 [ProblemObservation].[id]
                 FROM [ProblemObservation]
                 INNER JOIN [Problem]
                 ON [ProblemObservation].[problem_id] = [Problem].[id]
                 WHERE [Problem].[legacy_id] = @legacy_id_prefix + 1)
           ,@resident_id_var)

INSERT INTO [DeliveryLocation]
           ([code_id]
           ,[name]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = 'GACH'  and code_system = '2.16.840.1.113883.5.111')
           ,'Good Health Insurance'
           ,@database_id_var)

INSERT INTO [Encounter_DeliveryLocation]
           ([encounter_id]
           ,[location_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) from [Encounter])
           ,(SELECT MAX(id) from [DeliveryLocation])
           ,@database_id_var)

/*Advance Directives are imported via DataSync (09.01.14)*/

/*Family History*/

INSERT INTO [FamilyHistory]
           ([related_subject_code_id]
           ,[person_information_id]
           ,[administrative_gender_code_id]
           ,[birth_time]
           ,[deceased_ind]
           ,[deceased_time]
           ,[resident_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = 'FTH'  and code_system = '2.16.840.1.113883.5.111')
           ,'d42ebf70-5c89-11db-b0de-0800200c9a66'
           ,(select id from CcdCode where code  = 'M'   and code_system = '2.16.840.1.113883.5.1')
           ,CURRENT_TIMESTAMP
           ,0
           ,null
           ,@resident_id_var
           ,@database_id_var)

INSERT INTO [FamilyHistoryObservation]
           ([age_observation_unit]
           ,[age_observation_value]
           ,[deceased]
           ,[effective_time]
           ,[problem_type_code_id]
           ,[problem_value_id]
           ,[family_history_id]
           ,[database_id])
     VALUES
           ('a'
           ,77
           ,null
           ,null
           ,(select id from CcdCode where code  = '404684003'  and code_system = '2.16.840.1.113883.6.96')
           ,(select id from CcdCode where code  = '302002000' and code_system = '2.16.840.1.113883.6.96')
           ,(SELECT MAX(id) from [FamilyHistory])
           ,@database_id_var),
            (null
           ,null
           ,null
           ,null
           ,null
           ,null
           ,(SELECT MAX(id) from [FamilyHistory])
           ,@database_id_var)

INSERT INTO [FamilyHistory]
            ([related_subject_code_id]
           ,[person_information_id]
           ,[administrative_gender_code_id]
           ,[birth_time]
           ,[deceased_ind]
           ,[deceased_time]
           ,[resident_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = 'GRPRN'  and code_system = '2.16.840.1.113883.5.111')
           ,'34ferf-sdf-11db-345-0800200cgddfg9a66'
           ,(select id from CcdCode where code  = 'F' and code_system = '2.16.840.1.113883.5.1')
           ,CURRENT_TIMESTAMP
           ,1
           ,CURRENT_TIMESTAMP
           ,@resident_id_var
           ,@database_id_var)

INSERT INTO [FamilyHistoryObservation]
           ([age_observation_unit]
           ,[age_observation_value]
           ,[deceased]
           ,[effective_time]
           ,[problem_type_code_id]
           ,[problem_value_id]
           ,[family_history_id]
           ,[database_id])
     VALUES
           ('a'
           ,99
           ,1
           ,CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = '282291009' and code_system = '2.16.840.1.113883.6.96')
           ,(select id from CcdCode where code  = '234422006' and code_system = '2.16.840.1.113883.6.96')
           ,(SELECT MAX(id) from [FamilyHistory])
           ,@database_id_var),
            ('a'
           ,55
           ,null
           ,null
           ,null   
           ,(select id from CcdCode where code  = '48167000' and code_system = '2.16.840.1.113883.6.96')
           ,(SELECT MAX(id) from [FamilyHistory])
           ,@database_id_var)

INSERT INTO [FamilyHistory]
            ([related_subject_code_id]
           ,[person_information_id]
           ,[administrative_gender_code_id]
           ,[birth_time]
           ,[deceased_ind]
           ,[deceased_time]
           ,[resident_id]
           ,[database_id])
     VALUES
           (null
           ,null
           ,(select id from CcdCode where code  = 'M' and code_system = '2.16.840.1.113883.5.1')
           ,CURRENT_TIMESTAMP
           ,0
           ,null
           ,@resident_id_var
           ,@database_id_var)

/*Medications are imported via DataSync*/

/*Immunizations*/

INSERT INTO [ImmunizationMedicationInformation]
           ([code_id]
           ,[text]
           ,[organization_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '88' and code_system = '2.16.840.1.113883.6.59')
           ,'Influenza virus vaccine, IM'
           ,(SELECT MAX(id) FROM [Organization])
           ,@database_id_var)

INSERT INTO [Immunization]
           ([legacy_id]
           ,[administration_unit_code_id]
           ,[code_id]
           ,[dose_quantity]
           ,[dose_units]
           ,[immunization_started]
           ,[immunization_stopped]
           ,[mood_code]
           ,[refusal]
           ,[repeat_number]
           ,[repeat_number_mood]
           ,[route_code_id]
           ,[site_code_id]
           ,[status_code]
           ,[text]
           ,[database_id]
           ,[immunization_refusal_reason_id]
           ,[instructions_id]
           ,[medication_dispense_id]
           ,[immunization_medication_information_id]
           ,[medication_supply_order_id]
           ,[reaction_observation_id]
           ,[resident_id])
     VALUES
           (@legacy_id_prefix + 1
           ,(select id from CcdCode where code  = 'C42887' and code_system = '2.16.840.1.113883.3.26.1.1')
           ,NULL
           ,100
           ,'ml'
           ,'20131218'
           ,'20131220'
           ,'EVN'
           ,'false'
           ,NULL
           ,NULL
           ,NULL
           ,NULL
           ,'completed'
           ,NULL
           ,@database_id_var
           ,NULL
           ,NULL
           ,NULL
           ,(SELECT MAX(id) FROM [ImmunizationMedicationInformation])
           ,NULL
           ,NULL
           ,@resident_id_var)

INSERT INTO [ImmunizationMedicationInformation]
           ([code_id]
           ,[text]
           ,[organization_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '33' and code_system = '2.16.840.1.113883.6.59')
           ,'Pneumococcal polysaccharide vaccine, IM'
           ,(SELECT MAX(id) FROM [Organization])
           ,@database_id_var)

INSERT INTO [Immunization]
           ([legacy_id]
           ,[administration_unit_code_id]
           ,[code_id]
           ,[dose_quantity]
           ,[dose_units]
           ,[immunization_started]
           ,[immunization_stopped]
           ,[mood_code]
           ,[refusal]
           ,[repeat_number]
           ,[repeat_number_mood]
           ,[route_code_id]
           ,[site_code_id]
           ,[status_code]
           ,[text]
           ,[database_id]
           ,[immunization_refusal_reason_id]
           ,[instructions_id]
           ,[medication_dispense_id]
           ,[immunization_medication_information_id]
           ,[medication_supply_order_id]
           ,[reaction_observation_id]
           ,[resident_id])
     VALUES
           (@legacy_id_prefix + 2
           ,(select id from CcdCode where code  = 'C42909' and code_system = '2.16.840.1.113883.3.26.1.1')
           ,NULL
           ,100
           ,'ml'
           ,'20120102'
           ,'20120105'
           ,'EVN'
           ,'false'
           ,NULL
           ,NULL
           ,NULL
           ,NULL
           ,'completed'
           ,NULL
           ,@database_id_var
           ,NULL
           ,NULL
           ,NULL
           ,(SELECT MAX(id) FROM [ImmunizationMedicationInformation])
           ,NULL
           ,NULL
           ,@resident_id_var)

INSERT INTO [ImmunizationMedicationInformation]
           ([code_id]
           ,[text]
           ,[organization_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '103'  and code_system = '2.16.840.1.113883.6.59')
           ,'Tetanus and diphtheria toxoids, IM'
           ,(SELECT MAX(id) FROM [Organization])
           ,@database_id_var)

INSERT INTO [Immunization]
           ([legacy_id]
           ,[administration_unit_code_id]
           ,[code_id]
           ,[dose_quantity]
           ,[dose_units]
           ,[immunization_started]
           ,[immunization_stopped]
           ,[mood_code]
           ,[refusal]
           ,[repeat_number]
           ,[repeat_number_mood]
           ,[route_code_id]
           ,[site_code_id]
           ,[status_code]
           ,[text]
           ,[database_id]
           ,[immunization_refusal_reason_id]
           ,[instructions_id]
           ,[medication_dispense_id]
           ,[immunization_medication_information_id]
           ,[medication_supply_order_id]
           ,[reaction_observation_id]
           ,[resident_id])
     VALUES
           (@legacy_id_prefix + 3
           ,(select id from CcdCode where code  = 'C42998' and code_system = '2.16.840.1.113883.3.26.1.1')
           ,NULL
           ,100
           ,'ml'
           ,'20000722'
           ,'20000723'
           ,'EVN'
           ,'false'
           ,NULL
           ,NULL
           ,NULL
           ,NULL
           ,'completed'
           ,NULL
           ,@database_id_var
           ,NULL
           ,NULL
           ,NULL
           ,(SELECT MAX(id) FROM [ImmunizationMedicationInformation])
           ,NULL
           ,NULL
           ,@resident_id_var)

/*Payer Section*/

INSERT INTO [Payer]
           ([coverage_activity_id]
           ,[resident_id]
           ,[database_id])
     VALUES
           ('23452345636546455'
           ,@resident_id_var
           ,@database_id_var)

INSERT INTO [PolicyActivity]
           ([guarantor_time]
           ,[health_insurance_type_code_id]
           ,[participant_date_of_birth]
           ,[payer_financially_responsible_party_code_id]
           ,[sequence_number]
           ,[guarantor_organization_id]
           ,[guarantor_person_id]
           ,[participant_id]
           ,[payer_id]
           ,[payer_org_id]
           ,[subscriber_id]
           ,[database_id])
     VALUES
           (    /*'638e8f80-41ff-11e3-9bb0-0002a5d5c51b'*/
           CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = '12' and code_system = '2.16.840.1.113883.6.255.1336')
           ,CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = 'INVSBJ' and code_system = '2.16.840.1.113883.5.110')
           ,1
           ,(SELECT MAX(id) FROM [Organization])
           ,null
           ,(SELECT MAX(id) FROM [Participant])
           ,(SELECT MAX(id) FROM [Payer])
           ,(SELECT MAX(id) FROM [Organization])
           ,(SELECT MAX(id) FROM [Participant])
           ,@database_id_var)

INSERT INTO [AuthorizationActivity]
           ([policy_activity_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [PolicyActivity])
           ,@database_id_var)
           
if 1 != (select id from CcdCode where code  = '73761001' and code_system = '2.16.840.1.113883.6.96')
 begin
	INSERT INTO [AuthorizationActivity_ClinicalStatement]
           ([authorization_activity_id]
           ,[clinical_statement_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [AuthorizationActivity])
           ,(select id from CcdCode where code  = '73761001' and code_system = '2.16.840.1.113883.6.96')
           ,@database_id_var)
end           

if 1 != (select id from CcdCode where code  = '534534544'  and code_system = '2.16.840.1.113883.6.96')
 begin
	INSERT INTO [AuthorizationActivity_ClinicalStatement]
           ([authorization_activity_id]
           ,[clinical_statement_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [AuthorizationActivity])
           ,(select id from CcdCode where code  = '534534544' and code_system = '2.16.840.1.113883.6.96')
           ,@database_id_var)
end           

INSERT INTO [Payer]
           ([coverage_activity_id]
           ,[resident_id]
           ,[database_id])
     VALUES
           ('535634563453456346'
           ,@resident_id_var
           ,@database_id_var)

INSERT INTO [PolicyActivity]
           ([guarantor_time]
           ,[health_insurance_type_code_id]
           ,[participant_date_of_birth]
           ,[payer_financially_responsible_party_code_id]
           ,[sequence_number]
           ,[guarantor_organization_id]
           ,[guarantor_person_id]
           ,[participant_id]
           ,[payer_id]
           ,[payer_org_id]
           ,[subscriber_id]
           ,[database_id])
     VALUES
           (/*'34093a20-4200-11e3-847f-0002a5d5c51b'*/
           CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = '13'  and code_system = '2.16.840.1.113883.6.255.1336')
           ,null
           ,null
           ,2
           ,null
           ,null
           ,null
           ,(SELECT MAX(id) FROM [Payer])
           ,(SELECT MAX(id) FROM [Organization])
           ,null
           ,@database_id_var)

INSERT INTO [AuthorizationActivity]
           ([policy_activity_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [PolicyActivity])
           ,@database_id_var)

if 1 != (select id from CcdCode where code  = '7375006'  and code_system = '2.16.840.1.113883.6.96')
 begin
	INSERT INTO [AuthorizationActivity_ClinicalStatement]
           ([authorization_activity_id]
           ,[clinical_statement_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [AuthorizationActivity])
           ,(select id from CcdCode where code  = '7375006' and code_system = '2.16.840.1.113883.6.96')
           ,@database_id_var)
end           

INSERT INTO [Payer]
           ([coverage_activity_id]
           ,[resident_id]
           ,[database_id])
     VALUES
           ('756754675367546754'
           ,@resident_id_var
           ,@database_id_var)

INSERT INTO [PolicyActivity]
           ([guarantor_time]
           ,[health_insurance_type_code_id]
           ,[participant_date_of_birth]
           ,[payer_financially_responsible_party_code_id]
           ,[sequence_number]
           ,[guarantor_organization_id]
           ,[guarantor_person_id]
           ,[participant_id]
           ,[payer_id]
           ,[payer_org_id]
           ,[subscriber_id]
           ,[database_id])
     VALUES
           (/*'64208460-4201-11e3-b62f-0002a5d5c51b'*/
           CURRENT_TIMESTAMP
           ,null
           ,CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = 'COVPTY' and code_system = '2.16.840.1.113883.5.110')
           ,4
           ,null
           ,null
           ,null
           ,(SELECT MAX(id) FROM [Payer])
           ,(SELECT MAX(id) FROM [Organization])
           ,(SELECT MAX(id) FROM [Participant])
           ,@database_id_var)

INSERT INTO [CoveragePlanDescription]
           ([text]
           ,[policy_activity_id]
           ,[database_id])
     VALUES
           ('Some policy activity'
           ,(SELECT MAX(id) FROM [PolicyActivity])
           ,@database_id_var)

INSERT INTO [PolicyActivity]
           ([guarantor_time]
           ,[health_insurance_type_code_id]
           ,[participant_date_of_birth]
           ,[payer_financially_responsible_party_code_id]
           ,[sequence_number]
           ,[guarantor_organization_id]
           ,[guarantor_person_id]
           ,[participant_id]
           ,[payer_id]
           ,[payer_org_id]
           ,[subscriber_id]
           ,[database_id])
     VALUES
           (/*'dc5877c0-4202-11e3-ab65-0002a5d5c51b'*/
           null
           ,(select id from CcdCode where code  = '14' and code_system = '2.16.840.1.113883.6.255.1336')
           ,null
           ,(select id from CcdCode where code  = 'COVPTY' and code_system = '2.16.840.1.113883.5.110')
           ,null
           ,null
           ,(SELECT MAX(id) FROM [Person])
           ,null
           ,(SELECT MAX(id) FROM [Payer])
           ,null
           ,(SELECT TOP 1 id FROM [Participant] WHERE [legacy_id] = @legacy_id_prefix + 1)
           ,@database_id_var)

INSERT INTO [CoveragePlanDescription]
           ([text]
           ,[policy_activity_id]
           ,[database_id])
     VALUES
           ('Some policy activity'
           ,(SELECT MAX(id) FROM [PolicyActivity])
           ,@database_id_var)

/* Social History*/

INSERT INTO [SocialHistory]
           ([legacy_id]
           ,[database_id]
           ,[resident_id])
     VALUES
           (@legacy_id_prefix + 1
           ,@database_id_var
           ,@resident_id_var)

INSERT INTO [SocialHistoryObservation]
           ([free_text]
           ,[type_code_id]
           ,[value_code_id]
           ,[social_history_id]
           ,[database_id])
     VALUES
           ('Health-related behavior'
           ,(select id from CcdCode where code  = '228272008'  and code_system = '2.16.840.1.113883.6.96')
           ,null
           ,(SELECT MAX(id) FROM [SocialHistory])
           ,@database_id_var),
           ('Exercise'
           ,(select id from CcdCode where code  = '256235009' and code_system = '2.16.840.1.113883.6.96')
           ,null
           ,(SELECT MAX(id) FROM [SocialHistory])
           ,@database_id_var)

INSERT INTO [PregnancyObservation]
           ([effective_time_low]
           ,[effective_time_high]
           ,[social_history_id]
           ,[database_id])
     VALUES
           ('20130629'
           ,'20140329'
           ,(SELECT MAX(id) FROM [SocialHistory])
           ,@database_id_var)

INSERT INTO [TobaccoUse]
           ([effective_time_low]
           ,[value_code_id]
           ,[social_history_id]
           ,[database_id])
     VALUES
           ('20001010'
           ,(select id from CcdCode where code  = '266919005'  and code_system = '2.16.840.1.113883.6.96')
           ,(SELECT MAX(id) FROM [SocialHistory])
           ,@database_id_var)

/* Medical Equipment section is imported via DataSync*/

/* Plan Of Care*/

INSERT INTO [PlanOfCare]
           ([resident_id]
           ,[database_id])
     VALUES
           (@resident_id_var
           ,@database_id_var)

INSERT INTO [PlanOfCareActivity]
           ([code_id]
           ,[effective_time]
           ,[mood_code]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '310634005'  and code_system = '2.16.840.1.113883.6.96')
           ,'20110310'
           ,'INT'
           ,@database_id_var)

INSERT INTO [PlanOfCare_Act]
           ([plan_of_care_id]
           ,[act_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [PlanOfCare])
           ,(SELECT MAX(id) FROM [PlanOfCareActivity])
           ,@database_id_var)

INSERT INTO [PlanOfCareActivity]
           ([mood_code]
           ,[database_id])
     VALUES
           ('ARQ'
           ,@database_id_var)

INSERT INTO [PlanOfCare_Encounter]
           ([plan_of_care_id]
           ,[encounter_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [PlanOfCare])
           ,(SELECT MAX(id) FROM [PlanOfCareActivity])
           ,@database_id_var)

INSERT INTO [PlanOfCareActivity]
           ([code_id]
           ,[effective_time]
           ,[mood_code]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '310634005' and code_system = '2.16.840.1.113883.6.96')
           ,'20110310'
           ,'PRMS'
           ,@database_id_var)

INSERT INTO [PlanOfCare_Observation]
           ([plan_of_care_id]
           ,[observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [PlanOfCare])
           ,(SELECT MAX(id) FROM [PlanOfCareActivity])
           ,@database_id_var)

INSERT INTO [PlanOfCareActivity]
           ([code_id]
           ,[effective_time]
           ,[mood_code]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '23426006' and code_system = '2.16.840.1.113883.6.96')
           ,CURRENT_TIMESTAMP
           ,'PRMS'
           ,@database_id_var)

INSERT INTO [PlanOfCare_Observation]
           ([plan_of_care_id]
           ,[observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [PlanOfCare])
           ,(SELECT MAX(id) FROM [PlanOfCareActivity])
           ,@database_id_var)

INSERT INTO [PlanOfCareActivity]
           ([code_id]
           ,[effective_time]
           ,[mood_code]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '28446108' and code_system = '2.16.840.1.113883.6.96')
           ,CURRENT_TIMESTAMP
           ,'PRP'
           ,@database_id_var)

INSERT INTO [PlanOfCare_Procedure]
           ([plan_of_care_id]
           ,[procedure_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [PlanOfCare])
           ,(SELECT MAX(id) FROM [PlanOfCareActivity])
           ,@database_id_var)

INSERT INTO [PlanOfCareActivity]
           ([mood_code]
           ,[database_id])
     VALUES
           ('INT'
           ,@database_id_var)

INSERT INTO [PlanOfCare_Supply]
           ([plan_of_care_id]
           ,[supply_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [PlanOfCare])
           ,(SELECT MAX(id) FROM [PlanOfCareActivity])
           ,@database_id_var)

INSERT INTO [PlanOfCareActivity]
		       ([code_id],
            [mood_code]
           ,[database_id])
     VALUES
		       ((select id from CcdCode where code  = '408816000' and code_system = '2.16.840.1.113883.6.96'),
            'RQO'
           ,@database_id_var)

INSERT INTO [PlanOfCare_SubstanceAdministration]
           ([plan_of_care_id]
           ,[substance_administration_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [PlanOfCare])
           ,(SELECT MAX(id) FROM [PlanOfCareActivity])
           ,@database_id_var)

INSERT INTO [Instructions]
           ([code_id]
           ,[text]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '171044003'  and code_system = '2.16.840.1.113883.6.96')
           ,'Patient may have low grade fever'
           ,@database_id_var)

INSERT INTO [PlanOfCare_Instructions]
           ([plan_of_care_id]
           ,[instruction_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [PlanOfCare]),
            (SELECT MAX(id) FROM [Instructions]),
            @database_id_var)

/*Functional Status*/

INSERT INTO [FunctionalStatus]
           ([resident_id]
           ,[database_id])
     VALUES
           (@resident_id_var
           ,@database_id_var)

INSERT INTO [PressureUlcerObservation]
           ([depth_of_wound_value]
           ,[effective_time]
           ,[length_of_wound_value]
           ,[negation_ind]
           ,[text]
           ,[value_id]
           ,[width_of_wound_value]
           ,[functional_status_id]
           ,[database_id])
     VALUES
           (0.2
           ,CURRENT_TIMESTAMP
           ,1.3
           ,0
           ,'Pressure ulcer stage 3'
           ,(select id from CcdCode where code  = '421927004' and code_system = '2.16.840.1.113883.6.96')
           ,0.5
           ,(SELECT MAX(id) FROM [FunctionalStatus])
           ,@database_id_var)

INSERT INTO [StatusResultOrganizer]
           ([code_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = 'd5')
           ,@database_id_var)

INSERT INTO [FunctionalStatus_FunctionalStatusResultOrganizer]
           ([functional_status_id]
           ,[result_organizer_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [FunctionalStatus])
           ,(SELECT MAX(id) FROM [StatusResultOrganizer])
           ,@database_id_var)

INSERT INTO [StatusResultObservation]
           ([code_id]
           ,[effective_time]
           ,[method_code_id]
           ,[target_site_code_id]
           ,[text]
		   ,[value_code_id]
           ,[value]
           ,[value_unit]
           ,[author_id]
           ,[database_id])
     VALUES
           (null
           ,CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = '168731009' and code_system = '2.16.840.1.113883.6.96')
           ,(select id from CcdCode where code  = '76552005' and code_system = '2.16.840.1.113883.6.96')
           ,'Dressing upper body in last 7D'
           ,(select id from CcdCode where code  = '371153006' and code_system = '2.16.840.1.113883.6.96')
		   ,null
           ,null
           ,null
           ,@database_id_var)

INSERT INTO [StatusResultOrganizer_StatusResultObservation]
           ([status_result_organizer_id]
           ,[status_result_observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [StatusResultOrganizer])
           ,(SELECT MAX(id) FROM [StatusResultObservation])
           ,@database_id_var)

INSERT INTO [StatusResultOrganizer]
           ([code_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '801460020' and code_system = '2.16.840.1.113883.6.96')
           ,@database_id_var)

INSERT INTO [FunctionalStatus_CognitiveStatusResultOrganizer]
           ([functional_status_id]
           ,[result_organizer_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [FunctionalStatus])
           ,(SELECT MAX(id) FROM [StatusResultOrganizer])
           ,@database_id_var)

INSERT INTO [StatusResultObservation]
            ([code_id]
           ,[effective_time]
           ,[method_code_id]
           ,[target_site_code_id]
           ,[text]
		   ,[value_code_id]
           ,[value]
           ,[value_unit]
           ,[author_id]
           ,[database_id])
     VALUES
           (null
           ,CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = '168731009' and code_system = '2.16.840.1.113883.6.96')
           ,(select id from CcdCode where code  = '76552005' and code_system = '2.16.840.1.113883.6.96')
           ,'aggressive behavior'
		   ,null
           ,'57'
           ,'a'
           ,(SELECT MAX(id) from [Author] where [resident_id] = @resident_id_var)
           ,@database_id_var)

INSERT INTO [StatusResultOrganizer_StatusResultObservation]
           ([status_result_organizer_id]
           ,[status_result_observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [StatusResultOrganizer])
           ,(SELECT MAX(id) FROM [StatusResultObservation])
           ,@database_id_var)

INSERT INTO [StatusProblemObservation]
           ([method_code_id]
           ,[negation_ind]
           ,[text]
           ,[time_high]
           ,[time_low]
           ,[value_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '168731009' and code_system = '2.16.840.1.113883.6.96')
           ,1
           ,'Dysphagia'
           ,CURRENT_TIMESTAMP
           ,CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = '409586006'  and code_system = '2.16.840.1.113883.6.96') 
           ,@database_id_var)

INSERT INTO [FunctionalStatus_FunctionalStatusProblemObservation]
           ([functional_status_id]
           ,[problem_observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [FunctionalStatus])
           ,(SELECT MAX(id) FROM [StatusProblemObservation])
           ,@database_id_var)

INSERT INTO [StatusProblemObservation]
           ([method_code_id]
           ,[negation_ind]
           ,[resolved]
           ,[text]
           ,[time_high]
           ,[time_low]
           ,[value_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '76552005' and code_system = '2.16.840.1.113883.6.96')
           ,1
           ,1
           ,'Dementia'
           ,null
           ,null
           ,(select id from CcdCode where code  = '162891007' and code_system = '2.16.840.1.113883.6.96')
           ,@database_id_var)

INSERT INTO [FunctionalStatus_CognitiveStatusProblemObservation]
           ([functional_status_id]
           ,[problem_observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [FunctionalStatus])
           ,(SELECT MAX(id) FROM [StatusProblemObservation])
           ,@database_id_var)

INSERT INTO [CaregiverCharacteristic]
           ([code_id]
           ,[participant_role_code_id]
           ,[participant_time_high]
           ,[participant_time_low]
           ,[value_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = 'ASSERTION' and code_system = '2.16.840.1.113883.5.4')
           ,(select id from CcdCode where code  = 'MTH' and code_system = '2.16.840.1.113883.5.111')
           ,CURRENT_TIMESTAMP
           ,CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = '422615001' and code_system = '2.16.840.1.113883.6.96')
           ,@database_id_var)

INSERT INTO [FunctionalStatus_CaregiverCharacteristic]
           ([functional_status_id]
           ,[caregiver_characteristic_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [FunctionalStatus])
           ,(SELECT MAX(id) FROM [CaregiverCharacteristic])
           ,@database_id_var)

INSERT INTO [CaregiverCharacteristic]
           ([code_id]
           ,[participant_role_code_id]
           ,[participant_time_high]
           ,[participant_time_low]
           ,[value_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = 'ASSERTION' and code_system = '2.16.840.1.113883.5.4')
           ,(select id from CcdCode where code  = 'MTH' and code_system = '2.16.840.1.113883.5.111')
           ,null
           ,null
           ,(select id from CcdCode where code  = '11450-4' and code_system = '2.16.840.1.113883.6.1')
           ,@database_id_var)

INSERT INTO [StatusResultObservation_CaregiverCharacteristic]
           ([status_result_observation_id]
           ,[caregiver_characteristic_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [StatusResultObservation])
           ,(SELECT MAX(id) FROM [CaregiverCharacteristic])
           ,@database_id_var)

INSERT INTO [AssessmentScaleObservation]
           ([code_id]
           ,[derivation_expr]
           ,[effective_time]
           ,[value]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '248241002' and code_system = '2.16.840.1.113883.6.1')
           ,'Text description of the calculation'
           ,CURRENT_TIMESTAMP
           ,7
           ,@database_id_var)

INSERT INTO [FunctionalStatus_AssessmentScaleObservation]
           ([functional_status_id]
           ,[observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [FunctionalStatus])
           ,(SELECT MAX(id) FROM [AssessmentScaleObservation])
           ,@database_id_var)

INSERT INTO [StatusResultObservation_AssessmentScaleObservation]
           ([status_result_observation_id]
           ,[assessment_scale_observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [StatusResultObservation])
           ,(SELECT MAX(id) FROM [AssessmentScaleObservation])
           ,@database_id_var)

INSERT INTO [AssessmentScaleSupportingObservation]
           ([code_id]
           ,[int_value]
           ,[value_code_id]
           ,[value_code_system]
           ,[assessment_scale_observation_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '248240001' and code_system = '2.16.840.1.113883.6.96')
           ,3
           ,null
           ,null
           ,(SELECT MAX(id) FROM [AssessmentScaleObservation])
           ,@database_id_var)

INSERT INTO [AssessmentScaleSupportingObservation]
           ([code_id]
           ,[int_value]
           ,[value_code_id]
           ,[assessment_scale_observation_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '52732-5' and code_system = '2.16.840.1.113883.6.1')
           ,null
           ,(select id from CcdCode where code  = 'LA10966-2' and code_system = '2.16.840.1.113883.6.1')
           ,(SELECT MAX(id) FROM [AssessmentScaleObservation])
           ,@database_id_var)

INSERT INTO [NonMedicinalSupplyActivity]
           ([effective_time_high]
           ,[mood_code]
           ,[quantity]
           ,[status_code]
           ,[product_instance_id]
           ,[database_id])
     VALUES
           (CURRENT_TIMESTAMP
           ,'EVN'
           ,2
           ,'completed'
           ,(SELECT MAX(id) FROM [ProductInstance])
           ,@database_id_var)

INSERT INTO [FunctionalStatus_NonMedicinalSupplyActivity]
           ([functional_status_id]
           ,[supply_activity_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [FunctionalStatus])
           ,(SELECT MAX(id) FROM [NonMedicinalSupplyActivity])
           ,@database_id_var)

INSERT INTO [StatusResultObservation_NonMedicinalSupplyActivity]
           ([status_result_observation_id]
           ,[non_medicinal_supply_activity_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [StatusResultObservation])
           ,(SELECT MAX(id) FROM [NonMedicinalSupplyActivity])
           ,@database_id_var)

INSERT INTO [NumberOfPressureUlcersObservation]
           ([effective_time]
           ,[observation_value_id]
           ,[value]
           ,[author_id]
           ,[functional_status_id]
           ,[database_id])
     VALUES
           (CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = '421927004' and code_system = '2.16.840.1.113883.6.96')
           ,3
           ,(SELECT MAX(id) from [Author] where [resident_id] = @resident_id_var)
           ,(SELECT MAX(id) FROM [FunctionalStatus])
           ,@database_id_var)

INSERT INTO [HighestPressureUlcerStage]
           ([value_id]
           ,[functional_status_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '421306004' and code_system = '2.16.840.1.113883.6.96')
           ,(SELECT MAX(id) FROM [FunctionalStatus])
           ,@database_id_var)

INSERT INTO [StatusResultObservation]
            ([code_id]
           ,[effective_time]
           ,[method_code_id]
           ,[target_site_code_id]
           ,[text]
		   ,[value_code_id]
           ,[value]
           ,[value_unit]
           ,[author_id]
           ,[database_id])
     VALUES
           (null
           ,CURRENT_TIMESTAMP
           ,(select id from CcdCode where code  = '168731009' and code_system = '2.16.840.1.113883.6.96')
           ,(select id from CcdCode where code  = '76552005' and code_system = '2.16.840.1.113883.6.96')
           ,'Patient Health Questionnaire'
           ,(select id from CcdCode where code  = '272022009' and code_system = '2.16.840.1.113883.6.96')
           ,null
           ,null
           ,null
           ,@database_id_var)

INSERT INTO [FunctionalStatus_CognitiveStatusResultObservation]
           ([functional_status_id]
           ,[result_observation_id]
           ,[database_id])
     VALUES
           ((SELECT MAX(id) FROM [FunctionalStatus]),
           (SELECT MAX(id) FROM [StatusResultObservation])
           ,@database_id_var)

INSERT INTO [TargetSiteCode]
           ([code_id]
           ,[value_id]
           ,[pressure_ulcer_observation_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '79951008' and code_system = '2.16.840.1.113883.6.96')
           ,(select id from CcdCode where code  = '255549009' and code_system = '2.16.840.1.113883.6.96')
           ,(SELECT MAX(id) FROM [PressureUlcerObservation])
           ,@database_id_var)

INSERT INTO [TargetSiteCode]
           ([code_id]
           ,[value_id]
           ,[pressure_ulcer_observation_id]
           ,[database_id])
     VALUES
           ((select id from CcdCode where code  = '76552005' and code_system = '2.16.840.1.113883.6.96')
           ,(select id from CcdCode where code  = '7771000' and code_system = '2.16.840.1.113883.6.96')
           ,(SELECT MAX(id) FROM [PressureUlcerObservation])
           ,@database_id_var)

COMMIT TRANSACTION;
GO

/* End. */