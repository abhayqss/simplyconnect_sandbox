
SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[SourceDatabaseAddressAndContacts](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[city] [varchar](100) NULL,
	[state_id] [bigint] NULL,
	[postal_code] [varchar](50) NULL,
	[street_address] [varchar](200) NULL,
	[phone] [varchar](50) NULL,
	[email] [varchar](100) NULL,
 CONSTRAINT [PK_SourceDatabaseAddressAndContacts] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[SourceDatabaseAddressAndContacts]  WITH CHECK ADD  CONSTRAINT [FK_SourceDatabaseAddressAndContacts_State] FOREIGN KEY([state_id])
REFERENCES [dbo].[State] ([id])
GO

ALTER TABLE [dbo].[SourceDatabaseAddressAndContacts] CHECK CONSTRAINT [FK_SourceDatabaseAddressAndContacts_State]
GO

ALTER TABLE dbo.SourceDatabase ADD address_and_contacts_id INT NULL


ALTER TABLE [dbo].[SourceDatabase]  WITH CHECK ADD  CONSTRAINT [FK_SourceDatabase_SourceDatabaseAddressAndContacts] FOREIGN KEY([address_and_contacts_id])
REFERENCES [dbo].[SourceDatabaseAddressAndContacts] ([id])
GO

ALTER TABLE [dbo].[SourceDatabase] CHECK CONSTRAINT [FK_SourceDatabase_SourceDatabaseAddressAndContacts]
GO

ALTER TABLE [dbo].[SourceDatabase] ALTER COLUMN [alternative_id] [varchar](255) NULL;
ALTER TABLE [dbo].[SourceDatabase] ADD [oid] [varchar](100) NULL;

ALTER TABLE [dbo].[SourceDatabase] ADD [last_modified] [datetime] NULL;
ALTER TABLE [dbo].[SourceDatabase] ADD [created_automatically] [bit] NULL;


ALTER TABLE [SourceDatabase] DROP CONSTRAINT [UK_g9dsx16cfdn733suvqmrqr3i5];
ALTER TABLE [SourceDatabase] DROP CONSTRAINT [UK_r2bxg281xjrrmdxl5k7dkdik5];

alter table dbo.[SourceDatabase] add [copy_event_notifications_for_patients] bit NULL;
GO

create view database_org_count as
select database_id, count(*) AS org_count, sum(case when module_hie=1 then 1 else 0 end) as org_hie_count, sum(case when module_cloud_storage=1 then 1 else 0 end)  as org_cloud_count, sum(case when (module_cloud_storage=1 or module_hie=1) then 1 else 0 end)  as org_hie_or_cloud_count
from dbo.Organization
where legacy_table='Company' and (testing_training IS NULL or testing_training=0) and (inactive IS NULL or inactive=0)
group by database_id;
GO
