insert into Assessment_SourceDatabase_Disabled (assessment_id, database_id)
select a.id, sd.id
from (select 'LSSI' org_alt_id, id from Assessment where code <> 'CARE_MGMT') a (org_alt_id, id)
         join SourceDatabase sd on sd.alternative_id = a.org_alt_id;
