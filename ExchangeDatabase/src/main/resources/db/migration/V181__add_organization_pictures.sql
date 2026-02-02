
alter table dbo.Organization add [oid] varchar(50) null;
GO
alter table dbo.Organization add created_automatically bit null;
GO
alter table dbo.Organization add email varchar(200) null;
GO

alter table dbo.Employee add created_automatically bit null;
GO

INSERT INTO [dbo].[CareTeamRole] ([name], [code])  VALUES   ('Administrator', 'ROLE_ADMINISTRATOR');
INSERT INTO [dbo].[CareTeamRole] ([name], [code])  VALUES   ('Super Administrator', 'ROLE_SUPER_ADMINISTRATOR');

alter table dbo.Organization add main_logo_path varchar(1000) NULL;
GO

alter table dbo.Organization add additional_logo_path varchar(1000) NULL;
GO

alter table dbo.Organization add external_logo_id varchar(20) NULL;
GO