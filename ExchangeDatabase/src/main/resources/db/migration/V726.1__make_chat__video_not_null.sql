update SourceDatabase
set is_chat_enabled = 0
where is_chat_enabled is null

update SourceDatabase
set is_video_enabled = 0
where is_video_enabled is null

alter table SourceDatabase
    alter column is_chat_enabled bit not null
alter table SourceDatabase
    alter column is_video_enabled bit not null
