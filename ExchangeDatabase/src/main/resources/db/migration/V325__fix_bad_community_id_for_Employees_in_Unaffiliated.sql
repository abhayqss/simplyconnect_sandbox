DECLARE @DatabaseId BIGINT = (SELECT [id]
                              FROM [dbo].[SourceDatabase]
                              WHERE [alternative_id] = 'PhysicianRepo');
DECLARE @OrgId BIGINT = (SELECT [id]
                         FROM [dbo].[Organization]
                         WHERE [database_id] = @DatabaseId AND [oid] = 'UNAFFILIATED');

UPDATE [dbo].[Employee_enc]
SET [ccn_community_id] = @OrgId
WHERE [database_id] = @DatabaseId AND [ccn_community_id] <> @OrgId;
GO
