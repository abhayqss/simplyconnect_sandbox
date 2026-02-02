ALTER table [dbo].[Assessment]
    ADD [editable] bit NULL;
GO

UPDATE [dbo].[Assessment]
SET [editable] = 1
WHERE [code] != 'CARE_MGMT'

UPDATE [dbo].[Assessment]
SET [editable] = 0
WHERE [code] = 'CARE_MGMT'

ALTER table [dbo].[Assessment]
    ALTER COLUMN [editable] bit NOT NULL;
GO