SET XACT_ABORT ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Activity] (
  [TYPE]                [VARCHAR](31)            NOT NULL, -- discriminator column
  [id]                  [BIGINT] IDENTITY (1, 1) NOT NULL,
  [date]                [DATETIME2](7)           NULL,
  [duration]            [INTEGER]                NULL,
  [incoming]            [BIT]                    NULL,
  [status]              [VARCHAR](255)           NULL,
  [event_id]            [BIGINT]                 NULL,
  [event_type_id]       [BIGINT]                 NULL,
  [responsibility]      [VARCHAR](2)             NULL,
  [care_team_member_id] [BIGINT]                 NOT NULL,
  [patient_id]          [BIGINT]                 NOT NULL,
  PRIMARY KEY ([id])
);
GO

CREATE TABLE [dbo].[UserMobileCareTeamMember] (
  [id]              [BIGINT]       NOT NULL,
  [first_name]      [VARCHAR](255) NULL,
  [last_name]       [VARCHAR](255) NULL,
  [secondary_email] [VARCHAR](255) NULL,
  [secondary_phone] [VARCHAR](255) NULL,
  [person_id]       [BIGINT]       NULL,
  [address_id]      [BIGINT]       NULL,
  PRIMARY KEY ([id]),
  CONSTRAINT [FK_UserMobileCareTeamMember_Parent] FOREIGN KEY ([id]) REFERENCES [dbo].[UserMobile] ([id])
    ON DELETE CASCADE
);
GO

CREATE TABLE [dbo].[CareTeamRelation] (
  [id]   [BIGINT] IDENTITY (1, 1) NOT NULL,
  [code] [VARCHAR](255)           NOT NULL,
  [name] [VARCHAR](255)           NULL,
  PRIMARY KEY ([id])
);
GO

CREATE TABLE [dbo].[CareTeamRelationship] (
  [id]   [BIGINT] IDENTITY (1, 1) NOT NULL,
  [code] [VARCHAR](255)           NOT NULL,
  [name] [VARCHAR](255)           NULL,
  PRIMARY KEY ([id])
);
GO

CREATE TABLE [dbo].[AccountType] (
  [id]   [BIGINT] IDENTITY (1, 1) NOT NULL,
  [type] [VARCHAR](255)           NOT NULL,
  [name] [VARCHAR](255)           NULL,
  PRIMARY KEY ([id])
);
GO

CREATE TABLE [dbo].[UserAccountType] (
  [id]              [BIGINT] IDENTITY (1, 1) NOT NULL,
  [user_id]         [BIGINT]                 NOT NULL,
  [account_type_id] [BIGINT]                 NOT NULL,
  [is_current]      [BIT]                    NOT NULL,
  PRIMARY KEY ([id])
);
GO

CREATE TABLE [dbo].[UserCTM_ResidentCareTeamMember] (
  [user_mobile_care_team_member_id] [BIGINT] NOT NULL,
  [resident_care_team_member_id]    [BIGINT] NOT NULL
);
GO

ALTER TABLE [dbo].[ResidentCareTeamMember]
  ADD [created_by_resident_id] [BIGINT] NULL,
  [emergency_contact] [BIT] NULL,
  [invitation_status] [VARCHAR](255) NULL,
  [care_team_relation_id] [BIGINT] NULL,
  [care_team_relationship_id] [BIGINT] NULL
--[user_mobile_care_team_member_id] [BIGINT] NULL
;
GO

ALTER TABLE [dbo].[ResidentCareTeamMember]
  WITH CHECK ADD CONSTRAINT [FK_ResidentCareTeamMember_Relationship] FOREIGN KEY ([care_team_relationship_id])
REFERENCES [dbo].[CareTeamRelationship] ([id]);
GO
ALTER TABLE [dbo].[ResidentCareTeamMember]
  WITH CHECK ADD CONSTRAINT [FK_ResidentCareTeamMember_Relation] FOREIGN KEY ([care_team_relation_id])
REFERENCES [dbo].[CareTeamRelation] ([id]);
GO
ALTER TABLE [dbo].[UserCTM_ResidentCareTeamMember]
  WITH CHECK ADD CONSTRAINT [FK_UserMobileCareTeamMember] FOREIGN KEY ([user_mobile_care_team_member_id])
REFERENCES [dbo].[UserMobileCareTeamMember] ([id]);
GO
ALTER TABLE [dbo].[UserCTM_ResidentCareTeamMember]
  WITH CHECK ADD CONSTRAINT [FK_ResidentCareTeamMember] FOREIGN KEY ([resident_care_team_member_id])
REFERENCES [dbo].[ResidentCareTeamMember] ([id]);
GO
ALTER TABLE [dbo].[UserCTM_ResidentCareTeamMember]
  WITH CHECK ADD CONSTRAINT [UQ_ResidentCareTeamMember] UNIQUE ([resident_care_team_member_id]);
GO

ALTER TABLE [dbo].[Activity]
  WITH CHECK ADD CONSTRAINT [FK_Activity_ResidentCareTeamMember] FOREIGN KEY ([care_team_member_id])
REFERENCES [dbo].[ResidentCareTeamMember] ([id]);
GO
ALTER TABLE [dbo].[Activity]
  WITH CHECK ADD CONSTRAINT [FK_Activity_UserMobile] FOREIGN KEY ([patient_id])
REFERENCES [dbo].[UserMobile] ([id]);
GO
ALTER TABLE [dbo].[Activity]
  WITH CHECK ADD CONSTRAINT [FK_Activity_Event] FOREIGN KEY ([event_id])
REFERENCES [dbo].[Event_enc] ([id]);
GO
ALTER TABLE [dbo].[Activity]
  WITH CHECK ADD CONSTRAINT [FK_Activity_EventType] FOREIGN KEY ([event_type_id])
REFERENCES [dbo].[EventType] ([id]);
GO

ALTER TABLE [dbo].[UserMobileCareTeamMember]
  WITH CHECK ADD CONSTRAINT [FK_UserMobileCareTeamMember_Person] FOREIGN KEY ([person_id])
REFERENCES [dbo].[Person] ([id]);
GO
ALTER TABLE [dbo].[UserMobileCareTeamMember]
  WITH CHECK ADD CONSTRAINT [FK_UserMobileCareTeamMember_PersonAddress] FOREIGN KEY ([address_id])
REFERENCES [dbo].[PersonAddress_enc] ([id]);
GO
ALTER TABLE [dbo].[UserAccountType]
  WITH CHECK ADD CONSTRAINT [FK_UserAccountType_User] FOREIGN KEY ([user_id])
REFERENCES [dbo].[UserMobile] ([id]);
GO
ALTER TABLE [dbo].[UserAccountType]
  WITH CHECK ADD CONSTRAINT [FK_UserAccountType_AccountType] FOREIGN KEY ([account_type_id])
REFERENCES [dbo].[AccountType] ([id]);
GO

CREATE UNIQUE INDEX IX_UserAccountType_UserId_IsCurrent
  ON UserAccountType (user_id)
  WHERE is_current = 1;
GO

-- Insert Care Team Relations

INSERT INTO [dbo].[CareTeamRelation] ([code], [name]) VALUES ('FAMILY', 'Family');
INSERT INTO [dbo].[CareTeamRelation] ([code], [name]) VALUES ('FRIEND', 'Friend');
INSERT INTO [dbo].[CareTeamRelation] ([code], [name]) VALUES ('GUARDIAN', 'Guardian');
INSERT INTO [dbo].[CareTeamRelation] ([code], [name]) VALUES ('PARTNER', 'Partner');
INSERT INTO [dbo].[CareTeamRelation] ([code], [name]) VALUES ('WORK', 'Work');
INSERT INTO [dbo].[CareTeamRelation] ([code], [name]) VALUES ('PARENT', 'Parent');
GO

-- Insert Care Team Relationships

INSERT INTO [dbo].[CareTeamRelationship] ([code], [name]) VALUES ('FRIEND_FAMILY', 'Friend or Family Member');
INSERT INTO [dbo].[CareTeamRelationship] ([code], [name]) VALUES ('MEDICAL_STAFF', 'Medical Staff');
GO

-- Insert Account Types

DECLARE @AccountTypePatient TABLE(id BIGINT);

INSERT INTO [dbo].[AccountType] ([type], [name])
OUTPUT INSERTED.id INTO @AccountTypePatient VALUES ('PATIENT', 'Patient');
INSERT INTO [dbo].[AccountType] ([type], [name]) VALUES ('GUARDIAN', 'Guardian');
INSERT INTO [dbo].[AccountType] ([type], [name]) VALUES ('PHYSICIAN', 'Physician');

INSERT INTO [dbo].[UserAccountType] (user_id, account_type_id, is_current)
  SELECT
    um.id                      AS user_id,
    (SELECT TOP (1) id
     FROM @AccountTypePatient) AS account_type_id,
    1                          AS is_current
  FROM [dbo].[UserMobile] um
  WHERE um.active = 1;
GO
