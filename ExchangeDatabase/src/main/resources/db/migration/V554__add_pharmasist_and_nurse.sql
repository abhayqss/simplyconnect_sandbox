update CareTeamRole
set position = position + 3
where position > 8 or code = 'ROLE_NOTIFY_USER'

insert into CareTeamRole (name, code, position, display_name)
values
  ('Pharmacist', 'ROLE_PHARMACIST', 9, 'Provider'),
  ('Nurse', 'ROLE_NURSE', 10, 'Provider')
