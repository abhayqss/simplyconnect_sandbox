IF COL_LENGTH('SourceDatabase', 'dont_auto_merge_residents') IS NOT NULL
    BEGIN
        alter table SourceDatabase
            drop constraint DF_SourceDatabase_dont_auto_merge_residents_disabled;
        alter table SourceDatabase
            drop column dont_auto_merge_residents;
    END
GO

alter table SourceDatabase
    add dont_auto_merge_residents bit not null
        CONSTRAINT DF_SourceDatabase_dont_auto_merge_residents_disabled DEFAULT (0)
go
