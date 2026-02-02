SET XACT_ABORT ON
GO
/****** Object:  Table [dbo].[EventType]    Script Date: 22-Sep-15 15:52:44  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[State](
 [id] [bigint] IDENTITY(1,1) NOT NULL,
 [name] [varchar](30) NOT NULL,
 [abbr] [varchar](10) NOT NULL,
PRIMARY KEY CLUSTERED
(
 [id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]


CREATE TABLE [dbo].[EventType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](50) NOT NULL,
	[description] [varchar](255) NOT NULL,
 CONSTRAINT [PK_EventType] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


CREATE TABLE [dbo].[NotificationPreferences](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[event_type_id] [bigint] NOT NULL,
	[notification_type] [varchar](50) NOT NULL,
	[responsibility] [varchar](50) NOT NULL,
 CONSTRAINT [PK_NotificationPreferences] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[NotificationPreferences]  WITH CHECK ADD  CONSTRAINT [FK_NotificationPreferences_EventType1] FOREIGN KEY([event_type_id])
REFERENCES [dbo].[EventType] ([id])
GO

ALTER TABLE [dbo].[NotificationPreferences] CHECK CONSTRAINT [FK_NotificationPreferences_EventType1]
GO

SET ANSI_PADDING ON
GO


CREATE TABLE [dbo].[CareTeamRole](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](50) NOT NULL,
 CONSTRAINT [PK_CareTeamRole] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[CareTeamMember](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[employee_id] [bigint] NOT NULL,
	[care_team_role_id] [bigint] NOT NULL,
	[description] [varchar](255) NULL,
 CONSTRAINT [PK_CareTeamMember] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[CareTeamMember]  WITH CHECK ADD  CONSTRAINT [FK_CareTeamMember_CareTeamRole] FOREIGN KEY([care_team_role_id])
REFERENCES [dbo].[CareTeamRole] ([id])
GO

ALTER TABLE [dbo].[CareTeamMember] CHECK CONSTRAINT [FK_CareTeamMember_CareTeamRole]
GO

ALTER TABLE [dbo].[CareTeamMember]  WITH CHECK ADD  CONSTRAINT [FK_CareTeamMember_Employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[CareTeamMember] CHECK CONSTRAINT [FK_CareTeamMember_Employee]
GO


SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[CareTeamMemberNotificationPreferences](
	[id] [bigint] NOT NULL,
	[care_team_member_id] [bigint] NOT NULL,
 CONSTRAINT [PK_CareTeamNotificationPreferences] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[CareTeamMemberNotificationPreferences]  WITH CHECK ADD  CONSTRAINT [FK_CareTeamNotificationPreferences_CareTeamMember] FOREIGN KEY([care_team_member_id])
REFERENCES [dbo].[CareTeamMember] ([id])
GO

ALTER TABLE [dbo].[CareTeamMemberNotificationPreferences] CHECK CONSTRAINT [FK_CareTeamNotificationPreferences_CareTeamMember]
GO



/****** Object:  Table [dbo].[OrganizationCareTeamMember]    Script Date: 22-Sep-15 18:16:08  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[OrganizationCareTeamMember](
	[id] [bigint] NOT NULL,
	[organization_id] [bigint] NOT NULL,
 CONSTRAINT [PK_OrganizationCareTeamMember] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[OrganizationCareTeamMember]  WITH CHECK ADD  CONSTRAINT [FK_OrganizationCareTeamMember_Organization] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[OrganizationCareTeamMember] CHECK CONSTRAINT [FK_OrganizationCareTeamMember_Organization]
GO



/****** Object:  Table [dbo].[ResidentCareTeamMember]    Script Date: 23-Sep-15 12:27:46  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[ResidentCareTeamMember](
	[id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
 CONSTRAINT [PK_ResidentCareTeamMember] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[ResidentCareTeamMember]  WITH CHECK ADD  CONSTRAINT [FK_ResidentCareTeamMember_Resident] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[ResidentCareTeamMember] CHECK CONSTRAINT [FK_ResidentCareTeamMember_Resident]
GO

/** ======================================================================*/
/****** Object:  Table [dbo].[EventAddress]    Script Date: 06-Oct-15 11:23:42  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EventAddress](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[street] [varchar](255) NOT NULL,
	[city] [varchar](128) NOT NULL,
	[state_id] [bigint] NOT NULL,
	[zip] [varchar](10) NOT NULL,
 CONSTRAINT [PK_EventAddress] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[EventAddress]  WITH CHECK ADD  CONSTRAINT [FK_EventAddress_State] FOREIGN KEY([state_id])
REFERENCES [dbo].[State] ([id])
GO

ALTER TABLE [dbo].[EventAddress] CHECK CONSTRAINT [FK_EventAddress_State]
GO


SET ANSI_PADDING OFF
GO

/****** Object:  Table [dbo].[EventRN]    Script Date: 06-Oct-15 11:25:05  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EventRN](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[first_name] [varchar](128) NOT NULL,
	[last_name] [varchar](128) NOT NULL,
	[event_address_id] [bigint] NULL,
 CONSTRAINT [PK_Event_RN] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[EventRN]  WITH CHECK ADD  CONSTRAINT [FK_Event_RN_EventAddress] FOREIGN KEY([event_address_id])
REFERENCES [dbo].[EventAddress] ([id])
GO

ALTER TABLE [dbo].[EventRN] CHECK CONSTRAINT [FK_Event_RN_EventAddress]
GO

/****** Object:  Table [dbo].[EventManager]    Script Date: 06-Oct-15 11:31:47  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EventManager](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[first_name] [varchar](128) NOT NULL,
	[last_name] [varchar](128) NOT NULL,
	[phone] [varchar](20) NULL,
	[email] [varchar](255) NULL,
 CONSTRAINT [PK_EventManager] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [dbo].[EventAuthor]    Script Date: 06-Oct-15 11:32:17  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EventAuthor](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[first_name] [varchar](128) NOT NULL,
	[last_name] [varchar](128) NOT NULL,
	[role] [varchar](50) NOT NULL,
	[organization] [varchar](128) NOT NULL,
 CONSTRAINT [PK_EventAuthor] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


/****** Object:  Table [dbo].[EventTreatingPhysician]    Script Date: 06-Oct-15 11:32:37  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EventTreatingPhysician](
	[id] [bigint] IDENTITY (1,1) NOT NULL,
	[first_name] [varchar](128) NOT NULL,
	[last_name] [varchar](128) NOT NULL,
	[phone] [varchar](15) NULL,
	[event_address_id] [bigint] NULL,
 CONSTRAINT [PK_EventTreatingPhysician] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[EventTreatingPhysician]  WITH CHECK ADD  CONSTRAINT [FK_EventTreatingPhysician_EventAddress] FOREIGN KEY([event_address_id])
REFERENCES [dbo].[EventAddress] ([id])
GO

ALTER TABLE [dbo].[EventTreatingPhysician] CHECK CONSTRAINT [FK_EventTreatingPhysician_EventAddress]
GO

/****** Object:  Table [dbo].[EventTreatingHospital]    Script Date: 06-Oct-15 11:33:15  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EventTreatingHospital](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NOT NULL,
	[phone] [varchar](15) NULL,
	[event_address_id] [bigint] NULL,
 CONSTRAINT [PK_EventThreatingHospital] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[EventTreatingHospital]  WITH CHECK ADD  CONSTRAINT [FK_EventThreatingHospital_EventAddress] FOREIGN KEY([event_address_id])
REFERENCES [dbo].[EventAddress] ([id])
GO

ALTER TABLE [dbo].[EventTreatingHospital] CHECK CONSTRAINT [FK_EventThreatingHospital_EventAddress]
GO

/****** Object:  Table [dbo].[Event]    Script Date: 06-Oct-15 11:34:27  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Event](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[resident_id] [bigint] NOT NULL,
	[event_type_id] [bigint] NOT NULL,
	[event_content] [varchar](max) NOT NULL,
	[event_datetime] [datetime] NOT NULL,
	[is_injury] [bit] NOT NULL DEFAULT 0,
	[location] [varchar](500) NULL,
	[situation] [varchar](5000) NULL,
	[background] [varchar](5000) NULL,
	[assessment] [varchar](5000) NULL,
	[is_followup] [bit] NOT NULL DEFAULT 0,
	[followup] [varchar](5000) NULL,
	[is_manual] [bit] NOT NULL DEFAULT 0,
	[event_manager_id] [bigint] NULL,
	[event_author_id] [bigint] NOT NULL,
	[event_rn_id] [bigint] NULL,
	[event_treating_physician_id] [bigint] NULL,
	[event_treating_hospital_id] [bigint] NULL,
	[is_er_visit] [bit] NOT NULL DEFAULT 0,
	[is_overnight_in] [bit] NOT NULL DEFAULT 0,


 CONSTRAINT [PK_Event] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventAuthor] FOREIGN KEY([event_author_id])
REFERENCES [dbo].[EventAuthor] ([id])
GO

ALTER TABLE [dbo].[Event] CHECK CONSTRAINT [FK_Event_EventAuthor]
GO

ALTER TABLE [dbo].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventManager] FOREIGN KEY([event_manager_id])
REFERENCES [dbo].[EventManager] ([id])
GO

ALTER TABLE [dbo].[Event] CHECK CONSTRAINT [FK_Event_EventManager]
GO

ALTER TABLE [dbo].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventRN] FOREIGN KEY([event_rn_id])
REFERENCES [dbo].[EventRN] ([id])
GO

ALTER TABLE [dbo].[Event] CHECK CONSTRAINT [FK_Event_EventRN]
GO

ALTER TABLE [dbo].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventTreatingHospital] FOREIGN KEY([event_treating_hospital_id])
REFERENCES [dbo].[EventTreatingHospital] ([id])
GO

ALTER TABLE [dbo].[Event] CHECK CONSTRAINT [FK_Event_EventTreatingHospital]
GO

ALTER TABLE [dbo].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventTreatingPhysician] FOREIGN KEY([event_treating_physician_id])
REFERENCES [dbo].[EventTreatingPhysician] ([id])
GO

ALTER TABLE [dbo].[Event] CHECK CONSTRAINT [FK_Event_EventTreatingPhysician]
GO

ALTER TABLE [dbo].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventType] FOREIGN KEY([event_type_id])
REFERENCES [dbo].[EventType] ([id])
GO

ALTER TABLE [dbo].[Event] CHECK CONSTRAINT [FK_Event_EventType]
GO

ALTER TABLE [dbo].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_Resident] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[Event] CHECK CONSTRAINT [FK_Event_Resident]
GO



/****** Object:  Table [dbo].[EventNotification]    Script Date: 28-Sep-15 15:09:42  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EventNotification](
	[id] [bigint] IDENTITY (1,1) NOT NULL,
	[event_id] [bigint] NOT NULL,
	[employee_id] [bigint] NOT NULL,
	[notification_type] [varchar](50) NOT NULL,
	[created_datetime] [datetime] NOT NULL,
	[care_team_role_id] [bigint] NOT NULL,
	[description] [varchar](50) NOT NULL,
	[responsibility] [varchar](50) NOT NULL,
	[sent_datetime] [datetime] NULL,
	[content] [varchar](max) NOT NULL,
	[destination] [varchar] (255) NOT NULL,
 CONSTRAINT [PK_EventNotification] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[EventNotification]  WITH CHECK ADD  CONSTRAINT [FK_EventNotification_CareTeamRole] FOREIGN KEY([care_team_role_id])
REFERENCES [dbo].[CareTeamRole] ([id])
GO

ALTER TABLE [dbo].[EventNotification] CHECK CONSTRAINT [FK_EventNotification_CareTeamRole]
GO

ALTER TABLE [dbo].[EventNotification]  WITH CHECK ADD  CONSTRAINT [FK_EventNotification_Employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[EventNotification] CHECK CONSTRAINT [FK_EventNotification_Employee]
GO

ALTER TABLE [dbo].[EventNotification]  WITH CHECK ADD  CONSTRAINT [FK_EventNotification_Event] FOREIGN KEY([event_id])
REFERENCES [dbo].[Event] ([id])
GO

ALTER TABLE [dbo].[EventNotification] CHECK CONSTRAINT [FK_EventNotification_Event]
GO



/****** Object:  Table [dbo].[Employee]    Script Date: 23-Sep-15 9:26:35  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[Employee] ADD [person_id] [bigint] NULL;

ALTER TABLE [dbo].[Employee]  WITH CHECK ADD  CONSTRAINT [FK_Employee_Person] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO

ALTER TABLE [dbo].[Employee] CHECK CONSTRAINT [FK_Employee_Person]
GO



/**======================================================== */


INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('SI', 'Serious injury');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('ME', 'Medical emergency');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('AS', 'Suspended abuse');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('MERR', 'Medication Errors');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('ARM', 'Adverse Reaction to Medication');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('SA', 'Sexual activity involving force/coercion');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('USI', 'Unexpected serious illness');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('ART', 'Accident requiring treatment');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('FIRE', 'Fire or event that requires relocation of services for more than 24 hours');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('CI', 'Circumstances involving law enforcement agency, fire department related to the health, safety or supervision of an individual');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('H', 'Hospitalization');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('ERV', 'ER Visit');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('SEVA', 'Suspected exploitation of a vulnerable adult');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('PA', 'Physical aggression toward another resulting in pain, injury or emotional distress');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('LIFE', 'Life Events (ie - change in guardianship, started a new job)');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('DEPRESSION', 'Demonstrating signs of depression');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('CB', 'Change in behavior');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('MNC', 'Medication non-compliance');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('EBS', 'Experienced Barrier to Service (ie - lack of transportation)');
INSERT INTO [dbo].[EventType] ([code], [description]) VALUES ('GENERAL', 'General change in functioning');


--

INSERT INTO [dbo].[CareTeamRole] ([name])  VALUES   ('Case Manager');
INSERT INTO [dbo].[CareTeamRole] ([name])  VALUES   ('Care Coordinator');
INSERT INTO [dbo].[CareTeamRole] ([name])  VALUES   ('Parent/Guardian');
INSERT INTO [dbo].[CareTeamRole] ([name])  VALUES   ('Person Receiving Services');
INSERT INTO [dbo].[CareTeamRole] ([name])  VALUES   ('Primary physician');
INSERT INTO [dbo].[CareTeamRole] ([name])  VALUES   ('Behavioral Health');
INSERT INTO [dbo].[CareTeamRole] ([name])  VALUES   ('Community Members');
INSERT INTO [dbo].[CareTeamRole] ([name])  VALUES   ('Service Provider');

-- insert states
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Alabama', N'AL')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Arizona', N'AZ')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'California', N'CA')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Connecticut', N'CT')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Florida', N'FL')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Georgia', N'GA')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Idaho', N'ID')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Indiana', N'IN')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Kansas', N'KS')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'New Hampshire', N'NH')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'New Mexico', N'NM')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'North Dakota', N'ND')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Oklahoma', N'OK')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Oregon', N'OR')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Rhode Island', N'RI')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'South Dakota', N'SD')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Tennessee', N'TN')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Utah', N'UT')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Virginia', N'VA')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'West Virginia', N'WV')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Wyoming', N'WY')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Maine', N'ME')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Massachusetts', N'MA')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Minnesota', N'MN')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Missouri', N'MO')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Nebraska', N'NE')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Nevada', N'NV')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'New Jersey', N'NJ')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'New York', N'NY')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'North Carolina', N'NC')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Ohio', N'OH')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Pennsylvania', N'PA')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'South Carolina', N'SC')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Vermont', N'VT')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Washington', N'WA')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Wisconsin', N'WI')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Alaska', N'AK')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Arkansas', N'AR')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Colorado', N'CO')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Delaware', N'DE')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Hawaii', N'HI')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Illinois', N'IL')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Iowa', N'IA')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Kentucky', N'KY')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Louisiana', N'LA')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Maryland', N'MD')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Michigan', N'MI')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Mississippi', N'MS')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Montana', N'MT')
INSERT [dbo].[state] ([name], [abbr]) VALUES (N'Texas', N'TX')

GO


