if col_length('SourceDatabase', 'exclude_from_record_search') is not null
    BEGIN
        alter table SourceDatabase
            drop constraint DF_SourceDatabase_exclude_from_record_search_0;
        alter table SourceDatabase
            drop column exclude_from_record_search;
    END
GO

alter table SourceDatabase
    add exclude_from_record_search bit not null
        constraint DF_SourceDatabase_exclude_from_record_search_0 default (0)
GO

update SourceDatabase
set exclude_from_record_search = 1
where alternative_id in ('Health_Partners', 'Health_Partners_Test')
GO
