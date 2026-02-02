alter table SourceDatabase
    add constraint DF_SourceDatabase_enabledChat DEFAULT 1 for is_chat_enabled
GO

alter table SourceDatabase
    add constraint DF_SourceDatabase_enabledVideo DEFAULT 1 for is_video_enabled
GO
