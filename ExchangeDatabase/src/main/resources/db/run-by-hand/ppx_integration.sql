BEGIN TRANSACTION;

DECLARE @ppx_alternative_id varchar(255)
SET @ppx_alternative_id = 'PPX';

INSERT INTO [dbo].[SourceDatabase] (alternative_id, name, url, is_eldermark) VALUES (@ppx_alternative_id, 'PPX', NULL, 0);

INSERT INTO [dbo].[Employee] (
  login,
  password,
  inactive,
  first_name,
  last_name,
  legacy_id,
  database_id,
  role_id
) VALUES (
  'PPXuser1',
  'fecca61e710a1178e2416c3319e9fb4f11095572714108f4a48eba257da1890be0ea3f2e57e8be43',
  0,
  'User1',
  'PPX',
  1,
  (SELECT [id] FROM [dbo].[SourceDatabase] WHERE alternative_id=@ppx_alternative_id),
  (SELECT [id] FROM [dbo].[Role] WHERE name='ROLE_PARTNER_USER')
);

INSERT INTO [dbo].[Employee] (
  login,
  password,
  inactive,
  first_name,
  last_name,
  legacy_id,
  database_id,
  role_id
) VALUES (
  'PPXuser2',
  '731ad8ac4fb5a2f1961b5e4f17daeeb09eb5d339b785d30595ab06e6031986668738089288bb81be',
  0,
  'User2',
  'PPX',
  2,
  (SELECT [id] FROM [dbo].[SourceDatabase] WHERE alternative_id=@ppx_alternative_id),
  (SELECT [id] FROM [dbo].[Role] WHERE name='ROLE_PARTNER_USER')
);

GO

COMMIT;