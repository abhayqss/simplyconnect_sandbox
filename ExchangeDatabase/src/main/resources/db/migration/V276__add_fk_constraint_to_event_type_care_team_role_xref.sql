SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[EventType_CareTeamRole_Xref]
  ADD CONSTRAINT FK_care_team_role FOREIGN KEY (care_team_role_id) REFERENCES CareTeamRole(id);

ALTER TABLE [dbo].[EventType_CareTeamRole_Xref]
  ADD CONSTRAINT FK_event_type FOREIGN KEY (event_type_id) REFERENCES EventType(id);
GO
