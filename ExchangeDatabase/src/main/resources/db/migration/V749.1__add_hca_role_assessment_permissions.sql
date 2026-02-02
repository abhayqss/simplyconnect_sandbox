insert into AssessmentPermission (role, assessment_id)
select 'ROLE_HCA', (select a.id from Assessment a where code = 'CARE_MGMT')
