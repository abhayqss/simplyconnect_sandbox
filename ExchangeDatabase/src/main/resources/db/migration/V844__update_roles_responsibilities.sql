UPDATE [dbo].[EventType_CareTeamRole_Xref]
SET [responsibility] = 'I'
WHERE [event_type_id] = (select et.id from EventType et where et.code = 'DS')
   or [event_type_id] = (select et.id from EventType et where et.code = 'COVID19')
   or [event_type_id] = (select et.id from EventType et where et.code = 'MEDS')
    AND ([care_team_role_id] = (SELECT r.id FROM CareTeamRole r WHERE code = 'ROLE_PARENT_GUARDIAN')
        OR [care_team_role_id] = (SELECT r.id FROM CareTeamRole r WHERE code = 'ROLE_PERSON_RECEIVING_SERVICES'))
GO

UPDATE [dbo].[EventType_CareTeamRole_Xref]
SET [responsibility] = 'N'
WHERE [event_type_id] <> (select et.id from EventType et where et.code = 'DS')
  and [event_type_id] <> (select et.id from EventType et where et.code = 'COVID19')
  and [event_type_id] <> (select et.id from EventType et where et.code = 'MEDS')
  AND ([care_team_role_id] = (SELECT r.id FROM CareTeamRole r WHERE code = 'ROLE_PARENT_GUARDIAN')
    or [care_team_role_id] = (SELECT r.id FROM CareTeamRole r WHERE code = 'ROLE_PERSON_RECEIVING_SERVICES'))
GO