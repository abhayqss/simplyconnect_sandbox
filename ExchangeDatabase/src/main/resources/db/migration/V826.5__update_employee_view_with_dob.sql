delete from EmployeeViewCustomColumns where column_name = 'birth_date'
go

insert into EmployeeViewCustomColumns(column_name, column_select, column_insert, column_update)
values ('birth_date',
        'convert(date, convert(varchar, DecryptByKey([birth_date])), 0)',
        'EncryptByKey(Key_GUID(''SymmetricKey1''), convert(varchar, [birth_date], 0))',
        'EncryptByKey(Key_GUID(''SymmetricKey1''), convert(varchar, i.[birth_date], 0))')
go

exec update_employee_view
