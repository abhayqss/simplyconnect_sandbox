declare @position int

select @position = position
from CareTeamRole
where code = 'ROLE_PHARMACIST'

update CareTeamRole
set position = position + 1
where position > @position

INSERT INTO [dbo].[CareTeamRole]
( [name]
, [code]
, [position]
, [display_name])
VALUES ( 'Pharmacy Technician'
       , 'ROLE_PHARMACY_TECHNICIAN'
       , @position + 1
       , 'Provider')
GO


insert into EventType_CareTeamRole_Xref (event_type_id, care_team_role_id, responsibility)
select event_type_id,
       (select id from CareTeamRole where code = 'ROLE_PHARMACY_TECHNICIAN'),
       responsibility
from EventType_CareTeamRole_Xref
where care_team_role_id = (select id from CareTeamRole where code = 'ROLE_PHARMACIST')
GO
