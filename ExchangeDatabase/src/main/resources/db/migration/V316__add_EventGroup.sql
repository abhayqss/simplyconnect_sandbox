SET XACT_ABORT ON
GO
CREATE TABLE [dbo].[EventGroup](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[name] [varchar](100) NOT NULL,
	[priority] [int] NOT NULL
)
GO
INSERT INTO [dbo].[EventGroup]([name],[priority]) VALUES('Emergency',1);
GO
INSERT INTO [dbo].[EventGroup]([name],[priority]) VALUES('Changing Health Conditions',2);
GO
INSERT INTO [dbo].[EventGroup]([name],[priority]) VALUES('Medications Alerts & Reactions',3);
GO
INSERT INTO [dbo].[EventGroup]([name],[priority]) VALUES('Behavior / Mental Health',4);
GO
INSERT INTO [dbo].[EventGroup]([name],[priority]) VALUES('General / Life / Assessment',5);
GO
INSERT INTO [dbo].[EventGroup]([name],[priority]) VALUES('Abuse / Safety',6);
GO
ALTER TABLE [dbo].[EventType] ADD [event_group_id] [bigint];
GO
ALTER TABLE [dbo].[EventType]  WITH CHECK ADD  CONSTRAINT [FK__EventGroup] FOREIGN KEY([event_group_id])
REFERENCES [dbo].[EventGroup] ([id])
GO
ALTER TABLE [dbo].[EventType] CHECK CONSTRAINT [FK__EventGroup]
GO
update EventType set event_group_id = (select id from EventGroup where priority = 1) where code in ('ART','ERV','ME');
GO
update EventType set event_group_id = (select id from EventGroup where priority = 2) where code in ('ARD','GENERAL','EADT','H','USI','SI');
GO
update EventType set event_group_id = (select id from EventGroup where priority = 3) where code in ('ARM','MEDAL','MEDS','MERR','MNC');
GO
update EventType set event_group_id = (select id from EventGroup where priority = 4) where code in ('CB','DEPRESSION','MHRI','PA');
GO
update EventType set event_group_id = (select id from EventGroup where priority = 5) where code in ('CI','EBS','FIRE','LIFE');
GO
update EventType set event_group_id = (select id from EventGroup where priority = 6) where code in ('SA','AS','SEVA');
GO