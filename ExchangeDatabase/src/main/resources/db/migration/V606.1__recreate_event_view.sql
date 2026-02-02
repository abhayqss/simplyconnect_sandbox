declare @eventView nvarchar(max) = OBJECT_DEFINITION(object_id('Event'));
set @eventView = REPLACE(@eventView, 'CREATE', 'ALTER')

EXEC sp_executesql @eventView;
