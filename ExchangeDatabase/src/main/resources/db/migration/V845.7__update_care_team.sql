update rctm
set on_hold = 1
from ResidentCareTeamMember rctm
         inner join CareTeamMember ctm on rctm.id = ctm.id
         inner join resident r on rctm.resident_id = r.id
         inner join Employee e on ctm.employee_id = e.id
         inner join CareTeamRole ctr on e.care_team_role_id = ctr.id
where r.hie_consent_policy_type = 'OPT_OUT'
  and (r.facility_id <> e.ccn_community_id
    or ctr.code in ('ROLE_PARENT_GUARDIAN', 'ROLE_PERSON_RECEIVING_SERVICES'))
