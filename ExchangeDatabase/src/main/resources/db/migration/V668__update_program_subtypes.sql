UPDATE ZCode
set code = 'ZSNAPC', description = 'Member currently enrolled in Supplemental Nutrition Assistance Program'
where code = 'ZSNAP'
INSERT INTO ZCode (code, description) VALUES
  ('ZSNAPN', 'Member newly enrolled in Supplemental Nutrition Assistance Program'),
  ('ZSNAPR', 'Member reenrolled in Supplemental Nutrition Assistance Program')


declare @programType bigint;
declare @subTypeId bigint;
select
  @subTypeId = id,
  @programType = program_type_id
from ProgramSubType
where code = 'SNAP_NUTRITION_PROGRAMS'

UPDATE ProgramSubType
set code = 'SNAP_NUTRITION_PROGRAMS_CURRENT', display_name = 'SNAP Nutrition Programs (member currently enrolled)'
where id = @subTypeId
INSERT INTO ProgramSubType (code, display_name, zcode_id, program_type_id)
VALUES
  ('SNAP_NUTRITION_PROGRAMS_NEWLY', 'SNAP Nutrition Programs (member newly enrolled)', (select id
                                                                                        from ZCode
                                                                                        where code = 'ZSNAPN'),
   @programType),
  ('SNAP_NUTRITION_PROGRAMS_REENROLLED', 'SNAP Nutrition Programs (member reenrolled)', (select id
                                                                                         from ZCode
                                                                                         where code = 'ZSNAPR'),
   @programType)


declare @newSubTypeId bigint;

select @newSubTypeId = id
from ProgramSubType
where code = 'SNAP_NUTRITION_PROGRAMS_NEWLY'
INSERT INTO ProgramSubType_ServicesTreatmentApproach (program_sub_type_id, service_id)
  select
    @newSubTypeId,
    service_id
  from ProgramSubType_ServicesTreatmentApproach
  where program_sub_type_id = @subTypeId

select @newSubTypeId = id
from ProgramSubType
where code = 'SNAP_NUTRITION_PROGRAMS_REENROLLED'
INSERT INTO ProgramSubType_ServicesTreatmentApproach (program_sub_type_id, service_id)
  select
    @newSubTypeId,
    service_id
  from ProgramSubType_ServicesTreatmentApproach
  where program_sub_type_id = @subTypeId

UPDATE SdohReportRowData
set icd_or_mbr_attribution_code = 'ZSNAPC'
where icd_or_mbr_attribution_code = 'ZSNAP'
