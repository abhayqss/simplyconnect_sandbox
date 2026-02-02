declare @insert_id bigint;

select @insert_id = pf.id
from PrimaryFocus pf
where pf.code = 'Post_Acute_Care'

insert into CommunityType
values ('Memory Care', 'Memory_Care', @insert_id)