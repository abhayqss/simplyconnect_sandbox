IF COL_LENGTH('VideoCallHistory', 'is_audio_only') IS NOT NULL
    BEGIN
        alter table VideoCallHistory
            drop constraint DF_VideoCallHistory_is_audio_only_false;
        alter table VideoCallHistory
            drop column is_audio_only;
    END
GO

ALTER TABLE VideoCallHistory ADD is_audio_only bit not null,
    constraint DF_VideoCallHistory_is_audio_only_false default 0 for is_audio_only;
