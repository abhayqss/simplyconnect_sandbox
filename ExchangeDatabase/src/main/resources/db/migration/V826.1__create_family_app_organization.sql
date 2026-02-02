if not exists(select 1
              from SourceDatabase
              where alternative_id = 'family')
    begin
        insert into SourceDatabase (alternative_id, name, is_eldermark, created_automatically, is_signature_enabled)
        values ('FAMILY', 'Family', 1, 1, 0)

        declare @database_id bigint
        select @database_id = id
        from SourceDatabase
        where alternative_id = 'FAMILY'

        insert into SystemSetup (database_id, login_company_id)
        values (@database_id, 'FAMILY')

        insert into Organization (legacy_id, legacy_table, name, database_id, module_hie, created_automatically)
        values ('temp', 'Company', 'Family', @database_id, 1, 1)

        update Organization
        set legacy_id = cast(id as varchar)
        where legacy_id = 'temp'
          and name = 'Family'
          and database_id = @database_id
    end
GO

delete
from DatabasePasswordSettings
where database_id = (select id from SourceDatabase where alternative_id = 'FAMILY')
GO

declare @databaseId bigint
select @databaseId = id
from SourceDatabase
where alternative_id = 'FAMILY';
insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value)
values
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT'), 1, 5),
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'ACCOUNT_LOCK_IN_MINUTES'), 1, 15),
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'ACCOUNT_RESET_FAILED_LOGON_COUNT_IN_MINUTES'), 1, 15),
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'COMPLEXITY_PASSWORD_LENGTH'), 1, 8),
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'COMPLEXITY_UPPERCASE_COUNT'), 1, 1),
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'COMPLEXITY_LOWERCASE_COUNT'), 1, 1),
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'COMPLEXITY_NON_ALPHANUMERIC_COUNT'), 1, 1),
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'COMPLEXITY_ARABIC_NUMERALS_COUNT'), 1, 1),
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'COMPLEXITY_LESS_SPACES_THAN'), 1, 1),
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'PASSWORD_MAXIMUM_AGE_IN_DAYS'), 1, 90),
    (@databaseId, (select ps.id from PasswordSettings ps where ps.name = 'COMPLEXITY_PASSWORD_HISTORY_COUNT'), 1, 8)
