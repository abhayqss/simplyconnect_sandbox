IF COL_LENGTH('PccAdtRecord', 'resident_admit_date') IS NOT NULL
    BEGIN
        alter table PccAdtRecord
            drop column resident_admit_date;
    END
GO

IF COL_LENGTH('PccAdtRecord', 'resident_discharge_date') IS NOT NULL
    BEGIN
        alter table PccAdtRecord
            drop column resident_discharge_date;
    END
GO

alter table PccAdtRecord
    add resident_admit_date datetime2(7), resident_discharge_date datetime2(7)
GO
