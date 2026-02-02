/****** Object:  Table [dbo].[SourceDatabase]    Script Date: 01/08/2014 11:35:57 ******/
SET XACT_ABORT ON
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[SourceDatabase](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[alternative_id] [varchar](255) NOT NULL,
	[name] [varchar](255) NOT NULL,
	[url] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_g9dsx16cfdn733suvqmrqr3i5] UNIQUE NONCLUSTERED 
(
	[alternative_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_og67xmj4bxqavy7d2mmvyqrl5] UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_r2bxg281xjrrmdxl5k7dkdik5] UNIQUE NONCLUSTERED 
(
	[url] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Document]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Document](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[author_db_alt_id] [varchar](255) NOT NULL,
	[author_legacy_id] [varchar](255) NOT NULL,
	[creation_time] [datetime2](7) NOT NULL,
	[document_title] [nvarchar](255) NOT NULL,
	[mime_type] [varchar](255) NOT NULL,
	[original_file_name] [nvarchar](255) NOT NULL,
	[res_db_alt_id] [varchar](255) NOT NULL,
	[res_legacy_id] [bigint] NOT NULL,
	[size] [int] NOT NULL,
	[uuid] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Employee]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Employee](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[first_name] [nvarchar](50) NULL,
	[inactive] [bit] NOT NULL,
	[last_name] [nvarchar](50) NULL,
	[legacy_id] [varchar](25) NOT NULL,
	[login] [varchar](50) NOT NULL,
	[password] [varchar](255) NOT NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_kd6qs9g86voxkvi19oww2x00d] UNIQUE NONCLUSTERED 
(
	[login] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_ribej2u54h0fsfw9johaov93l] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[DrugVehicle]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[DrugVehicle](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NULL,
	[name] [varchar](200) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[CommunicationType]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[CommunicationType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[inactive] [bit] NULL,
	[legacy_id] [varchar](255) NOT NULL,
	[name] [varchar](30) NULL,
	[type_code] [varchar](4) NULL,
	[type_name] [varchar](25) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_cx2mpsif1nmioehof6r8ogxuf] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[DeliveryLocation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[DeliveryLocation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](255) NULL,
	[name] [varchar](255) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[DataSyncProblem]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[DataSyncProblem](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[date] [datetime2](7) NULL,
	[failed_portion_ids] [varchar](max) NULL,
	[source_entity_name] [varchar](255) NULL,
	[stack_trace] [varchar](max) NULL,
	[database_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[DataSyncLog]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[DataSyncLog](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[date] [datetime2](7) NULL,
	[source_object] [varchar](max) NULL,
	[source_object_status] [varchar](255) NULL,
	[database_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[AssessmentScaleObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AssessmentScaleObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NULL,
	[code_system] [varchar](50) NULL,
	[derivation_expr] [varchar](max) NULL,
	[effective_time] [datetime2](7) NULL,
	[value] [int] NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[CaregiverCharacteristic]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[CaregiverCharacteristic](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](30) NULL,
	[participant_role_code] [varchar](30) NULL,
	[participant_time_high] [datetime2](7) NULL,
	[participant_time_low] [datetime2](7) NULL,
	[value] [varchar](30) NULL,
	[value_code_system] [varchar](30) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Instructions]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Instructions](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NULL,
	[text] [varchar](200) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Indication]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Indication](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[legacy_table] [varchar](255) NOT NULL,
	[code] [varchar](50) NULL,
	[effective_time_high] [datetime2](7) NULL,
	[effective_time_low] [datetime2](7) NULL,
	[value] [varchar](300) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ImmunizationRefusalReason]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ImmunizationRefusalReason](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[MedicationPrecondition]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[MedicationPrecondition](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[text] [varchar](255) NULL,
	[value] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Organization]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Organization](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [varchar](255) NOT NULL,
	[legacy_table] [varchar](255) NOT NULL,
	[logo_pict_id] [bigint] NULL,
	[name] [nvarchar](255) NULL,
	[sales_region] [varchar](20) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_q9o5gr9fhggbfdslg7eds17yu] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC,
	[legacy_table] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[PlanOfCareActivity]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[PlanOfCareActivity](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NULL,
	[effective_time] [datetime2](7) NULL,
	[mood_code] [varchar](8) NOT NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Person]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Person](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[type_code] [varchar](30) NULL,
	[legacy_table] [varchar](25) NOT NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ProductInstance]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ProductInstance](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[device_code] [varchar](max) NULL,
	[device_code_system] [varchar](255) NULL,
	[scoping_entity_id] [varchar](255) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ProcedureType]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ProcedureType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [varchar](255) NOT NULL,
	[procedure_type_code] [varchar](255) NULL,
	[procedure_type_code_system] [varchar](255) NULL,
	[procedure_type_text] [varchar](max) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[SeverityObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[SeverityObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[severity_code] [varchar](50) NULL,
	[severity_text] [varchar](200) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ReactionObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ReactionObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[reaction_code] [varchar](50) NULL,
	[reaction_text] [varchar](200) NULL,
	[effective_time_high] [datetime2](7) NULL,
	[effective_time_low] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[StatusResultOrganizer]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[StatusResultOrganizer](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NULL,
	[code_system] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[StatusProblemObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[StatusProblemObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[method_code] [varchar](50) NULL,
	[negation_ind] [bit] NULL,
	[resolved] [bit] NULL,
	[text] [varchar](max) NULL,
	[time_high] [datetime2](7) NULL,
	[time_low] [datetime2](7) NULL,
	[value] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[SystemSetup]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[SystemSetup](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[enable_marketing] [bit] NULL,
	[semi_private_count_as_half_unit] [varchar](5) NULL,
	[site_code] [varchar](12) NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[StatusProblemObservation_CaregiverCharacteristic]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[StatusProblemObservation_CaregiverCharacteristic](
	[status_problem_observation_id] [bigint] NOT NULL,
	[caregiver_characteristic_id] [bigint] NOT NULL,
 CONSTRAINT [UK_7nhdouldpxrw22i2fn8lyr4q5] UNIQUE NONCLUSTERED 
(
	[caregiver_characteristic_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[StatusProblemObservation_AssessmentScaleObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[StatusProblemObservation_AssessmentScaleObservation](
	[status_problem_observation_id] [bigint] NOT NULL,
	[assessment_scale_observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_24jxavnd887rcu76l9hevultd] UNIQUE NONCLUSTERED 
(
	[assessment_scale_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[UnitType]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[UnitType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[description] [varchar](50) NULL,
	[inactive] [bit] NULL,
	[legacy_id] [varchar](255) NOT NULL,
	[outpatient] [bit] NULL,
	[semi_private] [bit] NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_c0g5rtery9iky4i8aa2i69taw] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ReactionObservation_SeverityObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ReactionObservation_SeverityObservation](
	[reaction_observation_id] [bigint] NOT NULL,
	[severity_observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_rvi8a0xiahqmolutix07larwl] UNIQUE NONCLUSTERED 
(
	[severity_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Pharmacy]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Pharmacy](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[address1] [varchar](35) NULL,
	[address2] [varchar](35) NULL,
	[city] [varchar](25) NULL,
	[fax] [varchar](14) NULL,
	[name] [varchar](30) NULL,
	[phone] [varchar](20) NULL,
	[state] [varchar](2) NULL,
	[zip] [varchar](10) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[PersonTelecom]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[PersonTelecom](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[sync_qualifier] [int] NOT NULL,
	[use_code] [varchar](15) NULL,
	[value] [varchar](150) NULL,
	[value_normalized] [varchar](150) NULL,
	[database_id] [bigint] NOT NULL,
	[person_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_kf4ok4iymkuua6vmub0xm2fd2] UNIQUE NONCLUSTERED 
(
	[person_id] ASC,
	[sync_qualifier] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [PersonId_Index] ON [dbo].[PersonTelecom] 
(
	[person_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PersonAddress]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[PersonAddress](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[city] [varchar](100) NULL,
	[country] [varchar](100) NULL,
	[use_code] [varchar](15) NULL,
	[postal_code] [varchar](50) NULL,
	[state] [varchar](100) NULL,
	[street_address] [varchar](255) NULL,
	[database_id] [bigint] NOT NULL,
	[person_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [PersonId_Index] ON [dbo].[PersonAddress] 
(
	[person_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[MedicalProfessional]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[MedicalProfessional](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[address1] [varchar](35) NULL,
	[address2] [varchar](35) NULL,
	[city] [varchar](25) NULL,
	[display_name] [nvarchar](45) NULL,
	[email] [varchar](40) NULL,
	[fax] [varchar](25) NULL,
	[first_name] [nvarchar](50) NULL,
	[inactive] [bit] NULL,
	[last_name] [nvarchar](50) NULL,
	[middle_name] [nvarchar](50) NULL,
	[npi] [varchar](10) NULL,
	[pager] [varchar](25) NULL,
	[speciality] [varchar](30) NULL,
	[state] [varchar](2) NULL,
	[title_prefix] [nvarchar](30) NULL,
	[title_suffix] [nvarchar](30) NULL,
	[upin] [varchar](10) NULL,
	[work_phone] [varchar](25) NULL,
	[zip_code] [varchar](10) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[OrgReferralSource]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OrgReferralSource](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[is_a_referral_source] [bit] NULL,
	[related_to_all_facilities] [bit] NULL,
	[database_id] [bigint] NOT NULL,
	[employee_owner_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_3r4wwlv730k3we5jbyrpwudm9] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OrganizationTelecom]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[OrganizationTelecom](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[use_code] [varchar](15) NULL,
	[value] [varchar](100) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[OrganizationAddress]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[OrganizationAddress](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[city] [varchar](100) NULL,
	[country] [varchar](100) NULL,
	[use_code] [varchar](15) NULL,
	[postal_code] [varchar](50) NULL,
	[state] [varchar](100) NULL,
	[street_address] [varchar](255) NULL,
	[database_id] [bigint] NOT NULL,
	[org_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[NonMedicinalSupplyActivity]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[NonMedicinalSupplyActivity](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[effective_time_high] [datetime2](7) NULL,
	[mood_code] [varchar](8) NOT NULL,
	[quantity] [numeric](19, 2) NULL,
	[status_code] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[product_instance_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Name]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Name](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[family] [nvarchar](100) NULL,
	[family_normalized] [nvarchar](100) NULL,
	[family_qualifier] [varchar](30) NULL,
	[given] [nvarchar](100) NULL,
	[given_normalized] [nvarchar](100) NULL,
	[given_qualifier] [varchar](30) NULL,
	[middle] [nvarchar](100) NULL,
	[middle_normalized] [nvarchar](100) NULL,
	[middle_qualifier] [varchar](30) NULL,
	[use_code] [varchar](5) NULL,
	[prefix] [nvarchar](50) NULL,
	[prefix_qualifier] [varchar](30) NULL,
	[suffix] [nvarchar](50) NULL,
	[suffix_qualifier] [varchar](30) NULL,
	[database_id] [bigint] NOT NULL,
	[person_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [Names_Index] ON [dbo].[Name] 
(
	[family_normalized] ASC,
	[middle_normalized] ASC,
	[given_normalized] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [PersonId_Index] ON [dbo].[Name] 
(
	[person_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OccupancyGoal]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OccupancyGoal](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[budgeted_census] [bigint] NULL,
	[head_count_goal] [bigint] NULL,
	[is_startup] [bit] NULL,
	[month] [bigint] NULL,
	[units_occupied_goal] [bigint] NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_cmkso7rc2h5oqql0xg7lsnh9u] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[MedicationInformation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[MedicationInformation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[legacy_table] [varchar](255) NOT NULL,
	[product_name_code] [varchar](60) NULL,
	[product_name_text] [varchar](max) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Employee_Organization]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Employee_Organization](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[access_marketing] [bit] NULL,
	[database_id] [bigint] NOT NULL,
	[employee_id] [bigint] NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_re03xk9mlkv5hl1cgeluovytm] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[LegalAuthenticator]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[LegalAuthenticator](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[time] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[person_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ImmunizationMedicationInformation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ImmunizationMedicationInformation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NULL,
	[lot_number_text] [varchar](200) NULL,
	[text] [varchar](200) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Inquiry]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Inquiry](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[converted] [bit] NULL,
	[date] [date] NULL,
	[first_name] [nvarchar](50) NULL,
	[is_prospect] [bit] NULL,
	[is_related_party] [bit] NULL,
	[last_name] [nvarchar](50) NULL,
	[no_longer_active] [bit] NULL,
	[reason_no_longer_active] [varchar](40) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[sales_rep_employee_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_jvkjw9861gywacu74g3v55gwm] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[AssessmentScaleSupportingObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AssessmentScaleSupportingObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NULL,
	[code_system] [varchar](50) NULL,
	[int_value] [int] NULL,
	[value_code] [varchar](50) NULL,
	[value_code_system] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[assessment_scale_observation_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[AssessmentScaleObservationRange]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AssessmentScaleObservationRange](
	[observation_id] [bigint] NOT NULL,
	[observation_range] [varchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[AssessmentScaleObservationInterpretationCode]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AssessmentScaleObservationInterpretationCode](
	[observation_id] [bigint] NOT NULL,
	[interpretation_code] [varchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[DataEnterer]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DataEnterer](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[person_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Custodian]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Custodian](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DeliveryLocation_OrganizationTelecom]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DeliveryLocation_OrganizationTelecom](
	[delivery_location_id] [bigint] NOT NULL,
	[telecom_id] [bigint] NOT NULL,
 CONSTRAINT [UK_fulfqhld69lh6veh1gthjjn2s] UNIQUE NONCLUSTERED 
(
	[telecom_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DeliveryLocation_OrganizationAddress]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DeliveryLocation_OrganizationAddress](
	[delivery_location_id] [bigint] NOT NULL,
	[address_id] [bigint] NOT NULL,
 CONSTRAINT [UK_o4gmfm53ljnh8onhyq8viec1y] UNIQUE NONCLUSTERED 
(
	[address_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Resident]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Resident](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[admit_date] [datetime2](7) NULL,
	[birth_date] [date] NULL,
	[discharge_date] [datetime2](7) NULL,
	[ethnic_group] [varchar](20) NULL,
	[gender] [varchar](255) NULL,
	[marital_status] [varchar](20) NULL,
	[race] [varchar](30) NULL,
	[religion] [varchar](30) NULL,
	[ssn] [varchar](11) NULL,
	[ssn_last_four_digits] [varchar](4) NULL,
	[database_id] [bigint] NOT NULL,
	[custodian_id] [bigint] NULL,
	[data_enterer_id] [bigint] NULL,
	[facility_id] [bigint] NULL,
	[legal_authenticator_id] [bigint] NULL,
	[person_id] [bigint] NULL,
	[provider_organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_5rctot83jso6pjkr9754k7wxp] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [SSN_Last4Digits_Index] ON [dbo].[Resident] 
(
	[ssn_last_four_digits] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ProfessionalContact]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ProfessionalContact](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[contact_first_name] [nvarchar](50) NULL,
	[contact_last_name] [nvarchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[org_ref_source_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_3dmvxw5gyyjuh9feccmf381in] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OrgReferralSourceFacility]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OrgReferralSourceFacility](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL,
	[org_ref_source_id] [bigint] NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_25rytyme8tnhjovsk39uw3iig] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[StatusProblemObservation_NonMedicinalSupplyActivity]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[StatusProblemObservation_NonMedicinalSupplyActivity](
	[status_problem_observation_id] [bigint] NOT NULL,
	[non_medicinal_supply_activity_id] [bigint] NOT NULL,
 CONSTRAINT [UK_gftd4usnil1cbobsai6r89gs7] UNIQUE NONCLUSTERED 
(
	[non_medicinal_supply_activity_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Unit]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Unit](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[current_division_status] [varchar](20) NULL,
	[current_in_maintenance] [bit] NULL,
	[current_model] [bit] NULL,
	[current_out_of_service] [varchar](255) NULL,
	[current_product_type] [varchar](40) NULL,
	[unit_number] [varchar](10) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[unit_type_private_current_id] [bigint] NULL,
	[unit_type_semi_private_acrnt_id] [bigint] NULL,
	[unit_type_semi_private_bcrnt_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_e6hssh5ouqk8a3q6s5jomshc5] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[UnitHistory]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[UnitHistory](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[end_date] [date] NULL,
	[in_maintenance] [bit] NULL,
	[semi_private] [bit] NULL,
	[start_date] [date] NULL,
	[sub_divide_type] [varchar](30) NULL,
	[unit_number] [varchar](15) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[unit_id] [bigint] NULL,
	[unit_type_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_qwvqgvi8gkt7entlw8aythqsd] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[VitalSign]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[VitalSign](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[effective_time] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_rnb46q58dqybhqhqp60mrlf3f] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SocialHistory]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SocialHistory](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_idkg6rrks9h0crk81ab3gsqnq] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ResidentProcedure]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ResidentProcedure](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_9k9wv6354kkdtfesq2w7ak2ge] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ResidentAdmittanceHistory]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ResidentAdmittanceHistory](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[admit_date] [datetime2](7) NULL,
	[admit_facility_sequence] [int] NULL,
	[admit_sequence] [int] NULL,
	[admit_when] [int] NULL,
	[archive_date] [date] NULL,
	[assessment_date] [date] NULL,
	[county_admitted_from] [varchar](30) NULL,
	[date_created] [date] NULL,
	[deposit_date] [date] NULL,
	[discharge_date] [date] NULL,
	[discharge_date_future] [date] NULL,
	[discharge_reason] [varchar](80) NULL,
	[discharge_to] [varchar](30) NULL,
	[discharge_when_future] [int] NULL,
	[facility_unit_current] [varchar](21) NULL,
	[hospitalized_before_move_in] [bit] NULL,
	[initial_facility_unit] [varchar](21) NULL,
	[initial_is_second_occupant] [bit] NULL,
	[initial_res_unit_hist_id] [int] NULL,
	[initial_unit] [varchar](10) NULL,
	[not_admitted_from_own_home_rsn] [varchar](40) NULL,
	[prev_home_care] [bit] NULL,
	[previous_living_status] [varchar](20) NULL,
	[previously_in_nursing_home] [bit] NULL,
	[rental_agreement_date] [date] NULL,
	[reserved_from_date] [date] NULL,
	[reserved_to_date] [date] NULL,
	[unit_number] [varchar](10) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
	[sales_rep_employee_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Prospect]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Prospect](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[assessment_date] [date] NULL,
	[cancel_date] [date] NULL,
	[current_status] [varchar](30) NULL,
	[date_became_prospect] [date] NULL,
	[deposit_date] [date] NULL,
	[first_name] [nvarchar](50) NULL,
	[last_name] [nvarchar](50) NULL,
	[move_in_date] [date] NULL,
	[move_out_date] [date] NULL,
	[pros_phones] [varchar](255) NULL,
	[related_party_first_name] [nvarchar](50) NULL,
	[related_party_last_name] [nvarchar](50) NULL,
	[related_party_phones] [varchar](255) NULL,
	[reserve_unit_number] [varchar](10) NULL,
	[reserved_from] [date] NULL,
	[reserved_to] [date] NULL,
	[resident_status] [varchar](30) NULL,
	[resident_unit] [varchar](255) NULL,
	[second_occupant] [bit] NULL,
	[unit_is_reserved] [varchar](5) NULL,
	[database_id] [bigint] NOT NULL,
	[copied_from_inquiry_id] [bigint] NULL,
	[facility_primary_id] [bigint] NULL,
	[referral_source_prof_cont_id] [bigint] NULL,
	[reserve_facility_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
	[resident_facility_id] [bigint] NULL,
	[sales_rep_employee_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_kd49in7lbuef1k07l0g8lx262] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Result]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Result](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[class_code] [varchar](50) NULL,
	[code] [varchar](50) NULL,
	[code_system] [varchar](50) NULL,
	[status_code] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_sqjxm8n2sxuxcy9niqdoo9by4] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[MedicalEquipment]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[MedicalEquipment](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[effective_time_high] [datetime2](7) NULL,
	[mood_code] [varchar](50) NULL,
	[quantity] [int] NULL,
	[status_code] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[product_instance_id] [bigint] NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_ok5x9cbn93i171t8ychsvohmp] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Payer]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Payer](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[coverage_activity_id] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Participant]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Participant](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[relationship_code] [varchar](20) NULL,
	[role_code] [varchar](20) NULL,
	[effective_time_high] [datetime2](7) NULL,
	[effective_time_low] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[person_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_4hjfcijq4g13o3yt4oml2m5dv] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Problem]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Problem](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[status_code] [varchar](20) NULL,
	[effective_time_high] [datetime2](7) NULL,
	[effective_time_low] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[PlanOfCare]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PlanOfCare](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[InformationRecipient]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[InformationRecipient](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[person_id] [bigint] NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Informant]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Informant](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[person_id] [bigint] NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Guardian]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Guardian](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[relationship_code] [varchar](15) NULL,
	[database_id] [bigint] NOT NULL,
	[person_id] [bigint] NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[FunctionalStatus]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FunctionalStatus](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Language]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Language](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ability_mode] [varchar](10) NULL,
	[ability_proficiency] [varchar](10) NULL,
	[name] [varchar](50) NULL,
	[preference_ind] [bit] NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Allergy]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Allergy](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[status_code] [varchar](50) NULL,
	[effective_time_high] [datetime2](7) NULL,
	[effective_time_low] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_ee4sxidv52q5ekee27ttdtawa] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Author]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Author](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NULL,
	[legacy_table] [varchar](255) NULL,
	[time] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[person_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_20nbkdbedh90rxr9sjuqf28ya] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC,
	[legacy_table] ASC,
	[resident_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Authenticator]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Authenticator](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[time] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[person_id] [bigint] NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DocumentationOf]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DocumentationOf](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[effective_time_high] [datetime2](7) NULL,
	[effective_time_low] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FamilyHistory]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[FamilyHistory](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[administrative_gender_code] [varchar](2) NULL,
	[birth_time] [datetime2](7) NULL,
	[deceased_ind] [bit] NULL,
	[deceased_time] [datetime2](7) NULL,
	[person_information_id] [varchar](255) NULL,
	[related_subject_code] [varchar](30) NULL,
	[database_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[DocumentationOf_Person]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DocumentationOf_Person](
	[documentation_of_id] [bigint] NOT NULL,
	[person_id] [bigint] NOT NULL,
 CONSTRAINT [UK_eu6jhbkc14dffbxyhdp5lkdvq] UNIQUE NONCLUSTERED 
(
	[person_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Communication]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Communication](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[completed_date] [date] NULL,
	[parent_rec_id] [bigint] NULL,
	[parent_type] [varchar](10) NULL,
	[database_id] [bigint] NOT NULL,
	[communication_type_id] [bigint] NULL,
	[completed_by_empl_id] [bigint] NULL,
	[inquiry_id] [bigint] NULL,
	[prof_contact_id] [bigint] NULL,
	[prospect_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_79d5ofox9gcsjholgp87k283w] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[AssessmentScaleObservation_Author]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[AssessmentScaleObservation_Author](
	[observation_id] [bigint] NOT NULL,
	[author_id] [bigint] NOT NULL,
 CONSTRAINT [UK_h6lbo7q78esmoldlsf7shu54p] UNIQUE NONCLUSTERED 
(
	[author_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[AllergyObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AllergyObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[allergy_type_code] [varchar](50) NULL,
	[allergy_type_text] [varchar](200) NULL,
	[observation_status_code] [varchar](50) NULL,
	[product_code] [varchar](100) NULL,
	[product_text] [varchar](200) NULL,
	[effective_time_high] [datetime2](7) NULL,
	[effective_time_low] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[allergy_id] [bigint] NOT NULL,
	[severity_observation_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[AdvanceDirective]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AdvanceDirective](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[text_type] [varchar](max) NULL,
	[effective_time_high] [datetime2](7) NULL,
	[effective_time_low] [datetime2](7) NULL,
	[advance_directive_type] [varchar](15) NULL,
	[database_id] [bigint] NOT NULL,
	[custodian_id] [bigint] NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[NumberOfPressureUlcersObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[NumberOfPressureUlcersObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[effective_time] [datetime2](7) NULL,
	[observation_value] [varchar](30) NULL,
	[value] [int] NULL,
	[database_id] [bigint] NOT NULL,
	[author_id] [bigint] NULL,
	[functional_status_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[HighestPressureUlcerStage]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[HighestPressureUlcerStage](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[value] [varchar](30) NULL,
	[database_id] [bigint] NOT NULL,
	[functional_status_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[FamilyHistoryObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[FamilyHistoryObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[age_observation_unit] [varchar](5) NULL,
	[age_observation_value] [int] NULL,
	[deceased] [bit] NULL,
	[effective_time] [datetime2](7) NULL,
	[problem_type_code] [varchar](15) NULL,
	[problem_value] [varchar](15) NULL,
	[database_id] [bigint] NOT NULL,
	[family_history_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[FunctionalStatus_CognitiveStatusProblemObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FunctionalStatus_CognitiveStatusProblemObservation](
	[functional_status_id] [bigint] NOT NULL,
	[problem_observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_gg4etoyk6e119inwpuuoh17bj] UNIQUE NONCLUSTERED 
(
	[problem_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FunctionalStatus_CaregiverCharacteristic]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FunctionalStatus_CaregiverCharacteristic](
	[functional_status_id] [bigint] NOT NULL,
	[caregiver_characteristic_id] [bigint] NOT NULL,
 CONSTRAINT [UK_trtrnwgmhr2a1kgle7b0k74r4] UNIQUE NONCLUSTERED 
(
	[caregiver_characteristic_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FunctionalStatus_AssessmentScaleObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FunctionalStatus_AssessmentScaleObservation](
	[functional_status_id] [bigint] NOT NULL,
	[observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_230dhcfaga4vor3f5uo7rnxwq] UNIQUE NONCLUSTERED 
(
	[observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FunctionalStatus_FunctionalStatusProblemObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FunctionalStatus_FunctionalStatusProblemObservation](
	[functional_status_id] [bigint] NOT NULL,
	[problem_observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_4y5evskrsxsv8kqyemle1v6lr] UNIQUE NONCLUSTERED 
(
	[problem_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FunctionalStatus_CognitiveStatusResultOrganizer]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FunctionalStatus_CognitiveStatusResultOrganizer](
	[functional_status_id] [bigint] NOT NULL,
	[result_organizer_id] [bigint] NOT NULL,
 CONSTRAINT [UK_je5h28ndu2ehioxpos7w4tut9] UNIQUE NONCLUSTERED 
(
	[result_organizer_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FunctionalStatus_NonMedicinalSupplyActivity]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FunctionalStatus_NonMedicinalSupplyActivity](
	[functional_status_id] [bigint] NOT NULL,
	[supply_activity_id] [bigint] NOT NULL,
 CONSTRAINT [UK_gnu3sk4xvdmi6ipucq37jnhiv] UNIQUE NONCLUSTERED 
(
	[supply_activity_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FunctionalStatus_FunctionalStatusResultOrganizer]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FunctionalStatus_FunctionalStatusResultOrganizer](
	[functional_status_id] [bigint] NOT NULL,
	[result_organizer_id] [bigint] NOT NULL,
 CONSTRAINT [UK_lotqy3p0yh798tk7lgg34qqqp] UNIQUE NONCLUSTERED 
(
	[result_organizer_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SocialHistoryObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[SocialHistoryObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[social_history_free_text] [varchar](max) NULL,
	[social_history_type] [varchar](50) NULL,
	[social_history_value] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[social_history_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[PlanOfCare_Supply]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PlanOfCare_Supply](
	[plan_of_care_id] [bigint] NOT NULL,
	[supply_id] [bigint] NOT NULL,
 CONSTRAINT [UK_moi0bj3jo3g9ut3crlfiokxl] UNIQUE NONCLUSTERED 
(
	[supply_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PlanOfCare_SubstanceAdministration]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PlanOfCare_SubstanceAdministration](
	[plan_of_care_id] [bigint] NOT NULL,
	[substance_administration_id] [bigint] NOT NULL,
 CONSTRAINT [UK_r1adld8j5tmpogaaexminsewo] UNIQUE NONCLUSTERED 
(
	[substance_administration_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PlanOfCare_Procedure]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PlanOfCare_Procedure](
	[plan_of_care_id] [bigint] NOT NULL,
	[procedure_id] [bigint] NOT NULL,
 CONSTRAINT [UK_huq8ggnbxom2o0cse1v6uupcw] UNIQUE NONCLUSTERED 
(
	[procedure_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PlanOfCare_Observation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PlanOfCare_Observation](
	[plan_of_care_id] [bigint] NOT NULL,
	[observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_sjm526l6ci1a3ccok74epscgu] UNIQUE NONCLUSTERED 
(
	[observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PlanOfCare_Instructions]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PlanOfCare_Instructions](
	[plan_of_care_id] [bigint] NOT NULL,
	[instruction_id] [bigint] NOT NULL,
 CONSTRAINT [UK_mbfhkj0qpjrvatul15epfo21l] UNIQUE NONCLUSTERED 
(
	[instruction_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PlanOfCare_Encounter]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PlanOfCare_Encounter](
	[plan_of_care_id] [bigint] NOT NULL,
	[encounter_id] [bigint] NOT NULL,
 CONSTRAINT [UK_55hs60fql0b4r5feiqi699ugl] UNIQUE NONCLUSTERED 
(
	[encounter_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PlanOfCare_Act]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PlanOfCare_Act](
	[plan_of_care_id] [bigint] NOT NULL,
	[act_id] [bigint] NOT NULL,
 CONSTRAINT [UK_9osqjkp8ep9c2wlbrpkrnyok] UNIQUE NONCLUSTERED 
(
	[act_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PressureUlcerObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[PressureUlcerObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[depth_of_wound_value] [float] NULL,
	[effective_time] [datetime2](7) NULL,
	[length_of_wound_value] [float] NULL,
	[negation_ind] [bit] NULL,
	[text] [varchar](max) NULL,
	[value] [varchar](20) NULL,
	[width_of_wound_value] [float] NULL,
	[database_id] [bigint] NOT NULL,
	[functional_status_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[PregnancyObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PregnancyObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[effective_time_low] [datetime2](7) NULL,
	[effective_time_high] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[social_history_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PolicyActivity]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[PolicyActivity](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[guarantor_time] [datetime2](7) NULL,
	[health_insurance_type_code] [varchar](8) NULL,
	[participant_date_of_birth] [datetime2](7) NULL,
	[payer_financially_responsible_party_code] [varchar](20) NULL,
	[sequence_number] [numeric](19, 2) NULL,
	[database_id] [bigint] NOT NULL,
	[guarantor_organization_id] [bigint] NULL,
	[guarantor_person_id] [bigint] NULL,
	[participant_id] [bigint] NULL,
	[payer_id] [bigint] NOT NULL,
	[payer_org_id] [bigint] NULL,
	[subscriber_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[MedicationSupplyOrder]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[MedicationSupplyOrder](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[legacy_table] [varchar](255) NOT NULL,
	[quantity] [int] NULL,
	[repeat_number] [int] NULL,
	[status_code] [varchar](50) NULL,
	[effective_time_high] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[author_id] [bigint] NULL,
	[immunization_medication_information_id] [bigint] NULL,
	[instructions_id] [bigint] NULL,
	[medication_information_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ProblemObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ProblemObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[age_observation_unit] [varchar](5) NULL,
	[age_observation_value] [int] NULL,
	[health_status_code] [varchar](15) NULL,
	[health_status_observation_text] [varchar](max) NULL,
	[problem_code] [varchar](15) NULL,
	[effective_time_high] [datetime2](7) NULL,
	[effective_time_low] [datetime2](7) NULL,
	[problem_name] [varchar](max) NULL,
	[problem_status_code] [varchar](15) NULL,
	[problem_status_text] [varchar](max) NULL,
	[problem_type] [varchar](15) NULL,
	[database_id] [bigint] NOT NULL,
	[problem_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ResidentUnitHistory]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ResidentUnitHistory](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[facility_unit] [varchar](21) NULL,
	[is_second_occupant] [bit] NULL,
	[move_in] [date] NULL,
	[move_in_is_transfer] [bit] NULL,
	[move_out] [date] NULL,
	[move_out_future] [date] NULL,
	[move_out_is_transfer] [bit] NULL,
	[notice_given] [date] NULL,
	[unit_number] [varchar](10) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[res_admit_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
	[unit_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ResultObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ResultObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[effective_time] [datetime2](7) NULL,
	[method_code] [varchar](255) NULL,
	[result_type_code] [varchar](50) NULL,
	[result_type_code_system] [varchar](50) NULL,
	[status_code] [varchar](50) NULL,
	[site_code] [varchar](255) NULL,
	[result_text] [varchar](255) NULL,
	[result_value] [int] NULL,
	[result_value_unit] [varchar](255) NULL,
	[database_id] [bigint] NOT NULL,
	[author_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[StatusResultObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[StatusResultObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](30) NULL,
	[effective_time] [datetime2](7) NULL,
	[method_code] [varchar](255) NULL,
	[target_site_code] [varchar](255) NULL,
	[text] [varchar](max) NULL,
	[value] [varchar](30) NULL,
	[value_unit] [varchar](10) NULL,
	[database_id] [bigint] NOT NULL,
	[author_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[SmokingStatusObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[SmokingStatusObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[effective_time_low] [datetime2](7) NULL,
	[value] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[social_history_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[VitalSignObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[VitalSignObservation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[effective_time] [datetime2](7) NULL,
	[interpretation_code] [varchar](30) NULL,
	[method_code] [varchar](30) NULL,
	[result_type_code] [varchar](30) NULL,
	[target_site_code] [varchar](30) NULL,
	[unit] [varchar](15) NULL,
	[value] [numeric](19, 2) NULL,
	[database_id] [bigint] NOT NULL,
	[author_id] [bigint] NULL,
	[vital_sign_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[TobaccoUse]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[TobaccoUse](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[effective_time_low] [datetime2](7) NULL,
	[value] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[social_history_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[TargetSiteCode]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[TargetSiteCode](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](30) NULL,
	[value] [varchar](30) NULL,
	[database_id] [bigint] NOT NULL,
	[pressure_ulcer_observation_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[StatusResultOrganizer_StatusResultObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[StatusResultOrganizer_StatusResultObservation](
	[status_result_organizer_id] [bigint] NOT NULL,
	[status_result_observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_cw26ns0wubfc4lbj8eksqbye6] UNIQUE NONCLUSTERED 
(
	[status_result_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[StatusResultObservationRange]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[StatusResultObservationRange](
	[result_observation_id] [bigint] NOT NULL,
	[result_range] [varchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[StatusResultObservationInterpretationCode]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[StatusResultObservationInterpretationCode](
	[result_observation_id] [bigint] NOT NULL,
	[interpretation_code] [varchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[StatusResultObservation_NonMedicinalSupplyActivity]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[StatusResultObservation_NonMedicinalSupplyActivity](
	[status_result_observation_id] [bigint] NOT NULL,
	[non_medicinal_supply_activity_id] [bigint] NOT NULL,
 CONSTRAINT [UK_oj3pchwfs7ien7lpp5o9v0pup] UNIQUE NONCLUSTERED 
(
	[non_medicinal_supply_activity_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[StatusResultObservation_CaregiverCharacteristic]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[StatusResultObservation_CaregiverCharacteristic](
	[status_result_observation_id] [bigint] NOT NULL,
	[caregiver_characteristic_id] [bigint] NOT NULL,
 CONSTRAINT [UK_68rilxwuw2vqdms8t7evcs99e] UNIQUE NONCLUSTERED 
(
	[caregiver_characteristic_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[StatusResultObservation_AssessmentScaleObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[StatusResultObservation_AssessmentScaleObservation](
	[status_result_observation_id] [bigint] NOT NULL,
	[assessment_scale_observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_chx4axs3yjls0if3mehwx6692] UNIQUE NONCLUSTERED 
(
	[assessment_scale_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ResultObservationRange]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ResultObservationRange](
	[result_observation_id] [bigint] NOT NULL,
	[result_range] [varchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ResultObservationInterpretationCode]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ResultObservationInterpretationCode](
	[result_observation_id] [bigint] NOT NULL,
	[interpretation_code] [varchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Result_ResultObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Result_ResultObservation](
	[result_id] [bigint] NOT NULL,
	[result_observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_p0cchua5iccevhvwjfwafesql] UNIQUE NONCLUSTERED 
(
	[result_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Medication]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Medication](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[administration_timing_period] [int] NULL,
	[administration_timing_unit] [varchar](255) NULL,
	[administration_unit_code] [varchar](50) NULL,
	[delivery_method_code] [varchar](50) NULL,
	[dose_quantity] [int] NULL,
	[dose_units] [varchar](50) NULL,
	[free_text_sig] [varchar](max) NULL,
	[max_dose_quantity] [int] NULL,
	[medication_started] [datetime2](7) NULL,
	[medication_stopped] [datetime2](7) NULL,
	[mood_code] [varchar](50) NULL,
	[rate_quantity] [int] NULL,
	[rate_units] [varchar](50) NULL,
	[repeat_number] [int] NULL,
	[repeat_number_mood] [varchar](50) NULL,
	[route_code] [varchar](50) NULL,
	[site_code] [varchar](50) NULL,
	[status_code] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[instructions_id] [bigint] NULL,
	[medication_information_id] [bigint] NULL,
	[medication_supply_order_id] [bigint] NULL,
	[reaction_observation_id] [bigint] NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_d9xd7ru9y9gcys7kgcmvu3f1m] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[FunctionalStatus_FunctionalStatusResultObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FunctionalStatus_FunctionalStatusResultObservation](
	[functional_status_id] [bigint] NOT NULL,
	[result_observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_82veit0ycx3j1hmeerbc3mgq8] UNIQUE NONCLUSTERED 
(
	[result_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FunctionalStatus_CognitiveStatusResultObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FunctionalStatus_CognitiveStatusResultObservation](
	[functional_status_id] [bigint] NOT NULL,
	[result_observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_i08s52gmxiu1u3ijnhskkhoj4] UNIQUE NONCLUSTERED 
(
	[result_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[MedicationDispense]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[MedicationDispense](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[effective_time_high] [datetime2](7) NULL,
	[effective_time_low] [datetime2](7) NULL,
	[repeat_number] [int] NULL,
	[legacy_table] [varchar](255) NOT NULL,
	[prescription_number] [varchar](50) NULL,
	[quantity] [int] NULL,
	[status_code] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[immunization_medication_information_id] [bigint] NULL,
	[medication_information_id] [bigint] NULL,
	[medication_supply_order_id] [bigint] NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[AllergyObservation_ReactionObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[AllergyObservation_ReactionObservation](
	[allergy_observation_id] [bigint] NOT NULL,
	[reaction_observation_id] [bigint] NOT NULL,
 CONSTRAINT [UK_8vssyk7gx48xqwbiyn1y2nh71] UNIQUE NONCLUSTERED 
(
	[reaction_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[AdvanceDirectivesVerifier]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[AdvanceDirectivesVerifier](
	[advance_directive_id] [bigint] NOT NULL,
	[verifier_id] [bigint] NOT NULL,
 CONSTRAINT [UK_69ohodnvt7ye55pdm9qqpyu64] UNIQUE NONCLUSTERED 
(
	[verifier_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[AdvanceDirectiveDocument]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AdvanceDirectiveDocument](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[media_type] [varchar](255) NULL,
	[url] [varchar](255) NULL,
	[database_id] [bigint] NOT NULL,
	[advance_directive_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[AuthorizationActivity]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[AuthorizationActivity](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[policy_activity_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CoveragePlanDescription]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[CoveragePlanDescription](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[text] [varchar](max) NULL,
	[database_id] [bigint] NOT NULL,
	[policy_activity_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Encounter]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Encounter](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[disposition_code] [varchar](50) NULL,
	[effective_time] [datetime2](7) NULL,
	[type_code] [varchar](50) NULL,
	[type_text] [varchar](255) NULL,
	[database_id] [bigint] NOT NULL,
	[problem_observation_id] [bigint] NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_b4h6ufhfggo9qm14rko708kmn] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[EncounterProvider]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[EncounterProvider](
	[encounter_id] [bigint] NOT NULL,
	[provider_code] [varchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Encounter_Indication]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Encounter_Indication](
	[encounter_id] [bigint] NOT NULL,
	[indication_id] [bigint] NOT NULL,
 CONSTRAINT [UK_jy2r81w9g41pkpk12n8p11ygj] UNIQUE NONCLUSTERED 
(
	[indication_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Encounter_DeliveryLocation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Encounter_DeliveryLocation](
	[encounter_id] [bigint] NOT NULL,
	[location_id] [bigint] NOT NULL,
 CONSTRAINT [UK_3cj0dywv8hc3ke7enidu4cs71] UNIQUE NONCLUSTERED 
(
	[location_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[AuthorizationActivityClinicalStatement]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AuthorizationActivityClinicalStatement](
	[authorization_activity_id] [bigint] NOT NULL,
	[clinical_statement] [varchar](60) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Medication_MedicationPrecondition]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Medication_MedicationPrecondition](
	[medication_id] [bigint] NOT NULL,
	[precondition_id] [bigint] NOT NULL,
 CONSTRAINT [UK_e2tiqnoai98hvpaww0gdgak18] UNIQUE NONCLUSTERED 
(
	[precondition_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Medication_MedicationDispense]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Medication_MedicationDispense](
	[medication_id] [bigint] NOT NULL,
	[medication_dispense_id] [bigint] NOT NULL,
 CONSTRAINT [UK_iq7aapdf84pfddtcqvas2tfia] UNIQUE NONCLUSTERED 
(
	[medication_dispense_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Medication_Indication]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Medication_Indication](
	[medication_id] [bigint] NOT NULL,
	[indication_id] [bigint] NOT NULL,
 CONSTRAINT [UK_b4bjncm0nemkyjg2jcqhtooyc] UNIQUE NONCLUSTERED 
(
	[indication_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Medication_DrugVehicle]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Medication_DrugVehicle](
	[medication_id] [bigint] NOT NULL,
	[drug_vehicle_id] [bigint] NOT NULL,
 CONSTRAINT [UK_m3bku43irex9c3h9kuhof7al3] UNIQUE NONCLUSTERED 
(
	[drug_vehicle_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Immunization]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Immunization](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[administration_unit_code] [varchar](50) NULL,
	[code] [varchar](50) NULL,
	[dose_quantity] [int] NULL,
	[dose_units] [varchar](50) NULL,
	[immunization_started] [datetime2](7) NULL,
	[immunization_stopped] [datetime2](7) NULL,
	[mood_code] [varchar](50) NULL,
	[refusal] [bit] NULL,
	[repeat_number] [int] NULL,
	[repeat_number_mood] [varchar](50) NULL,
	[route_code] [varchar](50) NULL,
	[site_code] [varchar](50) NULL,
	[status_code] [varchar](50) NULL,
	[text] [varchar](max) NULL,
	[database_id] [bigint] NOT NULL,
	[immunization_medication_information_id] [bigint] NULL,
	[immunization_refusal_reason_id] [bigint] NULL,
	[instructions_id] [bigint] NULL,
	[medication_dispense_id] [bigint] NULL,
	[medication_supply_order_id] [bigint] NULL,
	[reaction_observation_id] [bigint] NULL,
	[resident_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_37rugduhsrn70e6h8o7000onf] UNIQUE NONCLUSTERED 
(
	[legacy_id] ASC,
	[database_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ProcedureActivity]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ProcedureActivity](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[method_code] [varchar](50) NULL,
	[mood_code] [varchar](50) NULL,
	[priority_code] [varchar](50) NULL,
	[procedure_started] [datetime2](7) NULL,
	[procedure_stopped] [datetime2](7) NULL,
	[status_code] [varchar](50) NULL,
	[value] [varchar](max) NULL,
	[database_id] [bigint] NOT NULL,
	[instructions_id] [bigint] NULL,
	[medication_id] [bigint] NULL,
	[procedure_type_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ProcedureActivitySpecimen]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ProcedureActivitySpecimen](
	[procedure_activity_id] [bigint] NOT NULL,
	[specimen_id] [varchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ProcedureActivitySite]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ProcedureActivitySite](
	[procedure_activity_id] [bigint] NOT NULL,
	[body_site_code] [varchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ProcedureActivityEncounter]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[ProcedureActivityEncounter](
	[procedure_activity_id] [bigint] NOT NULL,
	[encounter_id] [varchar](255) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[ProcedureActivity_ProductInstance]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ProcedureActivity_ProductInstance](
	[procedure_activity_id] [bigint] NOT NULL,
	[product_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[procedure_activity_id] ASC,
	[product_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_1va36crdlypeq9fupl6kj77qw] UNIQUE NONCLUSTERED 
(
	[product_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ProcedureActivity_Performer]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ProcedureActivity_Performer](
	[procedure_activity_id] [bigint] NOT NULL,
	[organization_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[procedure_activity_id] ASC,
	[organization_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_o5wm8eq6q42iwcve82y7yffit] UNIQUE NONCLUSTERED 
(
	[organization_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ProcedureActivity_Indication]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ProcedureActivity_Indication](
	[procedure_activity_id] [bigint] NOT NULL,
	[indication_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[procedure_activity_id] ASC,
	[indication_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_k1lgb429a2hogprl8mtteccg0] UNIQUE NONCLUSTERED 
(
	[indication_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ProcedureActivity_DeliveryLocation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ProcedureActivity_DeliveryLocation](
	[procedure_activity_id] [bigint] NOT NULL,
	[location_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[procedure_activity_id] ASC,
	[location_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_si0nl0doh36vtct5spq0yuuk5] UNIQUE NONCLUSTERED 
(
	[location_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Procedure_ActivityProcedure]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Procedure_ActivityProcedure](
	[procedure_id] [bigint] NOT NULL,
	[procedure_activity_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[procedure_id] ASC,
	[procedure_activity_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_tbalv7m32ae6wp5e8csmlb67l] UNIQUE NONCLUSTERED 
(
	[procedure_activity_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Procedure_ActivityObservation]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Procedure_ActivityObservation](
	[procedure_id] [bigint] NOT NULL,
	[procedure_observation_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[procedure_id] ASC,
	[procedure_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_go8yrtxr1uu19pxrkp0ie7334] UNIQUE NONCLUSTERED 
(
	[procedure_observation_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Procedure_ActivityAct]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Procedure_ActivityAct](
	[procedure_id] [bigint] NOT NULL,
	[procedure_act_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[procedure_id] ASC,
	[procedure_act_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UK_6qabhdme7w90rk33xrvp2pgm2] UNIQUE NONCLUSTERED 
(
	[procedure_act_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Immunization_MedicationPrecondition]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Immunization_MedicationPrecondition](
	[immunization_id] [bigint] NOT NULL,
	[precondition_id] [bigint] NOT NULL,
 CONSTRAINT [UK_37ntpjeyorlluwnkxb251t5ar] UNIQUE NONCLUSTERED 
(
	[precondition_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Immunization_Indication]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Immunization_Indication](
	[medication_id] [bigint] NOT NULL,
	[indication_id] [bigint] NOT NULL,
 CONSTRAINT [UK_ps836nuua6nrvjsohsm2hesg1] UNIQUE NONCLUSTERED 
(
	[indication_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Immunization_DrugVehicle]    Script Date: 01/08/2014 11:35:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Immunization_DrugVehicle](
	[immunization_id] [bigint] NOT NULL,
	[drug_vehicle_id] [bigint] NOT NULL,
 CONSTRAINT [UK_7bc33fsufenhhpf61ggecgg8o] UNIQUE NONCLUSTERED 
(
	[drug_vehicle_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  ForeignKey [FK_9gmmx67i86tlcoo08we3xvuvi]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Employee]  WITH CHECK ADD  CONSTRAINT [FK_9gmmx67i86tlcoo08we3xvuvi] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Employee] CHECK CONSTRAINT [FK_9gmmx67i86tlcoo08we3xvuvi]
GO
/****** Object:  ForeignKey [FK_ob8qvruy1vk0mu1b3o7evg5v7]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DrugVehicle]  WITH CHECK ADD  CONSTRAINT [FK_ob8qvruy1vk0mu1b3o7evg5v7] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[DrugVehicle] CHECK CONSTRAINT [FK_ob8qvruy1vk0mu1b3o7evg5v7]
GO
/****** Object:  ForeignKey [FK_ih2ffdc0n0fq1x9553gprrand]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[CommunicationType]  WITH CHECK ADD  CONSTRAINT [FK_ih2ffdc0n0fq1x9553gprrand] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[CommunicationType] CHECK CONSTRAINT [FK_ih2ffdc0n0fq1x9553gprrand]
GO
/****** Object:  ForeignKey [FK_c82land9ry56tmn2hldetbgw5]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DeliveryLocation]  WITH CHECK ADD  CONSTRAINT [FK_c82land9ry56tmn2hldetbgw5] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[DeliveryLocation] CHECK CONSTRAINT [FK_c82land9ry56tmn2hldetbgw5]
GO
/****** Object:  ForeignKey [FK_nw3u1m6iymyer8hdqsuxn1wli]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DataSyncProblem]  WITH CHECK ADD  CONSTRAINT [FK_nw3u1m6iymyer8hdqsuxn1wli] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[DataSyncProblem] CHECK CONSTRAINT [FK_nw3u1m6iymyer8hdqsuxn1wli]
GO
/****** Object:  ForeignKey [FK_3uovms1k7l8yafc3ly64afse3]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DataSyncLog]  WITH CHECK ADD  CONSTRAINT [FK_3uovms1k7l8yafc3ly64afse3] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[DataSyncLog] CHECK CONSTRAINT [FK_3uovms1k7l8yafc3ly64afse3]
GO
/****** Object:  ForeignKey [FK_h00r42yyvf2h8y9g5v1dykhqi]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AssessmentScaleObservation]  WITH CHECK ADD  CONSTRAINT [FK_h00r42yyvf2h8y9g5v1dykhqi] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[AssessmentScaleObservation] CHECK CONSTRAINT [FK_h00r42yyvf2h8y9g5v1dykhqi]
GO
/****** Object:  ForeignKey [FK_p595u6ye8ugqd6o9bsxvmebpq]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[CaregiverCharacteristic]  WITH CHECK ADD  CONSTRAINT [FK_p595u6ye8ugqd6o9bsxvmebpq] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[CaregiverCharacteristic] CHECK CONSTRAINT [FK_p595u6ye8ugqd6o9bsxvmebpq]
GO
/****** Object:  ForeignKey [FK_skry17qao77i3m3crmc5whyth]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Instructions]  WITH CHECK ADD  CONSTRAINT [FK_skry17qao77i3m3crmc5whyth] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Instructions] CHECK CONSTRAINT [FK_skry17qao77i3m3crmc5whyth]
GO
/****** Object:  ForeignKey [FK_dkwm7fdlnhl5dl10bg8bkfgf7]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Indication]  WITH CHECK ADD  CONSTRAINT [FK_dkwm7fdlnhl5dl10bg8bkfgf7] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Indication] CHECK CONSTRAINT [FK_dkwm7fdlnhl5dl10bg8bkfgf7]
GO
/****** Object:  ForeignKey [FK_l1qppdplbac04d9sv4alyq3xe]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ImmunizationRefusalReason]  WITH CHECK ADD  CONSTRAINT [FK_l1qppdplbac04d9sv4alyq3xe] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ImmunizationRefusalReason] CHECK CONSTRAINT [FK_l1qppdplbac04d9sv4alyq3xe]
GO
/****** Object:  ForeignKey [FK_98btbxobqdcdq1y56ac5eshpa]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationPrecondition]  WITH CHECK ADD  CONSTRAINT [FK_98btbxobqdcdq1y56ac5eshpa] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[MedicationPrecondition] CHECK CONSTRAINT [FK_98btbxobqdcdq1y56ac5eshpa]
GO
/****** Object:  ForeignKey [FK_8k0b8ajyugrwyknygi245wdce]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Organization]  WITH CHECK ADD  CONSTRAINT [FK_8k0b8ajyugrwyknygi245wdce] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Organization] CHECK CONSTRAINT [FK_8k0b8ajyugrwyknygi245wdce]
GO
/****** Object:  ForeignKey [FK_sr198vkv2ak74hfup3mqxkfb7]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCareActivity]  WITH CHECK ADD  CONSTRAINT [FK_sr198vkv2ak74hfup3mqxkfb7] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[PlanOfCareActivity] CHECK CONSTRAINT [FK_sr198vkv2ak74hfup3mqxkfb7]
GO
/****** Object:  ForeignKey [FK_6pv4wyj3lc7djwufrnor1reml]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Person]  WITH CHECK ADD  CONSTRAINT [FK_6pv4wyj3lc7djwufrnor1reml] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Person] CHECK CONSTRAINT [FK_6pv4wyj3lc7djwufrnor1reml]
GO
/****** Object:  ForeignKey [FK_oo590giub3biwg2lxjsgbwx1v]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProductInstance]  WITH CHECK ADD  CONSTRAINT [FK_oo590giub3biwg2lxjsgbwx1v] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ProductInstance] CHECK CONSTRAINT [FK_oo590giub3biwg2lxjsgbwx1v]
GO
/****** Object:  ForeignKey [FK_6kicxcwswayfv9602hljq3s14]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureType]  WITH CHECK ADD  CONSTRAINT [FK_6kicxcwswayfv9602hljq3s14] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ProcedureType] CHECK CONSTRAINT [FK_6kicxcwswayfv9602hljq3s14]
GO
/****** Object:  ForeignKey [FK_ahdvta63whm9n1232629u0fr0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[SeverityObservation]  WITH CHECK ADD  CONSTRAINT [FK_ahdvta63whm9n1232629u0fr0] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[SeverityObservation] CHECK CONSTRAINT [FK_ahdvta63whm9n1232629u0fr0]
GO
/****** Object:  ForeignKey [FK_mgexx7whnax72ejn82mbo7eeb]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ReactionObservation]  WITH CHECK ADD  CONSTRAINT [FK_mgexx7whnax72ejn82mbo7eeb] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ReactionObservation] CHECK CONSTRAINT [FK_mgexx7whnax72ejn82mbo7eeb]
GO
/****** Object:  ForeignKey [FK_q6mde4t6urchriatrmxr3mi55]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultOrganizer]  WITH CHECK ADD  CONSTRAINT [FK_q6mde4t6urchriatrmxr3mi55] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[StatusResultOrganizer] CHECK CONSTRAINT [FK_q6mde4t6urchriatrmxr3mi55]
GO
/****** Object:  ForeignKey [FK_hlm9krxost5p4s9jabdpdxc4w]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusProblemObservation]  WITH CHECK ADD  CONSTRAINT [FK_hlm9krxost5p4s9jabdpdxc4w] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[StatusProblemObservation] CHECK CONSTRAINT [FK_hlm9krxost5p4s9jabdpdxc4w]
GO
/****** Object:  ForeignKey [FK_kfg5il5ivk14yn6anrqo2ga17]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[SystemSetup]  WITH CHECK ADD  CONSTRAINT [FK_kfg5il5ivk14yn6anrqo2ga17] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[SystemSetup] CHECK CONSTRAINT [FK_kfg5il5ivk14yn6anrqo2ga17]
GO
/****** Object:  ForeignKey [FK_7nhdouldpxrw22i2fn8lyr4q5]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusProblemObservation_CaregiverCharacteristic]  WITH CHECK ADD  CONSTRAINT [FK_7nhdouldpxrw22i2fn8lyr4q5] FOREIGN KEY([caregiver_characteristic_id])
REFERENCES [dbo].[CaregiverCharacteristic] ([id])
GO
ALTER TABLE [dbo].[StatusProblemObservation_CaregiverCharacteristic] CHECK CONSTRAINT [FK_7nhdouldpxrw22i2fn8lyr4q5]
GO
/****** Object:  ForeignKey [FK_dhuejg5jsxql4an6l146s19o4]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusProblemObservation_CaregiverCharacteristic]  WITH CHECK ADD  CONSTRAINT [FK_dhuejg5jsxql4an6l146s19o4] FOREIGN KEY([status_problem_observation_id])
REFERENCES [dbo].[StatusProblemObservation] ([id])
GO
ALTER TABLE [dbo].[StatusProblemObservation_CaregiverCharacteristic] CHECK CONSTRAINT [FK_dhuejg5jsxql4an6l146s19o4]
GO
/****** Object:  ForeignKey [FK_24jxavnd887rcu76l9hevultd]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusProblemObservation_AssessmentScaleObservation]  WITH CHECK ADD  CONSTRAINT [FK_24jxavnd887rcu76l9hevultd] FOREIGN KEY([assessment_scale_observation_id])
REFERENCES [dbo].[AssessmentScaleObservation] ([id])
GO
ALTER TABLE [dbo].[StatusProblemObservation_AssessmentScaleObservation] CHECK CONSTRAINT [FK_24jxavnd887rcu76l9hevultd]
GO
/****** Object:  ForeignKey [FK_79vjxva5psom16adc4b1sv6w0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusProblemObservation_AssessmentScaleObservation]  WITH CHECK ADD  CONSTRAINT [FK_79vjxva5psom16adc4b1sv6w0] FOREIGN KEY([status_problem_observation_id])
REFERENCES [dbo].[StatusProblemObservation] ([id])
GO
ALTER TABLE [dbo].[StatusProblemObservation_AssessmentScaleObservation] CHECK CONSTRAINT [FK_79vjxva5psom16adc4b1sv6w0]
GO
/****** Object:  ForeignKey [FK_2ysr9unkbbnny1euu89yl09mt]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[UnitType]  WITH CHECK ADD  CONSTRAINT [FK_2ysr9unkbbnny1euu89yl09mt] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[UnitType] CHECK CONSTRAINT [FK_2ysr9unkbbnny1euu89yl09mt]
GO
/****** Object:  ForeignKey [FK_nfc8yd51rioj20rouqpqsut28]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[UnitType]  WITH CHECK ADD  CONSTRAINT [FK_nfc8yd51rioj20rouqpqsut28] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[UnitType] CHECK CONSTRAINT [FK_nfc8yd51rioj20rouqpqsut28]
GO
/****** Object:  ForeignKey [FK_8139o5fcppfuu88fm964db6ht]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ReactionObservation_SeverityObservation]  WITH CHECK ADD  CONSTRAINT [FK_8139o5fcppfuu88fm964db6ht] FOREIGN KEY([reaction_observation_id])
REFERENCES [dbo].[ReactionObservation] ([id])
GO
ALTER TABLE [dbo].[ReactionObservation_SeverityObservation] CHECK CONSTRAINT [FK_8139o5fcppfuu88fm964db6ht]
GO
/****** Object:  ForeignKey [FK_rvi8a0xiahqmolutix07larwl]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ReactionObservation_SeverityObservation]  WITH CHECK ADD  CONSTRAINT [FK_rvi8a0xiahqmolutix07larwl] FOREIGN KEY([severity_observation_id])
REFERENCES [dbo].[SeverityObservation] ([id])
GO
ALTER TABLE [dbo].[ReactionObservation_SeverityObservation] CHECK CONSTRAINT [FK_rvi8a0xiahqmolutix07larwl]
GO
/****** Object:  ForeignKey [FK_8lqiypsxyso4s9kmr2x44g8mt]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Pharmacy]  WITH CHECK ADD  CONSTRAINT [FK_8lqiypsxyso4s9kmr2x44g8mt] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Pharmacy] CHECK CONSTRAINT [FK_8lqiypsxyso4s9kmr2x44g8mt]
GO
/****** Object:  ForeignKey [FK_l5ajfvv99ahffa2a52526gf00]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Pharmacy]  WITH CHECK ADD  CONSTRAINT [FK_l5ajfvv99ahffa2a52526gf00] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Pharmacy] CHECK CONSTRAINT [FK_l5ajfvv99ahffa2a52526gf00]
GO
/****** Object:  ForeignKey [FK_6ugv14ya3txqk28ugjo4wyr4n]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PersonTelecom]  WITH CHECK ADD  CONSTRAINT [FK_6ugv14ya3txqk28ugjo4wyr4n] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[PersonTelecom] CHECK CONSTRAINT [FK_6ugv14ya3txqk28ugjo4wyr4n]
GO
/****** Object:  ForeignKey [FK_eqo86pvwg4yt13xcap6ksduy1]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PersonTelecom]  WITH CHECK ADD  CONSTRAINT [FK_eqo86pvwg4yt13xcap6ksduy1] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[PersonTelecom] CHECK CONSTRAINT [FK_eqo86pvwg4yt13xcap6ksduy1]
GO
/****** Object:  ForeignKey [FK_10qv97l554bcx35sjy1mfu470]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PersonAddress]  WITH CHECK ADD  CONSTRAINT [FK_10qv97l554bcx35sjy1mfu470] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[PersonAddress] CHECK CONSTRAINT [FK_10qv97l554bcx35sjy1mfu470]
GO
/****** Object:  ForeignKey [FK_3ri648ppphqq35ys0m940xcb3]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PersonAddress]  WITH CHECK ADD  CONSTRAINT [FK_3ri648ppphqq35ys0m940xcb3] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[PersonAddress] CHECK CONSTRAINT [FK_3ri648ppphqq35ys0m940xcb3]
GO
/****** Object:  ForeignKey [FK_hl8csp815xiguoay8b0b5pw6q]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicalProfessional]  WITH CHECK ADD  CONSTRAINT [FK_hl8csp815xiguoay8b0b5pw6q] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[MedicalProfessional] CHECK CONSTRAINT [FK_hl8csp815xiguoay8b0b5pw6q]
GO
/****** Object:  ForeignKey [FK_ly6odnds00x9mls8y1q6opoop]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicalProfessional]  WITH CHECK ADD  CONSTRAINT [FK_ly6odnds00x9mls8y1q6opoop] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[MedicalProfessional] CHECK CONSTRAINT [FK_ly6odnds00x9mls8y1q6opoop]
GO
/****** Object:  ForeignKey [FK_bo9k1trrjswrqdoec3wbkkwdv]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OrgReferralSource]  WITH CHECK ADD  CONSTRAINT [FK_bo9k1trrjswrqdoec3wbkkwdv] FOREIGN KEY([employee_owner_id])
REFERENCES [dbo].[Employee] ([id])
GO
ALTER TABLE [dbo].[OrgReferralSource] CHECK CONSTRAINT [FK_bo9k1trrjswrqdoec3wbkkwdv]
GO
/****** Object:  ForeignKey [FK_sas1gbhmyw23w71kv6xf6apw0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OrgReferralSource]  WITH CHECK ADD  CONSTRAINT [FK_sas1gbhmyw23w71kv6xf6apw0] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[OrgReferralSource] CHECK CONSTRAINT [FK_sas1gbhmyw23w71kv6xf6apw0]
GO
/****** Object:  ForeignKey [FK_9yjy679grdm4adore2cbf4g0d]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OrganizationTelecom]  WITH CHECK ADD  CONSTRAINT [FK_9yjy679grdm4adore2cbf4g0d] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[OrganizationTelecom] CHECK CONSTRAINT [FK_9yjy679grdm4adore2cbf4g0d]
GO
/****** Object:  ForeignKey [FK_tmgti1uauvapffit0cvujarwj]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OrganizationTelecom]  WITH CHECK ADD  CONSTRAINT [FK_tmgti1uauvapffit0cvujarwj] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[OrganizationTelecom] CHECK CONSTRAINT [FK_tmgti1uauvapffit0cvujarwj]
GO
/****** Object:  ForeignKey [FK_nxubc8p1hek5gfmgponh1xhoy]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OrganizationAddress]  WITH CHECK ADD  CONSTRAINT [FK_nxubc8p1hek5gfmgponh1xhoy] FOREIGN KEY([org_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[OrganizationAddress] CHECK CONSTRAINT [FK_nxubc8p1hek5gfmgponh1xhoy]
GO
/****** Object:  ForeignKey [FK_rsvt5wghk75wy47roxkpk0ei0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OrganizationAddress]  WITH CHECK ADD  CONSTRAINT [FK_rsvt5wghk75wy47roxkpk0ei0] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[OrganizationAddress] CHECK CONSTRAINT [FK_rsvt5wghk75wy47roxkpk0ei0]
GO
/****** Object:  ForeignKey [FK_5tkqclkwd6cpe5ii99ps7s1el]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[NonMedicinalSupplyActivity]  WITH CHECK ADD  CONSTRAINT [FK_5tkqclkwd6cpe5ii99ps7s1el] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[NonMedicinalSupplyActivity] CHECK CONSTRAINT [FK_5tkqclkwd6cpe5ii99ps7s1el]
GO
/****** Object:  ForeignKey [FK_qj21klxnroofeyxa6tyauo0ms]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[NonMedicinalSupplyActivity]  WITH CHECK ADD  CONSTRAINT [FK_qj21klxnroofeyxa6tyauo0ms] FOREIGN KEY([product_instance_id])
REFERENCES [dbo].[ProductInstance] ([id])
GO
ALTER TABLE [dbo].[NonMedicinalSupplyActivity] CHECK CONSTRAINT [FK_qj21klxnroofeyxa6tyauo0ms]
GO
/****** Object:  ForeignKey [FK_36e1v4ooq9cc4whahnhdc7083]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Name]  WITH CHECK ADD  CONSTRAINT [FK_36e1v4ooq9cc4whahnhdc7083] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[Name] CHECK CONSTRAINT [FK_36e1v4ooq9cc4whahnhdc7083]
GO
/****** Object:  ForeignKey [FK_og5tck91mb5gh2ghvs9bqsjvu]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Name]  WITH CHECK ADD  CONSTRAINT [FK_og5tck91mb5gh2ghvs9bqsjvu] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Name] CHECK CONSTRAINT [FK_og5tck91mb5gh2ghvs9bqsjvu]
GO
/****** Object:  ForeignKey [FK_5nri0v9rti1dqfy8yk2xnrnmo]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OccupancyGoal]  WITH CHECK ADD  CONSTRAINT [FK_5nri0v9rti1dqfy8yk2xnrnmo] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[OccupancyGoal] CHECK CONSTRAINT [FK_5nri0v9rti1dqfy8yk2xnrnmo]
GO
/****** Object:  ForeignKey [FK_cpjix5rcqhh9hp4v376r3nv72]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OccupancyGoal]  WITH CHECK ADD  CONSTRAINT [FK_cpjix5rcqhh9hp4v376r3nv72] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[OccupancyGoal] CHECK CONSTRAINT [FK_cpjix5rcqhh9hp4v376r3nv72]
GO
/****** Object:  ForeignKey [FK_1pevt3re2y69ll9coy8palgpa]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationInformation]  WITH CHECK ADD  CONSTRAINT [FK_1pevt3re2y69ll9coy8palgpa] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[MedicationInformation] CHECK CONSTRAINT [FK_1pevt3re2y69ll9coy8palgpa]
GO
/****** Object:  ForeignKey [FK_ggsmpln474w40kts82s9jhit1]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationInformation]  WITH CHECK ADD  CONSTRAINT [FK_ggsmpln474w40kts82s9jhit1] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[MedicationInformation] CHECK CONSTRAINT [FK_ggsmpln474w40kts82s9jhit1]
GO
/****** Object:  ForeignKey [FK_esribpygjtpkpqph6njrrdr6v]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Employee_Organization]  WITH CHECK ADD  CONSTRAINT [FK_esribpygjtpkpqph6njrrdr6v] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Employee_Organization] CHECK CONSTRAINT [FK_esribpygjtpkpqph6njrrdr6v]
GO
/****** Object:  ForeignKey [FK_quvsmegaptfwv48gu65xmoqc9]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Employee_Organization]  WITH CHECK ADD  CONSTRAINT [FK_quvsmegaptfwv48gu65xmoqc9] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Employee_Organization] CHECK CONSTRAINT [FK_quvsmegaptfwv48gu65xmoqc9]
GO
/****** Object:  ForeignKey [FK_t39snytrfpm5vts59tlkj2n7d]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Employee_Organization]  WITH CHECK ADD  CONSTRAINT [FK_t39snytrfpm5vts59tlkj2n7d] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee] ([id])
GO
ALTER TABLE [dbo].[Employee_Organization] CHECK CONSTRAINT [FK_t39snytrfpm5vts59tlkj2n7d]
GO
/****** Object:  ForeignKey [FK_a18gx5p3u9o7u27c8vsofjcfj]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[LegalAuthenticator]  WITH CHECK ADD  CONSTRAINT [FK_a18gx5p3u9o7u27c8vsofjcfj] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[LegalAuthenticator] CHECK CONSTRAINT [FK_a18gx5p3u9o7u27c8vsofjcfj]
GO
/****** Object:  ForeignKey [FK_dggcrivui4oj4s93q4bf1en2a]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[LegalAuthenticator]  WITH CHECK ADD  CONSTRAINT [FK_dggcrivui4oj4s93q4bf1en2a] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[LegalAuthenticator] CHECK CONSTRAINT [FK_dggcrivui4oj4s93q4bf1en2a]
GO
/****** Object:  ForeignKey [FK_atr93fkb114sm0b5ngokvehbd]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ImmunizationMedicationInformation]  WITH CHECK ADD  CONSTRAINT [FK_atr93fkb114sm0b5ngokvehbd] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ImmunizationMedicationInformation] CHECK CONSTRAINT [FK_atr93fkb114sm0b5ngokvehbd]
GO
/****** Object:  ForeignKey [FK_rreofq8dam9e4yxja6rbuk54w]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ImmunizationMedicationInformation]  WITH CHECK ADD  CONSTRAINT [FK_rreofq8dam9e4yxja6rbuk54w] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[ImmunizationMedicationInformation] CHECK CONSTRAINT [FK_rreofq8dam9e4yxja6rbuk54w]
GO
/****** Object:  ForeignKey [FK_1m9u7p8weqfju75c9jau5xgqd]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Inquiry]  WITH CHECK ADD  CONSTRAINT [FK_1m9u7p8weqfju75c9jau5xgqd] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Inquiry] CHECK CONSTRAINT [FK_1m9u7p8weqfju75c9jau5xgqd]
GO
/****** Object:  ForeignKey [FK_7y0mhmg9dk38965qf06tstj4u]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Inquiry]  WITH CHECK ADD  CONSTRAINT [FK_7y0mhmg9dk38965qf06tstj4u] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Inquiry] CHECK CONSTRAINT [FK_7y0mhmg9dk38965qf06tstj4u]
GO
/****** Object:  ForeignKey [FK_em3xnwmx4hc65lpvw1891qfh4]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Inquiry]  WITH CHECK ADD  CONSTRAINT [FK_em3xnwmx4hc65lpvw1891qfh4] FOREIGN KEY([sales_rep_employee_id])
REFERENCES [dbo].[Employee] ([id])
GO
ALTER TABLE [dbo].[Inquiry] CHECK CONSTRAINT [FK_em3xnwmx4hc65lpvw1891qfh4]
GO
/****** Object:  ForeignKey [FK_nu0o74by7j4ph3ie2gw9s2v4v]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AssessmentScaleSupportingObservation]  WITH CHECK ADD  CONSTRAINT [FK_nu0o74by7j4ph3ie2gw9s2v4v] FOREIGN KEY([assessment_scale_observation_id])
REFERENCES [dbo].[AssessmentScaleObservation] ([id])
GO
ALTER TABLE [dbo].[AssessmentScaleSupportingObservation] CHECK CONSTRAINT [FK_nu0o74by7j4ph3ie2gw9s2v4v]
GO
/****** Object:  ForeignKey [FK_nyqbb6buscn1e9htvimrxlm5l]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AssessmentScaleSupportingObservation]  WITH CHECK ADD  CONSTRAINT [FK_nyqbb6buscn1e9htvimrxlm5l] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[AssessmentScaleSupportingObservation] CHECK CONSTRAINT [FK_nyqbb6buscn1e9htvimrxlm5l]
GO
/****** Object:  ForeignKey [FK_k4r3mipeyv3vxwqu63cqtme0y]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AssessmentScaleObservationRange]  WITH CHECK ADD  CONSTRAINT [FK_k4r3mipeyv3vxwqu63cqtme0y] FOREIGN KEY([observation_id])
REFERENCES [dbo].[AssessmentScaleObservation] ([id])
GO
ALTER TABLE [dbo].[AssessmentScaleObservationRange] CHECK CONSTRAINT [FK_k4r3mipeyv3vxwqu63cqtme0y]
GO
/****** Object:  ForeignKey [FK_m8plbwrgq5v8dd498h75f8925]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AssessmentScaleObservationInterpretationCode]  WITH CHECK ADD  CONSTRAINT [FK_m8plbwrgq5v8dd498h75f8925] FOREIGN KEY([observation_id])
REFERENCES [dbo].[AssessmentScaleObservation] ([id])
GO
ALTER TABLE [dbo].[AssessmentScaleObservationInterpretationCode] CHECK CONSTRAINT [FK_m8plbwrgq5v8dd498h75f8925]
GO
/****** Object:  ForeignKey [FK_roa3rr70adksabu6887jm8jrh]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DataEnterer]  WITH CHECK ADD  CONSTRAINT [FK_roa3rr70adksabu6887jm8jrh] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[DataEnterer] CHECK CONSTRAINT [FK_roa3rr70adksabu6887jm8jrh]
GO
/****** Object:  ForeignKey [FK_t2cwdantdqrk7vsrt0x0wy9c1]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DataEnterer]  WITH CHECK ADD  CONSTRAINT [FK_t2cwdantdqrk7vsrt0x0wy9c1] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[DataEnterer] CHECK CONSTRAINT [FK_t2cwdantdqrk7vsrt0x0wy9c1]
GO
/****** Object:  ForeignKey [FK_22vvfydfcptkeabsp3duqj6hx]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Custodian]  WITH CHECK ADD  CONSTRAINT [FK_22vvfydfcptkeabsp3duqj6hx] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Custodian] CHECK CONSTRAINT [FK_22vvfydfcptkeabsp3duqj6hx]
GO
/****** Object:  ForeignKey [FK_eiwbktek7t3vfby8mor2fnnwx]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Custodian]  WITH CHECK ADD  CONSTRAINT [FK_eiwbktek7t3vfby8mor2fnnwx] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Custodian] CHECK CONSTRAINT [FK_eiwbktek7t3vfby8mor2fnnwx]
GO
/****** Object:  ForeignKey [FK_fulfqhld69lh6veh1gthjjn2s]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DeliveryLocation_OrganizationTelecom]  WITH CHECK ADD  CONSTRAINT [FK_fulfqhld69lh6veh1gthjjn2s] FOREIGN KEY([telecom_id])
REFERENCES [dbo].[OrganizationTelecom] ([id])
GO
ALTER TABLE [dbo].[DeliveryLocation_OrganizationTelecom] CHECK CONSTRAINT [FK_fulfqhld69lh6veh1gthjjn2s]
GO
/****** Object:  ForeignKey [FK_o02g2y3qlx44ej6ukq474j149]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DeliveryLocation_OrganizationTelecom]  WITH CHECK ADD  CONSTRAINT [FK_o02g2y3qlx44ej6ukq474j149] FOREIGN KEY([delivery_location_id])
REFERENCES [dbo].[DeliveryLocation] ([id])
GO
ALTER TABLE [dbo].[DeliveryLocation_OrganizationTelecom] CHECK CONSTRAINT [FK_o02g2y3qlx44ej6ukq474j149]
GO
/****** Object:  ForeignKey [FK_1awxotn5p6ehrpeiivy0kr095]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DeliveryLocation_OrganizationAddress]  WITH CHECK ADD  CONSTRAINT [FK_1awxotn5p6ehrpeiivy0kr095] FOREIGN KEY([delivery_location_id])
REFERENCES [dbo].[DeliveryLocation] ([id])
GO
ALTER TABLE [dbo].[DeliveryLocation_OrganizationAddress] CHECK CONSTRAINT [FK_1awxotn5p6ehrpeiivy0kr095]
GO
/****** Object:  ForeignKey [FK_o4gmfm53ljnh8onhyq8viec1y]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DeliveryLocation_OrganizationAddress]  WITH CHECK ADD  CONSTRAINT [FK_o4gmfm53ljnh8onhyq8viec1y] FOREIGN KEY([address_id])
REFERENCES [dbo].[OrganizationAddress] ([id])
GO
ALTER TABLE [dbo].[DeliveryLocation_OrganizationAddress] CHECK CONSTRAINT [FK_o4gmfm53ljnh8onhyq8viec1y]
GO
/****** Object:  ForeignKey [FK_1bur5oxjxg5vsq7t1aid832w1]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Resident]  WITH CHECK ADD  CONSTRAINT [FK_1bur5oxjxg5vsq7t1aid832w1] FOREIGN KEY([provider_organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Resident] CHECK CONSTRAINT [FK_1bur5oxjxg5vsq7t1aid832w1]
GO
/****** Object:  ForeignKey [FK_h2r1peoi5awf1bllrygnc8ckp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Resident]  WITH CHECK ADD  CONSTRAINT [FK_h2r1peoi5awf1bllrygnc8ckp] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[Resident] CHECK CONSTRAINT [FK_h2r1peoi5awf1bllrygnc8ckp]
GO
/****** Object:  ForeignKey [FK_i8o7p26mwqt3ie6av2krwbau8]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Resident]  WITH CHECK ADD  CONSTRAINT [FK_i8o7p26mwqt3ie6av2krwbau8] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Resident] CHECK CONSTRAINT [FK_i8o7p26mwqt3ie6av2krwbau8]
GO
/****** Object:  ForeignKey [FK_jncmpqjwfcjexx62ea1p06vlp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Resident]  WITH CHECK ADD  CONSTRAINT [FK_jncmpqjwfcjexx62ea1p06vlp] FOREIGN KEY([facility_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Resident] CHECK CONSTRAINT [FK_jncmpqjwfcjexx62ea1p06vlp]
GO
/****** Object:  ForeignKey [FK_ktex5g9qylm9p6fdjvq28ini8]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Resident]  WITH CHECK ADD  CONSTRAINT [FK_ktex5g9qylm9p6fdjvq28ini8] FOREIGN KEY([custodian_id])
REFERENCES [dbo].[Custodian] ([id])
GO
ALTER TABLE [dbo].[Resident] CHECK CONSTRAINT [FK_ktex5g9qylm9p6fdjvq28ini8]
GO
/****** Object:  ForeignKey [FK_l3n3jnme33y315exi0ka06vur]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Resident]  WITH CHECK ADD  CONSTRAINT [FK_l3n3jnme33y315exi0ka06vur] FOREIGN KEY([data_enterer_id])
REFERENCES [dbo].[DataEnterer] ([id])
GO
ALTER TABLE [dbo].[Resident] CHECK CONSTRAINT [FK_l3n3jnme33y315exi0ka06vur]
GO
/****** Object:  ForeignKey [FK_pkrwxcxj068h1y8mi2ys3jyip]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Resident]  WITH CHECK ADD  CONSTRAINT [FK_pkrwxcxj068h1y8mi2ys3jyip] FOREIGN KEY([legal_authenticator_id])
REFERENCES [dbo].[LegalAuthenticator] ([id])
GO
ALTER TABLE [dbo].[Resident] CHECK CONSTRAINT [FK_pkrwxcxj068h1y8mi2ys3jyip]
GO
/****** Object:  ForeignKey [FK_73g9ajn3sgjsjhc29jygys1wa]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProfessionalContact]  WITH CHECK ADD  CONSTRAINT [FK_73g9ajn3sgjsjhc29jygys1wa] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ProfessionalContact] CHECK CONSTRAINT [FK_73g9ajn3sgjsjhc29jygys1wa]
GO
/****** Object:  ForeignKey [FK_kf1fnxmgtmufya3dy6gow0u5p]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProfessionalContact]  WITH CHECK ADD  CONSTRAINT [FK_kf1fnxmgtmufya3dy6gow0u5p] FOREIGN KEY([org_ref_source_id])
REFERENCES [dbo].[OrgReferralSource] ([id])
GO
ALTER TABLE [dbo].[ProfessionalContact] CHECK CONSTRAINT [FK_kf1fnxmgtmufya3dy6gow0u5p]
GO
/****** Object:  ForeignKey [FK_cxjatlg5u9c95s32mpmtsr94m]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OrgReferralSourceFacility]  WITH CHECK ADD  CONSTRAINT [FK_cxjatlg5u9c95s32mpmtsr94m] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[OrgReferralSourceFacility] CHECK CONSTRAINT [FK_cxjatlg5u9c95s32mpmtsr94m]
GO
/****** Object:  ForeignKey [FK_h9gq0lwy033xy4ti4u4ponjqp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OrgReferralSourceFacility]  WITH CHECK ADD  CONSTRAINT [FK_h9gq0lwy033xy4ti4u4ponjqp] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[OrgReferralSourceFacility] CHECK CONSTRAINT [FK_h9gq0lwy033xy4ti4u4ponjqp]
GO
/****** Object:  ForeignKey [FK_rmfrdnt8njuvk03omwcbr3n2j]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[OrgReferralSourceFacility]  WITH CHECK ADD  CONSTRAINT [FK_rmfrdnt8njuvk03omwcbr3n2j] FOREIGN KEY([org_ref_source_id])
REFERENCES [dbo].[OrgReferralSource] ([id])
GO
ALTER TABLE [dbo].[OrgReferralSourceFacility] CHECK CONSTRAINT [FK_rmfrdnt8njuvk03omwcbr3n2j]
GO
/****** Object:  ForeignKey [FK_gftd4usnil1cbobsai6r89gs7]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusProblemObservation_NonMedicinalSupplyActivity]  WITH CHECK ADD  CONSTRAINT [FK_gftd4usnil1cbobsai6r89gs7] FOREIGN KEY([non_medicinal_supply_activity_id])
REFERENCES [dbo].[NonMedicinalSupplyActivity] ([id])
GO
ALTER TABLE [dbo].[StatusProblemObservation_NonMedicinalSupplyActivity] CHECK CONSTRAINT [FK_gftd4usnil1cbobsai6r89gs7]
GO
/****** Object:  ForeignKey [FK_lhrkftbcc5hnbhthe18x2jwgs]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusProblemObservation_NonMedicinalSupplyActivity]  WITH CHECK ADD  CONSTRAINT [FK_lhrkftbcc5hnbhthe18x2jwgs] FOREIGN KEY([status_problem_observation_id])
REFERENCES [dbo].[StatusProblemObservation] ([id])
GO
ALTER TABLE [dbo].[StatusProblemObservation_NonMedicinalSupplyActivity] CHECK CONSTRAINT [FK_lhrkftbcc5hnbhthe18x2jwgs]
GO
/****** Object:  ForeignKey [FK_6lr3w3nx2damv1vssq52xdm26]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Unit]  WITH CHECK ADD  CONSTRAINT [FK_6lr3w3nx2damv1vssq52xdm26] FOREIGN KEY([unit_type_semi_private_bcrnt_id])
REFERENCES [dbo].[UnitType] ([id])
GO
ALTER TABLE [dbo].[Unit] CHECK CONSTRAINT [FK_6lr3w3nx2damv1vssq52xdm26]
GO
/****** Object:  ForeignKey [FK_72c5hdwiamlppxar593jhbvlc]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Unit]  WITH CHECK ADD  CONSTRAINT [FK_72c5hdwiamlppxar593jhbvlc] FOREIGN KEY([unit_type_semi_private_acrnt_id])
REFERENCES [dbo].[UnitType] ([id])
GO
ALTER TABLE [dbo].[Unit] CHECK CONSTRAINT [FK_72c5hdwiamlppxar593jhbvlc]
GO
/****** Object:  ForeignKey [FK_bmlucnfgfuac5ghny7sdmp8yn]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Unit]  WITH CHECK ADD  CONSTRAINT [FK_bmlucnfgfuac5ghny7sdmp8yn] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Unit] CHECK CONSTRAINT [FK_bmlucnfgfuac5ghny7sdmp8yn]
GO
/****** Object:  ForeignKey [FK_e2pu7uktv9d2ood1bsd5pv44n]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Unit]  WITH CHECK ADD  CONSTRAINT [FK_e2pu7uktv9d2ood1bsd5pv44n] FOREIGN KEY([unit_type_private_current_id])
REFERENCES [dbo].[UnitType] ([id])
GO
ALTER TABLE [dbo].[Unit] CHECK CONSTRAINT [FK_e2pu7uktv9d2ood1bsd5pv44n]
GO
/****** Object:  ForeignKey [FK_v66q0kffwuhus98im4tiicpq]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Unit]  WITH CHECK ADD  CONSTRAINT [FK_v66q0kffwuhus98im4tiicpq] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Unit] CHECK CONSTRAINT [FK_v66q0kffwuhus98im4tiicpq]
GO
/****** Object:  ForeignKey [FK_637avh87vb493bk24jpecnv3x]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[UnitHistory]  WITH CHECK ADD  CONSTRAINT [FK_637avh87vb493bk24jpecnv3x] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[UnitHistory] CHECK CONSTRAINT [FK_637avh87vb493bk24jpecnv3x]
GO
/****** Object:  ForeignKey [FK_d4f4d19cyvusnms2nx41ua62f]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[UnitHistory]  WITH CHECK ADD  CONSTRAINT [FK_d4f4d19cyvusnms2nx41ua62f] FOREIGN KEY([unit_type_id])
REFERENCES [dbo].[UnitType] ([id])
GO
ALTER TABLE [dbo].[UnitHistory] CHECK CONSTRAINT [FK_d4f4d19cyvusnms2nx41ua62f]
GO
/****** Object:  ForeignKey [FK_f53p7te771y6pxox2cdpi4hcb]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[UnitHistory]  WITH CHECK ADD  CONSTRAINT [FK_f53p7te771y6pxox2cdpi4hcb] FOREIGN KEY([unit_id])
REFERENCES [dbo].[Unit] ([id])
GO
ALTER TABLE [dbo].[UnitHistory] CHECK CONSTRAINT [FK_f53p7te771y6pxox2cdpi4hcb]
GO
/****** Object:  ForeignKey [FK_f5ihblwtrkfqcg12lfes90yrv]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[UnitHistory]  WITH CHECK ADD  CONSTRAINT [FK_f5ihblwtrkfqcg12lfes90yrv] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[UnitHistory] CHECK CONSTRAINT [FK_f5ihblwtrkfqcg12lfes90yrv]
GO
/****** Object:  ForeignKey [FK_l3fjiuqwot1bevxgw4bplpgei]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[VitalSign]  WITH CHECK ADD  CONSTRAINT [FK_l3fjiuqwot1bevxgw4bplpgei] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[VitalSign] CHECK CONSTRAINT [FK_l3fjiuqwot1bevxgw4bplpgei]
GO
/****** Object:  ForeignKey [FK_mgt899jm8rkwpi6m5gk1wm7qg]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[VitalSign]  WITH CHECK ADD  CONSTRAINT [FK_mgt899jm8rkwpi6m5gk1wm7qg] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[VitalSign] CHECK CONSTRAINT [FK_mgt899jm8rkwpi6m5gk1wm7qg]
GO
/****** Object:  ForeignKey [FK_6kh4a4o1lpc88b4iyvvf8mdxx]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[SocialHistory]  WITH CHECK ADD  CONSTRAINT [FK_6kh4a4o1lpc88b4iyvvf8mdxx] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[SocialHistory] CHECK CONSTRAINT [FK_6kh4a4o1lpc88b4iyvvf8mdxx]
GO
/****** Object:  ForeignKey [FK_feu4j20yysl37h3bf442xshvq]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[SocialHistory]  WITH CHECK ADD  CONSTRAINT [FK_feu4j20yysl37h3bf442xshvq] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[SocialHistory] CHECK CONSTRAINT [FK_feu4j20yysl37h3bf442xshvq]
GO
/****** Object:  ForeignKey [FK_566rmmkh31fnj4vbwrmnsindi]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentProcedure]  WITH CHECK ADD  CONSTRAINT [FK_566rmmkh31fnj4vbwrmnsindi] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[ResidentProcedure] CHECK CONSTRAINT [FK_566rmmkh31fnj4vbwrmnsindi]
GO
/****** Object:  ForeignKey [FK_juvpvyh8j2jlbpphia5ddjmkr]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentProcedure]  WITH CHECK ADD  CONSTRAINT [FK_juvpvyh8j2jlbpphia5ddjmkr] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ResidentProcedure] CHECK CONSTRAINT [FK_juvpvyh8j2jlbpphia5ddjmkr]
GO
/****** Object:  ForeignKey [FK_cnf2f2s3f1wr1m68y0ppxjck]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentAdmittanceHistory]  WITH CHECK ADD  CONSTRAINT [FK_cnf2f2s3f1wr1m68y0ppxjck] FOREIGN KEY([sales_rep_employee_id])
REFERENCES [dbo].[Employee] ([id])
GO
ALTER TABLE [dbo].[ResidentAdmittanceHistory] CHECK CONSTRAINT [FK_cnf2f2s3f1wr1m68y0ppxjck]
GO
/****** Object:  ForeignKey [FK_i43h1b73kqk6rafp67gsg5l29]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentAdmittanceHistory]  WITH CHECK ADD  CONSTRAINT [FK_i43h1b73kqk6rafp67gsg5l29] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[ResidentAdmittanceHistory] CHECK CONSTRAINT [FK_i43h1b73kqk6rafp67gsg5l29]
GO
/****** Object:  ForeignKey [FK_livejeb2eqd029bl56h6ro55g]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentAdmittanceHistory]  WITH CHECK ADD  CONSTRAINT [FK_livejeb2eqd029bl56h6ro55g] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[ResidentAdmittanceHistory] CHECK CONSTRAINT [FK_livejeb2eqd029bl56h6ro55g]
GO
/****** Object:  ForeignKey [FK_oti2av8tydtcldmdpma8d8ohd]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentAdmittanceHistory]  WITH CHECK ADD  CONSTRAINT [FK_oti2av8tydtcldmdpma8d8ohd] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ResidentAdmittanceHistory] CHECK CONSTRAINT [FK_oti2av8tydtcldmdpma8d8ohd]
GO
/****** Object:  ForeignKey [FK_57ce87pyqf6rcw4jl7katy2xm]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Prospect]  WITH CHECK ADD  CONSTRAINT [FK_57ce87pyqf6rcw4jl7katy2xm] FOREIGN KEY([reserve_facility_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Prospect] CHECK CONSTRAINT [FK_57ce87pyqf6rcw4jl7katy2xm]
GO
/****** Object:  ForeignKey [FK_b0ameybib2j1c3q4m75u8h3vh]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Prospect]  WITH CHECK ADD  CONSTRAINT [FK_b0ameybib2j1c3q4m75u8h3vh] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Prospect] CHECK CONSTRAINT [FK_b0ameybib2j1c3q4m75u8h3vh]
GO
/****** Object:  ForeignKey [FK_chvykscng4pvmcskuhyvx5gdn]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Prospect]  WITH CHECK ADD  CONSTRAINT [FK_chvykscng4pvmcskuhyvx5gdn] FOREIGN KEY([facility_primary_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Prospect] CHECK CONSTRAINT [FK_chvykscng4pvmcskuhyvx5gdn]
GO
/****** Object:  ForeignKey [FK_efrpev5ef2kmi4rp1hy07ojtv]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Prospect]  WITH CHECK ADD  CONSTRAINT [FK_efrpev5ef2kmi4rp1hy07ojtv] FOREIGN KEY([referral_source_prof_cont_id])
REFERENCES [dbo].[ProfessionalContact] ([id])
GO
ALTER TABLE [dbo].[Prospect] CHECK CONSTRAINT [FK_efrpev5ef2kmi4rp1hy07ojtv]
GO
/****** Object:  ForeignKey [FK_geaa3s1ht4kb2elmifyv20wjg]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Prospect]  WITH CHECK ADD  CONSTRAINT [FK_geaa3s1ht4kb2elmifyv20wjg] FOREIGN KEY([resident_facility_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Prospect] CHECK CONSTRAINT [FK_geaa3s1ht4kb2elmifyv20wjg]
GO
/****** Object:  ForeignKey [FK_gmvwrt9ycd7ne87s44s3meiuf]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Prospect]  WITH CHECK ADD  CONSTRAINT [FK_gmvwrt9ycd7ne87s44s3meiuf] FOREIGN KEY([sales_rep_employee_id])
REFERENCES [dbo].[Employee] ([id])
GO
ALTER TABLE [dbo].[Prospect] CHECK CONSTRAINT [FK_gmvwrt9ycd7ne87s44s3meiuf]
GO
/****** Object:  ForeignKey [FK_k7r2traqq6nlx8vykj61a3d3w]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Prospect]  WITH CHECK ADD  CONSTRAINT [FK_k7r2traqq6nlx8vykj61a3d3w] FOREIGN KEY([copied_from_inquiry_id])
REFERENCES [dbo].[Inquiry] ([id])
GO
ALTER TABLE [dbo].[Prospect] CHECK CONSTRAINT [FK_k7r2traqq6nlx8vykj61a3d3w]
GO
/****** Object:  ForeignKey [FK_lsgkh6ugropg45lnf4psir3xf]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Prospect]  WITH CHECK ADD  CONSTRAINT [FK_lsgkh6ugropg45lnf4psir3xf] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Prospect] CHECK CONSTRAINT [FK_lsgkh6ugropg45lnf4psir3xf]
GO
/****** Object:  ForeignKey [FK_52m3vvnk2vhx1y1nx7r5nlpw0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Result]  WITH CHECK ADD  CONSTRAINT [FK_52m3vvnk2vhx1y1nx7r5nlpw0] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Result] CHECK CONSTRAINT [FK_52m3vvnk2vhx1y1nx7r5nlpw0]
GO
/****** Object:  ForeignKey [FK_mqwe5vyeck9bw8j1h5oj5gas]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Result]  WITH CHECK ADD  CONSTRAINT [FK_mqwe5vyeck9bw8j1h5oj5gas] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Result] CHECK CONSTRAINT [FK_mqwe5vyeck9bw8j1h5oj5gas]
GO
/****** Object:  ForeignKey [FK_jd69p70onptsg5g262mru717k]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicalEquipment]  WITH CHECK ADD  CONSTRAINT [FK_jd69p70onptsg5g262mru717k] FOREIGN KEY([product_instance_id])
REFERENCES [dbo].[ProductInstance] ([id])
GO
ALTER TABLE [dbo].[MedicalEquipment] CHECK CONSTRAINT [FK_jd69p70onptsg5g262mru717k]
GO
/****** Object:  ForeignKey [FK_l6qi91hk4ya9nnc4qgrww2e86]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicalEquipment]  WITH CHECK ADD  CONSTRAINT [FK_l6qi91hk4ya9nnc4qgrww2e86] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[MedicalEquipment] CHECK CONSTRAINT [FK_l6qi91hk4ya9nnc4qgrww2e86]
GO
/****** Object:  ForeignKey [FK_lmp45w1p1jbmet8bknsutf7jg]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicalEquipment]  WITH CHECK ADD  CONSTRAINT [FK_lmp45w1p1jbmet8bknsutf7jg] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[MedicalEquipment] CHECK CONSTRAINT [FK_lmp45w1p1jbmet8bknsutf7jg]
GO
/****** Object:  ForeignKey [FK_9rhgwljchyjf6h8mpciix89i9]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Payer]  WITH CHECK ADD  CONSTRAINT [FK_9rhgwljchyjf6h8mpciix89i9] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Payer] CHECK CONSTRAINT [FK_9rhgwljchyjf6h8mpciix89i9]
GO
/****** Object:  ForeignKey [FK_l05c3s6h2xct5291qefdckpl6]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Payer]  WITH CHECK ADD  CONSTRAINT [FK_l05c3s6h2xct5291qefdckpl6] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Payer] CHECK CONSTRAINT [FK_l05c3s6h2xct5291qefdckpl6]
GO
/****** Object:  ForeignKey [FK_7rsqoe4wk8q71xp3py68svpd6]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Participant]  WITH CHECK ADD  CONSTRAINT [FK_7rsqoe4wk8q71xp3py68svpd6] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Participant] CHECK CONSTRAINT [FK_7rsqoe4wk8q71xp3py68svpd6]
GO
/****** Object:  ForeignKey [FK_e6fxgodsbhm2mwwviuyr44txw]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Participant]  WITH CHECK ADD  CONSTRAINT [FK_e6fxgodsbhm2mwwviuyr44txw] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Participant] CHECK CONSTRAINT [FK_e6fxgodsbhm2mwwviuyr44txw]
GO
/****** Object:  ForeignKey [FK_ltw4cvkoppkpsfrxwfykmwf5d]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Participant]  WITH CHECK ADD  CONSTRAINT [FK_ltw4cvkoppkpsfrxwfykmwf5d] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Participant] CHECK CONSTRAINT [FK_ltw4cvkoppkpsfrxwfykmwf5d]
GO
/****** Object:  ForeignKey [FK_p2i49iuhnwlumuf2nn1g5dpdo]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Participant]  WITH CHECK ADD  CONSTRAINT [FK_p2i49iuhnwlumuf2nn1g5dpdo] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[Participant] CHECK CONSTRAINT [FK_p2i49iuhnwlumuf2nn1g5dpdo]
GO
/****** Object:  ForeignKey [FK_eouetw2irwehxeounj379unim]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Problem]  WITH CHECK ADD  CONSTRAINT [FK_eouetw2irwehxeounj379unim] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Problem] CHECK CONSTRAINT [FK_eouetw2irwehxeounj379unim]
GO
/****** Object:  ForeignKey [FK_fjl3gwydwdsuqki7i6cn1u4jb]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Problem]  WITH CHECK ADD  CONSTRAINT [FK_fjl3gwydwdsuqki7i6cn1u4jb] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Problem] CHECK CONSTRAINT [FK_fjl3gwydwdsuqki7i6cn1u4jb]
GO
/****** Object:  ForeignKey [FK_2qnsyi3dy3gx8q52b25txbroh]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare]  WITH CHECK ADD  CONSTRAINT [FK_2qnsyi3dy3gx8q52b25txbroh] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare] CHECK CONSTRAINT [FK_2qnsyi3dy3gx8q52b25txbroh]
GO
/****** Object:  ForeignKey [FK_qvsvpws699xkkcd7485042jsg]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare]  WITH CHECK ADD  CONSTRAINT [FK_qvsvpws699xkkcd7485042jsg] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare] CHECK CONSTRAINT [FK_qvsvpws699xkkcd7485042jsg]
GO
/****** Object:  ForeignKey [FK_g3ekibim5pdg0u9qbjsetb8bb]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[InformationRecipient]  WITH CHECK ADD  CONSTRAINT [FK_g3ekibim5pdg0u9qbjsetb8bb] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[InformationRecipient] CHECK CONSTRAINT [FK_g3ekibim5pdg0u9qbjsetb8bb]
GO
/****** Object:  ForeignKey [FK_gjwit9ll7ton8t4jpfm7b0oen]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[InformationRecipient]  WITH CHECK ADD  CONSTRAINT [FK_gjwit9ll7ton8t4jpfm7b0oen] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[InformationRecipient] CHECK CONSTRAINT [FK_gjwit9ll7ton8t4jpfm7b0oen]
GO
/****** Object:  ForeignKey [FK_gmb78kd1dfypoot7v33x2rpyx]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[InformationRecipient]  WITH CHECK ADD  CONSTRAINT [FK_gmb78kd1dfypoot7v33x2rpyx] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[InformationRecipient] CHECK CONSTRAINT [FK_gmb78kd1dfypoot7v33x2rpyx]
GO
/****** Object:  ForeignKey [FK_h08gjqo5jnlneemcsnxsl3uvc]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[InformationRecipient]  WITH CHECK ADD  CONSTRAINT [FK_h08gjqo5jnlneemcsnxsl3uvc] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[InformationRecipient] CHECK CONSTRAINT [FK_h08gjqo5jnlneemcsnxsl3uvc]
GO
/****** Object:  ForeignKey [FK_27jr804ibsx74xnr35a7r2djo]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Informant]  WITH CHECK ADD  CONSTRAINT [FK_27jr804ibsx74xnr35a7r2djo] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[Informant] CHECK CONSTRAINT [FK_27jr804ibsx74xnr35a7r2djo]
GO
/****** Object:  ForeignKey [FK_4amnh62tvb7g88gfjhpbeog0m]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Informant]  WITH CHECK ADD  CONSTRAINT [FK_4amnh62tvb7g88gfjhpbeog0m] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Informant] CHECK CONSTRAINT [FK_4amnh62tvb7g88gfjhpbeog0m]
GO
/****** Object:  ForeignKey [FK_m2pbs1bxkdehso6c07ghum2n0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Informant]  WITH CHECK ADD  CONSTRAINT [FK_m2pbs1bxkdehso6c07ghum2n0] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Informant] CHECK CONSTRAINT [FK_m2pbs1bxkdehso6c07ghum2n0]
GO
/****** Object:  ForeignKey [FK_2m04t50b0s2mf1j9t2evp9582]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Guardian]  WITH CHECK ADD  CONSTRAINT [FK_2m04t50b0s2mf1j9t2evp9582] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Guardian] CHECK CONSTRAINT [FK_2m04t50b0s2mf1j9t2evp9582]
GO
/****** Object:  ForeignKey [FK_mgmbj9jjxndt728cu90x077ao]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Guardian]  WITH CHECK ADD  CONSTRAINT [FK_mgmbj9jjxndt728cu90x077ao] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Guardian] CHECK CONSTRAINT [FK_mgmbj9jjxndt728cu90x077ao]
GO
/****** Object:  ForeignKey [FK_t699nfxeb3x0brv7swuxrhoqk]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Guardian]  WITH CHECK ADD  CONSTRAINT [FK_t699nfxeb3x0brv7swuxrhoqk] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[Guardian] CHECK CONSTRAINT [FK_t699nfxeb3x0brv7swuxrhoqk]
GO
/****** Object:  ForeignKey [FK_7rwca85pa4dnwus25ntk2ix0f]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus]  WITH CHECK ADD  CONSTRAINT [FK_7rwca85pa4dnwus25ntk2ix0f] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus] CHECK CONSTRAINT [FK_7rwca85pa4dnwus25ntk2ix0f]
GO
/****** Object:  ForeignKey [FK_imka9p3sev53p1t7jnoxeshn0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus]  WITH CHECK ADD  CONSTRAINT [FK_imka9p3sev53p1t7jnoxeshn0] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus] CHECK CONSTRAINT [FK_imka9p3sev53p1t7jnoxeshn0]
GO
/****** Object:  ForeignKey [FK_3ua3u1xnol53capj63nlml5jt]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Language]  WITH CHECK ADD  CONSTRAINT [FK_3ua3u1xnol53capj63nlml5jt] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Language] CHECK CONSTRAINT [FK_3ua3u1xnol53capj63nlml5jt]
GO
/****** Object:  ForeignKey [FK_ig2x0jeq95uf7du09j4y35j1q]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Language]  WITH CHECK ADD  CONSTRAINT [FK_ig2x0jeq95uf7du09j4y35j1q] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Language] CHECK CONSTRAINT [FK_ig2x0jeq95uf7du09j4y35j1q]
GO
/****** Object:  ForeignKey [FK_3gkulbmqu9pfxp52x9f722jrh]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Allergy]  WITH CHECK ADD  CONSTRAINT [FK_3gkulbmqu9pfxp52x9f722jrh] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Allergy] CHECK CONSTRAINT [FK_3gkulbmqu9pfxp52x9f722jrh]
GO
/****** Object:  ForeignKey [FK_8qew71nq2h4dqsyiolwdcq1k1]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Allergy]  WITH CHECK ADD  CONSTRAINT [FK_8qew71nq2h4dqsyiolwdcq1k1] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Allergy] CHECK CONSTRAINT [FK_8qew71nq2h4dqsyiolwdcq1k1]
GO
/****** Object:  ForeignKey [FK_sv4jbvuj3x667l17iufwywcyp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Allergy]  WITH CHECK ADD  CONSTRAINT [FK_sv4jbvuj3x667l17iufwywcyp] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Allergy] CHECK CONSTRAINT [FK_sv4jbvuj3x667l17iufwywcyp]
GO
/****** Object:  ForeignKey [FK_bburqh12r5h331rm265c6xx20]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Author]  WITH CHECK ADD  CONSTRAINT [FK_bburqh12r5h331rm265c6xx20] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Author] CHECK CONSTRAINT [FK_bburqh12r5h331rm265c6xx20]
GO
/****** Object:  ForeignKey [FK_gm7q9s1e0rlflv7of2xfpd7fl]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Author]  WITH CHECK ADD  CONSTRAINT [FK_gm7q9s1e0rlflv7of2xfpd7fl] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Author] CHECK CONSTRAINT [FK_gm7q9s1e0rlflv7of2xfpd7fl]
GO
/****** Object:  ForeignKey [FK_h3od3v5w9qiqqyu0hm31jxnou]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Author]  WITH CHECK ADD  CONSTRAINT [FK_h3od3v5w9qiqqyu0hm31jxnou] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Author] CHECK CONSTRAINT [FK_h3od3v5w9qiqqyu0hm31jxnou]
GO
/****** Object:  ForeignKey [FK_mjdgph9n2ibgp7w1oaav6x1fw]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Author]  WITH CHECK ADD  CONSTRAINT [FK_mjdgph9n2ibgp7w1oaav6x1fw] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[Author] CHECK CONSTRAINT [FK_mjdgph9n2ibgp7w1oaav6x1fw]
GO
/****** Object:  ForeignKey [FK_br95yepy8k4vvcp9bqj4rc4l5]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Authenticator]  WITH CHECK ADD  CONSTRAINT [FK_br95yepy8k4vvcp9bqj4rc4l5] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[Authenticator] CHECK CONSTRAINT [FK_br95yepy8k4vvcp9bqj4rc4l5]
GO
/****** Object:  ForeignKey [FK_iwv3fmw31p7ddbjrt3y3t6v4r]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Authenticator]  WITH CHECK ADD  CONSTRAINT [FK_iwv3fmw31p7ddbjrt3y3t6v4r] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Authenticator] CHECK CONSTRAINT [FK_iwv3fmw31p7ddbjrt3y3t6v4r]
GO
/****** Object:  ForeignKey [FK_rayw399l6m8yeoycrkrc7lfhm]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Authenticator]  WITH CHECK ADD  CONSTRAINT [FK_rayw399l6m8yeoycrkrc7lfhm] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Authenticator] CHECK CONSTRAINT [FK_rayw399l6m8yeoycrkrc7lfhm]
GO
/****** Object:  ForeignKey [FK_1exg2teqe44uh88i4r142wa5i]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DocumentationOf]  WITH CHECK ADD  CONSTRAINT [FK_1exg2teqe44uh88i4r142wa5i] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[DocumentationOf] CHECK CONSTRAINT [FK_1exg2teqe44uh88i4r142wa5i]
GO
/****** Object:  ForeignKey [FK_c31boa4xs6sen5xi0ay1c1vvk]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DocumentationOf]  WITH CHECK ADD  CONSTRAINT [FK_c31boa4xs6sen5xi0ay1c1vvk] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[DocumentationOf] CHECK CONSTRAINT [FK_c31boa4xs6sen5xi0ay1c1vvk]
GO
/****** Object:  ForeignKey [FK_4d152nn6ewcxbedhr8fdknmeu]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FamilyHistory]  WITH CHECK ADD  CONSTRAINT [FK_4d152nn6ewcxbedhr8fdknmeu] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[FamilyHistory] CHECK CONSTRAINT [FK_4d152nn6ewcxbedhr8fdknmeu]
GO
/****** Object:  ForeignKey [FK_qxfmxi5du6sea4hfarnjntm2i]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FamilyHistory]  WITH CHECK ADD  CONSTRAINT [FK_qxfmxi5du6sea4hfarnjntm2i] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[FamilyHistory] CHECK CONSTRAINT [FK_qxfmxi5du6sea4hfarnjntm2i]
GO
/****** Object:  ForeignKey [FK_1ytngdsudv7ierjx12wyn7krm]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DocumentationOf_Person]  WITH CHECK ADD  CONSTRAINT [FK_1ytngdsudv7ierjx12wyn7krm] FOREIGN KEY([documentation_of_id])
REFERENCES [dbo].[DocumentationOf] ([id])
GO
ALTER TABLE [dbo].[DocumentationOf_Person] CHECK CONSTRAINT [FK_1ytngdsudv7ierjx12wyn7krm]
GO
/****** Object:  ForeignKey [FK_eu6jhbkc14dffbxyhdp5lkdvq]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[DocumentationOf_Person]  WITH CHECK ADD  CONSTRAINT [FK_eu6jhbkc14dffbxyhdp5lkdvq] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[DocumentationOf_Person] CHECK CONSTRAINT [FK_eu6jhbkc14dffbxyhdp5lkdvq]
GO
/****** Object:  ForeignKey [FK_8qkbhedp2bu2c2oo7mr3167dm]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Communication]  WITH CHECK ADD  CONSTRAINT [FK_8qkbhedp2bu2c2oo7mr3167dm] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Communication] CHECK CONSTRAINT [FK_8qkbhedp2bu2c2oo7mr3167dm]
GO
/****** Object:  ForeignKey [FK_94fljupf0io9xuq8hw8cqmyph]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Communication]  WITH CHECK ADD  CONSTRAINT [FK_94fljupf0io9xuq8hw8cqmyph] FOREIGN KEY([inquiry_id])
REFERENCES [dbo].[Inquiry] ([id])
GO
ALTER TABLE [dbo].[Communication] CHECK CONSTRAINT [FK_94fljupf0io9xuq8hw8cqmyph]
GO
/****** Object:  ForeignKey [FK_h24qdfgdif3jva7cp4wxhn1ar]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Communication]  WITH CHECK ADD  CONSTRAINT [FK_h24qdfgdif3jva7cp4wxhn1ar] FOREIGN KEY([prospect_id])
REFERENCES [dbo].[Prospect] ([id])
GO
ALTER TABLE [dbo].[Communication] CHECK CONSTRAINT [FK_h24qdfgdif3jva7cp4wxhn1ar]
GO
/****** Object:  ForeignKey [FK_lebl7bta95yl53rs7y98gppqx]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Communication]  WITH CHECK ADD  CONSTRAINT [FK_lebl7bta95yl53rs7y98gppqx] FOREIGN KEY([prof_contact_id])
REFERENCES [dbo].[ProfessionalContact] ([id])
GO
ALTER TABLE [dbo].[Communication] CHECK CONSTRAINT [FK_lebl7bta95yl53rs7y98gppqx]
GO
/****** Object:  ForeignKey [FK_pgbvew5hve8in5030ole3x8fe]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Communication]  WITH CHECK ADD  CONSTRAINT [FK_pgbvew5hve8in5030ole3x8fe] FOREIGN KEY([communication_type_id])
REFERENCES [dbo].[CommunicationType] ([id])
GO
ALTER TABLE [dbo].[Communication] CHECK CONSTRAINT [FK_pgbvew5hve8in5030ole3x8fe]
GO
/****** Object:  ForeignKey [FK_sisvhjvcubyfcnamqgesbkvqj]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Communication]  WITH CHECK ADD  CONSTRAINT [FK_sisvhjvcubyfcnamqgesbkvqj] FOREIGN KEY([completed_by_empl_id])
REFERENCES [dbo].[Employee] ([id])
GO
ALTER TABLE [dbo].[Communication] CHECK CONSTRAINT [FK_sisvhjvcubyfcnamqgesbkvqj]
GO
/****** Object:  ForeignKey [FK_3s0yufn7biqe1qkdlubehaioi]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AssessmentScaleObservation_Author]  WITH CHECK ADD  CONSTRAINT [FK_3s0yufn7biqe1qkdlubehaioi] FOREIGN KEY([observation_id])
REFERENCES [dbo].[AssessmentScaleObservation] ([id])
GO
ALTER TABLE [dbo].[AssessmentScaleObservation_Author] CHECK CONSTRAINT [FK_3s0yufn7biqe1qkdlubehaioi]
GO
/****** Object:  ForeignKey [FK_h6lbo7q78esmoldlsf7shu54p]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AssessmentScaleObservation_Author]  WITH CHECK ADD  CONSTRAINT [FK_h6lbo7q78esmoldlsf7shu54p] FOREIGN KEY([author_id])
REFERENCES [dbo].[Author] ([id])
GO
ALTER TABLE [dbo].[AssessmentScaleObservation_Author] CHECK CONSTRAINT [FK_h6lbo7q78esmoldlsf7shu54p]
GO
/****** Object:  ForeignKey [FK_6kqv7ghjae81yfmcpjtjyxf6e]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AllergyObservation]  WITH CHECK ADD  CONSTRAINT [FK_6kqv7ghjae81yfmcpjtjyxf6e] FOREIGN KEY([allergy_id])
REFERENCES [dbo].[Allergy] ([id])
GO
ALTER TABLE [dbo].[AllergyObservation] CHECK CONSTRAINT [FK_6kqv7ghjae81yfmcpjtjyxf6e]
GO
/****** Object:  ForeignKey [FK_7awatt7s43jm4mu75hsuu9ixp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AllergyObservation]  WITH CHECK ADD  CONSTRAINT [FK_7awatt7s43jm4mu75hsuu9ixp] FOREIGN KEY([severity_observation_id])
REFERENCES [dbo].[SeverityObservation] ([id])
GO
ALTER TABLE [dbo].[AllergyObservation] CHECK CONSTRAINT [FK_7awatt7s43jm4mu75hsuu9ixp]
GO
/****** Object:  ForeignKey [FK_sbyu7tff9qql93tlo0c2pb443]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AllergyObservation]  WITH CHECK ADD  CONSTRAINT [FK_sbyu7tff9qql93tlo0c2pb443] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[AllergyObservation] CHECK CONSTRAINT [FK_sbyu7tff9qql93tlo0c2pb443]
GO
/****** Object:  ForeignKey [FK_gdcgtojw8go98tbna9vpf2c1l]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AdvanceDirective]  WITH CHECK ADD  CONSTRAINT [FK_gdcgtojw8go98tbna9vpf2c1l] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[AdvanceDirective] CHECK CONSTRAINT [FK_gdcgtojw8go98tbna9vpf2c1l]
GO
/****** Object:  ForeignKey [FK_gichkoh2pbm1rmuiwl00bnwai]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AdvanceDirective]  WITH CHECK ADD  CONSTRAINT [FK_gichkoh2pbm1rmuiwl00bnwai] FOREIGN KEY([custodian_id])
REFERENCES [dbo].[Participant] ([id])
GO
ALTER TABLE [dbo].[AdvanceDirective] CHECK CONSTRAINT [FK_gichkoh2pbm1rmuiwl00bnwai]
GO
/****** Object:  ForeignKey [FK_nu5dcleolgvw5cg4lkg75643o]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AdvanceDirective]  WITH CHECK ADD  CONSTRAINT [FK_nu5dcleolgvw5cg4lkg75643o] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[AdvanceDirective] CHECK CONSTRAINT [FK_nu5dcleolgvw5cg4lkg75643o]
GO
/****** Object:  ForeignKey [FK_7p3nec1s4235rdy6au0903gba]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[NumberOfPressureUlcersObservation]  WITH CHECK ADD  CONSTRAINT [FK_7p3nec1s4235rdy6au0903gba] FOREIGN KEY([author_id])
REFERENCES [dbo].[Author] ([id])
GO
ALTER TABLE [dbo].[NumberOfPressureUlcersObservation] CHECK CONSTRAINT [FK_7p3nec1s4235rdy6au0903gba]
GO
/****** Object:  ForeignKey [FK_c942p7bpaxa1fryfsot2bfjly]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[NumberOfPressureUlcersObservation]  WITH CHECK ADD  CONSTRAINT [FK_c942p7bpaxa1fryfsot2bfjly] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[NumberOfPressureUlcersObservation] CHECK CONSTRAINT [FK_c942p7bpaxa1fryfsot2bfjly]
GO
/****** Object:  ForeignKey [FK_cf79yjdpp6cns8le4c75gr4kn]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[NumberOfPressureUlcersObservation]  WITH CHECK ADD  CONSTRAINT [FK_cf79yjdpp6cns8le4c75gr4kn] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[NumberOfPressureUlcersObservation] CHECK CONSTRAINT [FK_cf79yjdpp6cns8le4c75gr4kn]
GO
/****** Object:  ForeignKey [FK_asj9kc8ecokvsaueuuhyr26te]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[HighestPressureUlcerStage]  WITH CHECK ADD  CONSTRAINT [FK_asj9kc8ecokvsaueuuhyr26te] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[HighestPressureUlcerStage] CHECK CONSTRAINT [FK_asj9kc8ecokvsaueuuhyr26te]
GO
/****** Object:  ForeignKey [FK_b9umks72vj9okv9674rkn9usf]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[HighestPressureUlcerStage]  WITH CHECK ADD  CONSTRAINT [FK_b9umks72vj9okv9674rkn9usf] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[HighestPressureUlcerStage] CHECK CONSTRAINT [FK_b9umks72vj9okv9674rkn9usf]
GO
/****** Object:  ForeignKey [FK_2d8kb1xjl1tfpme28gvx9k0t4]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FamilyHistoryObservation]  WITH CHECK ADD  CONSTRAINT [FK_2d8kb1xjl1tfpme28gvx9k0t4] FOREIGN KEY([family_history_id])
REFERENCES [dbo].[FamilyHistory] ([id])
GO
ALTER TABLE [dbo].[FamilyHistoryObservation] CHECK CONSTRAINT [FK_2d8kb1xjl1tfpme28gvx9k0t4]
GO
/****** Object:  ForeignKey [FK_omeg8edu6pu0ypi00s30haxod]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FamilyHistoryObservation]  WITH CHECK ADD  CONSTRAINT [FK_omeg8edu6pu0ypi00s30haxod] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[FamilyHistoryObservation] CHECK CONSTRAINT [FK_omeg8edu6pu0ypi00s30haxod]
GO
/****** Object:  ForeignKey [FK_27d1vjne183965q30ostlh3py]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusProblemObservation]  WITH CHECK ADD  CONSTRAINT [FK_27d1vjne183965q30ostlh3py] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusProblemObservation] CHECK CONSTRAINT [FK_27d1vjne183965q30ostlh3py]
GO
/****** Object:  ForeignKey [FK_gg4etoyk6e119inwpuuoh17bj]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusProblemObservation]  WITH CHECK ADD  CONSTRAINT [FK_gg4etoyk6e119inwpuuoh17bj] FOREIGN KEY([problem_observation_id])
REFERENCES [dbo].[StatusProblemObservation] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusProblemObservation] CHECK CONSTRAINT [FK_gg4etoyk6e119inwpuuoh17bj]
GO
/****** Object:  ForeignKey [FK_1rpn15bwqi1lqby0n4t1ugw81]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_CaregiverCharacteristic]  WITH CHECK ADD  CONSTRAINT [FK_1rpn15bwqi1lqby0n4t1ugw81] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_CaregiverCharacteristic] CHECK CONSTRAINT [FK_1rpn15bwqi1lqby0n4t1ugw81]
GO
/****** Object:  ForeignKey [FK_trtrnwgmhr2a1kgle7b0k74r4]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_CaregiverCharacteristic]  WITH CHECK ADD  CONSTRAINT [FK_trtrnwgmhr2a1kgle7b0k74r4] FOREIGN KEY([caregiver_characteristic_id])
REFERENCES [dbo].[CaregiverCharacteristic] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_CaregiverCharacteristic] CHECK CONSTRAINT [FK_trtrnwgmhr2a1kgle7b0k74r4]
GO
/****** Object:  ForeignKey [FK_230dhcfaga4vor3f5uo7rnxwq]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_AssessmentScaleObservation]  WITH CHECK ADD  CONSTRAINT [FK_230dhcfaga4vor3f5uo7rnxwq] FOREIGN KEY([observation_id])
REFERENCES [dbo].[AssessmentScaleObservation] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_AssessmentScaleObservation] CHECK CONSTRAINT [FK_230dhcfaga4vor3f5uo7rnxwq]
GO
/****** Object:  ForeignKey [FK_am62vkf77i1yr5m7nmkhn3dst]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_AssessmentScaleObservation]  WITH CHECK ADD  CONSTRAINT [FK_am62vkf77i1yr5m7nmkhn3dst] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_AssessmentScaleObservation] CHECK CONSTRAINT [FK_am62vkf77i1yr5m7nmkhn3dst]
GO
/****** Object:  ForeignKey [FK_2p1vtpqt3jki3td10ckcswo53]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusProblemObservation]  WITH CHECK ADD  CONSTRAINT [FK_2p1vtpqt3jki3td10ckcswo53] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusProblemObservation] CHECK CONSTRAINT [FK_2p1vtpqt3jki3td10ckcswo53]
GO
/****** Object:  ForeignKey [FK_4y5evskrsxsv8kqyemle1v6lr]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusProblemObservation]  WITH CHECK ADD  CONSTRAINT [FK_4y5evskrsxsv8kqyemle1v6lr] FOREIGN KEY([problem_observation_id])
REFERENCES [dbo].[StatusProblemObservation] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusProblemObservation] CHECK CONSTRAINT [FK_4y5evskrsxsv8kqyemle1v6lr]
GO
/****** Object:  ForeignKey [FK_a8p5jjp14jkg5s558dodk3b9x]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultOrganizer]  WITH CHECK ADD  CONSTRAINT [FK_a8p5jjp14jkg5s558dodk3b9x] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultOrganizer] CHECK CONSTRAINT [FK_a8p5jjp14jkg5s558dodk3b9x]
GO
/****** Object:  ForeignKey [FK_je5h28ndu2ehioxpos7w4tut9]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultOrganizer]  WITH CHECK ADD  CONSTRAINT [FK_je5h28ndu2ehioxpos7w4tut9] FOREIGN KEY([result_organizer_id])
REFERENCES [dbo].[StatusResultOrganizer] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultOrganizer] CHECK CONSTRAINT [FK_je5h28ndu2ehioxpos7w4tut9]
GO
/****** Object:  ForeignKey [FK_gnu3sk4xvdmi6ipucq37jnhiv]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_NonMedicinalSupplyActivity]  WITH CHECK ADD  CONSTRAINT [FK_gnu3sk4xvdmi6ipucq37jnhiv] FOREIGN KEY([supply_activity_id])
REFERENCES [dbo].[NonMedicinalSupplyActivity] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_NonMedicinalSupplyActivity] CHECK CONSTRAINT [FK_gnu3sk4xvdmi6ipucq37jnhiv]
GO
/****** Object:  ForeignKey [FK_kfsmxb8qnt2b8ha8hf0e2pydt]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_NonMedicinalSupplyActivity]  WITH CHECK ADD  CONSTRAINT [FK_kfsmxb8qnt2b8ha8hf0e2pydt] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_NonMedicinalSupplyActivity] CHECK CONSTRAINT [FK_kfsmxb8qnt2b8ha8hf0e2pydt]
GO
/****** Object:  ForeignKey [FK_hbajnd481avhhv1ers5p4h3o8]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultOrganizer]  WITH CHECK ADD  CONSTRAINT [FK_hbajnd481avhhv1ers5p4h3o8] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultOrganizer] CHECK CONSTRAINT [FK_hbajnd481avhhv1ers5p4h3o8]
GO
/****** Object:  ForeignKey [FK_lotqy3p0yh798tk7lgg34qqqp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultOrganizer]  WITH CHECK ADD  CONSTRAINT [FK_lotqy3p0yh798tk7lgg34qqqp] FOREIGN KEY([result_organizer_id])
REFERENCES [dbo].[StatusResultOrganizer] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultOrganizer] CHECK CONSTRAINT [FK_lotqy3p0yh798tk7lgg34qqqp]
GO
/****** Object:  ForeignKey [FK_4tvnkcn7v5t91qvgq7klarf1w]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[SocialHistoryObservation]  WITH CHECK ADD  CONSTRAINT [FK_4tvnkcn7v5t91qvgq7klarf1w] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[SocialHistoryObservation] CHECK CONSTRAINT [FK_4tvnkcn7v5t91qvgq7klarf1w]
GO
/****** Object:  ForeignKey [FK_rnjdnpcie06y1m4hvxyhy3v6d]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[SocialHistoryObservation]  WITH CHECK ADD  CONSTRAINT [FK_rnjdnpcie06y1m4hvxyhy3v6d] FOREIGN KEY([social_history_id])
REFERENCES [dbo].[SocialHistory] ([id])
GO
ALTER TABLE [dbo].[SocialHistoryObservation] CHECK CONSTRAINT [FK_rnjdnpcie06y1m4hvxyhy3v6d]
GO
/****** Object:  ForeignKey [FK_2syq24iaqalgly28ahlt7xx38]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Supply]  WITH CHECK ADD  CONSTRAINT [FK_2syq24iaqalgly28ahlt7xx38] FOREIGN KEY([plan_of_care_id])
REFERENCES [dbo].[PlanOfCare] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Supply] CHECK CONSTRAINT [FK_2syq24iaqalgly28ahlt7xx38]
GO
/****** Object:  ForeignKey [FK_moi0bj3jo3g9ut3crlfiokxl]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Supply]  WITH CHECK ADD  CONSTRAINT [FK_moi0bj3jo3g9ut3crlfiokxl] FOREIGN KEY([supply_id])
REFERENCES [dbo].[PlanOfCareActivity] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Supply] CHECK CONSTRAINT [FK_moi0bj3jo3g9ut3crlfiokxl]
GO
/****** Object:  ForeignKey [FK_8smc2xhmuiew6unt1c3ksbtvp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_SubstanceAdministration]  WITH CHECK ADD  CONSTRAINT [FK_8smc2xhmuiew6unt1c3ksbtvp] FOREIGN KEY([plan_of_care_id])
REFERENCES [dbo].[PlanOfCare] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_SubstanceAdministration] CHECK CONSTRAINT [FK_8smc2xhmuiew6unt1c3ksbtvp]
GO
/****** Object:  ForeignKey [FK_r1adld8j5tmpogaaexminsewo]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_SubstanceAdministration]  WITH CHECK ADD  CONSTRAINT [FK_r1adld8j5tmpogaaexminsewo] FOREIGN KEY([substance_administration_id])
REFERENCES [dbo].[PlanOfCareActivity] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_SubstanceAdministration] CHECK CONSTRAINT [FK_r1adld8j5tmpogaaexminsewo]
GO
/****** Object:  ForeignKey [FK_huq8ggnbxom2o0cse1v6uupcw]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Procedure]  WITH CHECK ADD  CONSTRAINT [FK_huq8ggnbxom2o0cse1v6uupcw] FOREIGN KEY([procedure_id])
REFERENCES [dbo].[PlanOfCareActivity] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Procedure] CHECK CONSTRAINT [FK_huq8ggnbxom2o0cse1v6uupcw]
GO
/****** Object:  ForeignKey [FK_s3pd59i86lbsntgiu05w49xeo]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Procedure]  WITH CHECK ADD  CONSTRAINT [FK_s3pd59i86lbsntgiu05w49xeo] FOREIGN KEY([plan_of_care_id])
REFERENCES [dbo].[PlanOfCare] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Procedure] CHECK CONSTRAINT [FK_s3pd59i86lbsntgiu05w49xeo]
GO
/****** Object:  ForeignKey [FK_ix1rc2bvjb4elb1yhaxdphxw1]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Observation]  WITH CHECK ADD  CONSTRAINT [FK_ix1rc2bvjb4elb1yhaxdphxw1] FOREIGN KEY([plan_of_care_id])
REFERENCES [dbo].[PlanOfCare] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Observation] CHECK CONSTRAINT [FK_ix1rc2bvjb4elb1yhaxdphxw1]
GO
/****** Object:  ForeignKey [FK_sjm526l6ci1a3ccok74epscgu]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Observation]  WITH CHECK ADD  CONSTRAINT [FK_sjm526l6ci1a3ccok74epscgu] FOREIGN KEY([observation_id])
REFERENCES [dbo].[PlanOfCareActivity] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Observation] CHECK CONSTRAINT [FK_sjm526l6ci1a3ccok74epscgu]
GO
/****** Object:  ForeignKey [FK_klgfl2txtq2ucf0vgbroawp1w]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Instructions]  WITH CHECK ADD  CONSTRAINT [FK_klgfl2txtq2ucf0vgbroawp1w] FOREIGN KEY([plan_of_care_id])
REFERENCES [dbo].[PlanOfCare] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Instructions] CHECK CONSTRAINT [FK_klgfl2txtq2ucf0vgbroawp1w]
GO
/****** Object:  ForeignKey [FK_mbfhkj0qpjrvatul15epfo21l]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Instructions]  WITH CHECK ADD  CONSTRAINT [FK_mbfhkj0qpjrvatul15epfo21l] FOREIGN KEY([instruction_id])
REFERENCES [dbo].[Instructions] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Instructions] CHECK CONSTRAINT [FK_mbfhkj0qpjrvatul15epfo21l]
GO
/****** Object:  ForeignKey [FK_55hs60fql0b4r5feiqi699ugl]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Encounter]  WITH CHECK ADD  CONSTRAINT [FK_55hs60fql0b4r5feiqi699ugl] FOREIGN KEY([encounter_id])
REFERENCES [dbo].[PlanOfCareActivity] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Encounter] CHECK CONSTRAINT [FK_55hs60fql0b4r5feiqi699ugl]
GO
/****** Object:  ForeignKey [FK_fhcg8x6ex7kn07cdj0l9b9hjh]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Encounter]  WITH CHECK ADD  CONSTRAINT [FK_fhcg8x6ex7kn07cdj0l9b9hjh] FOREIGN KEY([plan_of_care_id])
REFERENCES [dbo].[PlanOfCare] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Encounter] CHECK CONSTRAINT [FK_fhcg8x6ex7kn07cdj0l9b9hjh]
GO
/****** Object:  ForeignKey [FK_2s67r2qouinvmmqgg8vtfjven]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Act]  WITH CHECK ADD  CONSTRAINT [FK_2s67r2qouinvmmqgg8vtfjven] FOREIGN KEY([plan_of_care_id])
REFERENCES [dbo].[PlanOfCare] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Act] CHECK CONSTRAINT [FK_2s67r2qouinvmmqgg8vtfjven]
GO
/****** Object:  ForeignKey [FK_9osqjkp8ep9c2wlbrpkrnyok]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PlanOfCare_Act]  WITH CHECK ADD  CONSTRAINT [FK_9osqjkp8ep9c2wlbrpkrnyok] FOREIGN KEY([act_id])
REFERENCES [dbo].[PlanOfCareActivity] ([id])
GO
ALTER TABLE [dbo].[PlanOfCare_Act] CHECK CONSTRAINT [FK_9osqjkp8ep9c2wlbrpkrnyok]
GO
/****** Object:  ForeignKey [FK_98p9v0k2kpvxhib946rrh59]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PressureUlcerObservation]  WITH CHECK ADD  CONSTRAINT [FK_98p9v0k2kpvxhib946rrh59] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[PressureUlcerObservation] CHECK CONSTRAINT [FK_98p9v0k2kpvxhib946rrh59]
GO
/****** Object:  ForeignKey [FK_o53o76ft1lyhbowjuav112mp9]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PressureUlcerObservation]  WITH CHECK ADD  CONSTRAINT [FK_o53o76ft1lyhbowjuav112mp9] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[PressureUlcerObservation] CHECK CONSTRAINT [FK_o53o76ft1lyhbowjuav112mp9]
GO
/****** Object:  ForeignKey [FK_6gkfmfqbyt4is0jlberf50dyo]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PregnancyObservation]  WITH CHECK ADD  CONSTRAINT [FK_6gkfmfqbyt4is0jlberf50dyo] FOREIGN KEY([social_history_id])
REFERENCES [dbo].[SocialHistory] ([id])
GO
ALTER TABLE [dbo].[PregnancyObservation] CHECK CONSTRAINT [FK_6gkfmfqbyt4is0jlberf50dyo]
GO
/****** Object:  ForeignKey [FK_mtkuqvdxv1stma474brt19us4]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PregnancyObservation]  WITH CHECK ADD  CONSTRAINT [FK_mtkuqvdxv1stma474brt19us4] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[PregnancyObservation] CHECK CONSTRAINT [FK_mtkuqvdxv1stma474brt19us4]
GO
/****** Object:  ForeignKey [FK_f7qa2drxylhi8lb2pasyt1i29]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PolicyActivity]  WITH CHECK ADD  CONSTRAINT [FK_f7qa2drxylhi8lb2pasyt1i29] FOREIGN KEY([guarantor_organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_f7qa2drxylhi8lb2pasyt1i29]
GO
/****** Object:  ForeignKey [FK_fffju6dn9esqyfwl3a4hpbirv]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PolicyActivity]  WITH CHECK ADD  CONSTRAINT [FK_fffju6dn9esqyfwl3a4hpbirv] FOREIGN KEY([guarantor_person_id])
REFERENCES [dbo].[Person] ([id])
GO
ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_fffju6dn9esqyfwl3a4hpbirv]
GO
/****** Object:  ForeignKey [FK_i0bjfn1o4yt46s56lm7a3vql3]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PolicyActivity]  WITH CHECK ADD  CONSTRAINT [FK_i0bjfn1o4yt46s56lm7a3vql3] FOREIGN KEY([participant_id])
REFERENCES [dbo].[Participant] ([id])
GO
ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_i0bjfn1o4yt46s56lm7a3vql3]
GO
/****** Object:  ForeignKey [FK_lcfoa65o7c0f0qg2ybyh9bxh8]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PolicyActivity]  WITH CHECK ADD  CONSTRAINT [FK_lcfoa65o7c0f0qg2ybyh9bxh8] FOREIGN KEY([payer_id])
REFERENCES [dbo].[Payer] ([id])
GO
ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_lcfoa65o7c0f0qg2ybyh9bxh8]
GO
/****** Object:  ForeignKey [FK_nkj42nesiny305q39h2nu0lb]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PolicyActivity]  WITH CHECK ADD  CONSTRAINT [FK_nkj42nesiny305q39h2nu0lb] FOREIGN KEY([payer_org_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_nkj42nesiny305q39h2nu0lb]
GO
/****** Object:  ForeignKey [FK_rn8qoq473l8vul06g0cpd8u3s]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PolicyActivity]  WITH CHECK ADD  CONSTRAINT [FK_rn8qoq473l8vul06g0cpd8u3s] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_rn8qoq473l8vul06g0cpd8u3s]
GO
/****** Object:  ForeignKey [FK_ru5ypvn0xcinjymbs371ynbd2]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[PolicyActivity]  WITH CHECK ADD  CONSTRAINT [FK_ru5ypvn0xcinjymbs371ynbd2] FOREIGN KEY([subscriber_id])
REFERENCES [dbo].[Participant] ([id])
GO
ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_ru5ypvn0xcinjymbs371ynbd2]
GO
/****** Object:  ForeignKey [FK_37nf468tdwjc7vbu36tkt9vcc]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationSupplyOrder]  WITH CHECK ADD  CONSTRAINT [FK_37nf468tdwjc7vbu36tkt9vcc] FOREIGN KEY([immunization_medication_information_id])
REFERENCES [dbo].[ImmunizationMedicationInformation] ([id])
GO
ALTER TABLE [dbo].[MedicationSupplyOrder] CHECK CONSTRAINT [FK_37nf468tdwjc7vbu36tkt9vcc]
GO
/****** Object:  ForeignKey [FK_6l7kmc42o7a6txxl18pmuwvxm]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationSupplyOrder]  WITH CHECK ADD  CONSTRAINT [FK_6l7kmc42o7a6txxl18pmuwvxm] FOREIGN KEY([medication_information_id])
REFERENCES [dbo].[MedicationInformation] ([id])
GO
ALTER TABLE [dbo].[MedicationSupplyOrder] CHECK CONSTRAINT [FK_6l7kmc42o7a6txxl18pmuwvxm]
GO
/****** Object:  ForeignKey [FK_gnb5c8ekfdju4cadtr7x1tfpi]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationSupplyOrder]  WITH CHECK ADD  CONSTRAINT [FK_gnb5c8ekfdju4cadtr7x1tfpi] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[MedicationSupplyOrder] CHECK CONSTRAINT [FK_gnb5c8ekfdju4cadtr7x1tfpi]
GO
/****** Object:  ForeignKey [FK_ja0e16sbpdxkae9jm7ak7v6eo]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationSupplyOrder]  WITH CHECK ADD  CONSTRAINT [FK_ja0e16sbpdxkae9jm7ak7v6eo] FOREIGN KEY([instructions_id])
REFERENCES [dbo].[Instructions] ([id])
GO
ALTER TABLE [dbo].[MedicationSupplyOrder] CHECK CONSTRAINT [FK_ja0e16sbpdxkae9jm7ak7v6eo]
GO
/****** Object:  ForeignKey [FK_nioi88vdpg3xqyky33gv02ngg]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationSupplyOrder]  WITH CHECK ADD  CONSTRAINT [FK_nioi88vdpg3xqyky33gv02ngg] FOREIGN KEY([author_id])
REFERENCES [dbo].[Author] ([id])
GO
ALTER TABLE [dbo].[MedicationSupplyOrder] CHECK CONSTRAINT [FK_nioi88vdpg3xqyky33gv02ngg]
GO
/****** Object:  ForeignKey [FK_f4e10e5o7vxyuj4tywtoa7dwp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProblemObservation]  WITH CHECK ADD  CONSTRAINT [FK_f4e10e5o7vxyuj4tywtoa7dwp] FOREIGN KEY([problem_id])
REFERENCES [dbo].[Problem] ([id])
GO
ALTER TABLE [dbo].[ProblemObservation] CHECK CONSTRAINT [FK_f4e10e5o7vxyuj4tywtoa7dwp]
GO
/****** Object:  ForeignKey [FK_ftxv6w2lj42sr9p3qtwsiuj74]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProblemObservation]  WITH CHECK ADD  CONSTRAINT [FK_ftxv6w2lj42sr9p3qtwsiuj74] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ProblemObservation] CHECK CONSTRAINT [FK_ftxv6w2lj42sr9p3qtwsiuj74]
GO
/****** Object:  ForeignKey [FK_464n9v0qdax92dtahjpxdo2wr]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentUnitHistory]  WITH CHECK ADD  CONSTRAINT [FK_464n9v0qdax92dtahjpxdo2wr] FOREIGN KEY([res_admit_id])
REFERENCES [dbo].[ResidentAdmittanceHistory] ([id])
GO
ALTER TABLE [dbo].[ResidentUnitHistory] CHECK CONSTRAINT [FK_464n9v0qdax92dtahjpxdo2wr]
GO
/****** Object:  ForeignKey [FK_b10gxcebxly7qxoj6o975l2mr]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentUnitHistory]  WITH CHECK ADD  CONSTRAINT [FK_b10gxcebxly7qxoj6o975l2mr] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[ResidentUnitHistory] CHECK CONSTRAINT [FK_b10gxcebxly7qxoj6o975l2mr]
GO
/****** Object:  ForeignKey [FK_lsndfji4ye6toh8tjjxqiq8br]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentUnitHistory]  WITH CHECK ADD  CONSTRAINT [FK_lsndfji4ye6toh8tjjxqiq8br] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ResidentUnitHistory] CHECK CONSTRAINT [FK_lsndfji4ye6toh8tjjxqiq8br]
GO
/****** Object:  ForeignKey [FK_maqncuieq5iqvhwkjjnggt5i1]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentUnitHistory]  WITH CHECK ADD  CONSTRAINT [FK_maqncuieq5iqvhwkjjnggt5i1] FOREIGN KEY([unit_id])
REFERENCES [dbo].[Unit] ([id])
GO
ALTER TABLE [dbo].[ResidentUnitHistory] CHECK CONSTRAINT [FK_maqncuieq5iqvhwkjjnggt5i1]
GO
/****** Object:  ForeignKey [FK_qa3q6gisnu5yhu99vmr1fh4fx]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResidentUnitHistory]  WITH CHECK ADD  CONSTRAINT [FK_qa3q6gisnu5yhu99vmr1fh4fx] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[ResidentUnitHistory] CHECK CONSTRAINT [FK_qa3q6gisnu5yhu99vmr1fh4fx]
GO
/****** Object:  ForeignKey [FK_19xm1isq42x5pyega14sliop5]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_19xm1isq42x5pyega14sliop5] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ResultObservation] CHECK CONSTRAINT [FK_19xm1isq42x5pyega14sliop5]
GO
/****** Object:  ForeignKey [FK_rdong0i3ctlx7pe6slyrqgl6h]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_rdong0i3ctlx7pe6slyrqgl6h] FOREIGN KEY([author_id])
REFERENCES [dbo].[Author] ([id])
GO
ALTER TABLE [dbo].[ResultObservation] CHECK CONSTRAINT [FK_rdong0i3ctlx7pe6slyrqgl6h]
GO
/****** Object:  ForeignKey [FK_1ibdm3dxqss2t5i88b4sdvmls]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_1ibdm3dxqss2t5i88b4sdvmls] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservation] CHECK CONSTRAINT [FK_1ibdm3dxqss2t5i88b4sdvmls]
GO
/****** Object:  ForeignKey [FK_ramow29repaki02vb15p3012k]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_ramow29repaki02vb15p3012k] FOREIGN KEY([author_id])
REFERENCES [dbo].[Author] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservation] CHECK CONSTRAINT [FK_ramow29repaki02vb15p3012k]
GO
/****** Object:  ForeignKey [FK_126pcmcpruefcld9jblu2iq80]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[SmokingStatusObservation]  WITH CHECK ADD  CONSTRAINT [FK_126pcmcpruefcld9jblu2iq80] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[SmokingStatusObservation] CHECK CONSTRAINT [FK_126pcmcpruefcld9jblu2iq80]
GO
/****** Object:  ForeignKey [FK_sjgxi0klwa2rd5e36b1kf026s]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[SmokingStatusObservation]  WITH CHECK ADD  CONSTRAINT [FK_sjgxi0klwa2rd5e36b1kf026s] FOREIGN KEY([social_history_id])
REFERENCES [dbo].[SocialHistory] ([id])
GO
ALTER TABLE [dbo].[SmokingStatusObservation] CHECK CONSTRAINT [FK_sjgxi0klwa2rd5e36b1kf026s]
GO
/****** Object:  ForeignKey [FK_8xhtau8lq5lingi31psuwbty7]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[VitalSignObservation]  WITH CHECK ADD  CONSTRAINT [FK_8xhtau8lq5lingi31psuwbty7] FOREIGN KEY([author_id])
REFERENCES [dbo].[Author] ([id])
GO
ALTER TABLE [dbo].[VitalSignObservation] CHECK CONSTRAINT [FK_8xhtau8lq5lingi31psuwbty7]
GO
/****** Object:  ForeignKey [FK_g6br5oklkcux8tkju45w6bdoc]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[VitalSignObservation]  WITH CHECK ADD  CONSTRAINT [FK_g6br5oklkcux8tkju45w6bdoc] FOREIGN KEY([vital_sign_id])
REFERENCES [dbo].[VitalSign] ([id])
GO
ALTER TABLE [dbo].[VitalSignObservation] CHECK CONSTRAINT [FK_g6br5oklkcux8tkju45w6bdoc]
GO
/****** Object:  ForeignKey [FK_sb7yh7gl29nvvkgns4vg8rrl4]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[VitalSignObservation]  WITH CHECK ADD  CONSTRAINT [FK_sb7yh7gl29nvvkgns4vg8rrl4] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[VitalSignObservation] CHECK CONSTRAINT [FK_sb7yh7gl29nvvkgns4vg8rrl4]
GO
/****** Object:  ForeignKey [FK_kwrwmr0wv9647qchn4rbw1374]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[TobaccoUse]  WITH CHECK ADD  CONSTRAINT [FK_kwrwmr0wv9647qchn4rbw1374] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[TobaccoUse] CHECK CONSTRAINT [FK_kwrwmr0wv9647qchn4rbw1374]
GO
/****** Object:  ForeignKey [FK_nv6ovg6h9qrpwv7jiqm9trw4m]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[TobaccoUse]  WITH CHECK ADD  CONSTRAINT [FK_nv6ovg6h9qrpwv7jiqm9trw4m] FOREIGN KEY([social_history_id])
REFERENCES [dbo].[SocialHistory] ([id])
GO
ALTER TABLE [dbo].[TobaccoUse] CHECK CONSTRAINT [FK_nv6ovg6h9qrpwv7jiqm9trw4m]
GO
/****** Object:  ForeignKey [FK_diqcp1mfqbjc6kp882u2qn2i5]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[TargetSiteCode]  WITH CHECK ADD  CONSTRAINT [FK_diqcp1mfqbjc6kp882u2qn2i5] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[TargetSiteCode] CHECK CONSTRAINT [FK_diqcp1mfqbjc6kp882u2qn2i5]
GO
/****** Object:  ForeignKey [FK_dqqf6qnihuv3b238tq49k15hp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[TargetSiteCode]  WITH CHECK ADD  CONSTRAINT [FK_dqqf6qnihuv3b238tq49k15hp] FOREIGN KEY([pressure_ulcer_observation_id])
REFERENCES [dbo].[PressureUlcerObservation] ([id])
GO
ALTER TABLE [dbo].[TargetSiteCode] CHECK CONSTRAINT [FK_dqqf6qnihuv3b238tq49k15hp]
GO
/****** Object:  ForeignKey [FK_cw26ns0wubfc4lbj8eksqbye6]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultOrganizer_StatusResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_cw26ns0wubfc4lbj8eksqbye6] FOREIGN KEY([status_result_observation_id])
REFERENCES [dbo].[StatusResultObservation] ([id])
GO
ALTER TABLE [dbo].[StatusResultOrganizer_StatusResultObservation] CHECK CONSTRAINT [FK_cw26ns0wubfc4lbj8eksqbye6]
GO
/****** Object:  ForeignKey [FK_i0myi66klcr4vpal0sun101x4]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultOrganizer_StatusResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_i0myi66klcr4vpal0sun101x4] FOREIGN KEY([status_result_organizer_id])
REFERENCES [dbo].[StatusResultOrganizer] ([id])
GO
ALTER TABLE [dbo].[StatusResultOrganizer_StatusResultObservation] CHECK CONSTRAINT [FK_i0myi66klcr4vpal0sun101x4]
GO
/****** Object:  ForeignKey [FK_8x0r26fo9afy6e1ae9y73upqb]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultObservationRange]  WITH CHECK ADD  CONSTRAINT [FK_8x0r26fo9afy6e1ae9y73upqb] FOREIGN KEY([result_observation_id])
REFERENCES [dbo].[StatusResultObservation] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservationRange] CHECK CONSTRAINT [FK_8x0r26fo9afy6e1ae9y73upqb]
GO
/****** Object:  ForeignKey [FK_t0a8n826tccn3n47qehhe5d3g]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultObservationInterpretationCode]  WITH CHECK ADD  CONSTRAINT [FK_t0a8n826tccn3n47qehhe5d3g] FOREIGN KEY([result_observation_id])
REFERENCES [dbo].[StatusResultObservation] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservationInterpretationCode] CHECK CONSTRAINT [FK_t0a8n826tccn3n47qehhe5d3g]
GO
/****** Object:  ForeignKey [FK_esyq0arpyvsidpknaxuxpqcju]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultObservation_NonMedicinalSupplyActivity]  WITH CHECK ADD  CONSTRAINT [FK_esyq0arpyvsidpknaxuxpqcju] FOREIGN KEY([status_result_observation_id])
REFERENCES [dbo].[StatusResultObservation] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservation_NonMedicinalSupplyActivity] CHECK CONSTRAINT [FK_esyq0arpyvsidpknaxuxpqcju]
GO
/****** Object:  ForeignKey [FK_oj3pchwfs7ien7lpp5o9v0pup]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultObservation_NonMedicinalSupplyActivity]  WITH CHECK ADD  CONSTRAINT [FK_oj3pchwfs7ien7lpp5o9v0pup] FOREIGN KEY([non_medicinal_supply_activity_id])
REFERENCES [dbo].[NonMedicinalSupplyActivity] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservation_NonMedicinalSupplyActivity] CHECK CONSTRAINT [FK_oj3pchwfs7ien7lpp5o9v0pup]
GO
/****** Object:  ForeignKey [FK_68rilxwuw2vqdms8t7evcs99e]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultObservation_CaregiverCharacteristic]  WITH CHECK ADD  CONSTRAINT [FK_68rilxwuw2vqdms8t7evcs99e] FOREIGN KEY([caregiver_characteristic_id])
REFERENCES [dbo].[CaregiverCharacteristic] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservation_CaregiverCharacteristic] CHECK CONSTRAINT [FK_68rilxwuw2vqdms8t7evcs99e]
GO
/****** Object:  ForeignKey [FK_kyjey02smrjlld6ccx68f9fog]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultObservation_CaregiverCharacteristic]  WITH CHECK ADD  CONSTRAINT [FK_kyjey02smrjlld6ccx68f9fog] FOREIGN KEY([status_result_observation_id])
REFERENCES [dbo].[StatusResultObservation] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservation_CaregiverCharacteristic] CHECK CONSTRAINT [FK_kyjey02smrjlld6ccx68f9fog]
GO
/****** Object:  ForeignKey [FK_chx4axs3yjls0if3mehwx6692]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultObservation_AssessmentScaleObservation]  WITH CHECK ADD  CONSTRAINT [FK_chx4axs3yjls0if3mehwx6692] FOREIGN KEY([assessment_scale_observation_id])
REFERENCES [dbo].[AssessmentScaleObservation] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservation_AssessmentScaleObservation] CHECK CONSTRAINT [FK_chx4axs3yjls0if3mehwx6692]
GO
/****** Object:  ForeignKey [FK_rq73oj0xnrwedm6iqsa75sosi]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[StatusResultObservation_AssessmentScaleObservation]  WITH CHECK ADD  CONSTRAINT [FK_rq73oj0xnrwedm6iqsa75sosi] FOREIGN KEY([status_result_observation_id])
REFERENCES [dbo].[StatusResultObservation] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservation_AssessmentScaleObservation] CHECK CONSTRAINT [FK_rq73oj0xnrwedm6iqsa75sosi]
GO
/****** Object:  ForeignKey [FK_kifkbpuvhvtv3irvxx4ovjro5]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResultObservationRange]  WITH CHECK ADD  CONSTRAINT [FK_kifkbpuvhvtv3irvxx4ovjro5] FOREIGN KEY([result_observation_id])
REFERENCES [dbo].[ResultObservation] ([id])
GO
ALTER TABLE [dbo].[ResultObservationRange] CHECK CONSTRAINT [FK_kifkbpuvhvtv3irvxx4ovjro5]
GO
/****** Object:  ForeignKey [FK_5ke3hsfuggjmgdvtc693ay948]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ResultObservationInterpretationCode]  WITH CHECK ADD  CONSTRAINT [FK_5ke3hsfuggjmgdvtc693ay948] FOREIGN KEY([result_observation_id])
REFERENCES [dbo].[ResultObservation] ([id])
GO
ALTER TABLE [dbo].[ResultObservationInterpretationCode] CHECK CONSTRAINT [FK_5ke3hsfuggjmgdvtc693ay948]
GO
/****** Object:  ForeignKey [FK_ig37k063wxih70v5tmfvxclh8]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Result_ResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_ig37k063wxih70v5tmfvxclh8] FOREIGN KEY([result_id])
REFERENCES [dbo].[Result] ([id])
GO
ALTER TABLE [dbo].[Result_ResultObservation] CHECK CONSTRAINT [FK_ig37k063wxih70v5tmfvxclh8]
GO
/****** Object:  ForeignKey [FK_p0cchua5iccevhvwjfwafesql]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Result_ResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_p0cchua5iccevhvwjfwafesql] FOREIGN KEY([result_observation_id])
REFERENCES [dbo].[ResultObservation] ([id])
GO
ALTER TABLE [dbo].[Result_ResultObservation] CHECK CONSTRAINT [FK_p0cchua5iccevhvwjfwafesql]
GO
/****** Object:  ForeignKey [FK_1jl654lrfd8hovcft7rn5ymbj]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication]  WITH CHECK ADD  CONSTRAINT [FK_1jl654lrfd8hovcft7rn5ymbj] FOREIGN KEY([medication_supply_order_id])
REFERENCES [dbo].[MedicationSupplyOrder] ([id])
GO
ALTER TABLE [dbo].[Medication] CHECK CONSTRAINT [FK_1jl654lrfd8hovcft7rn5ymbj]
GO
/****** Object:  ForeignKey [FK_1me961mp9bedb5g09ceiic16f]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication]  WITH CHECK ADD  CONSTRAINT [FK_1me961mp9bedb5g09ceiic16f] FOREIGN KEY([reaction_observation_id])
REFERENCES [dbo].[ReactionObservation] ([id])
GO
ALTER TABLE [dbo].[Medication] CHECK CONSTRAINT [FK_1me961mp9bedb5g09ceiic16f]
GO
/****** Object:  ForeignKey [FK_7qdvsquif5wohuaccveb4gwsw]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication]  WITH CHECK ADD  CONSTRAINT [FK_7qdvsquif5wohuaccveb4gwsw] FOREIGN KEY([medication_information_id])
REFERENCES [dbo].[MedicationInformation] ([id])
GO
ALTER TABLE [dbo].[Medication] CHECK CONSTRAINT [FK_7qdvsquif5wohuaccveb4gwsw]
GO
/****** Object:  ForeignKey [FK_7w51pjnv4st5qh4hynpipe6ce]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication]  WITH CHECK ADD  CONSTRAINT [FK_7w51pjnv4st5qh4hynpipe6ce] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Medication] CHECK CONSTRAINT [FK_7w51pjnv4st5qh4hynpipe6ce]
GO
/****** Object:  ForeignKey [FK_b8728gnr6j64p9iruigk1ffiv]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication]  WITH CHECK ADD  CONSTRAINT [FK_b8728gnr6j64p9iruigk1ffiv] FOREIGN KEY([instructions_id])
REFERENCES [dbo].[Instructions] ([id])
GO
ALTER TABLE [dbo].[Medication] CHECK CONSTRAINT [FK_b8728gnr6j64p9iruigk1ffiv]
GO
/****** Object:  ForeignKey [FK_ro4krl7moaqen4p0cxajjy6bg]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication]  WITH CHECK ADD  CONSTRAINT [FK_ro4krl7moaqen4p0cxajjy6bg] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Medication] CHECK CONSTRAINT [FK_ro4krl7moaqen4p0cxajjy6bg]
GO
/****** Object:  ForeignKey [FK_7vjw3wrbg1x8np91o2yhmpi90]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_7vjw3wrbg1x8np91o2yhmpi90] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultObservation] CHECK CONSTRAINT [FK_7vjw3wrbg1x8np91o2yhmpi90]
GO
/****** Object:  ForeignKey [FK_82veit0ycx3j1hmeerbc3mgq8]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_82veit0ycx3j1hmeerbc3mgq8] FOREIGN KEY([result_observation_id])
REFERENCES [dbo].[StatusResultObservation] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultObservation] CHECK CONSTRAINT [FK_82veit0ycx3j1hmeerbc3mgq8]
GO
/****** Object:  ForeignKey [FK_i08s52gmxiu1u3ijnhskkhoj4]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_i08s52gmxiu1u3ijnhskkhoj4] FOREIGN KEY([result_observation_id])
REFERENCES [dbo].[StatusResultObservation] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultObservation] CHECK CONSTRAINT [FK_i08s52gmxiu1u3ijnhskkhoj4]
GO
/****** Object:  ForeignKey [FK_l52jcsynelovto77p2j4kak39]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultObservation]  WITH CHECK ADD  CONSTRAINT [FK_l52jcsynelovto77p2j4kak39] FOREIGN KEY([functional_status_id])
REFERENCES [dbo].[FunctionalStatus] ([id])
GO
ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultObservation] CHECK CONSTRAINT [FK_l52jcsynelovto77p2j4kak39]
GO
/****** Object:  ForeignKey [FK_3c32slf5infrw0wy8ehed2qy4]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationDispense]  WITH CHECK ADD  CONSTRAINT [FK_3c32slf5infrw0wy8ehed2qy4] FOREIGN KEY([immunization_medication_information_id])
REFERENCES [dbo].[ImmunizationMedicationInformation] ([id])
GO
ALTER TABLE [dbo].[MedicationDispense] CHECK CONSTRAINT [FK_3c32slf5infrw0wy8ehed2qy4]
GO
/****** Object:  ForeignKey [FK_8svicpuetxynxu1k2321vcm86]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationDispense]  WITH CHECK ADD  CONSTRAINT [FK_8svicpuetxynxu1k2321vcm86] FOREIGN KEY([medication_supply_order_id])
REFERENCES [dbo].[MedicationSupplyOrder] ([id])
GO
ALTER TABLE [dbo].[MedicationDispense] CHECK CONSTRAINT [FK_8svicpuetxynxu1k2321vcm86]
GO
/****** Object:  ForeignKey [FK_9rtapv8u53f9vgqyl7vwfetjy]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationDispense]  WITH CHECK ADD  CONSTRAINT [FK_9rtapv8u53f9vgqyl7vwfetjy] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[MedicationDispense] CHECK CONSTRAINT [FK_9rtapv8u53f9vgqyl7vwfetjy]
GO
/****** Object:  ForeignKey [FK_a0fx1hj3v0xyc4xrtema8v53a]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationDispense]  WITH CHECK ADD  CONSTRAINT [FK_a0fx1hj3v0xyc4xrtema8v53a] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[MedicationDispense] CHECK CONSTRAINT [FK_a0fx1hj3v0xyc4xrtema8v53a]
GO
/****** Object:  ForeignKey [FK_dqg6jhqi3tj1n3vc7257862c7]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[MedicationDispense]  WITH CHECK ADD  CONSTRAINT [FK_dqg6jhqi3tj1n3vc7257862c7] FOREIGN KEY([medication_information_id])
REFERENCES [dbo].[MedicationInformation] ([id])
GO
ALTER TABLE [dbo].[MedicationDispense] CHECK CONSTRAINT [FK_dqg6jhqi3tj1n3vc7257862c7]
GO
/****** Object:  ForeignKey [FK_8vssyk7gx48xqwbiyn1y2nh71]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AllergyObservation_ReactionObservation]  WITH CHECK ADD  CONSTRAINT [FK_8vssyk7gx48xqwbiyn1y2nh71] FOREIGN KEY([reaction_observation_id])
REFERENCES [dbo].[ReactionObservation] ([id])
GO
ALTER TABLE [dbo].[AllergyObservation_ReactionObservation] CHECK CONSTRAINT [FK_8vssyk7gx48xqwbiyn1y2nh71]
GO
/****** Object:  ForeignKey [FK_jb18xqnh8jtpq1x3ems0pm60v]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AllergyObservation_ReactionObservation]  WITH CHECK ADD  CONSTRAINT [FK_jb18xqnh8jtpq1x3ems0pm60v] FOREIGN KEY([allergy_observation_id])
REFERENCES [dbo].[AllergyObservation] ([id])
GO
ALTER TABLE [dbo].[AllergyObservation_ReactionObservation] CHECK CONSTRAINT [FK_jb18xqnh8jtpq1x3ems0pm60v]
GO
/****** Object:  ForeignKey [FK_69ohodnvt7ye55pdm9qqpyu64]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AdvanceDirectivesVerifier]  WITH CHECK ADD  CONSTRAINT [FK_69ohodnvt7ye55pdm9qqpyu64] FOREIGN KEY([verifier_id])
REFERENCES [dbo].[Participant] ([id])
GO
ALTER TABLE [dbo].[AdvanceDirectivesVerifier] CHECK CONSTRAINT [FK_69ohodnvt7ye55pdm9qqpyu64]
GO
/****** Object:  ForeignKey [FK_94xv15iqvtbsibs2j7enm0rvs]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AdvanceDirectivesVerifier]  WITH CHECK ADD  CONSTRAINT [FK_94xv15iqvtbsibs2j7enm0rvs] FOREIGN KEY([advance_directive_id])
REFERENCES [dbo].[AdvanceDirective] ([id])
GO
ALTER TABLE [dbo].[AdvanceDirectivesVerifier] CHECK CONSTRAINT [FK_94xv15iqvtbsibs2j7enm0rvs]
GO
/****** Object:  ForeignKey [FK_8nynq13housecc3myp28wchn8]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AdvanceDirectiveDocument]  WITH CHECK ADD  CONSTRAINT [FK_8nynq13housecc3myp28wchn8] FOREIGN KEY([advance_directive_id])
REFERENCES [dbo].[AdvanceDirective] ([id])
GO
ALTER TABLE [dbo].[AdvanceDirectiveDocument] CHECK CONSTRAINT [FK_8nynq13housecc3myp28wchn8]
GO
/****** Object:  ForeignKey [FK_fcjyj348a5fg7slhrqb7i50ws]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AdvanceDirectiveDocument]  WITH CHECK ADD  CONSTRAINT [FK_fcjyj348a5fg7slhrqb7i50ws] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[AdvanceDirectiveDocument] CHECK CONSTRAINT [FK_fcjyj348a5fg7slhrqb7i50ws]
GO
/****** Object:  ForeignKey [FK_md5g9i4swfjhv1x0dfxqgtunb]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AuthorizationActivity]  WITH CHECK ADD  CONSTRAINT [FK_md5g9i4swfjhv1x0dfxqgtunb] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[AuthorizationActivity] CHECK CONSTRAINT [FK_md5g9i4swfjhv1x0dfxqgtunb]
GO
/****** Object:  ForeignKey [FK_rdgo935axcu4sdcp91ynu66xc]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AuthorizationActivity]  WITH CHECK ADD  CONSTRAINT [FK_rdgo935axcu4sdcp91ynu66xc] FOREIGN KEY([policy_activity_id])
REFERENCES [dbo].[PolicyActivity] ([id])
GO
ALTER TABLE [dbo].[AuthorizationActivity] CHECK CONSTRAINT [FK_rdgo935axcu4sdcp91ynu66xc]
GO
/****** Object:  ForeignKey [FK_4ggyecsbpkovemhubq1rymmck]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[CoveragePlanDescription]  WITH CHECK ADD  CONSTRAINT [FK_4ggyecsbpkovemhubq1rymmck] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[CoveragePlanDescription] CHECK CONSTRAINT [FK_4ggyecsbpkovemhubq1rymmck]
GO
/****** Object:  ForeignKey [FK_ilxlvo4ev08efrmxmnw2qg657]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[CoveragePlanDescription]  WITH CHECK ADD  CONSTRAINT [FK_ilxlvo4ev08efrmxmnw2qg657] FOREIGN KEY([policy_activity_id])
REFERENCES [dbo].[PolicyActivity] ([id])
GO
ALTER TABLE [dbo].[CoveragePlanDescription] CHECK CONSTRAINT [FK_ilxlvo4ev08efrmxmnw2qg657]
GO
/****** Object:  ForeignKey [FK_4i5aejoddqbvk1g6mn3r50w5e]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Encounter]  WITH CHECK ADD  CONSTRAINT [FK_4i5aejoddqbvk1g6mn3r50w5e] FOREIGN KEY([problem_observation_id])
REFERENCES [dbo].[ProblemObservation] ([id])
GO
ALTER TABLE [dbo].[Encounter] CHECK CONSTRAINT [FK_4i5aejoddqbvk1g6mn3r50w5e]
GO
/****** Object:  ForeignKey [FK_gjud8i6o02g8ys3jxafskudvn]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Encounter]  WITH CHECK ADD  CONSTRAINT [FK_gjud8i6o02g8ys3jxafskudvn] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Encounter] CHECK CONSTRAINT [FK_gjud8i6o02g8ys3jxafskudvn]
GO
/****** Object:  ForeignKey [FK_igjkmumxuqbyo8pgye3eudvby]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Encounter]  WITH CHECK ADD  CONSTRAINT [FK_igjkmumxuqbyo8pgye3eudvby] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Encounter] CHECK CONSTRAINT [FK_igjkmumxuqbyo8pgye3eudvby]
GO
/****** Object:  ForeignKey [FK_gmko275yi9ghjebhht7hv0s2r]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[EncounterProvider]  WITH CHECK ADD  CONSTRAINT [FK_gmko275yi9ghjebhht7hv0s2r] FOREIGN KEY([encounter_id])
REFERENCES [dbo].[Encounter] ([id])
GO
ALTER TABLE [dbo].[EncounterProvider] CHECK CONSTRAINT [FK_gmko275yi9ghjebhht7hv0s2r]
GO
/****** Object:  ForeignKey [FK_98qx89pnv4trogdq9rswhhru5]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Encounter_Indication]  WITH CHECK ADD  CONSTRAINT [FK_98qx89pnv4trogdq9rswhhru5] FOREIGN KEY([encounter_id])
REFERENCES [dbo].[Encounter] ([id])
GO
ALTER TABLE [dbo].[Encounter_Indication] CHECK CONSTRAINT [FK_98qx89pnv4trogdq9rswhhru5]
GO
/****** Object:  ForeignKey [FK_jy2r81w9g41pkpk12n8p11ygj]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Encounter_Indication]  WITH CHECK ADD  CONSTRAINT [FK_jy2r81w9g41pkpk12n8p11ygj] FOREIGN KEY([indication_id])
REFERENCES [dbo].[Indication] ([id])
GO
ALTER TABLE [dbo].[Encounter_Indication] CHECK CONSTRAINT [FK_jy2r81w9g41pkpk12n8p11ygj]
GO
/****** Object:  ForeignKey [FK_3cj0dywv8hc3ke7enidu4cs71]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Encounter_DeliveryLocation]  WITH CHECK ADD  CONSTRAINT [FK_3cj0dywv8hc3ke7enidu4cs71] FOREIGN KEY([location_id])
REFERENCES [dbo].[DeliveryLocation] ([id])
GO
ALTER TABLE [dbo].[Encounter_DeliveryLocation] CHECK CONSTRAINT [FK_3cj0dywv8hc3ke7enidu4cs71]
GO
/****** Object:  ForeignKey [FK_b877oitxnk8pemvpflp2o3q7w]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Encounter_DeliveryLocation]  WITH CHECK ADD  CONSTRAINT [FK_b877oitxnk8pemvpflp2o3q7w] FOREIGN KEY([encounter_id])
REFERENCES [dbo].[Encounter] ([id])
GO
ALTER TABLE [dbo].[Encounter_DeliveryLocation] CHECK CONSTRAINT [FK_b877oitxnk8pemvpflp2o3q7w]
GO
/****** Object:  ForeignKey [FK_t1xj9c3cg1yyxplf76pncb7a7]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[AuthorizationActivityClinicalStatement]  WITH CHECK ADD  CONSTRAINT [FK_t1xj9c3cg1yyxplf76pncb7a7] FOREIGN KEY([authorization_activity_id])
REFERENCES [dbo].[AuthorizationActivity] ([id])
GO
ALTER TABLE [dbo].[AuthorizationActivityClinicalStatement] CHECK CONSTRAINT [FK_t1xj9c3cg1yyxplf76pncb7a7]
GO
/****** Object:  ForeignKey [FK_9yatv1jifw7amrv9e7crb51kp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication_MedicationPrecondition]  WITH CHECK ADD  CONSTRAINT [FK_9yatv1jifw7amrv9e7crb51kp] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])
GO
ALTER TABLE [dbo].[Medication_MedicationPrecondition] CHECK CONSTRAINT [FK_9yatv1jifw7amrv9e7crb51kp]
GO
/****** Object:  ForeignKey [FK_e2tiqnoai98hvpaww0gdgak18]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication_MedicationPrecondition]  WITH CHECK ADD  CONSTRAINT [FK_e2tiqnoai98hvpaww0gdgak18] FOREIGN KEY([precondition_id])
REFERENCES [dbo].[MedicationPrecondition] ([id])
GO
ALTER TABLE [dbo].[Medication_MedicationPrecondition] CHECK CONSTRAINT [FK_e2tiqnoai98hvpaww0gdgak18]
GO
/****** Object:  ForeignKey [FK_iq7aapdf84pfddtcqvas2tfia]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication_MedicationDispense]  WITH CHECK ADD  CONSTRAINT [FK_iq7aapdf84pfddtcqvas2tfia] FOREIGN KEY([medication_dispense_id])
REFERENCES [dbo].[MedicationDispense] ([id])
GO
ALTER TABLE [dbo].[Medication_MedicationDispense] CHECK CONSTRAINT [FK_iq7aapdf84pfddtcqvas2tfia]
GO
/****** Object:  ForeignKey [FK_qn6uuk8h6lld9bl0wdmngh7p0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication_MedicationDispense]  WITH CHECK ADD  CONSTRAINT [FK_qn6uuk8h6lld9bl0wdmngh7p0] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])
GO
ALTER TABLE [dbo].[Medication_MedicationDispense] CHECK CONSTRAINT [FK_qn6uuk8h6lld9bl0wdmngh7p0]
GO
/****** Object:  ForeignKey [FK_b4bjncm0nemkyjg2jcqhtooyc]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication_Indication]  WITH CHECK ADD  CONSTRAINT [FK_b4bjncm0nemkyjg2jcqhtooyc] FOREIGN KEY([indication_id])
REFERENCES [dbo].[Indication] ([id])
GO
ALTER TABLE [dbo].[Medication_Indication] CHECK CONSTRAINT [FK_b4bjncm0nemkyjg2jcqhtooyc]
GO
/****** Object:  ForeignKey [FK_t5r97j9kv5fxtt7pd7blujqkl]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication_Indication]  WITH CHECK ADD  CONSTRAINT [FK_t5r97j9kv5fxtt7pd7blujqkl] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])
GO
ALTER TABLE [dbo].[Medication_Indication] CHECK CONSTRAINT [FK_t5r97j9kv5fxtt7pd7blujqkl]
GO
/****** Object:  ForeignKey [FK_1nlfofspm6f0q4gjy934d7q0d]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication_DrugVehicle]  WITH CHECK ADD  CONSTRAINT [FK_1nlfofspm6f0q4gjy934d7q0d] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])
GO
ALTER TABLE [dbo].[Medication_DrugVehicle] CHECK CONSTRAINT [FK_1nlfofspm6f0q4gjy934d7q0d]
GO
/****** Object:  ForeignKey [FK_m3bku43irex9c3h9kuhof7al3]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Medication_DrugVehicle]  WITH CHECK ADD  CONSTRAINT [FK_m3bku43irex9c3h9kuhof7al3] FOREIGN KEY([drug_vehicle_id])
REFERENCES [dbo].[DrugVehicle] ([id])
GO
ALTER TABLE [dbo].[Medication_DrugVehicle] CHECK CONSTRAINT [FK_m3bku43irex9c3h9kuhof7al3]
GO
/****** Object:  ForeignKey [FK_1ea8hxaiaw0p8bh5htcq7ehe7]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization]  WITH CHECK ADD  CONSTRAINT [FK_1ea8hxaiaw0p8bh5htcq7ehe7] FOREIGN KEY([medication_dispense_id])
REFERENCES [dbo].[MedicationDispense] ([id])
GO
ALTER TABLE [dbo].[Immunization] CHECK CONSTRAINT [FK_1ea8hxaiaw0p8bh5htcq7ehe7]
GO
/****** Object:  ForeignKey [FK_6jfejnm92bgtgi6j2gjm1bsxa]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization]  WITH CHECK ADD  CONSTRAINT [FK_6jfejnm92bgtgi6j2gjm1bsxa] FOREIGN KEY([immunization_refusal_reason_id])
REFERENCES [dbo].[ImmunizationRefusalReason] ([id])
GO
ALTER TABLE [dbo].[Immunization] CHECK CONSTRAINT [FK_6jfejnm92bgtgi6j2gjm1bsxa]
GO
/****** Object:  ForeignKey [FK_74iaa26b5v6p5srhexdijjdxp]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization]  WITH CHECK ADD  CONSTRAINT [FK_74iaa26b5v6p5srhexdijjdxp] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO
ALTER TABLE [dbo].[Immunization] CHECK CONSTRAINT [FK_74iaa26b5v6p5srhexdijjdxp]
GO
/****** Object:  ForeignKey [FK_d9bxhui30vbn4gkw3rmwe6liw]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization]  WITH CHECK ADD  CONSTRAINT [FK_d9bxhui30vbn4gkw3rmwe6liw] FOREIGN KEY([instructions_id])
REFERENCES [dbo].[Instructions] ([id])
GO
ALTER TABLE [dbo].[Immunization] CHECK CONSTRAINT [FK_d9bxhui30vbn4gkw3rmwe6liw]
GO
/****** Object:  ForeignKey [FK_f2qvh8robe83rcyh5tl19je9l]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization]  WITH CHECK ADD  CONSTRAINT [FK_f2qvh8robe83rcyh5tl19je9l] FOREIGN KEY([medication_supply_order_id])
REFERENCES [dbo].[MedicationSupplyOrder] ([id])
GO
ALTER TABLE [dbo].[Immunization] CHECK CONSTRAINT [FK_f2qvh8robe83rcyh5tl19je9l]
GO
/****** Object:  ForeignKey [FK_lltvxmevq5kdvjysdryhhamlk]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization]  WITH CHECK ADD  CONSTRAINT [FK_lltvxmevq5kdvjysdryhhamlk] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[Immunization] CHECK CONSTRAINT [FK_lltvxmevq5kdvjysdryhhamlk]
GO
/****** Object:  ForeignKey [FK_rgw4pj9qrmbqxrashh0qh1bgg]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization]  WITH CHECK ADD  CONSTRAINT [FK_rgw4pj9qrmbqxrashh0qh1bgg] FOREIGN KEY([reaction_observation_id])
REFERENCES [dbo].[ReactionObservation] ([id])
GO
ALTER TABLE [dbo].[Immunization] CHECK CONSTRAINT [FK_rgw4pj9qrmbqxrashh0qh1bgg]
GO
/****** Object:  ForeignKey [FK_s2fyf4i1cluw013hisow923j2]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization]  WITH CHECK ADD  CONSTRAINT [FK_s2fyf4i1cluw013hisow923j2] FOREIGN KEY([immunization_medication_information_id])
REFERENCES [dbo].[ImmunizationMedicationInformation] ([id])
GO
ALTER TABLE [dbo].[Immunization] CHECK CONSTRAINT [FK_s2fyf4i1cluw013hisow923j2]
GO
/****** Object:  ForeignKey [FK_75bknqgjg6dhrm943is5uf781]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity]  WITH CHECK ADD  CONSTRAINT [FK_75bknqgjg6dhrm943is5uf781] FOREIGN KEY([instructions_id])
REFERENCES [dbo].[Instructions] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity] CHECK CONSTRAINT [FK_75bknqgjg6dhrm943is5uf781]
GO
/****** Object:  ForeignKey [FK_8ue56l5coup2tstbyqsjxnxt9]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity]  WITH CHECK ADD  CONSTRAINT [FK_8ue56l5coup2tstbyqsjxnxt9] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity] CHECK CONSTRAINT [FK_8ue56l5coup2tstbyqsjxnxt9]
GO
/****** Object:  ForeignKey [FK_d7sj1n2848al1brsb8hfimfyl]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity]  WITH CHECK ADD  CONSTRAINT [FK_d7sj1n2848al1brsb8hfimfyl] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity] CHECK CONSTRAINT [FK_d7sj1n2848al1brsb8hfimfyl]
GO
/****** Object:  ForeignKey [FK_jk8heiwios8jn5jwf9c9hyg35]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity]  WITH CHECK ADD  CONSTRAINT [FK_jk8heiwios8jn5jwf9c9hyg35] FOREIGN KEY([procedure_type_id])
REFERENCES [dbo].[ProcedureType] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity] CHECK CONSTRAINT [FK_jk8heiwios8jn5jwf9c9hyg35]
GO
/****** Object:  ForeignKey [FK_hjkm5roh70x01gxbqqykak919]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivitySpecimen]  WITH CHECK ADD  CONSTRAINT [FK_hjkm5roh70x01gxbqqykak919] FOREIGN KEY([procedure_activity_id])
REFERENCES [dbo].[ProcedureActivity] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivitySpecimen] CHECK CONSTRAINT [FK_hjkm5roh70x01gxbqqykak919]
GO
/****** Object:  ForeignKey [FK_nwyf4kphvm0xu8c5g90b7jyrr]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivitySite]  WITH CHECK ADD  CONSTRAINT [FK_nwyf4kphvm0xu8c5g90b7jyrr] FOREIGN KEY([procedure_activity_id])
REFERENCES [dbo].[ProcedureActivity] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivitySite] CHECK CONSTRAINT [FK_nwyf4kphvm0xu8c5g90b7jyrr]
GO
/****** Object:  ForeignKey [FK_aimr4abb1x815xh1lalwc5qao]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivityEncounter]  WITH CHECK ADD  CONSTRAINT [FK_aimr4abb1x815xh1lalwc5qao] FOREIGN KEY([procedure_activity_id])
REFERENCES [dbo].[ProcedureActivity] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivityEncounter] CHECK CONSTRAINT [FK_aimr4abb1x815xh1lalwc5qao]
GO
/****** Object:  ForeignKey [FK_1va36crdlypeq9fupl6kj77qw]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity_ProductInstance]  WITH CHECK ADD  CONSTRAINT [FK_1va36crdlypeq9fupl6kj77qw] FOREIGN KEY([product_id])
REFERENCES [dbo].[ProductInstance] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity_ProductInstance] CHECK CONSTRAINT [FK_1va36crdlypeq9fupl6kj77qw]
GO
/****** Object:  ForeignKey [FK_22fgssssvwa9okc8pfwxxc6tf]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity_ProductInstance]  WITH CHECK ADD  CONSTRAINT [FK_22fgssssvwa9okc8pfwxxc6tf] FOREIGN KEY([procedure_activity_id])
REFERENCES [dbo].[ProcedureActivity] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity_ProductInstance] CHECK CONSTRAINT [FK_22fgssssvwa9okc8pfwxxc6tf]
GO
/****** Object:  ForeignKey [FK_o5wm8eq6q42iwcve82y7yffit]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity_Performer]  WITH CHECK ADD  CONSTRAINT [FK_o5wm8eq6q42iwcve82y7yffit] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity_Performer] CHECK CONSTRAINT [FK_o5wm8eq6q42iwcve82y7yffit]
GO
/****** Object:  ForeignKey [FK_rixwbhdu058kutbjd4f4wdbm0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity_Performer]  WITH CHECK ADD  CONSTRAINT [FK_rixwbhdu058kutbjd4f4wdbm0] FOREIGN KEY([procedure_activity_id])
REFERENCES [dbo].[ProcedureActivity] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity_Performer] CHECK CONSTRAINT [FK_rixwbhdu058kutbjd4f4wdbm0]
GO
/****** Object:  ForeignKey [FK_5x0js6onhvmp4hkb688odiwr0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity_Indication]  WITH CHECK ADD  CONSTRAINT [FK_5x0js6onhvmp4hkb688odiwr0] FOREIGN KEY([procedure_activity_id])
REFERENCES [dbo].[ProcedureActivity] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity_Indication] CHECK CONSTRAINT [FK_5x0js6onhvmp4hkb688odiwr0]
GO
/****** Object:  ForeignKey [FK_k1lgb429a2hogprl8mtteccg0]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity_Indication]  WITH CHECK ADD  CONSTRAINT [FK_k1lgb429a2hogprl8mtteccg0] FOREIGN KEY([indication_id])
REFERENCES [dbo].[Indication] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity_Indication] CHECK CONSTRAINT [FK_k1lgb429a2hogprl8mtteccg0]
GO
/****** Object:  ForeignKey [FK_aaoe4992iet9lnr3istb56jri]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity_DeliveryLocation]  WITH CHECK ADD  CONSTRAINT [FK_aaoe4992iet9lnr3istb56jri] FOREIGN KEY([procedure_activity_id])
REFERENCES [dbo].[ProcedureActivity] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity_DeliveryLocation] CHECK CONSTRAINT [FK_aaoe4992iet9lnr3istb56jri]
GO
/****** Object:  ForeignKey [FK_si0nl0doh36vtct5spq0yuuk5]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[ProcedureActivity_DeliveryLocation]  WITH CHECK ADD  CONSTRAINT [FK_si0nl0doh36vtct5spq0yuuk5] FOREIGN KEY([location_id])
REFERENCES [dbo].[DeliveryLocation] ([id])
GO
ALTER TABLE [dbo].[ProcedureActivity_DeliveryLocation] CHECK CONSTRAINT [FK_si0nl0doh36vtct5spq0yuuk5]
GO
/****** Object:  ForeignKey [FK_f0uyjoe9i93pb0wq3hkedwhp8]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Procedure_ActivityProcedure]  WITH CHECK ADD  CONSTRAINT [FK_f0uyjoe9i93pb0wq3hkedwhp8] FOREIGN KEY([procedure_id])
REFERENCES [dbo].[ResidentProcedure] ([id])
GO
ALTER TABLE [dbo].[Procedure_ActivityProcedure] CHECK CONSTRAINT [FK_f0uyjoe9i93pb0wq3hkedwhp8]
GO
/****** Object:  ForeignKey [FK_tbalv7m32ae6wp5e8csmlb67l]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Procedure_ActivityProcedure]  WITH CHECK ADD  CONSTRAINT [FK_tbalv7m32ae6wp5e8csmlb67l] FOREIGN KEY([procedure_activity_id])
REFERENCES [dbo].[ProcedureActivity] ([id])
GO
ALTER TABLE [dbo].[Procedure_ActivityProcedure] CHECK CONSTRAINT [FK_tbalv7m32ae6wp5e8csmlb67l]
GO
/****** Object:  ForeignKey [FK_4oebu3l9emlnerl45qww8f157]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Procedure_ActivityObservation]  WITH CHECK ADD  CONSTRAINT [FK_4oebu3l9emlnerl45qww8f157] FOREIGN KEY([procedure_id])
REFERENCES [dbo].[ResidentProcedure] ([id])
GO
ALTER TABLE [dbo].[Procedure_ActivityObservation] CHECK CONSTRAINT [FK_4oebu3l9emlnerl45qww8f157]
GO
/****** Object:  ForeignKey [FK_go8yrtxr1uu19pxrkp0ie7334]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Procedure_ActivityObservation]  WITH CHECK ADD  CONSTRAINT [FK_go8yrtxr1uu19pxrkp0ie7334] FOREIGN KEY([procedure_observation_id])
REFERENCES [dbo].[ProcedureActivity] ([id])
GO
ALTER TABLE [dbo].[Procedure_ActivityObservation] CHECK CONSTRAINT [FK_go8yrtxr1uu19pxrkp0ie7334]
GO
/****** Object:  ForeignKey [FK_6qabhdme7w90rk33xrvp2pgm2]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Procedure_ActivityAct]  WITH CHECK ADD  CONSTRAINT [FK_6qabhdme7w90rk33xrvp2pgm2] FOREIGN KEY([procedure_act_id])
REFERENCES [dbo].[ProcedureActivity] ([id])
GO
ALTER TABLE [dbo].[Procedure_ActivityAct] CHECK CONSTRAINT [FK_6qabhdme7w90rk33xrvp2pgm2]
GO
/****** Object:  ForeignKey [FK_n0aqsc2yxj4yrx848urjbca13]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Procedure_ActivityAct]  WITH CHECK ADD  CONSTRAINT [FK_n0aqsc2yxj4yrx848urjbca13] FOREIGN KEY([procedure_id])
REFERENCES [dbo].[ResidentProcedure] ([id])
GO
ALTER TABLE [dbo].[Procedure_ActivityAct] CHECK CONSTRAINT [FK_n0aqsc2yxj4yrx848urjbca13]
GO
/****** Object:  ForeignKey [FK_37ntpjeyorlluwnkxb251t5ar]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization_MedicationPrecondition]  WITH CHECK ADD  CONSTRAINT [FK_37ntpjeyorlluwnkxb251t5ar] FOREIGN KEY([precondition_id])
REFERENCES [dbo].[MedicationPrecondition] ([id])
GO
ALTER TABLE [dbo].[Immunization_MedicationPrecondition] CHECK CONSTRAINT [FK_37ntpjeyorlluwnkxb251t5ar]
GO
/****** Object:  ForeignKey [FK_85ijs1m8qrvnekdyiv76a0v7k]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization_MedicationPrecondition]  WITH CHECK ADD  CONSTRAINT [FK_85ijs1m8qrvnekdyiv76a0v7k] FOREIGN KEY([immunization_id])
REFERENCES [dbo].[Immunization] ([id])
GO
ALTER TABLE [dbo].[Immunization_MedicationPrecondition] CHECK CONSTRAINT [FK_85ijs1m8qrvnekdyiv76a0v7k]
GO
/****** Object:  ForeignKey [FK_ps836nuua6nrvjsohsm2hesg1]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization_Indication]  WITH CHECK ADD  CONSTRAINT [FK_ps836nuua6nrvjsohsm2hesg1] FOREIGN KEY([indication_id])
REFERENCES [dbo].[Indication] ([id])
GO
ALTER TABLE [dbo].[Immunization_Indication] CHECK CONSTRAINT [FK_ps836nuua6nrvjsohsm2hesg1]
GO
/****** Object:  ForeignKey [FK_sfdejeo779jd9mfbmx1v8njb]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization_Indication]  WITH CHECK ADD  CONSTRAINT [FK_sfdejeo779jd9mfbmx1v8njb] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Immunization] ([id])
GO
ALTER TABLE [dbo].[Immunization_Indication] CHECK CONSTRAINT [FK_sfdejeo779jd9mfbmx1v8njb]
GO
/****** Object:  ForeignKey [FK_42h6aqwh6cj1qqo18mrlrhpkl]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization_DrugVehicle]  WITH CHECK ADD  CONSTRAINT [FK_42h6aqwh6cj1qqo18mrlrhpkl] FOREIGN KEY([immunization_id])
REFERENCES [dbo].[Immunization] ([id])
GO
ALTER TABLE [dbo].[Immunization_DrugVehicle] CHECK CONSTRAINT [FK_42h6aqwh6cj1qqo18mrlrhpkl]
GO
/****** Object:  ForeignKey [FK_7bc33fsufenhhpf61ggecgg8o]    Script Date: 01/08/2014 11:35:57 ******/
ALTER TABLE [dbo].[Immunization_DrugVehicle]  WITH CHECK ADD  CONSTRAINT [FK_7bc33fsufenhhpf61ggecgg8o] FOREIGN KEY([drug_vehicle_id])
REFERENCES [dbo].[DrugVehicle] ([id])
GO
ALTER TABLE [dbo].[Immunization_DrugVehicle] CHECK CONSTRAINT [FK_7bc33fsufenhhpf61ggecgg8o]
GO
