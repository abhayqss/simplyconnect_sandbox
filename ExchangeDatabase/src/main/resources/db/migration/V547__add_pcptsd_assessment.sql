INSERT INTO [dbo].[Assessment]
([assessment_group_id]
,[name]
,[json_content]
,[short_name]
,[scoring_enabled]
,[severity_column_name]
,[management_comment]
,[has_numeration]
,[code]
,[type])
VALUES
     (1
     ,'Primary Care PTSD Screen (PC-PTSD) assessment'
     ,N'{
 "pages": [
  {
   "name": "page1",
   "elements": [
    {
	 "type":"panel",
     "name":"panel1",
	 "elements":[
		{
		 "type": "radiogroup",
		 "name": "question1",
		 "title": "Have had nightmares about it or thought about it when you did not want to? ",
		 "validators":[
			{
                "type":"expression",
                "text":"Please fill the required field",
				"expression":"{question1} notempty"
            }
         ],
		 "choices": [
		  {
		   "value": "item1",
		   "text": "Yes"
		  },
		  {
		   "value": "item2",
		   "text": "No"
		  }
		 ]
		},
		{
		 "type": "radiogroup",
		 "name": "question2",
		 "title": "Tried hard not to think about it or went out of your way to avoid situations that reminded you of it?",
		 "validators":[
			{
                "type":"expression",
                "text":"Please fill the required field",
				"expression":"{question2} notempty"
            }
         ],
		 "choices": [
		  {
		   "value": "item1",
		   "text": "Yes"
		  },
		  {
		   "value": "item2",
		   "text": "No"
		  }
		 ]
		},
		{
		 "type": "radiogroup",
		 "name": "question3",
		 "title": "Were constantly on guard, watchful, or easily startled? ",
		 "validators":[
			{
                "type":"expression",
                "text":"Please fill the required field",
				"expression":"{question3} notempty"
            }
         ],
		 "choices": [
		  {
		   "value": "item1",
		   "text": "Yes"
		  },
		  {
		   "value": "item2",
		   "text": "No"
		  }
		 ]
		},
		{
		 "type": "radiogroup",
		 "name": "question4",
		 "title": "Felt numb or detached from others, activities, or your surroundings?",
		 "validators":[
			{
                "type":"expression",
                "text":"Please fill the required field",
				"expression":"{question4} notempty"
            }
         ],
		 "choices": [
		  {
		   "value": "item1",
		   "text": "Yes"
		  },
		  {
		   "value": "item2",
		   "text": "No"
		  }
		 ]
		}
	],
	 "isRequired":true,
	 "title":"In your life, have you ever had any experience that was so frightening, horrible, or upsetting that, in the past month, you: "
    },
	{
		"type":"panel",
		"name":"panel2",
		"elements": [
			{
			   "type": "comment",
			   "name": "question5",
			   "visibleIf": "({question1} = \"item1\" and {question2} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\"\n and {question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\"\n and {question3} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\"\n and {question3} = \"item1\" and {question4} = \"item1\")",
			   "title": "Whether the patient has had a traumatic experience: \"I notice from your answers to our questionnaire that you experience some symptoms of stress. At some point in their lives, many people have experienced extremely distressing events such as combat, physical or sexual assault, or a bad accident, and sometimes those events lead to the kinds of symptoms you have. Have you ever had any experiences like that?\" ",
			   "maxLength": 5000
			},
			{
			   "type": "comment",
			   "name": "question6",
			   "visibleIf": "({question1} = \"item1\" and {question2} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\"\n and {question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\"\n and {question3} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\"\n and {question3} = \"item1\" and {question4} = \"item1\")",
			   "title": "Whether endorsed screen items are really trauma-related symptoms: \"I see that you have said you have nightmares about or have thought about an upsetting experience when you did not want to. Can you give me an example of a nightmare or thinking about an upsetting experience when you didn''t want to?\"",
			   "maxLength": 5000
			},
			{
			   "type": "comment",
			   "name": "question7",
			   "visibleIf": "({question1} = \"item1\" and {question2} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\"\n and {question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\"\n and {question3} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\"\n and {question3} = \"item1\" and {question4} = \"item1\")",
			   "title": "Whether endorsed screen items are disruptive to the patient''s life \"How have these thoughts, memories, or feelings affected your life? Have they interfered with your relationships? Your work? How about with recreation or your enjoyment of activities?\" ",
			   "maxLength": 5000
			},
			{
			   "type": "comment",
			   "name": "question8",
			   "visibleIf": "({question1} = \"item1\" and {question2} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\"\n and {question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\"\n and {question3} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\"\n and {question3} = \"item1\" and {question4} = \"item1\")",
			   "title": "Discern whether traumatic events are ongoing in a patient''s life: \"Are any of these dangerous or life-threatening experiences still continuing in your life now?\" ",
			   "maxLength": 5000
			}
		],
		"isRequired":true,
		"title": "Trauma-related questions"
	}
   ]
  }
 ]
}'
     ,'PC-PTSD'
     ,1
     ,'Result'
     ,''
     ,0
     ,'PC-PTSD'
     ,0)
  GO

DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE name = 'Primary Care PTSD Screen (PC-PTSD) assessment';

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item2', 0);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting) VALUES (@assessmentId, 0, 2, 'Negative', 'Negative', 'Low');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting) VALUES (@assessmentId, 3, 4, 'Positive.
A positive result indicates that an individual may have Posttraumatic Stess Disorder, which can be determined by a clinical interview.
This screening tool is not a replacement for advice from a medical/clinical professional.', 'Positive', 'High');




