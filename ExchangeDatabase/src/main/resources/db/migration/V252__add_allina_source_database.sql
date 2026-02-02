SET XACT_ABORT ON
GO

DECLARE @allinaDataSource table(id bigint);

INSERT INTO [dbo].[SourceDatabase] (
[alternative_id], [name], [url], [is_service], [name_and_port], [is_eldermark]
)
OUTPUT INSERTED.id
INTO @allinaDataSource
VALUES (
'AllinaHealth', 'Allina Health', 'Allina_url', 0, 'AllinaHealth' , 0
);

INSERT INTO [dbo].[Organization]
([legacy_id]
  ,[legacy_table]
  ,[name]
  ,[database_id]
  ,[testing_training]
  ,[inactive]
  ,[module_hie]
  ,[module_cloud_storage]
  ,[oid]
  ,[created_automatically])
VALUES
  ('2'
    , 'Company'
    , 'Allina Health'
    , (SELECT id FROM @allinaDataSource)
    , 0
    , 0
    , 1
    , 0
    , null
    , null)
GO