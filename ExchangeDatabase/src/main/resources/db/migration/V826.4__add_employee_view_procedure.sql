if object_id('update_employee_view') is not null
    drop procedure update_employee_view
go

if object_id('EmployeeViewCustomColumns') is not null
    drop table EmployeeViewCustomColumns
go

create table EmployeeViewCustomColumns
(
    column_name   nvarchar(max) not null,
    column_select nvarchar(max) not null,
    column_insert nvarchar(max),
    column_update nvarchar(max)
)
go

-- add all previous columns just for order
insert into EmployeeViewCustomColumns(column_name, column_select, column_insert, column_update)
values ('id', '[id]', null, null),
       ('inactive', '[inactive]', '[inactive]', 'i.[inactive]'),
       ('legacy_id', '[legacy_id]', '[legacy_id]', 'i.[legacy_id]'),
       ('password', '[password]', '[password]', 'i.[password]'),
       ('database_id', '[database_id]', '[database_id]', 'i.[database_id]'),
       ('person_id', '[person_id]', '[person_id]', 'i.[person_id]'),
       ('care_team_role_id', '[care_team_role_id]', '[care_team_role_id]', 'i.[care_team_role_id]'),
       ('created_automatically', '[created_automatically]', '[created_automatically]', 'i.[created_automatically]'),
       ('secure_email_active', '[secure_email_active]', 'isnull([secure_email_active], 0)', 'isnull(i.[secure_email_active], 0)'),
       ('secure_email_error', '[secure_email_error]', '[secure_email_error]', 'i.[secure_email_error]'),
       ('modified_timestamp', '[modified_timestamp]', 'isnull([modified_timestamp], 0)', 'isnull(i.[modified_timestamp], 0)'),
       ('contact_4d', '[contact_4d]', 'isnull([contact_4d], 0)', 'isnull(i.[contact_4d], 0)'),
       ('ccn_community_id', '[ccn_community_id]', '[ccn_community_id]', 'i.[ccn_community_id]'),
       ('first_name', 'convert(nvarchar(256), DecryptByKey([first_name]))', 'EncryptByKey(Key_GUID(''SymmetricKey1''), [first_name])', 'EncryptByKey(Key_GUID(''SymmetricKey1''), i.[first_name])'),
       ('first_name_hash', '[first_name_hash]', '[dbo].[hash_string]([first_name], 150)', '[dbo].[hash_string](i.[first_name], 150)'),
       ('last_name', 'convert(nvarchar(256), DecryptByKey([last_name]))', 'EncryptByKey(Key_GUID(''SymmetricKey1''), [last_name])', 'EncryptByKey(Key_GUID(''SymmetricKey1''), i.[last_name])'),
       ('last_name_hash', '[last_name_hash]', '[dbo].[hash_string]([last_name], 150)', '[dbo].[hash_string](i.[last_name], 150)'),
       ('login', 'convert(nvarchar(256), DecryptByKey([login]))', 'EncryptByKey(Key_GUID(''SymmetricKey1''), [login])', 'EncryptByKey(Key_GUID(''SymmetricKey1''), i.[login])'),
       ('login_hash', '[login_hash]', '[dbo].[hash_string]([login], 150)', '[dbo].[hash_string](i.[login], 150)'),
       ('secure_email', 'convert(varchar(256), DecryptByKey([secure_email]))', 'EncryptByKey(Key_GUID(''SymmetricKey1''), [secure_email])', 'EncryptByKey(Key_GUID(''SymmetricKey1''), i.[secure_email])'),
       ('ccn_company', 'convert(varchar(256), DecryptByKey([ccn_company]))', 'EncryptByKey(Key_GUID(''SymmetricKey1''), [ccn_company])', 'EncryptByKey(Key_GUID(''SymmetricKey1''), i.[ccn_company])'),
       ('qa_incident_reports', '[qa_incident_reports]', '[qa_incident_reports]', 'i.[qa_incident_reports]'),
       ('creator_id', '[creator_id]', '[creator_id]', 'i.[creator_id]'),
       ('labs_coordinator', '[labs_coordinator]', 'isnull([labs_coordinator], 0)', 'isnull(i.[labs_coordinator], 0)'),
       ('is_incident_report_reviewer', '[is_incident_report_reviewer]', 'isnull([is_incident_report_reviewer], 0)', 'isnull(i.[is_incident_report_reviewer], 0)'),
       ('twilio_user_sid', '[twilio_user_sid]', '[twilio_user_sid]', 'i.[twilio_user_sid]'),
       ('twilio_service_conversation_sid', '[twilio_service_conversation_sid]', '[twilio_service_conversation_sid]', 'i.[twilio_service_conversation_sid]'),
       ('is_community_address_used', '[is_community_address_used]', 'isnull([is_community_address_used], 0)', 'isnull(i.[is_community_address_used], 0)'),
       ('is_auto_status_changed', '[is_auto_status_changed]', 'isnull([is_auto_status_changed], 0)', 'isnull(i.[is_auto_status_changed], 0)'),
       ('deactivate_datetime', '[deactivate_datetime]', '[deactivate_datetime]', 'i.[deactivate_datetime]'),
       ('manual_activation_datetime', '[manual_activation_datetime]', '[manual_activation_datetime]', 'i.[manual_activation_datetime]'),
       ('last_session_datetime', '[last_session_datetime]', '[last_session_datetime]', 'i.[last_session_datetime]')
go

create procedure update_employee_view
as
begin
    set nocount on

    declare @createViewSql nvarchar(max);

    declare @missingColumns table (
        column_name   nvarchar(max) not null,
        column_select nvarchar(max) not null,
        column_insert nvarchar(max),
        column_update nvarchar(max)
    )

    insert into @missingColumns
    select column_name               [column_name],
           '[' + column_name + ']'   [column_select],
           '[' + column_name + ']'   [column_insert],
           'i.[' + column_name + ']' [column_update]
    from information_schema.columns
    where table_name = 'Employee_enc'
      and column_name not in
          (select custom_columns.column_name from EmployeeViewCustomColumns custom_columns)

    select @createViewSql = coalesce(
                @createViewSql + ',' + CHAR(13) + CHAR(10) + '  ' + column_select + ' [' + column_name + ']',
                CHAR(13) + CHAR(10) + '  ' + column_select + ' [' + column_name + ']'
        )
    from (select column_name, column_select
          from EmployeeViewCustomColumns c
          union all
          select column_name, column_select
          from @missingColumns) columns

    set @createViewSql = N'create view Employee' + CHAR(13) + CHAR(10) +
                         'as' + CHAR(13) + CHAR(10) +
                         'select ' + @createViewSql + CHAR(13) + CHAR(10) +
                         'from Employee_enc'

    declare @createViewInsertTriggerSqlFieldList nvarchar(max)
    select @createViewInsertTriggerSqlFieldList = coalesce(
                @createViewInsertTriggerSqlFieldList + ',' + CHAR(13) + CHAR(10) + '  [' + column_name + ']',
                CHAR(13) + CHAR(10) + '  [' + column_name + ']'
        )
    from (select column_name
          from EmployeeViewCustomColumns c
          where column_insert is not null
          union all
          select column_name
          from @missingColumns) columns

    declare @createViewInsertTriggerSqlFieldValuesList nvarchar(max)
    select @createViewInsertTriggerSqlFieldValuesList = coalesce(
                @createViewInsertTriggerSqlFieldValuesList + ',' + CHAR(13) + CHAR(10) + ' ' + column_insert + ' [' +
                column_name + ']',
                CHAR(13) + CHAR(10) + ' ' + column_insert + ' [' + column_name + ']'
        )
    from (select column_name, column_insert
          from EmployeeViewCustomColumns c
          where column_insert is not null
          union all
          select column_name, column_insert
          from @missingColumns) columns

    declare @createViewInsertTriggerSql nvarchar(max)
    set @createViewInsertTriggerSql = 'create trigger EmployeeInsert' + CHAR(13) + CHAR(10) +
                                      'on Employee' + CHAR(13) + CHAR(10) +
                                      'instead of insert' + CHAR(13) + CHAR(10) +
                                      'as' + CHAR(13) + CHAR(10) +
                                      'begin' + CHAR(13) + CHAR(10) +
                                      'insert into Employee_enc(' +
                                      @createViewInsertTriggerSqlFieldList + CHAR(13) + CHAR(10) +
                                      ')' + CHAR(13) + CHAR(10) +
                                      'select ' + @createViewInsertTriggerSqlFieldValuesList + CHAR(13) + CHAR(10) +
                                      'from inserted select @@IDENTITY' + CHAR(13) + CHAR(10) +
                                      'end'


    declare @createViewUpdateTriggerSql nvarchar(max)
    select @createViewUpdateTriggerSql = coalesce(
                @createViewUpdateTriggerSql + ',' + CHAR(13) + CHAR(10) + '  [' + column_name + '] = ' + column_update,
                CHAR(13) + CHAR(10) + '  [' + column_name + '] = ' + column_update
        )
    from (select column_name, column_update
          from EmployeeViewCustomColumns c
          where column_update is not null
          union all
          select column_name, column_update
          from @missingColumns) columns

    set @createViewUpdateTriggerSql = 'create trigger EmployeeUpdate' + CHAR(13) + CHAR(10) +
                                      'on Employee' + CHAR(13) + CHAR(10) +
                                      'instead of update' + CHAR(13) + CHAR(10) +
                                      'as' + CHAR(13) + CHAR(10) +
                                      'begin' + CHAR(13) + CHAR(10) +
                                      'update Employee_enc' + CHAR(13) + CHAR(10) +
                                      'set ' + @createViewUpdateTriggerSql + CHAR(13) + CHAR(10) +
                                      'from inserted i' + CHAR(13) + CHAR(10) +
                                      'where Employee_enc.id = i.id' + CHAR(13) + CHAR(10) +
                                      'end'

    if object_id('Employee') is not null
        drop view Employee

    exec (@createViewSql)
    exec (@createViewInsertTriggerSql)
    exec (@createViewUpdateTriggerSql)
end
go
