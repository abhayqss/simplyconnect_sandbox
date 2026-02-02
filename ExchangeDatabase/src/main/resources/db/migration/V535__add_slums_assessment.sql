UPDATE Assessment SET has_numeration = 0 WHERE name = 'Geriatric Depression Scale (short) assessment';
UPDATE Assessment SET has_numeration = 0 WHERE name = 'Geriatric Depression Scale (long) assessment';

ALTER TABLE AssessmentScoringGroup ADD passed_high_school BIT default NULL;
GO

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
     ,'Saint Louis University Mental Status (SLUMS) assessment'
     ,N'{
   "pages":[
      {
         "name":"page1",
         "elements":[
            {
               "type":"radiogroup",
               "name":"question1",
               "title":"Individual education?",
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
                     "text":"High school education"
                  },
                  {
                     "value":"item2",
                     "text":"Less than high school education"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question2",
               "title":"What day of the week is it?",
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
                     "text":"Correct"
                  },
                  {
                     "value":"item2",
                     "text":"Incorrect"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question3",
               "title":"What is the year?",
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
                     "text":"Correct"
                  },
                  {
                     "value":"item2",
                     "text":"Incorrect"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question4",
               "title":"What state are we in?",
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
                     "text":"Correct"
                  },
                  {
                     "value":"item2",
                     "text":"Incorrect"
                  }
               ]
            },
            {
               "type":"imagepicker",
               "name":"question5",
               "title":"Please remember these five objects. I will ask you what they are later.",
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
                     "imageLink":"https://raw.githubusercontent.com/abaturyna/simplyhie-resources/master/slums/apple.png"
                  },
                  {
                     "value":"item2",
                     "imageLink":"https://raw.githubusercontent.com/abaturyna/simplyhie-resources/master/slums/car.png"
                  },
                  {
                     "value":"item3",
                     "imageLink":"https://raw.githubusercontent.com/abaturyna/simplyhie-resources/master/slums/house.png"
                  },
                  {
                     "value":"item4",
                     "imageLink":"https://raw.githubusercontent.com/abaturyna/simplyhie-resources/master/slums/pen.png"
                  },
                  {
                     "value":"item5",
                     "imageLink":"https://raw.githubusercontent.com/abaturyna/simplyhie-resources/master/slums/tie.png"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question6",
               "title":"You have $100 and you go to the store and buy a dozen apples for $3 and a tricycle for $20. How much did you spend?",
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
                     "text":"Correct"
                  },
                  {
                     "value":"item2",
                     "text":"Incorrect"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question7",
               "title":"You have $100 and you go to the store and buy a dozen apples for $3 and a tricycle for $20. How much do you have left?",
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
                     "text":"Correct"
                  },
                  {
                     "value":"item2",
                     "text":"Incorrect"
                  }
               ]
            },
            {
               "type":"radiogroup",
               "name":"question8",
               "title":"Please name as many animals as you can in one minute.",
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
                     "text":"0-4 animals"
                  },
                  {
                     "value":"item2",
                     "text":"5-9 animals"
                  },
                  {
                     "value":"item3",
                     "text":"10-14 animals"
                  },
                  {
                     "value":"item4",
                     "text":"15+ animals"
                  }
               ]
            },
            {
               "type":"checkbox",
               "name":"question9",
               "title":"What were the five objects I asked you to remember?",
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
                     "text":"Apple"
                  },
                  {
                     "value":"item2",
                     "text":"Pen"
                  },
                  {
                     "value":"item3",
                     "text":"Tie"
                  },
                  {
                     "value":"item4",
                     "text":"House"
                  },
                  {
                     "value":"item5",
                     "text":"Car"
                  }
               ]
            },
            {
               "type":"checkbox",
               "name":"question10",
               "title":"I am going to give you a series of numbers and I would like you to give them to me backwards. For example, if I say 42, you would say 24.",
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
                     "text":"87"
                  },
                  {
                     "value":"item2",
                     "text":"649"
                  },
                  {
                     "value":"item3",
                     "text":"8537"
                  }
               ]
            },
            {
               "type":"checkbox",
               "name":"question11",
               "title":"Please put in the hour markers and the time at ten minutes to eleven o''clock.",
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
                     "text":"Hour markers okay"
                  },
                  {
                     "value":"item2",
                     "text":"Time correct"
                  }
               ]
            },
            {
               "type":"checkbox",
               "name":"question12",
               "title":"*Present an individual with a square, triangle, rectangle figures depicted.Please place an X in the triangle. Which of the above figures is largest?",
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
                     "text":"X is placed in the triangle."
                  },
                  {
                     "value":"item2",
                     "text":"Largest figure defined correct."
                  }
               ]
            },
            {
               "type":"checkbox",
               "name":"question13",
               "title":"I am going to tell you a story. Please listen carefully because afterwards, I’m going to ask you some questions about it. Jill was a very successful stockbroker. She made a lot of money on the stock market. She then met Jack, a devastatingly handsome man. She married him and had three children. They lived in Chicago. She then stopped work and stayed at home to bring up her children. When they were teenagers, she went back to work. She and Jack lived happily ever after",
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
                     "text":"What was the female''s name?"
                  },
                  {
                     "value":"item2",
                     "text":"When did she go back to work?"
                  },
                  {
                     "value":"item3",
                     "text":"What work did she do?"
                  },
                  {
                     "value":"item4",
                     "text":"What state did she live in?"
                  }
               ]
            }
         ],
         "title":"Saint Louis University Mental Status (SLUMS)"
      }
   ]
}'
     ,'SLUMS'
     ,1
     ,'Result'
     ,''
     ,0
     ,'SLUMS'
     ,0)
  GO

SET XACT_ABORT ON
  GO

DECLARE @assessmentId BIGINT;
SELECT @assessmentId = ID FROM Assessment WHERE name = 'Saint Louis University Mental Status (SLUMS) assessment';

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item2', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item3', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item4', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item5', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item1', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item2', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item3', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item4', 3);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item3', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item4', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item5', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item1', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item2', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item3', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item1', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item2', 2);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item2', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item1', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item3', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item4', 2);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments, passed_high_school) VALUES (@assessmentId, 27, 30, 'Scores 27-30 are considered normal.', 'Normal', 'Low', 'Normal', 1);
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments, passed_high_school) VALUES (@assessmentId, 25, 30, 'Scores 25-30 are considered normal.', 'Normal', 'Low', 'Normal', 0);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments, passed_high_school) VALUES (@assessmentId, 21, 26, 'Scores 21-26 are indicative of Mild Neurocognitive Disorder.', 'Mild Neurocognitive Disorder', 'Medium', 'Medium', 1);
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments, passed_high_school) VALUES (@assessmentId, 20, 24, 'Scores 20-24 are indicative of Mild Neurocognitive Disorder.', 'Mild Neurocognitive Disorder', 'Medium', 'Medium', 0);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments, passed_high_school) VALUES (@assessmentId, 1, 20, 'Scores 1-20 are indicative of Dementia.', 'Dementia', 'High', 'Almost always indicative of depression', 1);
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments, passed_high_school) VALUES (@assessmentId, 1, 19, 'Scores 1-19 are indicative of Dementia.', 'Dementia', 'High', 'Almost always indicative of depression', 0);

