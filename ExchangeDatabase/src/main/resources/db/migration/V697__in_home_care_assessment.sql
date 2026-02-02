declare @assessment_group_id bigint;
select @assessment_group_id = id from AssessmentGroup where name = 'Behavioral Health'


INSERT INTO [dbo].[Assessment]
           ([assessment_group_id]
           ,[name]
           ,[json_content]
           ,[short_name]
           ,[scoring_enabled]
           ,[severity_column_name]
           ,[management_comment]
           ,[has_numeration]
           ,[type]
           ,[code]
           ,[send_event_enabled])
     VALUES
           (@assessment_group_id
           ,'In-Home Care'
           ,'{"pages":[{"name":"page1","elements":[{"type":"radiogroup","name":"Can I get you a glass of water?","title":"Can I get you a glass of water? ","hideNumber":true,"isRequired":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"comment","name":"How are your ankles feeling today?","title":"How are your ankles feeling today?","hideNumber":true,"isRequired":true},{"type":"radiogroup","name":"Have they swollen anymore?","title":"Have they swollen anymore? ","hideNumber":true,"isRequired":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"comment","name":"Comment1","visibleIf":"{Have they swollen anymore?}= ''item1''","title":"Comment","hideNumber":true},{"type":"radiogroup","name":"Have you run out of any meds?","title":"Have you run out of any meds? ","hideNumber":true,"isRequired":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"radiogroup","name":"Can I help you order refills today?","visibleIf":"{Have you run out of any meds?}= ''item1''","title":"Can I help you order refills today?","hideNumber":true,"isRequired":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"comment","name":"Comment2","visibleIf":"{Can I help you order refills today?}= ''item1''","title":"Comment","hideNumber":true},{"type":"radiogroup","name":"Do you have a doctor’s appointment this week?","title":"Do you have a doctor’s appointment this week?","hideNumber":true,"isRequired":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"comment","name":"Comment3","title":"Comment","hideNumber":true},{"type":"comment","name":"What did you have yesterday to eat?","title":"What did you have yesterday to eat?","hideNumber":true,"isRequired":true},{"type":"radiogroup","name":"Do you feel well rested today?","title":"Do you feel well rested today?","hideNumber":true,"isRequired":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"comment","name":"Comment4","visibleIf":"{Do you feel well rested today?}= ''item2''","title":"Comment","hideNumber":true},{"type":"radiogroup","name":"Any aches or pains that you want to talk about?","title":"Any aches or pains that you want to talk about?","hideNumber":true,"isRequired":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"comment","name":"Comment5","title":"Comment","hideNumber":true},{"type":"radiogroup","name":"Do you want your care coordinator to reach out to you?","title":"Do you want your care coordinator to reach out to you? ","hideNumber":true,"isRequired":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"comment","name":"Best time?","visibleIf":"{Do you want your care coordinator to reach out to you?}= ''item1''","title":"Best time?","hideNumber":true},{"type":"radiogroup","name":"Have you tripped or fell since my last visit?","title":"Have you tripped or fell since my last visit?","hideNumber":true,"isRequired":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"comment","name":"Comment6","visibleIf":"{Have you tripped or fell since my last visit?}= ''item1''","title":"Comment","hideNumber":true},{"type":"radiogroup","name":"Have you had a chance to connect with friends or family since my last visit?","title":"Have you had a chance to connect with friends or family since my last visit?","hideNumber":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"comment","name":"Comment7","title":"Comment","hideNumber":true},{"type":"radiogroup","name":"Do you want to go for a walk?","title":"Do you want to go for a walk?","hideNumber":true,"isRequired":true,"renderAs":"prettycheckbox","choices":[{"value":"YES","text":"Yes"},{"value":"NO","text":"No"}]},{"type":"comment","name":"Comment8","title":"Comment","hideNumber":true}]}]}'
           ,'In-Home Care'
           ,0
           ,NULL
           ,NULL
           ,0
           ,0
           ,'IN_HOME_CARE'
           ,0)
GO


