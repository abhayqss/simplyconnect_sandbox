DECLARE @sql NVARCHAR(MAX);

SELECT @sql = N'ALTER TABLE dbo.Procedure_ActivityProcedure DROP CONSTRAINT '
  + QUOTENAME(k.name) + ';'
    FROM sys.key_constraints k
INNER JOIN sys.objects AS o
ON k.parent_object_id = o.[object_id]
INNER JOIN sys.schemas AS s
ON o.[schema_id] = s.[schema_id]
WHERE o.name = N'Procedure_ActivityProcedure'
AND k.type_desc = 'PRIMARY_KEY_CONSTRAINT'
AND s.name = N'dbo';

PRINT @sql;
EXEC sp_executesql @sql; 

GO

DECLARE @sql NVARCHAR(MAX);

SELECT @sql = N'ALTER TABLE dbo.ProcedureActivity_DeliveryLocation DROP CONSTRAINT '
  + QUOTENAME(k.name) + ';'
    FROM sys.key_constraints k
INNER JOIN sys.objects AS o
ON k.parent_object_id = o.[object_id]
INNER JOIN sys.schemas AS s
ON o.[schema_id] = s.[schema_id]
WHERE o.name = N'ProcedureActivity_DeliveryLocation'
AND k.type_desc = 'PRIMARY_KEY_CONSTRAINT'
AND s.name = N'dbo';

PRINT @sql;
EXEC sp_executesql @sql; 

GO

DECLARE @sql NVARCHAR(MAX);

SELECT @sql = N'ALTER TABLE dbo.ProcedureActivity_Indication DROP CONSTRAINT '
  + QUOTENAME(k.name) + ';'
    FROM sys.key_constraints k
INNER JOIN sys.objects AS o
ON k.parent_object_id = o.[object_id]
INNER JOIN sys.schemas AS s
ON o.[schema_id] = s.[schema_id]
WHERE o.name = N'ProcedureActivity_Indication'
AND k.type_desc = 'PRIMARY_KEY_CONSTRAINT'
AND s.name = N'dbo';

PRINT @sql;
EXEC sp_executesql @sql; 

GO

DECLARE @sql NVARCHAR(MAX);

SELECT @sql = N'ALTER TABLE dbo.ProcedureActivity_Performer DROP CONSTRAINT '
  + QUOTENAME(k.name) + ';'
    FROM sys.key_constraints k
INNER JOIN sys.objects AS o
ON k.parent_object_id = o.[object_id]
INNER JOIN sys.schemas AS s
ON o.[schema_id] = s.[schema_id]
WHERE o.name = N'ProcedureActivity_Performer'
AND k.type_desc = 'PRIMARY_KEY_CONSTRAINT'
AND s.name = N'dbo';

PRINT @sql;
EXEC sp_executesql @sql; 

GO

DECLARE @sql NVARCHAR(MAX);

SELECT @sql = N'ALTER TABLE dbo.ProcedureActivity_ProductInstance DROP CONSTRAINT '
  + QUOTENAME(k.name) + ';'
    FROM sys.key_constraints k
INNER JOIN sys.objects AS o
ON k.parent_object_id = o.[object_id]
INNER JOIN sys.schemas AS s
ON o.[schema_id] = s.[schema_id]
WHERE o.name = N'ProcedureActivity_ProductInstance'
AND k.type_desc = 'PRIMARY_KEY_CONSTRAINT'
AND s.name = N'dbo';

PRINT @sql;
EXEC sp_executesql @sql; 

GO

DECLARE @sql NVARCHAR(MAX);

SELECT @sql = N'ALTER TABLE dbo.Procedure_ActivityAct DROP CONSTRAINT '
  + QUOTENAME(k.name) + ';'
    FROM sys.key_constraints k
INNER JOIN sys.objects AS o
ON k.parent_object_id = o.[object_id]
INNER JOIN sys.schemas AS s
ON o.[schema_id] = s.[schema_id]
WHERE o.name = N'Procedure_ActivityAct'
AND k.type_desc = 'PRIMARY_KEY_CONSTRAINT'
AND s.name = N'dbo';

PRINT @sql;
EXEC sp_executesql @sql; 

GO

DECLARE @sql NVARCHAR(MAX);

SELECT @sql = N'ALTER TABLE dbo.Procedure_ActivityObservation DROP CONSTRAINT '
  + QUOTENAME(k.name) + ';'
    FROM sys.key_constraints k
INNER JOIN sys.objects AS o
ON k.parent_object_id = o.[object_id]
INNER JOIN sys.schemas AS s
ON o.[schema_id] = s.[schema_id]
WHERE o.name = N'Procedure_ActivityObservation'
AND k.type_desc = 'PRIMARY_KEY_CONSTRAINT'
AND s.name = N'dbo';

PRINT @sql;
EXEC sp_executesql @sql; 

GO

DECLARE @sql NVARCHAR(MAX);

SELECT @sql = N'ALTER TABLE dbo.Procedure_ActivityProcedure DROP CONSTRAINT '
  + QUOTENAME(k.name) + ';'
    FROM sys.key_constraints k
INNER JOIN sys.objects AS o
ON k.parent_object_id = o.[object_id]
INNER JOIN sys.schemas AS s
ON o.[schema_id] = s.[schema_id]
WHERE o.name = N'Procedure_ActivityProcedure'
AND k.type_desc = 'PRIMARY_KEY_CONSTRAINT'
AND s.name = N'dbo';

PRINT @sql;
EXEC sp_executesql @sql; 