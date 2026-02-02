ALTER TABLE [dbo].[SourceDatabaseAddressAndContacts]
  ALTER COLUMN [phone] [varchar](255) NULL;

ALTER TABLE [dbo].[Organization]
  ALTER COLUMN [phone] [varchar](255) NULL;

ALTER TABLE [dbo].[EventManager]
  ALTER COLUMN [phone] [varchar](255) NULL;

ALTER TABLE [dbo].[EventAuthor]
  ALTER COLUMN [role] [varchar](255) NOT NULL; --not null constraint as in production

ALTER TABLE [dbo].[EventAuthor]
  ALTER COLUMN [organization] [varchar](255) NOT NULL; --not null constraint as in production

/* https://jira.scnsoft.com/browse/CCN-1236
1) [Organization] Phone field max length

    xsd schema: 255 characters
    DB: 50 characters

2) [Community] Phone field max length

    xsd schema: 255 characters
    DB: 100 characters

3) [Manager] Phone field max length

    xsd schema: 255 characters
    DB: 20 characters

4) [Form Author] Role field max length

    xsd schema: 255 characters
    DB: 50 characters

5) [Form Author] Organization field max length

    xsd schema: 255 characters
    DB: 128 characters

*/

