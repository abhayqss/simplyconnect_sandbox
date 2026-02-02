IF OBJECT_ID('LabResearchOrder_SpecimenType') IS NOT NULL
  DROP TABLE [dbo].[LabResearchOrder_SpecimenType]
GO

IF OBJECT_ID('LabResearchOrder_Icd10Code') IS NOT NULL
  DROP TABLE [dbo].[LabResearchOrder_Icd10Code]
GO

IF OBJECT_ID('LabResearchOrder') IS NOT NULL
  DROP TABLE [dbo].[LabResearchOrder]
GO

IF OBJECT_ID('LabIcd10Code') IS NOT NULL
  DROP TABLE [dbo].[LabIcd10Code]
GO

IF OBJECT_ID('LabIcd10Group') IS NOT NULL
  DROP TABLE [dbo].[LabIcd10Group]
GO

IF OBJECT_ID('SpecimenType') IS NOT NULL
  DROP TABLE [dbo].[SpecimenType]
GO

CREATE TABLE [dbo].[SpecimenType](
	[id] [bigint] NOT NULL,
	[item_order] [int] NULL,
	[name] [varchar](256) NOT NULL,
	[title] [varchar](256) NULL,
 CONSTRAINT [PK_Specimen] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

CREATE TABLE [dbo].[LabIcd10Group](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](256) NOT NULL,
	[title] [varchar](256) NOT NULL,
 CONSTRAINT [PK_LabIcd10Group] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


CREATE TABLE [dbo].[LabIcd10Code](
	[id] [bigint] NOT NULL,
	[title] [varchar](256) NULL,
	[code] [varchar](256) NOT NULL,
	[lab_icd10_group_id] [bigint] NULL,
 CONSTRAINT [PK_LabIcd10Code] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[LabIcd10Code]  WITH CHECK ADD  CONSTRAINT [FK_LabIcd10Code_LabIcd10Group] FOREIGN KEY([lab_icd10_group_id])
REFERENCES [dbo].[LabIcd10Group] ([id])
GO

ALTER TABLE [dbo].[LabIcd10Code] CHECK CONSTRAINT [FK_LabIcd10Code_LabIcd10Group]
GO

CREATE TABLE [dbo].[LabResearchOrder](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[requisition_number] [varchar](15) NOT NULL,
	[created_date] [datetime2](7) NOT NULL,
	[status] [varchar](64) NOT NULL,
	[created_by] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
	[clinic] [varchar](256) NULL,
	[clinic_address] [varchar](256) NULL,
	[address] [varchar](256) NOT NULL,
	[city] [varchar](256) NOT NULL,
	[state_id] [bigint] NOT NULL,
	[zip_code] [varchar](5) NOT NULL,
	[phone] [varchar](20) NOT NULL,
	[in_network_insurance] [varchar](256) NOT NULL,
	[policy_number] [varchar](256) NOT NULL,
	[policy_holder] [varchar](10) NOT NULL,
	[notes] [varchar](80) NULL,
	[reason_for_testing] [varchar](32) NOT NULL,
	[policy_holder_name] [varchar](256) NULL,
	[policy_holder_date_of_birth] [datetime2](7) NULL,
	[is_covid19] [bit] NOT NULL,
	[provider_first_name] [varchar](50) NULL,
	[provider_last_name] [varchar](50) NULL,
	[gender_id] [bigint] NOT NULL,
	[birth_date] [datetime2](7) NOT NULL,
	[collector_name] [varchar](256) NULL,
	[site] [varchar](256) NULL,
	[specimen_date] [datetime2](7) NOT NULL,
	[order_date] [datetime2](7) NULL,
	[reviewed_date] [datetime2](7) NULL,
	[reviewed_by] [bigint] NULL,
 CONSTRAINT [PK_LabOrder] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[LabResearchOrder]  WITH CHECK ADD  CONSTRAINT [FK_LabOrder_AnyCcdCode] FOREIGN KEY([gender_id])
REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[LabResearchOrder] CHECK CONSTRAINT [FK_LabOrder_AnyCcdCode]
GO

ALTER TABLE [dbo].[LabResearchOrder]  WITH CHECK ADD  CONSTRAINT [FK_LabOrder_Employee_enc] FOREIGN KEY([created_by])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[LabResearchOrder] CHECK CONSTRAINT [FK_LabOrder_Employee_enc]
GO

ALTER TABLE [dbo].[LabResearchOrder]  WITH CHECK ADD  CONSTRAINT [FK_LabOrder_resident_enc] FOREIGN KEY([resident_id])
REFERENCES [dbo].[resident_enc] ([id])
GO

ALTER TABLE [dbo].[LabResearchOrder] CHECK CONSTRAINT [FK_LabOrder_resident_enc]
GO

ALTER TABLE [dbo].[LabResearchOrder]  WITH CHECK ADD  CONSTRAINT [FK_LabOrder_State] FOREIGN KEY([state_id])
REFERENCES [dbo].[State] ([id])
GO

ALTER TABLE [dbo].[LabResearchOrder] CHECK CONSTRAINT [FK_LabOrder_State]
GO

ALTER TABLE [dbo].[LabResearchOrder]  WITH CHECK ADD  CONSTRAINT [FK_LabResearchOrder_Employee_enc] FOREIGN KEY([reviewed_by])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[LabResearchOrder] CHECK CONSTRAINT [FK_LabResearchOrder_Employee_enc]
GO


CREATE TABLE [dbo].[LabResearchOrder_Icd10Code](
	[lab_research_order_id] [bigint] NOT NULL,
	[code] [varchar](256) NOT NULL,
 CONSTRAINT [PK_LabResearchOrder_Icd10Code] PRIMARY KEY CLUSTERED 
(
	[lab_research_order_id] ASC,
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[LabResearchOrder_Icd10Code]  WITH CHECK ADD  CONSTRAINT [FK_LabResearchOrder_Icd10Code_LabResearchOrder] FOREIGN KEY([lab_research_order_id])
REFERENCES [dbo].[LabResearchOrder] ([id])
GO

ALTER TABLE [dbo].[LabResearchOrder_Icd10Code] CHECK CONSTRAINT [FK_LabResearchOrder_Icd10Code_LabResearchOrder]
GO

CREATE TABLE [dbo].[LabResearchOrder_SpecimenType](
	[lab_research_order_id] [bigint] NOT NULL,
	[specimen_type_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[LabResearchOrder_SpecimenType]  WITH CHECK ADD  CONSTRAINT [FK_LabResearchOrder_Specimen_LabResearchOrder] FOREIGN KEY([lab_research_order_id])
REFERENCES [dbo].[LabResearchOrder] ([id])
GO

ALTER TABLE [dbo].[LabResearchOrder_SpecimenType] CHECK CONSTRAINT [FK_LabResearchOrder_Specimen_LabResearchOrder]
GO

ALTER TABLE [dbo].[LabResearchOrder_SpecimenType]  WITH CHECK ADD  CONSTRAINT [FK_LabResearchOrder_Specimen_Specimen] FOREIGN KEY([specimen_type_id])
REFERENCES [dbo].[SpecimenType] ([id])
GO

ALTER TABLE [dbo].[LabResearchOrder_SpecimenType] CHECK CONSTRAINT [FK_LabResearchOrder_Specimen_Specimen]
GO