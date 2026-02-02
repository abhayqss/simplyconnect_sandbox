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
     ,'SAD PERSONS Scale assessment'
     ,N'{
   "pages":[
      {
         "name":"page1",
         "elements":[
            {
               "type":"radiogroup",
               "name":"question1",
               "title":"Gender?",
               "validators":[
                  {
                     "type":"expression",
                     "text":"Please fill the required field",
                     "expression":"{question1} notempty"
                  }
               ],
               "choices":[
                  {
                     "value":"item1",
                     "text":"Male"
                  },
                  {
                     "value":"item2",
                     "text":"Female"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question2",
               "title":"Age?",
               "validators":[
                  {
                     "type":"expression",
                     "text":"Please fill the required field",
                     "expression":"{question2} notempty"
                  }
               ],
               "choices":[
                  {
                     "value":"item1",
                     "text":"19 years or younger"
                  },
                  {
                     "value":"item2",
                     "text":"20-44 years"
                  },
                  {
                     "value":"item3",
                     "text":"45 years or older"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question3",
               "title":"Depression or Hopelessness?",
               "validators":[
                  {
                     "type":"expression",
                     "text":"Please fill the required field",
                     "expression":"{question3} notempty"
                  }
               ],
               "choices":[
                  {
                     "value":"item1",
                     "text":"Yes"
                  },
                  {
                     "value":"item2",
                     "text":"No"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question4",
               "title":"Previous Suicide Attempts?",
               "validators":[
                  {
                     "type":"expression",
                     "text":"Please fill the required field",
                     "expression":"{question4} notempty"
                  }
               ],
               "choices":[
                  {
                     "value":"item1",
                     "text":"Yes"
                  },
                  {
                     "value":"item2",
                     "text":"No"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question5",
               "title":"Excessive alcohol or Drug Use?",
               "validators":[
                  {
                     "type":"expression",
                     "text":"Please fill the required field",
                     "expression":"{question5} notempty"
                  }
               ],
               "choices":[
                  {
                     "value":"item1",
                     "text":"Yes"
                  },
                  {
                     "value":"item2",
                     "text":"No"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question6",
               "title":"Rational Thinking Loss/Psychosis?",
               "validators":[
                  {
                     "type":"expression",
                     "text":"Please fill the required field",
                     "expression":"{question6} notempty"
                  }
               ],
               "choices":[
                  {
                     "value":"item1",
                     "text":"Yes"
                  },
                  {
                     "value":"item2",
                     "text":"No"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question7",
               "title":"Separated, Divorced or Widowed?",
               "validators":[
                  {
                     "type":"expression",
                     "text":"Please fill the required field",
                     "expression":"{question7} notempty"
                  }
               ],
               "choices":[
                  {
                     "value":"item1",
                     "text":"Yes"
                  },
                  {
                     "value":"item2",
                     "text":"No"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question8",
               "title":"Organized or Serious Attempt?",
               "validators":[
                  {
                     "type":"expression",
                     "text":"Please fill the required field",
                     "expression":"{question8} notempty"
                  }
               ],
				"choices":[
                  {
                     "value":"item1",
                     "text":"Yes"
                  },
                  {
                     "value":"item2",
                     "text":"No"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question9",
               "title":"No social support?",
			    "validators":[
                  {
                     "type":"expression",
                     "text":"Please fill the required field",
                     "expression":"{question9} notempty"
                  }
               ],
               "choices":[
                  {
                     "value":"item1",
                     "text":"Yes"
                  },
                  {
                     "value":"item2",
                     "text":"No"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question10",
               "title":"Stated future intent?",
               "validators":[
                  {
                     "type":"expression",
                     "text":"Please fill the required field",
                     "expression":"{question10} notempty"
                  }
               ],
               "choices":[
                  {
                     "value":"item1",
                     "text":"Yes"
                  },
                  {
                     "value":"item2",
                     "text":"No"
                  }
               ]
            }
         ],
         "title":"SAD PERSONS Scale"
      }
   ]
}'
     ,'Sad Persons Scale'
     ,1
     ,'Score'
     ,''
     ,0
     ,'Sad Persons'
     ,0)
  GO

DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE name = 'SAD PERSONS Scale assessment';

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item2', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item3', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item1', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item1', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item1', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item1', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item2', 0);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting) VALUES (@assessmentId, 0, 5, 'May discharge home if have someone to care for them, follow-up within 1 day, call follow up MD, and create safety plan (contracting for safety is not helpful, but creating a crisis plan may be)', 'Create safety plan', 'Low');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting) VALUES (@assessmentId, 6, 8, '50% admission rate (consult psychiatry for further risk assessment)', 'Consult psychiatry', 'Medium');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting) VALUES (@assessmentId, 9, 14, 'Admit: immediate psychiatric hospitalization', 'Psychiatric hospitalization', 'High');


