
CREATE TABLE [dbo].[DiagnosisSetup](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NULL,
	[database_id] [bigint] NULL,
	[name] [varchar](200) NULL,
	[code] [varchar](70) NULL,
	[icd9cm] [varchar](70) NULL,
	[icd10cm] [varchar](70) NULL,
	[icd10pcs] [varchar](70) NULL,
	[isManual] [bit] NULL,
	[isStandardCode] [bit] NULL,
	[inactive] [bit] NULL,
 CONSTRAINT [PK_DiagnosisSetup] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
