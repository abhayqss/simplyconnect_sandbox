DECLARE @insert TABLE(
program_sub_type_code    varchar(255),
service_code             varchar(255)
)

INSERT INTO @insert (program_sub_type_code, service_code) VALUES
('EMOTIONAL_SUPPORT_HOTLINE', 'Crisis_Intervention_8'),
('EMOTIONAL_SUPPORT_HOTLINE', 'Crisis_Intervention_13'),
('EMOTIONAL_SUPPORT_HOTLINE', 'Crisis_Services_14'),
('EAF_AND_GAF', 'Energy_Assistance_14'),
('ENERGY_REBATES', 'Energy_Assistance_14'),
('FUEL', 'Energy_Assistance_14'),
('UTILITIES_ELECTRIC_GAS', 'Energy_Assistance_14')

IF (SELECT COUNT (*)
  FROM @insert i
  LEFT JOIN ProgramSubType pst ON i.program_sub_type_code = pst.code
  WHERE pst.id IS NULL) > 0
RAISERROR ('Program sub type code not found', 15, 1);

IF (SELECT COUNT (*)
  FROM @insert i
  LEFT JOIN ServicesTreatmentApproach sta ON i.service_code = sta.code
  WHERE sta.id IS NULL) > 0
RAISERROR ('Services code not found', 15, 1);

WITH cte AS (
  SELECT
    pst.id                                                                  AS pst_id,
    sta.id                                                                  AS sta_id
  FROM @insert i
  JOIN ProgramSubType pst ON i.program_sub_type_code = pst.code
  JOIN ServicesTreatmentApproach sta ON i.service_code = sta.code
)
merge INTO ProgramSubType_ServicesTreatmentApproach ps
USING cte
ON 1 <> 1
WHEN NOT matched THEN INSERT (program_sub_type_id, service_id) VALUES (pst_id, sta_id);
GO