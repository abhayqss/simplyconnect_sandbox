UPDATE Assessment
SET json_content = N'{
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
               "choices":[
                  {
                     "value":"item1",
                     "imageLink":"https://raw.githubusercontent.com/simplyhie100/resources/master/apple.png"
                  },
                  {
                     "value":"item2",
                     "imageLink":"https://raw.githubusercontent.com/simplyhie100/resources/master/car.png"
                  },
                  {
                     "value":"item3",
                     "imageLink":"https://raw.githubusercontent.com/simplyhie100/resources/master/house.png"
                  },
                  {
                     "value":"item4",
                     "imageLink":"https://raw.githubusercontent.com/simplyhie100/resources/master/pen.png"
                  },
                  {
                     "value":"item5",
                     "imageLink":"https://raw.githubusercontent.com/simplyhie100/resources/master/tie.png"
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
               "title":"Present the individual with a blank clock face. Please put in the hour markers and the time at ten minutes to eleven o''clock.",
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
               "title":"Please place an X in the triangle. Which of the figures is the largest?",
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
               "type":"html",
               "name": "image",
               "html": "<img src=\"https://raw.githubusercontent.com/simplyhie100/resources/master/squaretrirect.JPG\"/>"
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
WHERE short_name = 'SLUMS';