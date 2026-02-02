UPDATE Assessment
SET json_content = N'{
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
                  },
				  {
                     "value":"item3",
                     "text":"Unspecified"
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
               "title":"Depression or hopelessness?",
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
               "title":"Previous suicide attempts?",
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
               "title":"Excessive alcohol or drug use?",
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
               "title":"Rational thinking loss/psychosis?",
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
               "title":"Separated, divorced or widowed?",
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
               "title":"Organized or serious attempt?",
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
WHERE name = 'SAD PERSONS Scale assessment';

DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE name = 'SAD PERSONS Scale assessment';

UPDATE AssessmentScoringValue SET value = 1 WHERE assessment_id = @assessmentId AND  question_name = 'question9' AND answer_name = 'item1';
UPDATE AssessmentScoringValue SET value = 0 WHERE assessment_id = @assessmentId AND  question_name = 'question9' AND answer_name = 'item2';