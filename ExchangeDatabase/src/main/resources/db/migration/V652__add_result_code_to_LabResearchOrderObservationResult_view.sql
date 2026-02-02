IF (OBJECT_ID('LabResearchOrderObservationResult') IS NOT NULL)
  DROP VIEW [dbo].[LabResearchOrderObservationResult]
GO


CREATE VIEW [dbo].[LabResearchOrderObservationResult]
AS
  SELECT
    obx_5.id                               AS id,
    orderOru.lab_research_order_id         AS lab_research_order_id,
    obx_3.identifier                       AS code,
    obx_3.text                             AS name,
    obx_5.obsv_value                       AS value,
    obx_6.text                             AS units_text,
    obx.references_range                   AS limits,
    STUFF
    (
      (
        SELECT DISTINCT ', ' + ISNULL(t.value, v.raw_code)
        FROM OBX_IS_abnormal_flags obx_8
          JOIN IS_CodedValueForUserDefinedTables v ON obx_8.abnormal_flag_id = v.id
          LEFT JOIN HL7CodeTable t ON v.hl7_user_defined_code_table_id = t.id
        WHERE obx_8.obx_id = obx.id
        ORDER BY ', ' + ISNULL(t.value, v.raw_code)
        FOR XML PATH ('')
      ), 1, 2, ''
    )                                      AS abnormal_flags,
    obx_3.name_of_coding_system            AS observation_source,
    obx.datetime_of_observation            AS datetime_of_observation,
    obx_23.organization_name               AS performing_org_name,
    obx.performing_org_addr_id             AS performing_org_addr_id,
    obx.performing_org_medical_director_id AS performing_org_medical_director_id

  FROM LabResearchOrderORU orderOru
       JOIN ORU_R01_OBX oru_obx ON oru_obx.oru_id = orderOru.oru_id
       JOIN OBX_Observation_Result obx ON obx.id = oru_obx.obx_id
       JOIN OBX_Observation_Result_value obx_5 ON obx.id = obx_5.obx_id
       LEFT JOIN CE_CodedElement obx_3 ON obx.obsv_identifier_id = obx_3.id
       LEFT JOIN CE_CodedElement obx_6 ON obx.units_id = obx_6.id
       LEFT JOIN XON_ExtendedCompositeNameAndIdForOrganizations obx_23 ON obx_23.id = obx.performing_org_name_id
  WHERE obx.value_type = 'ST'
GO