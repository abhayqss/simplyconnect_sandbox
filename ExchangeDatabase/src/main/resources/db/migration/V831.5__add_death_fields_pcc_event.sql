IF COL_LENGTH('PccAdtRecord', 'resident_death_date') IS NOT NULL
    BEGIN
        alter table PccAdtRecord
            drop column resident_death_date;
    END
GO

IF COL_LENGTH('PccAdtRecord', 'resident_deceased') IS NOT NULL
    BEGIN
        alter table PccAdtRecord
            drop column resident_deceased;
    END
GO

alter table PccAdtRecord
    add resident_death_date datetime2(7), resident_deceased bit
GO
