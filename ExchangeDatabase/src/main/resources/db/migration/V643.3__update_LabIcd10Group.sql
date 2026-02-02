declare @group_id bigint;

select @group_id = id from LabIcd10Group where [name] = 'COVID19)'

UPDATE [dbo].[LabIcd10Group]
   SET [name] = 'COVID19'
 WHERE id = @group_id
GO