IF COL_LENGTH('resident_enc', 'maiden_name') IS NOT NULL
    BEGIN
        alter table resident_enc
            drop column maiden_name;
    END
GO

alter table resident_enc
    add maiden_name varbinary(max)
GO

delete from ResidentEncryptedColumns where column_name = 'maiden_name'
GO

insert into ResidentEncryptedColumns (column_name, column_type) values ('maiden_name', 'VARCHAR(256)')
GO

exec resident_table_modified
go
