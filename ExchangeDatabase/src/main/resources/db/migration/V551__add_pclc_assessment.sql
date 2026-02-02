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
     ,'Posttraumatic Stress Disorder Checklist (PCL-C) assessment'
     ,N'{
 "pages": [
  {
   "name": "page1",
   "elements": [
    {
     "type": "radiogroup",
     "name": "question1",
     "title": "Repeated, disturbing memories, thoughts, or images of a stressful experience from the past?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question1} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately"
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question2",
     "title": "Repeated, disturbing dreams of a stressful experience from the past?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question2} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately"
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question3",
     "title": "Suddenly acting or feeling as if a stressful experience were happening again (as if you were reliving it)?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question3} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately"
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question4",
     "title": "Feeling very upset when something reminded you of a stressful experience from the past?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question4} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately"
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question5",
     "title": "Having physical reactions (e.g., heart pounding, trouble breathing, or sweating) when something reminded you of a stressful experience from the past?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question5} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately"
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question6",
     "title": "Avoid thinking about or talking about a stressful experience from the past or avoid having feelings related to it?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question6} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately"
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question7",
     "title": "Avoid activities or situations because they remind you of a stressful experience from the past?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question7} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately"
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question8",
     "title": "Trouble remembering important parts of a stressful experience from the past?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question8} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately"
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question9",
     "title": "Loss of interest in things that you used to enjoy?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question9} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately "
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question10",
     "title": "Feeling distant or cut off from other people?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question10} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately "
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question11",
     "title": "Feeling emotionally numb or being unable to have loving feelings for those close to you?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question11} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately "
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question12",
     "title": "Feeling as if your future will somehow be cut short?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question12} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately "
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question13",
     "title": "Trouble falling or staying asleep?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question13} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately "
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question14",
     "title": "Feeling irritable or having angry outbursts?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question14} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately "
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question15",
     "title": "Having difficulty concentrating?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question15} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately "
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question16",
     "title": "Being “super alert” or watchful on guard?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question16} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately "
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "question17",
     "title": "Feeling jumpy or easily startled?",
	 "validators":[{
		"type":"expression",
        "text":"Please fill the required field",
        "expression":"{question17} notempty"
        }
      ],
     "choices": [
      {
       "value": "item1",
       "text": "Not at all"
      },
      {
       "value": "item2",
       "text": "A little bit"
      },
      {
       "value": "item3",
       "text": "Moderately "
      },
      {
       "value": "item4",
       "text": "Quite a bit"
      },
      {
       "value": "item5",
       "text": "Extremely"
      }
     ]
    }
   ],
   "title": "Below is a list of problems and complaints that people sometimes have in response to stressful life experiences. Please read each one carefully, pick the answer that indicates how much you have been bothered by that problem in the last month."
  }
 ]
}',
   'PTSD',
   1,
   'Interpretation',
   '',
   0,
   'PTSD',
   0)

GO

DECLARE @assessmentId BIGINT;
SELECT @assessmentId = ID FROM Assessment WHERE name = 'Posttraumatic Stress Disorder Checklist (PCL-C) assessment';

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question1', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question2', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question3', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question4', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question5', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question6', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question7', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question8', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question9', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question10', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question11', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question12', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question13', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question14', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question14', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question14', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question14', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question14', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question15', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question16', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question16', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question16', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question16', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question16', 'item5', 5);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question17', 'item1', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question17', 'item2', 2);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question17', 'item3', 3);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question17', 'item4', 4);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'question17', 'item5', 5);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting) VALUES (@assessmentId, 17, 27, 'Little to no severity', 'Little to no severity', 'Low');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting) VALUES (@assessmentId, 28, 29, 'Some PTSD symptoms', 'Some PTSD symptoms', 'Low');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting) VALUES (@assessmentId, 30, 44, 'Moderate to Moderately High severity of PTSD symptoms', 'Moderate to Moderately High severity of PTSD symptoms', 'Medium');
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting) VALUES (@assessmentId, 45, 85, 'High Severity of PTSD symptoms', 'High Severity of PTSD symptoms', 'High');
