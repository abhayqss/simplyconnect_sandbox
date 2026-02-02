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
     ,'Geriatric Depression Scale (long) assessment'
     ,N'{
  "pages": [
    {
      "name": "page1",
      "elements": [
        {
          "type": "radiogroup",
          "name": "question1",
          "title": "Are you basically satisfied with your life?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question1} notempty"
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
          "title": "Have you dropped many of your activities and interests?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question2} notempty"
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
          "title": "Do you feel that your life is empty?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question3} notempty"
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
          "title": "Do you often get bored?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question4} notempty"
				}
		  ],
          "choices": [
            {
              "value": "item1",
              "text": "Yes "
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
          "title": "Are you hopeful about the future?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question5} notempty"
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
          "title": "Are you bothered by thoughts you can''t get out of your head?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question6} notempty"
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
          "title": "Are you in good spirits most of the time?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question7} notempty"
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
          "title": "Are you afraid that something bad is going to happen to you?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question8} notempty"
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
          "title": "Do you feel happy most of the time?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question9} notempty"
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
          "name": "question10",
          "title": "Do you often feel hopeless?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question10} notempty"
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
          "name": "question11",
          "title": "Do you often get restless and fidgety?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question11} notempty"
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
          "name": "question12",
          "title": "Do you prefer to stay at home, rather than going out and doing new things?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question12} notempty"
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
          "name": "question13",
          "title": "Do you frequently worry about the future?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question13} notempty"
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
          "name": "question14",
          "title": "Do you feel you have more problems with memory than most?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question14} notempty"
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
          "name": "question15",
          "title": "Do you think it is wonderful to be alive now?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question15} notempty"
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
          "name": "question16",
          "title": "Do you often feel downhearted and blue?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question16} notempty"
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
          "name": "question17",
          "title": "Do you feel pretty worthless the way you are now?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question17} notempty"
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
          "name": "question18",
          "title": "Do you worry a lot about the past?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question18} notempty"
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
          "name": "question19",
          "title": "Do you find life very exciting?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question19} notempty"
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
          "name": "question20",
          "title": "Is it hard for you to get started on new projects?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question20} notempty"
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
          "name": "question21",
          "title": "Do you feel full of energy?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question21} notempty"
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
          "name": "question22",
          "title": "Do you feel that your situation is hopeless?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question22} notempty"
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
          "name": "question23",
          "title": "Do you think that most people are better off than you are?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question23} notempty"
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
          "name": "question24",
          "title": "Do you frequently get upset over little things?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question24} notempty"
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
          "name": "question25",
          "title": "Do you frequently feel like crying?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question25} notempty"
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
          "name": "question26",
          "title": "Do you have trouble concentrating?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question26} notempty"
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
          "name": "question27",
          "title": "Do you enjoy getting up in the morning?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question27} notempty"
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
          "name": "question28",
          "title": "Do you prefer to avoid social gatherings?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question28} notempty"
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
          "name": "question29",
          "title": "Is it easy for you to make decisions?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question29} notempty"
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
          "name": "question30",
          "title": "Is your mind as clear as it used to be?",
		  "validators": [
				{
				  "type": "expression",
				  "text": "Please fill the required field",
				  "expression": "{question30} notempty"
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
         "title":"Directions to give to the Client: Please choose the best answer for how you have felt over the past week?\n"
    }
  ]
}'
     ,'GDSL'
     ,1
     ,'Depression severity'
     ,''
     ,1
     , 'GDSL'
     ,0)
  GO

SET XACT_ABORT ON
  GO

BEGIN TRANSACTION TransactionWithGos;
GO

DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE name = 'Geriatric Depression Scale (long) assessment';

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question14', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question14', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question16', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question16', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question17', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question17', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question18', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question18', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question19', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question19', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question20', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question20', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question21', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question21', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question22', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question22', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question23', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question23', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question24', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question24', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question25', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question25', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question26', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question26', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question27', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question27', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question28', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question28', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question29', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question29', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question30', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question30', 'item2', 1);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 0, 9, 'Scores 0-9 are considered normal, depending on age, education, and complaints.', 'Normal', 'Low', 'Normal');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 10, 19, 'Scores 10-19 are indicative of mild depressives.', 'Medium', 'Medium', 'Mild depressives');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 20, 30, 'Scores 20-30 are indicative of severe depressives.', 'High', 'High', 'Severe depressives');

commit TRANSACTION TransactionWithGos;
GO

