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
     ,'Adolescent alcohol use disorder screening (CRAFFT) assessment'
     ,N'{
 "pages": [
  {
   "name": "page1",
   "elements": [
    {
		"type":"panel",
        "name":"panel1",
        "elements": [
			{
				 "type": "radiogroup",
				 "name": "question1",
				 "title": "Drink any alcohol (more than a few sips)? Do not count sips of alcohol taken during family or religious events",
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
				 "title": "Smoke any marijuana or hashish? ",
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
				 "title": "Use anything else to get high? “Anything else” includes illegal drugs, over the counter and prescription drugs, and things that you sniff or “huff.”",
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
			}
		],
		"isRequired":true,
		"title":"Part A. During the PAST 12 MONTHS, did you : "
	},
	{
		"type":"panel",
        "name":"panel2",
        "elements": [
			{
				 "type": "radiogroup",
				 "name": "question4",
				 "title": "Have you ever ridden in a CAR driven by someone (including yourself) who was “high” or had been using alcohol or drugs? ",
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
			},
			{
				 "type": "radiogroup",
				 "name": "question5",
				 "visibleIf": "{question1} = \"item1\" or {question2} = \"item1\" or {question3} = \"item1\"",
				 "title": " Do you ever use alcohol or drugs to RELAX, feel better about yourself, or fit in? ",
				 "validators":[
                        {
                           "type":"expression",
                           "text":"Please fill the required field",
                           "expression":"{question5} notempty"
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
				 "name": "question6",
				 "visibleIf": "{question1} = \"item1\" or {question2} = \"item1\" or {question3} = \"item1\"",
				 "title": "Do you ever use alcohol or drugs while you are by yourself, or ALONE? ",
				 "validators":[
                        {
                           "type":"expression",
                           "text":"Please fill the required field",
                           "expression":"{question6} notempty"
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
				 "name": "question7",
				 "visibleIf": "{question1} = \"item1\" or {question2} = \"item1\" or {question3} = \"item1\"",
				 "title": "Do you ever FORGET things you did while using alcohol or drugs? ",
				 "validators":[
                        {
                           "type":"expression",
                           "text":"Please fill the required field",
                           "expression":"{question7} notempty"
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
				 "name": "question8",
				 "visibleIf": "{question1} = \"item1\" or {question2} = \"item1\" or {question3} = \"item1\"",
				 "title": " Do your FAMILY or FRIENDS ever tell you that you should cut down on your drinking or drug use? ",
				 "validators":[
                        {
                           "type":"expression",
                           "text":"Please fill the required field",
                           "expression":"{question8} notempty"
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
				 "name": "question9",
				 "visibleIf": "{question1} = \"item1\" or {question2} = \"item1\" or {question3} = \"item1\"",
				 "title": "Have you ever gotten into TROUBLE while you were using alcohol or drugs? ",
				 "validators":[
                        {
                           "type":"expression",
                           "text":"Please fill the required field",
                           "expression":"{question9} notempty"
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
			   "type": "comment",
			   "name": "question10",
			   "title": "What substances do you use?",
			   "visibleIf": "({question1} = \"item1\" and {question2} = \"item1\") or ({question1} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\" and {question5} = \"item1\") or ({question1} = \"item1\" and {question6} = \"item1\") or ({question1} = \"item1\" and {question7} = \"item1\") or ({question1} = \"item1\" and {question8} = \"item1\") or ({question1} = \"item1\" and {question9} = \"item1\") or ({question2} = \"item1\" and {question3} = \"item1\") or ({question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\" and {question5} = \"item1\") or ({question2} = \"item1\" and {question6} = \"item1\") or ({question2} = \"item1\" and {question7} = \"item1\") or ({question2} = \"item1\" and {question8} = \"item1\") or ({question2} = \"item1\" and {question9} = \"item1\") or ({question3} = \"item1\" and {question4} = \"item1\") or ({question3} = \"item1\" and {question5} = \"item1\") or ({question3} = \"item1\" and {question6} = \"item1\") or ({question3} = \"item1\" and {question7} = \"item1\") or ({question3} = \"item1\" and {question8} = \"item1\") or ({question3} = \"item1\" and {question9} = \"item1\") or ({question4} = \"item1\" and {question5} = \"item1\") or ({question4} = \"item1\" and {question6} = \"item1\") or ({question4} = \"item1\" and {question7} = \"item1\") or ({question4} = \"item1\" and {question8} = \"item1\") or ({question4} = \"item1\" and {question9} = \"item1\") or ({question5} = \"item1\" and {question6} = \"item1\") or ({question5} = \"item1\" and {question7} = \"item1\") or ({question5} = \"item1\" and {question8} = \"item1\") or ({question5} = \"item1\" and {question9} = \"item1\") or ({question6} = \"item1\" and {question7} = \"item1\") or ({question6} = \"item1\" and {question8} = \"item1\") or ({question6} = \"item1\" and {question9} = \"item1\") or ({question7} = \"item1\" and {question8} = \"item1\") or ({question7} = \"item1\" and {question9} = \"item1\") or ({question8} = \"item1\" and {question9} = \"item1\")",
			   "maxLength": 5000
			},
			{
			   "type": "comment",
			   "name": "question11",
			   "title": "How, where, and with whom do you use?",
			   "visibleIf": "({question1} = \"item1\" and {question2} = \"item1\") or ({question1} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\" and {question5} = \"item1\") or ({question1} = \"item1\" and {question6} = \"item1\") or ({question1} = \"item1\" and {question7} = \"item1\") or ({question1} = \"item1\" and {question8} = \"item1\") or ({question1} = \"item1\" and {question9} = \"item1\") or ({question2} = \"item1\" and {question3} = \"item1\") or ({question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\" and {question5} = \"item1\") or ({question2} = \"item1\" and {question6} = \"item1\") or ({question2} = \"item1\" and {question7} = \"item1\") or ({question2} = \"item1\" and {question8} = \"item1\") or ({question2} = \"item1\" and {question9} = \"item1\") or ({question3} = \"item1\" and {question4} = \"item1\") or ({question3} = \"item1\" and {question5} = \"item1\") or ({question3} = \"item1\" and {question6} = \"item1\") or ({question3} = \"item1\" and {question7} = \"item1\") or ({question3} = \"item1\" and {question8} = \"item1\") or ({question3} = \"item1\" and {question9} = \"item1\") or ({question4} = \"item1\" and {question5} = \"item1\") or ({question4} = \"item1\" and {question6} = \"item1\") or ({question4} = \"item1\" and {question7} = \"item1\") or ({question4} = \"item1\" and {question8} = \"item1\") or ({question4} = \"item1\" and {question9} = \"item1\") or ({question5} = \"item1\" and {question6} = \"item1\") or ({question5} = \"item1\" and {question7} = \"item1\") or ({question5} = \"item1\" and {question8} = \"item1\") or ({question5} = \"item1\" and {question9} = \"item1\") or ({question6} = \"item1\" and {question7} = \"item1\") or ({question6} = \"item1\" and {question8} = \"item1\") or ({question6} = \"item1\" and {question9} = \"item1\") or ({question7} = \"item1\" and {question8} = \"item1\") or ({question7} = \"item1\" and {question9} = \"item1\") or ({question8} = \"item1\" and {question9} = \"item1\")",
			   "maxLength": 5000
			},
			{
			   "type": "comment",
			   "name": "question12",
			   "title": "How often do you use?",
			   "visibleIf": "({question1} = \"item1\" and {question2} = \"item1\") or ({question1} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\" and {question5} = \"item1\") or ({question1} = \"item1\" and {question6} = \"item1\") or ({question1} = \"item1\" and {question7} = \"item1\") or ({question1} = \"item1\" and {question8} = \"item1\") or ({question1} = \"item1\" and {question9} = \"item1\") or ({question2} = \"item1\" and {question3} = \"item1\") or ({question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\" and {question5} = \"item1\") or ({question2} = \"item1\" and {question6} = \"item1\") or ({question2} = \"item1\" and {question7} = \"item1\") or ({question2} = \"item1\" and {question8} = \"item1\") or ({question2} = \"item1\" and {question9} = \"item1\") or ({question3} = \"item1\" and {question4} = \"item1\") or ({question3} = \"item1\" and {question5} = \"item1\") or ({question3} = \"item1\" and {question6} = \"item1\") or ({question3} = \"item1\" and {question7} = \"item1\") or ({question3} = \"item1\" and {question8} = \"item1\") or ({question3} = \"item1\" and {question9} = \"item1\") or ({question4} = \"item1\" and {question5} = \"item1\") or ({question4} = \"item1\" and {question6} = \"item1\") or ({question4} = \"item1\" and {question7} = \"item1\") or ({question4} = \"item1\" and {question8} = \"item1\") or ({question4} = \"item1\" and {question9} = \"item1\") or ({question5} = \"item1\" and {question6} = \"item1\") or ({question5} = \"item1\" and {question7} = \"item1\") or ({question5} = \"item1\" and {question8} = \"item1\") or ({question5} = \"item1\" and {question9} = \"item1\") or ({question6} = \"item1\" and {question7} = \"item1\") or ({question6} = \"item1\" and {question8} = \"item1\") or ({question6} = \"item1\" and {question9} = \"item1\") or ({question7} = \"item1\" and {question8} = \"item1\") or ({question7} = \"item1\" and {question9} = \"item1\") or ({question8} = \"item1\" and {question9} = \"item1\")",
        	   "maxLength": 5000
			},
			{
			   "type": "comment",
			   "name": "question13",
			   "title": "Because of your alcohol use, how often have you had trouble at school or with grades, had arguments with family or friends, or gotten into fights?",
			   "visibleIf": "({question1} = \"item1\" and {question2} = \"item1\") or ({question1} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\" and {question5} = \"item1\") or ({question1} = \"item1\" and {question6} = \"item1\") or ({question1} = \"item1\" and {question7} = \"item1\") or ({question1} = \"item1\" and {question8} = \"item1\") or ({question1} = \"item1\" and {question9} = \"item1\") or ({question2} = \"item1\" and {question3} = \"item1\") or ({question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\" and {question5} = \"item1\") or ({question2} = \"item1\" and {question6} = \"item1\") or ({question2} = \"item1\" and {question7} = \"item1\") or ({question2} = \"item1\" and {question8} = \"item1\") or ({question2} = \"item1\" and {question9} = \"item1\") or ({question3} = \"item1\" and {question4} = \"item1\") or ({question3} = \"item1\" and {question5} = \"item1\") or ({question3} = \"item1\" and {question6} = \"item1\") or ({question3} = \"item1\" and {question7} = \"item1\") or ({question3} = \"item1\" and {question8} = \"item1\") or ({question3} = \"item1\" and {question9} = \"item1\") or ({question4} = \"item1\" and {question5} = \"item1\") or ({question4} = \"item1\" and {question6} = \"item1\") or ({question4} = \"item1\" and {question7} = \"item1\") or ({question4} = \"item1\" and {question8} = \"item1\") or ({question4} = \"item1\" and {question9} = \"item1\") or ({question5} = \"item1\" and {question6} = \"item1\") or ({question5} = \"item1\" and {question7} = \"item1\") or ({question5} = \"item1\" and {question8} = \"item1\") or ({question5} = \"item1\" and {question9} = \"item1\") or ({question6} = \"item1\" and {question7} = \"item1\") or ({question6} = \"item1\" and {question8} = \"item1\") or ({question6} = \"item1\" and {question9} = \"item1\") or ({question7} = \"item1\" and {question8} = \"item1\") or ({question7} = \"item1\" and {question9} = \"item1\") or ({question8} = \"item1\" and {question9} = \"item1\")",
			   "maxLength": 5000
			},
			{
			   "type": "comment",
			   "name": "question14",
			   "title": "What types of risky behavior have you displayed while drinking?",
			   "visibleIf": "({question1} = \"item1\" and {question2} = \"item1\") or ({question1} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\" and {question5} = \"item1\") or ({question1} = \"item1\" and {question6} = \"item1\") or ({question1} = \"item1\" and {question7} = \"item1\") or ({question1} = \"item1\" and {question8} = \"item1\") or ({question1} = \"item1\" and {question9} = \"item1\") or ({question2} = \"item1\" and {question3} = \"item1\") or ({question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\" and {question5} = \"item1\") or ({question2} = \"item1\" and {question6} = \"item1\") or ({question2} = \"item1\" and {question7} = \"item1\") or ({question2} = \"item1\" and {question8} = \"item1\") or ({question2} = \"item1\" and {question9} = \"item1\") or ({question3} = \"item1\" and {question4} = \"item1\") or ({question3} = \"item1\" and {question5} = \"item1\") or ({question3} = \"item1\" and {question6} = \"item1\") or ({question3} = \"item1\" and {question7} = \"item1\") or ({question3} = \"item1\" and {question8} = \"item1\") or ({question3} = \"item1\" and {question9} = \"item1\") or ({question4} = \"item1\" and {question5} = \"item1\") or ({question4} = \"item1\" and {question6} = \"item1\") or ({question4} = \"item1\" and {question7} = \"item1\") or ({question4} = \"item1\" and {question8} = \"item1\") or ({question4} = \"item1\" and {question9} = \"item1\") or ({question5} = \"item1\" and {question6} = \"item1\") or ({question5} = \"item1\" and {question7} = \"item1\") or ({question5} = \"item1\" and {question8} = \"item1\") or ({question5} = \"item1\" and {question9} = \"item1\") or ({question6} = \"item1\" and {question7} = \"item1\") or ({question6} = \"item1\" and {question8} = \"item1\") or ({question6} = \"item1\" and {question9} = \"item1\") or ({question7} = \"item1\" and {question8} = \"item1\") or ({question7} = \"item1\" and {question9} = \"item1\") or ({question8} = \"item1\" and {question9} = \"item1\")",
			   "maxLength": 5000
			}
		],
		"isRequired":true,
		"title":"Part B"
	}
	'
     ,'CRAFFT'
     ,1
     ,'Result'
     ,''
     ,0
     ,'CRAFFT'
     ,0)
  GO

DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE name = 'Adolescent alcohol use disorder screening (CRAFFT) assessment';

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item2', 0);


INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 0, 1, 'Low risk of drug and/or alcohol use disorder', 'Low risk of drug and/or alcohol use disorder', 'Low', 'Review CRAFFT score with patient.
Discuss alcohol and substance use risks and consequences (e.g., motor vehicle
crashes).
Recognize and commend the patient for avoiding alcohol and drug use.');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 2, 9, 'High risk of drug and/or alcohol use disorder	', 'High risk of drug and/or alcohol use disorder	', 'High', 'Review the CRAFFT score with patient.

Discuss with the patient in an empathetic, patient-centered manner that—based on the screen—you are concerned about their use of alcohol.
•	“Kids who have this score may be drinking in a way that could cause problems at home or at school, or could lead to criminal activity.”
•	“I’m concerned that you are drinking enough to cause other serious problems in your life.”
Acknowledge that alcohol use has pros and cons for the patient. Ask what they
like most—and least—about drinking.

When the parent is also in the room, ask if they have any concerns about the adolescent’s moods, school, friends, or alcohol use in general (with no
disclosure of the actual patient conversation).');


