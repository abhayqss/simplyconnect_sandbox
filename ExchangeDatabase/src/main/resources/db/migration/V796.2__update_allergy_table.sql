IF COL_LENGTH('Allergy', 'al1_id') IS NOT NULL
    BEGIN
        ALTER TABLE Allergy
            DROP CONSTRAINT [FK_ALLERGY_AL1_SGMNT];
        ALTER TABLE Allergy
            DROP COLUMN [al1_id];
    END
GO

ALTER TABLE Allergy
    ADD [al1_id] [bigint] NULL
        CONSTRAINT [FK_ALLERGY_AL1_SGMNT] FOREIGN KEY REFERENCES ADT_SGMNT_AL1_Allergy ([id]);
GO
