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
     ,'Alcohol Use Disorder Identification Test'
     ,N'{
 "pages": [
  {
   "name": "page1",
   "elements": [
    {
     "type": "radiogroup",
     "name": "question1",
     "title": "How often do you have a drink containing alcohol?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question1} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Never"
      },
      {
       "value": "item2",
       "text": "Monthly or less"
      },
      {
       "value": "item3",
       "text": "2 to 4 times a month"
      },
      {
       "value": "item4",
       "text": "2 to 3 times a month"
      },
      {
       "value": "item5",
       "text": "4 or more times a week"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question2",
     "visibleIf": "{question1} = \"item2\" or {question1} = \"item3\" or {question1} = \"item4\" or {question1} = \"item5\"",
     "title": "How many drinks containing alcohol do you have on a typical day when you are drinking?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question2} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "1 or 2"
      },
      {
       "value": "item2",
       "text": "3 or 4"
      },
      {
       "value": "item3",
       "text": "5 or 6"
      },
      {
       "value": "item4",
       "text": "7, 8 or 9"
      },
      {
       "value": "item5",
       "text": "10 or more"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question3",
     "visibleIf": "({question1} = \"item2\" or {question1} = \"item3\" or {question1} = \"item4\" or {question1} = \"item5\")",
     "title": "How often do you have six or more drinks on one occasion?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question3} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Never"
      },
      {
       "value": "item2",
       "text": "Less than monthly"
      },
      {
       "value": "item3",
       "text": "Monthly"
      },
      {
       "value": "item4",
       "text": "Weekly"
      },
      {
       "value": "item5",
       "text": "Daily or almost daily"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question4",
     "visibleIf": "{question1} = \"item2\" or {question1} = \"item3\" or {question1} = \"item4\" or {question1} = \"item5\" and ({question2} = \"item2\" or {question2} = \"item3\" or {question2} = \"item4\" or {question2} = \"item5\" or {question3} = \"item2\" or {question3} = \"item3\" or {question3} = \"item4\" or {question3} = \"item5\")",
     "title": "How often during the last year have you found that you were not able to stop drinking once you had started?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question4} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Never"
      },
      {
       "value": "item2",
       "text": "Less than monthly"
      },
      {
       "value": "item3",
       "text": "Monthly"
      },
      {
       "value": "item4",
       "text": "Weekly"
      },
      {
       "value": "item5",
       "text": "Daily or almost daily"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question5",
     "visibleIf": "{question1} = \"item2\" or {question1} = \"item3\" or {question1} = \"item4\" or {question1} = \"item5\" and ({question2} = \"item2\" or {question2} = \"item3\" or {question2} = \"item4\" or {question2} = \"item5\" or {question3} = \"item2\" or {question3} = \"item3\" or {question3} = \"item4\" or {question3} = \"item5\")",
     "title": "How often during the last year have you failed to do what was normally expected from you because of drinking?",
     "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question5} notempty"
        }
     ],
	 "choices": [
      {
       "value": "item1",
       "text": "Never"
      },
      {
       "value": "item2",
       "text": "Less than monthly"
      },
      {
       "value": "item3",
       "text": "Monthly"
      },
      {
       "value": "item4",
       "text": "Weekly"
      },
      {
       "value": "item5",
       "text": "Daily or almost daily"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question6",
     "visibleIf": "{question1} = \"item2\" or {question1} = \"item3\" or {question1} = \"item4\" or {question1} = \"item5\" and ({question2} = \"item2\" or {question2} = \"item3\" or {question2} = \"item4\" or {question2} = \"item5\" or {question3} = \"item2\" or {question3} = \"item3\" or {question3} = \"item4\" or {question3} = \"item5\")",
     "title": "How often during the last year have you needed a first drink in the morning to get yourself going after a heavy drinking session?",
     "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question6} notempty"
        }
      ],
	 "choices": [
      {
       "value": "item1",
       "text": "Never"
      },
      {
       "value": "item2",
       "text": "Less than monthly"
      },
      {
       "value": "item3",
       "text": "Monthly"
      },
      {
       "value": "item4",
       "text": "Weekly"
      },
      {
       "value": "item5",
       "text": "Daily or almost daily"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question7",
     "visibleIf": "{question1} = \"item2\" or {question1} = \"item3\" or {question1} = \"item4\" or {question1} = \"item5\" and ({question2} = \"item2\" or {question2} = \"item3\" or {question2} = \"item4\" or {question2} = \"item5\" or {question3} = \"item2\" or {question3} = \"item3\" or {question3} = \"item4\" or {question3} = \"item5\")",
     "title": "How often during the last year have you had a feeling of guilt or remorse after drinking?",
     "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question7} notempty"
        }
      ],
	 "choices": [
      {
       "value": "item1",
       "text": "Never"
      },
      {
       "value": "item2",
       "text": "Less than monthly"
      },
      {
       "value": "item3",
       "text": "Monthly"
      },
      {
       "value": "item4",
       "text": "Weekly"
      },
      {
       "value": "item5",
       "text": "Daily or almost daily"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question8",
     "visibleIf": "{question1} = \"item2\" or {question1} = \"item3\" or {question1} = \"item4\" or {question1} = \"item5\" and ({question2} = \"item2\" or {question2} = \"item3\" or {question2} = \"item4\" or {question2} = \"item5\" or {question3} = \"item2\" or {question3} = \"item3\" or {question3} = \"item4\" or {question3} = \"item5\")",
     "title": "How often during the last year have you been unable to remember what happened the night before because you had been drinking?",
     "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question8} notempty"
        }
      ],
	 "choices": [
      {
       "value": "item1",
       "text": "Never"
      },
      {
       "value": "item2",
       "text": "Less than monthly"
      },
      {
       "value": "item3",
       "text": "Monthly"
      },
      {
       "value": "item4",
       "text": "Weekly"
      },
      {
       "value": "item5",
       "text": "Daily or almost daily"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question9",
     "title": "Have you or someone else been injured as a result of your drinking?",
	  "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question9} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "No"
      },
      {
       "value": "item2",
       "text": "Yes, but not in the last year"
      },
      {
       "value": "item3",
       "text": "Yes, during the last year"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question10",
     "title": "Has a relative or friend or a doctor or another health worker been concerned about your drinking or suggested you cut down?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question10} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "No"
      },
      {
       "value": "item2",
       "text": "Yes, but not in the last year"
      },
      {
       "value": "item3",
       "text": "Yes, during the last year"
      }
     ]
    }
   ],
   "title":"Alcohol Use Disorder Identification Test"
  }
 ]
}'
     ,'AUDIT'
     ,1
     ,'Result'
     ,''
     ,0
     ,'AUDIT'
     ,0)
  GO

DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE name = 'Alcohol Use Disorder Identification Test';

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item3', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item4', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item5', 4);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item3', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item4', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item5', 4);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item3', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item4', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item5', 4);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item3', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item4', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item5', 4);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item3', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item4', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item5', 4);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item3', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item4', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item5', 4);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item3', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item4', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item5', 4);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item3', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item4', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item5', 4);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item3', 4);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item3', 4);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 0, 7, 'Scores 0-7 suggest low risk of problems with alcohol.', 'Low risk', 'Low', 'You probably do not have a problem with alcohol. Continue drinking in moderation or not at all.');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 8, 15, 'Scores 8-15 suggest medium risk of problems with alcohol.', 'Medium risk', 'Medium', 'You may drink too much on occasion. This may put you or others at risk. Try to cut down on alcohol or stop drinking completely.');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 16, 19, 'Scores 16-19 suggest high risk of problems with alcohol.', 'High risk', 'High', 'Your drinking could lead to harm, if it has not already. It is important that you cut down on alcohol or stop drinking completely. Ask your doctor or nurse for advice on how best to cut down.');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 20, 40, 'Scores 20-40 suggest high risk of problems with alcohol.', 'Addiction likely', 'High', 'It is likely that your drinking is causing harm. Speak to your doctor or nurse, or an addiction specialist. Ask about medications and counseling that can help you stop drinking. If you are dependent on alcohol, do not stop drinking without the help of a healthcare professional.');


