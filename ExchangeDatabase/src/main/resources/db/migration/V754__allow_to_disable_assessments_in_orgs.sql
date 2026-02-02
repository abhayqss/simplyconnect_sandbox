IF OBJECT_ID('Assessment_SourceDatabase_Disabled') is not null
    drop table Assessment_SourceDatabase_Disabled
GO

create table Assessment_SourceDatabase_Disabled
(
    assessment_id bigint not null
        constraint FK_Assessment_SourceDatabase_Disabled_Assessment
            references Assessment,
    database_id   bigint not null
        constraint FK_Assessment_SourceDatabase_Disabled_SourceDatabase
            references SourceDatabase
)
go

--don't 'share for all' assessments which have specific organizations restrictions
update Assessment set is_shared = 0 where id in (select assessment_id from Assessment_SourceDatabase)
GO
