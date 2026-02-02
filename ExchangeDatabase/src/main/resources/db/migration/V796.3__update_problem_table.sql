alter table ADT_SGMNT_DG1_Diagnosis alter column diagnosis_coding_method varchar(10)
GO

alter table ADT_SGMNT_DG1_Diagnosis alter column diagnosis_type_id bigint
GO

IF COL_LENGTH('Problem', 'dg1_id') IS NOT NULL
    BEGIN
        ALTER TABLE Problem
            DROP CONSTRAINT [FK_Problem_ADT_SGMNT_DG1_DiagnosisALLERGY_dg1_id];
        ALTER TABLE Problem
            DROP COLUMN [dg1_id];
    END
GO

ALTER TABLE Problem
    ADD [dg1_id] [bigint] NULL
        CONSTRAINT [FK_Problem_ADT_SGMNT_DG1_DiagnosisALLERGY_dg1_id] FOREIGN KEY REFERENCES ADT_SGMNT_DG1_Diagnosis ([id]);
GO
