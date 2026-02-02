create procedure update_history_table @tableName nvarchar(max),
                                      @historyTableName nvarchar(max),
                                      @idRefColumnName nvarchar(max)
as
begin
    set nocount on;

    declare @columns table (
        column_name nvarchar(max),
        column_type nvarchar(max)
    )

    insert into @columns
    values (@idRefColumnName, 'bigint'),
           ('updated_datetime', 'datetime2'),
           ('deleted_datetime', 'datetime2')

    insert into @columns
    select COLUMN_NAME,
           DATA_TYPE + iif(
               CHARACTER_MAXIMUM_LENGTH is null,
               '',
               iif(
                   CHARACTER_MAXIMUM_LENGTH = -1,
                   '(max)',
                   '(' + cast(CHARACTER_MAXIMUM_LENGTH as varchar) + ')'
               )
           )
    from INFORMATION_SCHEMA.COLUMNS
    where TABLE_NAME = @tableName and COLUMN_NAME not in ('id', 'updated_datetime', 'deleted_datetime')

    declare @missingColumns as table (
       column_name nvarchar(max),
       column_type nvarchar(max)
   )

    insert into @missingColumns
    select *
    from @columns
    where column_name not in (
        select COLUMN_NAME
        from INFORMATION_SCHEMA.COLUMNS
        where TABLE_NAME = @historyTableName
    )

    declare @sqlCommand nvarchar(max)
    select @sqlCommand = coalesce(@sqlCommand + ', ' + lines.line, lines.line)
    from (
        select column_name + ' ' + column_type as line
        from @missingColumns
    ) as lines
    set @sqlCommand = 'alter table ' + @historyTableName + ' add ' + @sqlCommand

    exec (@sqlCommand)
end
go

create procedure generate_history_table_triggers @tableName nvarchar(max),
                                                 @historyTableName nvarchar(max),
                                                 @idRefColumnName nvarchar(max),
                                                 @triggerCondition nvarchar(max) = ''
as
begin
    declare @clrf varchar(2) = char(13) + char(10)

    declare @data table (
        column_name  nvarchar(max),
        column_value nvarchar(max)
    )
    insert into @data
    select COLUMN_NAME, 'd.' + COLUMN_NAME
    from INFORMATION_SCHEMA.COLUMNS
    where TABLE_NAME = @tableName and COLUMN_NAME not in ('id', 'updated_datetime', 'deleted_datetime')

    insert into @data
    values (@idRefColumnName, 'd.id'),
           ('updated_datetime', 'getdate()'),
           ('deleted_datetime', 'null')

    declare @sqlColumns nvarchar(max)
    select @sqlColumns = coalesce(@sqlColumns + ',' + @clrf + '  ' + column_name, '  ' + column_name) from @data

    declare @sqlValues nvarchar(max)
    select @sqlValues = coalesce(@sqlValues + ',' + @clrf + '  ' + column_value, '  ' + column_value) from @data

    declare @insertSqlStatement nvarchar(max)
    set @insertSqlStatement = 'insert into ' + @historyTableName + '(' + @clrf +
                              @sqlColumns + ') ' + @clrf +
                              'select ' + @clrf +
                              @sqlValues + @clrf +
                              'from deleted as d ' + @clrf +
                              coalesce(@triggerCondition, '')

    if object_id(@tableName + '_UpdateHistoryTrigger') is not null
        exec ('drop trigger ' + @tableName + '_UpdateHistoryTrigger')
    if object_id(@tableName + '_DeleteHistoryTrigger') is not null
        exec ('drop trigger ' + @tableName + '_DeleteHistoryTrigger')

    exec ('create trigger ' + @tableName + '_UpdateHistoryTrigger ' + @clrf +
        'on ' + @tableName + ' after update as ' + @clrf +
        'begin ' + @clrf +
        'set nocount on ' + @clrf +
        @insertSqlStatement + @clrf +
        'end')

    set @triggerCondition = coalesce('  ' + @triggerCondition + @clrf, '')
    exec ('create trigger ' + @tableName + '_DeleteHistoryTrigger ' + @clrf +
        'on ' + @tableName + ' after delete as ' + @clrf +
        'begin ' + @clrf +
        'set nocount on ' + @clrf +
        'update ' + @historyTableName + @clrf +
        '  set deleted_datetime = getdate()' + @clrf +
        'where id in (' + @clrf +
        '  select max(h.id) from deleted as d ' + @clrf +
        '  inner join ' + @historyTableName + ' as h on h.' + @idRefColumnName + ' = d.id ' + @clrf +
        @triggerCondition +
        '  group by h.' + @idRefColumnName + @clrf +
        ') ' + @clrf +
        'end')
end
go

create procedure update_resident_history_tables as
begin
    exec update_history_table 'resident_enc', 'resident_enc_History', 'resident_id'
    exec update_history_table 'Person', 'Person_History', 'person_id'
    exec update_history_table 'PersonAddress_enc', 'PersonAddress_enc_History', 'person_address_id'
    exec update_history_table 'name_enc', 'name_enc_History', 'name_id'
    exec update_history_table 'PersonTelecom_enc', 'PersonTelecom_enc_History', 'person_telecom_id'
end
go

create procedure generate_resident_history_tables_triggers as
begin
    exec generate_history_table_triggers 'resident_enc', 'resident_enc_History', 'resident_id'

    declare @triggerCondition nvarchar(max) = 'inner join resident_enc as r on r.person_id = d.id'

    exec generate_history_table_triggers 'Person', 'Person_History', 'person_id', @triggerCondition

    set @triggerCondition = 'inner join resident_enc as r on r.person_id = d.person_id'

    exec generate_history_table_triggers 'PersonAddress_enc',
         'PersonAddress_enc_History',
         'person_address_id',
         @triggerCondition
    exec generate_history_table_triggers
         'name_enc',
         'name_enc_History',
         'name_id',
         @triggerCondition
    exec generate_history_table_triggers
         'PersonTelecom_enc',
         'PersonTelecom_enc_History',
         'person_telecom_id',
         @triggerCondition
end
go

alter table resident_enc_History
    drop constraint FK_resident_enc_History_person_history
alter table resident_enc_History
    drop column person_history_id
alter table PersonAddress_enc_History
    drop column person_history_id
alter table name_enc_History
    drop column person_history_id
alter table PersonTelecom_enc_History
    drop column person_history_id

exec update_resident_history_tables
exec generate_resident_history_tables_triggers
go

alter procedure update_resident_history_view
as
begin
    set nocount on

    if (object_id('resident_History') is not null)
        drop view resident_History

    declare @clrf varchar(2) = char(13) + char(10)

    --columns order is not important, just placing id and resident_id for viewing convenience
    declare @selectValues table(select_value nvarchar(max))
    insert into @selectValues values ('[id]'), ('[resident_id]')

    declare @missingColumns table(column_name nvarchar(max));
    insert into @missingColumns
    select COLUMN_NAME
    from INFORMATION_SCHEMA.COLUMNS
    where TABLE_NAME = 'resident_enc_History'
      and COLUMN_NAME not in ('id', 'resident_id');

    insert into @selectValues
    select '[' + column_name + ']'
    from @missingColumns as m
    where column_name not in (select column_name from ResidentEncryptedColumns)
      and column_name not in (select column_name from ResidentViewCustomColumns)

    insert into @selectValues
    select 'convert(' + e.column_type + ', DecryptByKey([' + e.column_name + '])) as [' + e.column_name + ']'
    from @missingColumns m inner join ResidentEncryptedColumns e on m.column_name = e.column_name

    insert into @selectValues
    select column_select + ' as [' + column_name + ']'
    from ResidentViewCustomColumns

    declare @selectValuesSql nvarchar(max);
    select @selectValuesSql = coalesce(@selectValuesSql + ',' + @clrf + '  ' + select_value, select_value)
    from @selectValues

    exec ('create view resident_History' + @clrf +
        'as select ' + @selectValuesSql + @clrf +
        'from resident_enc_History')
end
go

create index IX_resident_person_id
    on resident_enc(person_id)
go

create index IX_resident_history_updated_datetime_deleted_datetime
    on resident_enc_History(updated_datetime, deleted_datetime)
    include (resident_id)
go

create index IX_person_history_updated_datetime_deleted_datetime
    on Person_History(updated_datetime, deleted_datetime)
    include (person_id)
go

create index IX_name_history_updated_datetime_deleted_datetime
    on name_enc_History(updated_datetime, deleted_datetime)
    include (name_id)
go

create index IX_person_address_history_updated_datetime_deleted_datetime
    on PersonAddress_enc_History(updated_datetime, deleted_datetime)
    include (person_address_id)
go

create index IX_person_telecom_history_updated_datetime_deleted_datetime
    on PersonTelecom_enc_History(updated_datetime, deleted_datetime)
    include (person_telecom_id)
go

alter view name_History
as
select
    id,
    name_id,
    person_id,
    use_code,
    database_id,
    degree,
    convert(nvarchar(256), DecryptByKey(family))                  as family,
    lower(convert(nvarchar(256), DecryptByKey(family)))           as family_normalized,
    convert(varchar(30), DecryptByKey(family_qualifier))          as family_qualifier,
    convert(nvarchar(256), DecryptByKey(given))                   as given,
    lower(convert(nvarchar(256), DecryptByKey(given)))            as given_normalized,
    convert(varchar(30), DecryptByKey(given_qualifier))           as given_qualifier,
    convert(nvarchar(256), DecryptByKey(middle))                  as middle,
    lower(convert(nvarchar(256), DecryptByKey(middle)))           as middle_normalized,
    convert(varchar(30), DecryptByKey(middle_qualifier))          as middle_qualifier,
    convert(nvarchar(50), DecryptByKey(prefix))                   as prefix,
    convert(varchar(30), DecryptByKey(prefix_qualifier))          as prefix_qualifier,
    convert(nvarchar(50), DecryptByKey(suffix))                   as suffix,
    convert(varchar(30), DecryptByKey(suffix_qualifier))          as suffix_qualifier,
    convert(varchar(25), DecryptByKey(legacy_id))                 as legacy_id,
    convert(varchar(255), DecryptByKey(legacy_table))             as legacy_table,
    convert(varchar(256), DecryptByKey(call_me))                  as call_me,
    convert(varchar(100), DecryptByKey(name_representation_code)) as name_representation_code,
    convert(varchar(2000), DecryptByKey(full_name))               as full_name,
    family_hash,
    given_hash,
    middle_hash,
    updated_datetime,
    deleted_datetime
from name_enc_History;
go

drop trigger NameHistoryInsert
drop trigger NameHistoryUpdate
go

alter view PersonAddress_History
as
select id,
       person_address_id,
       person_id,
       database_id,
       legacy_id,
       legacy_table,
       convert(nvarchar(256), DecryptByKey([city]))           as city,
       convert(varchar(100), DecryptByKey([country]))         as country,
       convert(varchar(15), DecryptByKey([use_code]))         as use_code,
       convert(varchar(100), DecryptByKey([state]))           as state,
       convert(varchar(50), DecryptByKey([postal_code]))      as postal_code,
       convert(nvarchar(256), DecryptByKey([street_address])) as street_address,
       updated_datetime,
       deleted_datetime
from PersonAddress_enc_History
go

drop trigger PersonAddressHistoryInsert
drop trigger PersonAddressHistoryUpdate
go

alter view PersonTelecom_History
as
select id,
       person_telecom_id,
       person_id,
       sync_qualifier,
       database_id,
       legacy_id,
       legacy_table,
       convert(varchar(15), DecryptByKey(use_code))                         as use_code,
       convert(varchar(256), DecryptByKey(value))                           as value,
       value_normalized_hash,
       iif(convert(varchar(15), DecryptByKey(use_code)) = 'EMAIL',
           lower(convert(varchar(256), DecryptByKey(value))),
           dbo.normalize_phone(convert(varchar(256), DecryptByKey(value)))) as value_normalized,
       updated_datetime,
       deleted_datetime
from PersonTelecom_enc_History
go

drop trigger PersonTelecomHistoryInsert
drop trigger PersonTelecomHistoryUpdate
go

exec update_resident_history_view
go

update resident_enc_History set updated_datetime = last_updated
