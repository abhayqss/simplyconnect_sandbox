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
     ,'Mood Disorder Questionnaire assessment'
     ,N'{
   "pages":[
      {
         "name":"page1",
         "elements":[
            {
               "type":"panel",
               "name":"panel1",
               "elements":[
                  {
                     "type":"radiogroup",
                     "name":"question1",
                     "title":"…you felt so good or so hyper that other people thought you were not your normal self or you were so hyper that you got into trouble?",
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
                     "name":"question2",
                     "title":"…you were so irritable that you shouted at people or started fights or arguments?",
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
                     "name":"question3",
                     "title":"…you felt much more self-confident than usual?",
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
                     "title":"…you got much less sleep than usual and found you didn’t really miss it?",
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
                     "title":"…you were much more talkative or spoke faster than usual?",
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
                     "title":"…thoughts raced through your head or you couldn’t slow your mind down?",
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
                     "title":"…you were so easily distracted by things around you that you had trouble concentrating or staying on track?",
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
                     "title":"…you had much more energy than usual?",
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
                     "title":"...you were much more active or did many more things than usual?",
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
                     "title":"…you were much more social or outgoing than usual, for example, you telephoned friends in the middle of the night?",
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
                  },
                  {
                     "type":"radiogroup",
                     "name":"question11",
                     "title":"…you were much more interested in sex than usual?",
                     "validators":[
                        {
                           "type":"expression",
                           "text":"Please fill the required field",
                           "expression":"{question11} notempty"
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
                     "name":"question12",
                     "title":"…you did things that were unusual for you or that other people might have thought were excessive, foolish, or risky?",
                     "validators":[
                        {
                           "type":"expression",
                           "text":"Please fill the required field",
                           "expression":"{question12} notempty"
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
                     "name":"question13",
                     "title":"…spending money got you or your family in trouble?",
                     "validators":[
                        {
                           "type":"expression",
                           "text":"Please fill the required field",
                           "expression":"{question13} notempty"
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
               "isRequired":true,
			   "title":"1. Has there ever been a period of time when you were not your usual self and…"
            },
            {
			"type":"panel",
			"name":"panel2",
			"elements":[{
				"type":"radiogroup",
               "name":"question14",
			   "title" : "Have several of these ever happened during the same period of time? ",
               "visibleIf":"{question1} = \"item1\" or {question2} = \"item1\" or {question3} = \"item1\" or {question4} = \"item1\" or {question5} = \"item1\" or {question6} = \"item1\" or {question7} = \"item1\" or {question8} = \"item1\" or {question9} = \"item1\" or {question10} = \"item1\" or {question11} = \"item1\" or {question12} = \"item1\" or {question13} = \"item1\"",
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
			}],
			"title":"Part #2"
            },
            {
			"type":"panel",
			"name":"panel3",
			"elements":[{
               "type":"radiogroup",
               "name":"question15",
               "title":"How much of a problem did any of these cause you — like being able to work; having family, money, or legal troubles; getting into arguments or fights?",
               "isRequired":true,
               "choices":[
                  {
                     "value":"item1",
                     "text":"No problem"
                  },
                  {
                     "value":"item2",
                     "text":"Minor problem"
                  },
                  {
                     "value":"item3",
                     "text":"Moderate problem"
                  },
                  {
                     "value":"item4",
                     "text":"Serious problem"
                  }
               ]
			   }],
			   "title":"Part #3"
            },
            {
			"type":"panel",
			"name":"panel4",
			"elements":[{
			   "type":"radiogroup",
               "name":"question16",
               "title":"Have any of your blood relatives (ie, children, siblings, parents, grandparents, aunts, uncles) had manic-depressive illness or bipolar disorder?",
               "isRequired":true,
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
			}],
			"title":"Part #4"
            },
            {
			"type":"panel",
			"name":"panel5",
			"elements":[{
				"type":"radiogroup",
               "name":"question17",
               "title":"Has a health professional ever told you that you have manic-depressive illness or bipolar disorder?",
               "isRequired":true,
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
			}],
			   "title":"Part #5"
            }
         ]
      }
   ]
}'
     ,'MDQ'
     ,1
     ,'Result'
     ,''
     ,0
     ,'MDQ'
     ,0)
  GO

DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE name = 'Mood Disorder Questionnaire assessment';

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

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question14', 'item1', 100);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question14', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item2', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item3', 1000);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item4', 1000);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question16', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question16', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question17', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question17', 'item2', 0);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 1107, 2107, 'Possible bipolar disorder.
A positive screen should be followed by a comprehensive evaluation.', 'Possible bipolar disorder.', 'High', '"Yes" to 7 or more of the 13 items in Question #1 (DR-MDQ-03-DR-MDQ-15) AND
     "Yes" to 7 Question #2 (DR-MDQ-16) AND
     "Moderate problem" or "Serious problem" is selected to Question #3 (DR-MDQ-15)');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments) VALUES (@assessmentId, 0, 1106, 'Normal', 'Normal', 'Low', 'All of the other cases ');

