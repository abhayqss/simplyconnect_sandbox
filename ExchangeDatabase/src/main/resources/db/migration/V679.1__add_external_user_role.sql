ALTER TABLE [dbo].[CareTeamRole]
  ADD [is_manually_assignable]  bit NOT NULL DEFAULT(1);
GO

INSERT INTO [dbo].[CareTeamRole]
           ([name]
           ,[code]
           ,[position]
           ,[display_name]
           ,[is_manually_assignable]
           )
     VALUES
           ('External User'
           ,'ROLE_EXTERNAL_USER'
           ,0
           ,'External User'
           ,0)        
GO

Update [dbo].[CareTeamRole]
SET [is_manually_assignable] = 0 where code = 'ROLE_NOTIFY_USER';

INSERT INTO [dbo].[SourceDatabase]
           ([name]
           ,[alternative_id]
           ,[oid]
           ,[is_eldermark]
)
     VALUES
           ('External Providers'
           ,'EXT'
           ,'EXT4567'
	   ,0
)        
GO

INSERT INTO [dbo].[SystemSetup]
           ([database_id]
           ,[login_company_id]
           
)
     VALUES
           ((select id from SourceDatabase where oid='EXT4567')
           ,'EXT'
          
)        
GO


INSERT INTO [dbo].[Organization]
           ([name]
           ,[oid]
	   ,[legacy_id]
           ,[legacy_table]
           ,[database_id]
           
)
     VALUES
           ('External Providers'
           ,'EXT0987'
	   ,' '
           ,'Company'
           ,(select id from SourceDatabase where oid='EXT4567')
)        
GO

Update [dbo].[Organization] 
SET [legacy_id] = (select id from [dbo].[Organization] where oid = 'EXT0987') where oid = 'EXT0987';



Declare @external_id BIGINT;
select @external_id = (select id from [dbo].[SourceDatabase] where oid = 'EXT4567')

insert into DatabasePasswordSettings
(
database_id, password_settings_id, enabled, value)
values
(@external_id, 1, 0, 0),
(@external_id, 2, 1, 5),
(@external_id, 3, 1, 15),
(@external_id, 4, 1, 15),
(@external_id, 5, 1, 8),
(@external_id, 6, 0, 0),
(@external_id, 7, 1, 1),
(@external_id, 8, 1, 1),
(@external_id, 9, 1, 1),
(@external_id, 10, 1, 1),
(@external_id, 11, 0, 0);


CREATE TABLE dbo.ExternalEmployeeRequest
(
    id BIGINT PRIMARY KEY NOT NULL IDENTITY,
    token varchar(256) NOT NULL,
    email varchar(100) NOT NULL,
    referral_request_id bigint NOT NULL,
    created_date_time datetime2(7) NOT NULL,
    type varchar(15)

CONSTRAINT ExternalEmployeeRequest_ReferralRequest_id_fk FOREIGN KEY (referral_request_id) REFERENCES dbo.ReferralRequest (id)
)
CREATE TABLE dbo.ExternalEmployeeReferralRequest
(
    id bigint PRIMARY KEY NOT NULL IDENTITY,
    employee_id bigint NOT NULL,
    referral_request_id bigint NOT NULL,
    CONSTRAINT ExternalEmployeeReferralRequest_Employee_enc_id_fk FOREIGN KEY (employee_id) REFERENCES dbo.Employee_enc (id),
    CONSTRAINT ExternalEmployeeReferralRequest_ReferralRequest_id_fk FOREIGN KEY (referral_request_id) REFERENCES dbo.ReferralRequest (id)
)
