
IF COL_LENGTH('resident_enc', 'primary_contact_id') IS NOT NULL
    BEGIN
		ALTER TABLE resident_enc DROP CONSTRAINT [FK_resident_enc_ResidentPrimaryContact]
        ALTER TABLE resident_enc DROP COLUMN primary_contact_id;
    END
GO

IF COL_LENGTH('resident_enc_History', 'primary_contact_id') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN primary_contact_id;
    END
GO


IF (object_id('ResidentPrimaryContact') is not null)
    DROP TABLE [dbo].[ResidentPrimaryContact]
GO

CREATE TABLE [dbo].[ResidentPrimaryContact](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[type] [varchar](20) NOT NULL,
	[notification_type] [varchar](10) NOT NULL,
	[resident_care_team_member_id] [bigint] NULL,
 CONSTRAINT [PK_ResidentPrimaryContact] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ResidentPrimaryContact]  WITH CHECK ADD  CONSTRAINT [FK_ResidentPrimaryContact_ResidentCareTeamMember] FOREIGN KEY([resident_care_team_member_id])
REFERENCES [dbo].[ResidentCareTeamMember] ([id])
GO

ALTER TABLE [dbo].[ResidentPrimaryContact] CHECK CONSTRAINT [FK_ResidentPrimaryContact_ResidentCareTeamMember]
GO


ALTER TABLE [dbo].[resident_enc]
    ADD [primary_contact_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[resident_enc]  WITH CHECK ADD  CONSTRAINT [FK_resident_enc_ResidentPrimaryContact] FOREIGN KEY([primary_contact_id])
REFERENCES [dbo].[ResidentPrimaryContact] ([id])
GO

ALTER TABLE [dbo].[resident_enc] CHECK CONSTRAINT [FK_resident_enc_ResidentPrimaryContact]
GO

ALTER TABLE [dbo].[resident_enc_History]
    ADD [primary_contact_id] [bigint] NULL;
GO

exec resident_table_modified
go




