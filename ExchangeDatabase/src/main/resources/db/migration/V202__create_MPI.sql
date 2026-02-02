CREATE TABLE [dbo].[MPI](
	[registry_patient_id] [varchar](255) NOT NULL,
	[resident_id] [bigint] NULL,
	[assigning_authority] [varchar](255) NOT NULL,
	[patient_id] [varchar](255) NOT NULL,
	[deleted] [varchar](1) NULL,
	[merged] [varchar](1) NULL,
	[surviving_patient_id] [varchar](255) NULL,
PRIMARY KEY CLUSTERED
(
	[registry_patient_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[MPI] WITH CHECK ADD  CONSTRAINT [FK_MPI_resident_id] FOREIGN KEY(resident_id)
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[Resident] ADD [mother_person_id] [bigint] NULL;
GO
ALTER TABLE [dbo].[Resident]  WITH CHECK ADD  CONSTRAINT [FK_mother_person_id] FOREIGN KEY([mother_person_id])
REFERENCES [dbo].[Person] ([id])
GO

ALTER TABLE [dbo].[Resident] ADD mother_account_number [varchar](255) NULL;
GO
ALTER TABLE [dbo].[Resident]  WITH CHECK ADD  CONSTRAINT [FK_mother_account_number] FOREIGN KEY(mother_account_number)
REFERENCES [dbo].[MPI] ([registry_patient_id])
GO

ALTER TABLE [dbo].[Resident] ADD patient_account_number [varchar](255) NULL;
GO
ALTER TABLE [dbo].[Resident]  WITH CHECK ADD  CONSTRAINT [FK_patient_account_number] FOREIGN KEY(patient_account_number)
REFERENCES [dbo].[MPI] ([registry_patient_id])
GO

ALTER TABLE [dbo].[Resident] ADD [birth_place] [varchar](500) NULL;
GO
ALTER TABLE [dbo].[Resident] ADD [citizenship] [varchar](500) NULL;
GO
ALTER TABLE [dbo].[Resident] ADD [birth_order] [int] NULL;
GO
ALTER TABLE [dbo].[Resident] ADD [death_indicator] [bit] NULL;
GO
ALTER TABLE [dbo].[Resident] ADD [death_date] [datetime2](7) NULL;
GO

ALTER TABLE [dbo].[Name] ADD [degree] [varchar](100) NULL;
GO
ALTER TABLE [dbo].[Name] ADD [name_representation_code] [varchar](100) NULL;
GO

INSERT INTO [dbo].[SourceDatabase] (
	[alternative_id], [name], [url], [is_service], [name_and_port], [is_eldermark] ,[direct_config_id]
)
VALUES (
	'ADT_Repo', 'ADT Repo' , 'ADT_Repo_url' , 0 , 'ADT' , 0, NULL
);

Declare @adtDataSourceId bigint;
SELECT  @adtDataSourceId = MAX(id) FROM [dbo].[SourceDatabase] where name='ADT Repo';

INSERT INTO [dbo].[Organization]
           ([legacy_id]
           ,[legacy_table]
           ,[name]
           ,[database_id]
           ,[testing_training]
           ,[inactive]
           ,[module_hie]
           ,[module_cloud_storage]
           ,[oid]
           ,[created_automatically])
     VALUES
           ('1'
           ,'Company'
           ,'ADT Organization'
           ,@adtDataSourceId
           ,0
           ,0
           ,1
           ,0
           ,null
           ,null)
GO

ALTER TABLE [dbo].[MPI] ADD [assigning_authority_namespace] [varchar](255) NULL;
GO
ALTER TABLE [dbo].[MPI] ADD [assigning_authority_universal] [varchar](255) NULL;
GO
ALTER TABLE [dbo].[MPI] ADD [assigning_authority_universal_type] [varchar](255) NULL;
GO
ALTER TABLE [dbo].[MPI] ADD [assigning_facility_namespace] [varchar](255) NULL;
GO
ALTER TABLE [dbo].[MPI] ADD [assigning_facility_universal] [varchar](255) NULL;
GO
ALTER TABLE [dbo].[MPI] ADD [assigning_facility_universal_type] [varchar](255) NULL;
GO
ALTER TABLE [dbo].[MPI] ADD [type_code] [varchar](50) NULL;
GO
ALTER TABLE [dbo].[MPI] ADD [effective_date] [datetime2](7) NULL;
GO
ALTER TABLE [dbo].[MPI] ADD [expiration_date] [datetime2](7) NULL;
GO
