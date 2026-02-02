alter table ProcedureActivity_ProductInstances DROP PK__Procedur__<some value>;

SELECT CONSTRAINT_NAME FROM information_schema.table_constraints AS kcu
where kcu.table_name='ProcedureActivity_ProductInstances' and constraint_type = 'PRIMARY KEY'