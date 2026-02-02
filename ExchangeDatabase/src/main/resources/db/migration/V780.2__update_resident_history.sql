if (object_id('resident_table_modified') is not null)
    drop procedure resident_table_modified;
go


create procedure resident_table_modified as
begin
    exec update_resident_view
    exec update_resident_history_tables
    exec update_resident_history_view
    exec generate_resident_history_tables_triggers
end
go

exec resident_table_modified
go
