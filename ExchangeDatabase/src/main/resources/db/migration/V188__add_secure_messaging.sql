alter table dbo.Employee add secure_email varchar(100);
GO

UPDATE e1
   SET e1.[secure_email] =
      case when e1.care_team_role_id is null and NULLIF(e1.login, '') is not null and SystemSetup.login_company_id is not null
          then e1.login + '.' + LOWER(SystemSetup.login_company_id) + '@direct.eldermarkexchange.com'
          else null
      end
from dbo.Employee e1
inner join dbo.SystemSetup on SystemSetup.database_id = e1.database_id
GO

ALTER TABLE dbo.Employee ADD secure_email_active BIT DEFAULT 0 NOT NULL