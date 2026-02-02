UPDATE Assessment
SET json_content = N'{
   "pages":[
      {
         "name":"page1",
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
                  },
				{
				"type":"radiogroup",
				"name":"question14",
				"title" : "Have several of these ever happened during the same period of time? ",
				"visibleIf":"({question1} = \"item1\" and {question2} = \"item1\") or ({question1} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\" and {question5} = \"item1\") or ({question1} = \"item1\" and {question6} = \"item1\") or ({question1} = \"item1\" and {question7} = \"item1\") or ({question1} = \"item1\" and {question8} = \"item1\") or ({question1} = \"item1\" and {question9} = \"item1\") or ({question1} = \"item1\" and {question10} = \"item1\") or ({question1} = \"item1\" and {question11} = \"item1\") or ({question1} = \"item1\" and {question12} = \"item1\") or ({question1} = \"item1\" and {question13} = \"item1\") or ({question2} = \"item1\" and {question3} = \"item1\") or ({question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\" and {question5} = \"item1\") or ({question2} = \"item1\" and {question6} = \"item1\") or ({question2} = \"item1\" and {question7} = \"item1\") or ({question2} = \"item1\" and {question8} = \"item1\") or ({question2} = \"item1\" and {question9} = \"item1\") or ({question2} = \"item1\" and {question10} = \"item1\") or ({question2} = \"item1\" and {question11} = \"item1\") or ({question2} = \"item1\" and {question12} = \"item1\") or ({question2} = \"item1\" and {question13} = \"item1\") or ({question3} = \"item1\" and {question4} = \"item1\") or ({question3} = \"item1\" and {question5} = \"item1\") or ({question3} = \"item1\" and {question6} = \"item1\") or ({question3} = \"item1\" and {question7} = \"item1\") or ({question3} = \"item1\" and {question8} = \"item1\") or ({question3} = \"item1\" and {question9} = \"item1\") or ({question3} = \"item1\" and {question10} = \"item1\") or ({question3} = \"item1\" and {question11} = \"item1\") or ({question3} = \"item1\" and {question12} = \"item1\") or ({question3} = \"item1\" and {question13} = \"item1\") or ({question4} = \"item1\" and {question5} = \"item1\") or ({question4} = \"item1\" and {question6} = \"item1\") or ({question4} = \"item1\" and {question7} = \"item1\") or ({question4} = \"item1\" and {question8} = \"item1\") or ({question4} = \"item1\" and {question9} = \"item1\") or ({question4} = \"item1\" and {question10} = \"item1\") or ({question4} = \"item1\" and {question11} = \"item1\") or ({question4} = \"item1\" and {question12} = \"item1\") or ({question4} = \"item1\" and {question13} = \"item1\") or ({question5} = \"item1\" and {question6} = \"item1\") or ({question5} = \"item1\" and {question7} = \"item1\") or ({question5} = \"item1\" and {question8} = \"item1\") or ({question5} = \"item1\" and {question9} = \"item1\") or ({question5} = \"item1\" and {question10} = \"item1\") or ({question5} = \"item1\" and {question11} = \"item1\") or ({question5} = \"item1\" and {question12} = \"item1\") or ({question5} = \"item1\" and {question13} = \"item1\") or ({question6} = \"item1\" and {question7} = \"item1\") or ({question6} = \"item1\" and {question8} = \"item1\") or ({question6} = \"item1\" and {question9} = \"item1\") or ({question6} = \"item1\" and {question10} = \"item1\") or ({question6} = \"item1\" and {question11} = \"item1\") or ({question6} = \"item1\" and {question12} = \"item1\") or ({question6} = \"item1\" and {question13} = \"item1\") or ({question7} = \"item1\" and {question8} = \"item1\") or ({question7} = \"item1\" and {question9} = \"item1\") or ({question7} = \"item1\" and {question10} = \"item1\") or ({question7} = \"item1\" and {question11} = \"item1\") or ({question7} = \"item1\" and {question12} = \"item1\") or ({question7} = \"item1\" and {question13} = \"item1\") or ({question8} = \"item1\" and {question9} = \"item1\") or ({question8} = \"item1\" and {question10} = \"item1\") or ({question8} = \"item1\" and {question11} = \"item1\") or ({question8} = \"item1\" and {question12} = \"item1\") or ({question8} = \"item1\" and {question13} = \"item1\") or ({question9} = \"item1\" and {question10} = \"item1\") or ({question9} = \"item1\" and {question11} = \"item1\") or ({question9} = \"item1\" and {question12} = \"item1\") or ({question9} = \"item1\" and {question13} = \"item1\") or ({question10} = \"item1\" and {question11} = \"item1\") or ({question10} = \"item1\" and {question12} = \"item1\") or ({question10} = \"item1\" and {question13} = \"item1\") or ({question11} = \"item1\" and {question12} = \"item1\") or ({question11} = \"item1\" and {question13} = \"item1\") or ({question12} = \"item1\" and {question13} = \"item1\")",
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
               "name":"question15",
               "title":"How much of a problem did any of these cause you — like being able to work; having family, money, or legal troubles; getting into arguments or fights?",
			   "visibleIf":"({question1} = \"item1\" and {question2} = \"item1\") or ({question1} = \"item1\" and {question3} = \"item1\") or ({question1} = \"item1\" and {question4} = \"item1\") or ({question1} = \"item1\" and {question5} = \"item1\") or ({question1} = \"item1\" and {question6} = \"item1\") or ({question1} = \"item1\" and {question7} = \"item1\") or ({question1} = \"item1\" and {question8} = \"item1\") or ({question1} = \"item1\" and {question9} = \"item1\") or ({question1} = \"item1\" and {question10} = \"item1\") or ({question1} = \"item1\" and {question11} = \"item1\") or ({question1} = \"item1\" and {question12} = \"item1\") or ({question1} = \"item1\" and {question13} = \"item1\") or ({question2} = \"item1\" and {question3} = \"item1\") or ({question2} = \"item1\" and {question4} = \"item1\") or ({question2} = \"item1\" and {question5} = \"item1\") or ({question2} = \"item1\" and {question6} = \"item1\") or ({question2} = \"item1\" and {question7} = \"item1\") or ({question2} = \"item1\" and {question8} = \"item1\") or ({question2} = \"item1\" and {question9} = \"item1\") or ({question2} = \"item1\" and {question10} = \"item1\") or ({question2} = \"item1\" and {question11} = \"item1\") or ({question2} = \"item1\" and {question12} = \"item1\") or ({question2} = \"item1\" and {question13} = \"item1\") or ({question3} = \"item1\" and {question4} = \"item1\") or ({question3} = \"item1\" and {question5} = \"item1\") or ({question3} = \"item1\" and {question6} = \"item1\") or ({question3} = \"item1\" and {question7} = \"item1\") or ({question3} = \"item1\" and {question8} = \"item1\") or ({question3} = \"item1\" and {question9} = \"item1\") or ({question3} = \"item1\" and {question10} = \"item1\") or ({question3} = \"item1\" and {question11} = \"item1\") or ({question3} = \"item1\" and {question12} = \"item1\") or ({question3} = \"item1\" and {question13} = \"item1\") or ({question4} = \"item1\" and {question5} = \"item1\") or ({question4} = \"item1\" and {question6} = \"item1\") or ({question4} = \"item1\" and {question7} = \"item1\") or ({question4} = \"item1\" and {question8} = \"item1\") or ({question4} = \"item1\" and {question9} = \"item1\") or ({question4} = \"item1\" and {question10} = \"item1\") or ({question4} = \"item1\" and {question11} = \"item1\") or ({question4} = \"item1\" and {question12} = \"item1\") or ({question4} = \"item1\" and {question13} = \"item1\") or ({question5} = \"item1\" and {question6} = \"item1\") or ({question5} = \"item1\" and {question7} = \"item1\") or ({question5} = \"item1\" and {question8} = \"item1\") or ({question5} = \"item1\" and {question9} = \"item1\") or ({question5} = \"item1\" and {question10} = \"item1\") or ({question5} = \"item1\" and {question11} = \"item1\") or ({question5} = \"item1\" and {question12} = \"item1\") or ({question5} = \"item1\" and {question13} = \"item1\") or ({question6} = \"item1\" and {question7} = \"item1\") or ({question6} = \"item1\" and {question8} = \"item1\") or ({question6} = \"item1\" and {question9} = \"item1\") or ({question6} = \"item1\" and {question10} = \"item1\") or ({question6} = \"item1\" and {question11} = \"item1\") or ({question6} = \"item1\" and {question12} = \"item1\") or ({question6} = \"item1\" and {question13} = \"item1\") or ({question7} = \"item1\" and {question8} = \"item1\") or ({question7} = \"item1\" and {question9} = \"item1\") or ({question7} = \"item1\" and {question10} = \"item1\") or ({question7} = \"item1\" and {question11} = \"item1\") or ({question7} = \"item1\" and {question12} = \"item1\") or ({question7} = \"item1\" and {question13} = \"item1\") or ({question8} = \"item1\" and {question9} = \"item1\") or ({question8} = \"item1\" and {question10} = \"item1\") or ({question8} = \"item1\" and {question11} = \"item1\") or ({question8} = \"item1\" and {question12} = \"item1\") or ({question8} = \"item1\" and {question13} = \"item1\") or ({question9} = \"item1\" and {question10} = \"item1\") or ({question9} = \"item1\" and {question11} = \"item1\") or ({question9} = \"item1\" and {question12} = \"item1\") or ({question9} = \"item1\" and {question13} = \"item1\") or ({question10} = \"item1\" and {question11} = \"item1\") or ({question10} = \"item1\" and {question12} = \"item1\") or ({question10} = \"item1\" and {question13} = \"item1\") or ({question11} = \"item1\" and {question12} = \"item1\") or ({question11} = \"item1\" and {question13} = \"item1\") or ({question12} = \"item1\" and {question13} = \"item1\")",
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
			   },
            {
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
			},
            {
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
			}
         ]
      }
   ]
}'
WHERE short_name = 'MDQ';