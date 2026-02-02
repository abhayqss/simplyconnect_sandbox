INSERT into PasswordSettings (name)
values ('COMPLEXITY_LESS_SPACES_THAN')
GO

insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value)
select database_id, (select id from PasswordSettings where name = 'COMPLEXITY_LESS_SPACES_THAN'), enabled, 1
from DatabasePasswordSettings
where password_settings_id = (select id from PasswordSettings where name = 'COMPLEXITY_PASSWORD_LENGTH')
GO
