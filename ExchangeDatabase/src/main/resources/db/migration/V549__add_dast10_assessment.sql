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
     ,'Drug Abuse Screening Test, DAST-10'
     ,N'{
	"pages": [{
		"name": "page1",
		"elements": [{
				"type": "html",
				"name": "text",
				"html": "\"Drug abuse\" refers to (1) the use of prescribed or over‐the‐counter drugs in excess of the directions, and (2) any nonmedical use of drugs.\nThe various classes of drugs may include cannabis (marijuana, hashish), solvents (e.g., paint thinner), tranquilizers (e.g., Valium), barbiturates, cocaine, stimulants (e.g., speed), hallucinogens (e.g., LSD) or narcotics (e.g., heroin). Remember that the questions do not include alcoholic beverages.\nPlease answer every question. If you have difficulty with a statement, then choose the response that is mostly right. Please answer the questions in the time frame of the last 12 months.\n"
			},
			{
				"type": "radiogroup",
				"name": "question1",
				"title": "Have you used drugs other than those required for medical reasons?",
				"validators": [{
					"type": "expression",
					"text": "Please fill the required field",
					"expression": "{question1} notempty"
				}],
				"choices": [{
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
				"title": "Do you abuse more than one drug at a time?",
				"validators": [{
					"type": "expression",
					"text": "Please fill the required field",
					"expression": "{question2} notempty"
				}],
				"choices": [{
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
				"title": "Are you always able to stop using drugs when you want to? ",
				"validators": [{
					"type": "expression",
					"text": "Please fill the required field",
					"expression": "{question3} notempty"
				}],
				"choices": [{
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
				"title": "Have you ever had \"blackouts\" or \"flashbacks\" as a result of drug use?",
				"validators": [{
					"type": "expression",
					"text": "Please fill the required field",
					"expression": "{question4} notempty"
				}],
				"choices": [{
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
				"title": "Do you ever feel bad or guilty about your drug use? ",
				"validators": [{
					"type": "expression",
					"text": "Please fill the required field",
					"expression": "{question5} notempty"
				}],
				"choices": [{
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
				"title": "Does your spouse (or parents) ever complain about your involvement with drugs?",
				"validators": [{
					"type": "expression",
					"text": "Please fill the required field",
					"expression": "{question6} notempty"
				}],
				"choices": [{
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
				"title": "Have you neglected your family because of your use of drugs?",
				"validators": [{
					"type": "expression",
					"text": "Please fill the required field",
					"expression": "{question7} notempty"
				}],
				"choices": [{
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
				"title": "Have you engaged in illegal activities in order to obtain drugs?",
				"validators": [{
					"type": "expression",
					"text": "Please fill the required field",
					"expression": "{question8} notempty"
				}],
				"choices": [{
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
				"title": "Have you ever experienced withdrawal symptoms (felt sick) when you stopped taking drugs?",
				"validators": [{
					"type": "expression",
					"text": "Please fill the required field",
					"expression": "{question9} notempty"
				}],
				"choices": [{
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
				"name": "question10",
				"title": "Have you had medical problems as a result of your drug use (e.g. memory loss, hepatitis, convulsions, bleeding)?",
				"validators": [{
					"type": "expression",
					"text": "Please fill the required field",
					"expression": "{question10} notempty"
				}],
				"choices": [{
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
		"title": "Drug Abuse Screening Test, DAST-10"
	}]
}'
     ,'DAST-10'
     ,1
     ,'Degree of Problems Related to Drug Abuse'
     ,''
     ,0
     ,'DAST_10'
     ,0)
  GO

DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE name = 'Drug Abuse Screening Test, DAST-10';

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item2', 0);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 0, 0, 'No problems reported', 'No problems reported', 'Low', 'None at this time');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 1, 2, 'Low level', 'Low level', 'Low', 'Monitor, reassess at a later date');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 3, 5, 'Moderate level', 'Moderate level', 'Medium', 'Further investigation');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 6, 8, 'Substantial level', 'Substantial level', 'High', 'Intensive assessment');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 9, 10, 'Severe level', 'Severe level', 'High', 'Intensive assessment');


