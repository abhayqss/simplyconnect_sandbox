ALTER TABLE [dbo].[EventRN] ALTER COLUMN [first_name] VARCHAR(256) NOT NULL;
ALTER TABLE [dbo].[EventRN] ALTER COLUMN [last_name] VARCHAR(256) NOT NULL;
ALTER TABLE [dbo].[EventManager] ALTER COLUMN [first_name] VARCHAR(256) NOT NULL;
ALTER TABLE [dbo].[EventManager] ALTER COLUMN [last_name] VARCHAR(256) NOT NULL;
ALTER TABLE [dbo].[EventAddress] ALTER COLUMN [street] VARCHAR(256) NOT NULL;
ALTER TABLE [dbo].[EventAddress] ALTER COLUMN [city] VARCHAR(256) NOT NULL;
ALTER TABLE [dbo].[EventTreatingHospital] ALTER COLUMN [name] VARCHAR(256) NOT NULL;
ALTER TABLE [dbo].[EventTreatingPhysician] ALTER COLUMN [first_name] VARCHAR(256) NOT NULL;
ALTER TABLE [dbo].[EventTreatingPhysician] ALTER COLUMN [last_name] VARCHAR(256) NOT NULL;
GO
