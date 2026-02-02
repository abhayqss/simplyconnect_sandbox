CREATE PROCEDURE #addDefaultAccessRights  -- temp procedure
AS
  BEGIN
    DECLARE @ctm_id BIGINT;

    -- iterate through existing care team members
    DECLARE cur CURSOR FOR SELECT id
                           FROM [dbo].[ResidentCareTeamMember];
    OPEN cur;

    FETCH NEXT FROM cur INTO @ctm_id;
    WHILE @@FETCH_STATUS = 0 BEGIN
      INSERT INTO [dbo].[CareTeamMember_AccessRight] (care_team_member_id, access_right_id) VALUES (@ctm_id, 1);
      INSERT INTO [dbo].[CareTeamMember_AccessRight] (care_team_member_id, access_right_id) VALUES (@ctm_id, 2);
      INSERT INTO [dbo].[CareTeamMember_AccessRight] (care_team_member_id, access_right_id) VALUES (@ctm_id, 3);
      INSERT INTO [dbo].[CareTeamMember_AccessRight] (care_team_member_id, access_right_id) VALUES (@ctm_id, 4);
      FETCH NEXT FROM cur INTO @ctm_id;
    END;

    CLOSE cur;
    DEALLOCATE cur;
  END;
GO

SET XACT_ABORT ON
GO


CREATE TABLE [dbo].[AccessRight] (
  [id]           [BIGINT] IDENTITY (1, 1) NOT NULL,
  [code]         [VARCHAR](50)            NOT NULL,
  [display_name] [VARCHAR](255)           NULL,
  PRIMARY KEY ([id])
);
GO

CREATE TABLE [dbo].[CareTeamMember_AccessRight] (
  [care_team_member_id] [BIGINT] NOT NULL,
  [access_right_id]     [BIGINT] NOT NULL
);
GO

ALTER TABLE [dbo].[CareTeamMember_AccessRight]
  WITH CHECK ADD CONSTRAINT [FK_CTMAR_AccessRight] FOREIGN KEY ([access_right_id])
REFERENCES [dbo].[AccessRight] ([id]) ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[CareTeamMember_AccessRight]
  WITH CHECK ADD CONSTRAINT [FK_CTMAR_ResidentCareTeamMember] FOREIGN KEY ([care_team_member_id])
REFERENCES [dbo].[ResidentCareTeamMember] ([id]) ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[CareTeamMember_AccessRight]
  ADD CONSTRAINT UQ_CareTeamMember_AccessRight UNIQUE(care_team_member_id, access_right_id);
GO

-- Insert Access Rights

INSERT INTO [dbo].[AccessRight] ([code], [display_name]) VALUES ('MY_PHR', 'My personal health record');
INSERT INTO [dbo].[AccessRight] ([code], [display_name]) VALUES ('MEDICATIONS_LIST', 'Medications list');
INSERT INTO [dbo].[AccessRight] ([code], [display_name]) VALUES ('EVENT_NOTIFICATIONS', 'Event notifications');
INSERT INTO [dbo].[AccessRight] ([code], [display_name]) VALUES ('MY_CT_VISIBILITY', 'My care team visibility');
GO

EXEC #addDefaultAccessRights;
GO
