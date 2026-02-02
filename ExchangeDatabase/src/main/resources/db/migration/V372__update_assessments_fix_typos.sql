SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
UPDATE [dbo].[Assessment]
   SET [json_content] = N'{"pages":[{"name":"page1","elements":[{"type":"radiogroup","name":"question1","title":"Little interest or pleasure in doing things?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question2","title":"Feeling down, depressed, or hopeless?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question3","title":"Trouble falling or staying asleep, or sleeping too much?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question4","title":"Feeling tired or having little energy?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question5","title":"Poor appetite or overeating?","isRequired":true,"choices":[{"value":"item1","text":" Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question6","title":"Feeling bad about yourself - or that you are a failure or have let yourself or your family down?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question7","title":"Trouble concentrating on things, such as reading the newspaper or watching television?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question8","title":"Moving or speaking so slowly that other people could have noticed? Or so fidgety or restless that you have been moving a lot more than usual?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question9","title":"Thoughts that you would be better off dead, or thoughts of hurting yourself in some way?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question10","title":"How difficult have these problems made it for you to do your work, take care of things at home, or get along with other people?","choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Somewhat difficult"},{"value":"item3","text":"Very difficult"},{"value":"item4","text":"Extremely difficult"}]}],"title":"Over the last 2 weeks, how often have you been bothered by any of the following problems?\n"}]}'
 WHERE id=2
GO

UPDATE [dbo].[AssessmentScoringGroup]
   SET [severity] = N'Scores <=4 suggest minimal depression which may not require treatment.'
 WHERE id=5
GO
UPDATE [dbo].[AssessmentScoringGroup]
   SET [severity] = N'Scores 5-9 suggest mild depression which may require only watchful waiting and repeated PHQ-9 at followup.'
 WHERE id=6
GO
UPDATE [dbo].[AssessmentScoringGroup]
   SET [severity] = N'Scores 10-14 suggest moderate depression severity; patients should have a treatment plan ranging form counseling, followup, and/or pharmacotherapy.'
 WHERE id=7
GO
UPDATE [dbo].[AssessmentScoringGroup]
   SET [severity] = N'Scores 15-19 suggest moderately severe depression; patients typically should have immediate initiation of pharmacotherapy and/or psychotherapy.'
 WHERE id=8
GO
UPDATE [dbo].[AssessmentScoringGroup]
   SET [severity] = N'Scores 20 and greater suggest severe depression; patients typically should have immediate initiation of pharmacotherapy and expedited referral to mental health specialist.'
 WHERE id=9
GO

SET ANSI_PADDING OFF
GO
