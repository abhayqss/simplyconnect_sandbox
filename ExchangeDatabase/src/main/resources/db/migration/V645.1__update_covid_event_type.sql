declare @event_type_id bigint;

select @event_type_id = id from [dbo].[EventType] where code = 'COVID19';

update [dbo].[EventType] set [description] = 'COVID-19' where id = @event_type_id;
GO
