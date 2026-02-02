DECLARE @event_type_id BIGINT;
select @event_type_id = (
                        SELECT [id]
                        FROM [dbo].[EventType]
                        WHERE [code] = 'NURSFI');

UPDATE [dbo].[EventType]
   SET [is_require_ir] = 0
 WHERE id = @event_type_id

