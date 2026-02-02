
SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[DiagnosisCcdCode] (
	[id] [bigint] NOT NULL,
	[diagnosis_setup_id] [bigint],
	code VARCHAR (25) NOT NULL,
	code_system VARCHAR (40) NOT NULL,
	display_name [varchar] (MAX),
	code_system_name VARCHAR (255)
	PRIMARY KEY ([id]),
	CONSTRAINT [FK_DiagnosisCcdCode_Parent] FOREIGN KEY ([id]) REFERENCES [dbo].[AnyCcdCode] ([id]) ON DELETE CASCADE,
	-- Ensure data integrity in case if DiagnosisSetup record is deleted after sync
	CONSTRAINT [FK_DiagnosisCcdCode_Original] FOREIGN KEY ([diagnosis_setup_id]) REFERENCES [dbo].[DiagnosisSetup] ([id]) ON DELETE SET NULL
);
GO

IF OBJECT_ID ('CcdCode') IS NOT NULL
	DROP VIEW CcdCode;
GO

CREATE VIEW CcdCode
AS
	SELECT
		acc.id,
		ccc.value_set_name,
		ccc.value_set,
		ccc.code,
		ccc.code_system,
		ccc.display_name,
		ccc.inactive,
		ccc.code_system_name
	FROM AnyCcdCode acc
		INNER JOIN ConcreteCcdCode ccc ON acc.id = ccc.id
	UNION
	SELECT
		acc.id,
		NULL AS value_set_name,
		NULL AS value_set,
		ucc.code,
		ucc.code_system,
		ucc.display_name,
		0 AS inactive,
		ucc.code_system_name
	FROM AnyCcdCode acc
		INNER JOIN UnknownCcdCode ucc ON acc.id = ucc.id
	UNION
	SELECT
		acc.id,
		occ.value_set_name,
		occ.value_set,
		occ.code,
		occ.code_system,
		icc.display_name,
		occ.inactive,
		occ.code_system_name
	FROM AnyCcdCode acc
		INNER JOIN InterpretiveCcdCode icc ON acc.id = icc.id
		INNER JOIN ConcreteCcdCode occ ON icc.referred_ccd_code = occ.id
	UNION
	SELECT
		acc.id,
		NULL AS value_set_name,
		NULL AS value_set,
		COALESCE(diagnosis.code, dcc.code),
		dcc.code_system,
		COALESCE(diagnosis.name, dcc.display_name),
		diagnosis.inactive,
		dcc.code_system_name
	FROM AnyCcdCode acc
		INNER JOIN DiagnosisCcdCode dcc ON acc.id = dcc.id
		LEFT OUTER JOIN DiagnosisSetup diagnosis ON dcc.diagnosis_setup_id = diagnosis.id;
GO
