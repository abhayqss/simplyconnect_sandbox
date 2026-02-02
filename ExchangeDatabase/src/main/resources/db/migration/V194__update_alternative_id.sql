UPDATE d
   SET d.alternative_id = s.login_company_id
from dbo.SourceDatabase d
inner join dbo.SystemSetup s on s.database_id = d.id
where d.alternative_id is null
GO