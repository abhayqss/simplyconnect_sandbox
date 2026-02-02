UPDATE [dbo].[NoteSubType] SET description = 'Assessment note (auto generated)' WHERE code='ASSESSMENT_NOTE';
UPDATE [dbo].[CareTeamRole] SET display_name = 'Parent/Guardian' WHERE code='ROLE_PARENT_GUARDIAN';
UPDATE [dbo].[CareTeamRole] SET display_name = 'Person Receiving Services' WHERE code='ROLE_PERSON_RECEIVING_SERVICES';
GO
