UPDATE Assessment
SET json_content = N'{
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
       "text": "2 to 3 times a week"
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
where short_name = 'AUDIT';
