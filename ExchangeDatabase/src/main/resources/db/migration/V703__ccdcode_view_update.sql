IF OBJECT_ID('CcdCode') is not null
  drop view CcdCode
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE VIEW [dbo].[CcdCode]
  AS
    SELECT
           acc.id,
           ccc.value_set_name,
           ccc.value_set,
           ccc.code,
           ccc.code_system,
           ccc.display_name,
           ccc.inactive,
           ccc.code_system_name,
           0 as is_interpretation
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
           ucc.code_system_name,
           0 as is_interpretation
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
           occ.code_system_name,
           1 as is_interpretation
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
           dcc.code_system_name,
           0 as is_interpretation
    FROM AnyCcdCode acc
           INNER JOIN DiagnosisCcdCode dcc ON acc.id = dcc.id
           LEFT OUTER JOIN DiagnosisSetup diagnosis ON dcc.diagnosis_setup_id = diagnosis.id;

GO


