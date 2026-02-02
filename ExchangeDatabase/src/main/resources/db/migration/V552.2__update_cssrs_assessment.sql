UPDATE Assessment
SET json_content = N'{
  "pages": [
    {
      "name": "page1",
      "elements": [
        {
          "type": "html",
          "name": "Collapse/Expand All",
          "html": "<a class=\"sv-expand-all-sections-btn\" >Expand All</a>\n<a class=\"sv-collapse-all-sections-btn\">Collapse All</a>"
        },
        {
          "type": "panel",
          "name": "Suicidal ideation",
          "assessmentSectionAnchor": "Suicidal ideation",
          "isNavigable": true,
          "isExpandable": true,
          "elements": [
            {
              "type": "panel",
              "name": "panel11",
              "panelAnchor": "panel11",
              "isNavDestination": true,
              "elements": [
			    {
				  "type": "html",
				  "name": "Collapse/Expand All",
				  "html": "<i>Subject endorses thoughts about a wish to be dead or not alive anymore, or wish to fall asleep and not wake up. </i>"
				},
                {
                  "type": "radiogroup",
                  "name": "question1",
                  "title": "Have you thought about being dead or what it would be like to be dead? Have you wished you were dead or wished you could go to sleep and never wake up? Do you wish you weren’t alive anymore?",
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
                  "type": "comment",
                  "name": "question2",
                  "title": "Describe",
                  "visibleIf": "{question1} = \"item1\" ",
                  "maxLength": 5000,
                  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question2} notempty"
                    }
                  ]
                }
              ],
              "title": "1. Wish to be Dead"
            },
            {
              "type": "panel",
              "name": "panel12",
              "panelAnchor": "panel12",
              "isNavDestination": true,
              "elements": [
			  	{
				  "type": "html",
				  "name": "Collapse/Expand All",
				  "html": "<i>General, non-specific thoughts of wanting to end one’s life/commit suicide (e.g., “I’ve thought about killing myself”) without thoughts of ways to kill oneself/associated methods, intent, or plan during the assessment period.</i>"
				},
                {
                  "type": "radiogroup",
                  "name": "question3",
                  "title": "Have you thought about doing something to make yourself not alive anymore? Have you had any thoughts about killing yourself?",
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
                  "type": "comment",
                  "name": "question4",
                  "title": "Describe",
                  "visibleIf": "{question3} = \"item1\" ",
                  "maxLength": 5000,
                  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question4} notempty"
                    }
                  ]
                }
              ],
              "title": "2. Non-Specific Active Suicidal Thoughts."
            },
            {
              "type": "panel",
              "name": "panel13",
              "panelAnchor": "panel13",
              "isNavDestination": true,
              "elements": [
			  	{
				  "type": "html",
				  "name": "Collapse/Expand All",
				  "visibleIf": "{question3} = \"item1\" or ({question1} = \"item1\" and {question3} = \"item1\")",
				  "html": "<i>Subject endorses thoughts of suicide and has thought of at least one method during the assessment period. This is different than a specific plan with time, place or method details worked out (e.g., thought of method to kill self but not a specific plan). Includes person who would say, “I thought about taking an overdose but I never made a specific plan as to when, where or how I would actually do it…and I would never go through with it.”</i>"
				},
                {
                  "type": "radiogroup",
                  "name": "question5",
                  "title": "Have you thought about how you would do that or how you would make yourself not alive anymore (kill yourself)? What did you think about?",
                  "visibleIf": "{question3} = \"item1\" or ({question1} = \"item1\" and {question3} = \"item1\")",
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
                  "type": "comment",
                  "name": "question6",
                  "title": "Describe",
                  "visibleIf": "{question5} = \"item1\" and ({question3} = \"item1\" or ({question1} = \"item1\" and {question3} = \"item1\"))",
                  "maxLength": 5000,
                  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question6} notempty"
                    }
                  ]
                }
              ],
              "title": "3. Active Suicidal Ideation with Any Methods (Not Plan) without Intent to Act."
            },
            {
              "type": "panel",
              "name": "panel14",
              "panelAnchor": "panel14",
              "isNavDestination": true,
              "elements": [
			  	{
				  "type": "html",
				  "name": "Collapse/Expand All",
				  "visibleIf": "{question3} = \"item1\" or ({question1} = \"item1\" and {question3} = \"item1\") ",
				  "html": "<i>Active suicidal thoughts of killing oneself and subject reports having some intent to act on such thoughts, as opposed to “I have the thoughts but I definitely will not do anything about them.”</i>"
				},
                {
                  "type": "radiogroup",
                  "name": "question7",
                  "title": "When you thought about making yourself not alive anymore (or killing yourself), did you think that this was something you might actually do? This is different from (as opposed to) having the thoughts but knowing you wouldn’t do anything about it.",
                  "visibleIf": "{question3} = \"item1\" or ({question1} = \"item1\" and {question3} = \"item1\") ",
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
                  "type": "comment",
                  "name": "question8",
                  "title": "Describe",
                  "visibleIf": "{question7} = \"item1\" and ({question3} = \"item1\" or ({question1} = \"item1\" and {question3} = \"item1\")) ",
                  "maxLength": 5000,
                  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question8} notempty"
                    }
                  ]
                }
              ],
              "title": "4. Active Suicidal Ideation with Some Intent to Act, without Specific Plan."
            },
            {
              "type": "panel",
              "name": "panel15",
              "panelAnchor": "panel15",
              "isNavDestination": true,
              "elements": [
			  	{
				  "type": "html",
				  "name": "Collapse/Expand All",
				  "visibleIf": "{question3} = \"item1\" or ({question1} = \"item1\" and {question3} = \"item1\") ",
				  "html": "<i>Thoughts of killing oneself with details of plan fully or partially worked out and subject has some intent to carry it out.”</i>"
				},
                {
                  "type": "radiogroup",
                  "name": "question9",
                  "title": "Have you decided how or when you would make yourself not alive anymore/kill yourself? Have you planned out (worked out the details of) how you would do it? What was your plan? When you made this plan (or worked out these details), was any part of you thinking about actually doing it?",
                  "visibleIf": "{question3} = \"item1\" or ({question1} = \"item1\" and {question3} = \"item1\") ",
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
                  "type": "comment",
                  "name": "question10",
                  "title": "Describe",
                  "visibleIf": "{question9} = \"item1\" and ({question3} = \"item1\" or ({question1} = \"item1\" and {question3} = \"item1\"))",
                  "maxLength": 5000,
                  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question10} notempty"
                    }
                  ]
                }
              ],
              "title": "5. Active Suicidal Ideation with Specific Plan and Intent."
            }
          ],
          "title": "Suicidal ideation",
          "state": "expanded"
        },
        {
          "type": "panel",
          "name": "panel2",
          "assessmentSectionAnchor": "Intensity of Ideation",
          "isNavigable": true,
          "isExpandable": true,
          "elements": [
            {
              "type": "panel",
              "name": "panel21",
              "panelAnchor": "panel21",
              "isNavDestination": true,
              "elements": [
                {
                  "type": "dropdown",
                  "name": "question11",
				  "title":"Most Severe Ideation with 1 being the least severe and 5 being the most severe",
                  "visibleIf": "{question1} = \"item1\" or {question3} = \"item1\" ",
                  "choices": [
                    {
                      "value": "item1",
                      "text": "1"
                    },
                    {
                      "value": "item2",
                      "text": "2"
                    },
                    {
                      "value": "item3",
                      "text": "3"
                    },
                    {
                      "value": "item4",
                      "text": "4"
                    },
                    {
                      "value": "item5",
                      "text": "5"
                    }
                  ]
                },
                {
                  "type": "comment",
                  "name": "question12",
                  "title": "Description of Ideation",
                  "visibleIf": "{question1} = \"item1\" or {question3} = \"item1\" ",
                  "maxLength": 5000,
                  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question12} notempty"
                    }
                  ]
                }
              ],
              "title": "Most Severe Ideation"
            },
            {
              "type": "panel",
              "name": "panel22",
              "panelAnchor": "panel22",
              "isNavDestination": true,
              "elements": [
                {
                  "type": "radiogroup",
                  "name": "question13",
                  "title": "How many times have you had these thoughts?",
                  "visibleIf": "{question1} = \"item1\" or {question3} = \"item1\" ",
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
                      "text": "Only one time"
                    },
                    {
                      "value": "item2",
                      "text": "A few times"
                    },
                    {
                      "value": "item3",
                      "text": "A lot"
                    },
                    {
                      "value": "item4",
                      "text": "All the time"
                    },
                    {
                      "value": "item5",
                      "text": "Don’t know/Not applicable"
                    }
                  ]
                },
				{
                  "type": "comment",
                  "name": "question14",
                  "title": "Response",
                  "visibleIf": "{question1} = \"item1\" or {question3} = \"item1\" ",
                  "maxLength": 5000
                }
              ],
              "title": "Frequency"
            }
          ],
          "title": "Intensity of Ideation",
          "state": "collapsed"
        },
        {
          "type": "panel",
          "name": "panel3",
          "assessmentSectionAnchor": "panel3",
          "isExpandable": true,
          "elements": [
            {
              "type": "panel",
              "name": "panel31",
              "panelAnchor": "panel31",
              "isNavDestination": true,
              "elements": [
			  	{
				  "type": "html",
				  "name": "Collapse/Expand All",
				  "html": "<i>A potentially self-injurious act committed with at least some wish to die, as a result of act. Behavior was in part thought of as method to kill oneself. Intent does not have to be 100%. If there is any intent/desire to die associated with the act, then it can be considered an actual suicide attempt. There does not have to be any injury or harm, just the potential for injury or harm. If person pulls trigger while gun is in mouth but gun is broken so no injury results, this is considered an attempt.</i>\n<i>Inferring Intent: Even if an individual denies intent/wish to die, it may be inferred clinically from the behavior or circumstances. For example, a highly lethal act that is clearly not an accident so no other intent but suicide can be inferred (e.g., gunshot to head, jumping from window of a high floor/story). Also, if someone denies intent to die, but they thought that what they did could be lethal, intent may be inferred.</i>"
				},
				{
                  "type": "radiogroup",
                  "name": "question15",
                  "title": "Did you do anything to try to kill yourself or make yourself not alive anymore? What did you do? Did you hurt yourself on purpose? Why did you do that? Did you______ as a way to end your life? Did you want to die (even a little) when you_____? Were you trying to make yourself not alive anymore when you _____? Or did you think it was possible you could have died from_____? Or did you do it purely for other reasons, not at all to end your life or kill yourself (like to make yourself feel better, or get something else to happen)? (Self-Injurious Behavior without suicidal intent)",
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
                  "type": "comment",
                  "name": "question16",
				  "title": "Describe",
                  "maxLength": 20000
                },
				{
                  "type": "comment",
                  "name": "question17",
                  "title": "Total # of Attempts",
				  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question17} notempty"
                    }
                  ],
                  "maxLength": 5
                },
				{
                  "type": "radiogroup",
                  "name": "question18",
				  "title": "Has subject engaged in Non-Suicidal Self-Injurious Behavior?",
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
				  "title": "Has subject engaged in Self-Injurious Behavior, intent unknown",
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
                }
			  ],
              "title": "Actual Attempt"
            },
            {
              "type": "panel",
              "name": "panel32",
              "panelAnchor": "panel32",
              "isNavDestination": true,
              "elements": [
			  	{
				  "type": "html",
				  "name": "Collapse/Expand All",
				  "html": "<i>Interrupted Attempt: When the person is interrupted (by an outside circumstance) from starting the potentially self-injurious act (if not for that, actual attempt would have occurred).</i>\n<i>Overdose: Person has pills in hand but is stopped from ingesting. Once they ingest any pills, this becomes an attempt rather than an interrupted attempt.</i>\n<i>Shooting: Person has gun pointed toward self, gun is taken away by someone else, or is somehow prevented from pulling trigger. Once they pull the trigger,even if the gun fails to fire, it is an attempt. Jumping: Person is poised to jump, is grabbed and taken down from ledge. Hanging: Person has noose around neck but has not yet started to hang - is stopped from doing so.</i>"
				},
				{
                  "type": "radiogroup",
                  "name": "question20",
				  "title": "Has there been a time when you started to do something to make yourself not alive anymore (end your life or kill yourself) but someone or something stopped you before you actually did anything? What did you do?",
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
                  "type": "comment",
                  "name": "question21",
				  "visibleIf": "{question20} = \"item1\"",
                  "title": "Describe",
                  "maxLength": 20000,
				  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question21} notempty"
                    }
                  ]
                },
				{
                  "type": "comment",
                  "name": "question22",
				  "title": "Total # of interrupted ",
                  "maxLength": 5,
				  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question22} notempty"
                    }
                  ]
                }
			  ],
              "title": "Interrupted Attempt"
            },
            {
              "type": "panel",
              "name": "panel33",
              "panelAnchor": "panel33",
              "isNavDestination": true,
              "elements": [
			  	{
				  "type": "html",
				  "name": "Collapse/Expand All",
				  "html": "<i>When person begins to take steps toward making a suicide attempt, but stops themselves before they actually have engaged in any self-destructive behavior. Examples are similar to interrupted attempts, except that the individual stops him/herself, instead of being stopped by something else.</i>"
				},
				{
                  "type": "radiogroup",
                  "name": "question23",
				  "title": "Has there been a time when you started to do something to make yourself not alive anymore (end your life or kill yourself) but you changed your mind (stopped yourself) before you actually did anything? What did you do?",
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
                  "type": "comment",
                  "name": "question24",
				  "visibleIf": "{question23} = \"item1\"",
                  "title": "Describe",
                  "maxLength": 20000,
				  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question24} notempty"
                    }
                  ]
                },
				{
                  "type": "comment",
                  "name": "question25",
				  "title": "Total # of aborted or selfinterrupted ",
                  "maxLength": 5,
				  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question25} notempty"
                    }
                  ]
                }
			  ],
              "title": "Aborted Attempt or Self-Interrupted Attempt"
            },
            {
              "type": "panel",
              "name": "panel34",
              "panelAnchor": "panel34",
              "isNavDestination": true,
              "elements": [
			  	{
				  "type": "html",
				  "name": "Collapse/Expand All",
				  "html": "<i>Acts or preparation towards imminently making a suicide attempt. This can include anything beyond a verbalization or thought, such as assembling a specific method (e.g., buying pills, purchasing a gun) or preparing for one’s death by suicide (e.g., giving things away, writing a suicide note).</i>"
				},
				{
                  "type": "radiogroup",
                  "name": "question26",
				  "title": "Have you done anything to get ready to make yourself not alive anymore (to end your life or kill yourself)- like giving things away, writing a goodbye note, getting things you need to kill yourself?",
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
                  "type": "comment",
                  "name": "question27",
				  "visibleIf": "{question26} = \"item1\"",
                  "title": "Describe",
                  "maxLength": 20000,
				  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question27} notempty"
                    }
                  ]
                },
				{
                  "type": "comment",
                  "name": "question28",
				  "title": "Total # of preparatory acts ",
                  "maxLength": 5,
				  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question28} notempty"
                    }
                  ]
                }
			  ],
              "title": "Preparatory Acts or Behavior"
            },
            {
              "type": "panel",
              "name": "panel35",
              "panelAnchor": "panel35",
              "isNavDestination": true,
              "elements": [
				{
                  "type": "radiogroup",
                  "name": "question29",
				  "title": "Death by suicide occurred since last assessment",
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
                  "type": "text",
                  "name": "question30",
				  "calendarFlag": "noLimit",
                  "customdatepicker": true,
                  "title": "Most Lethal Attempt Date"
                }
			  ],
              "title": "Suicide"
            }
          ],
          "title": "Suicidal behavior",
          "state": "collapsed"
        },
        {
          "type": "panel",
          "name": "panel4",
          "assessmentSectionAnchor": "panel4",
          "isNavigable": true,
          "isExpandable": true,
          "elements": [
			{
                  "type": "radiogroup",
                  "name": "question31",
				  "title": "Actual Lethality/Medical Damage",
                  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question31} notempty"
                    }
                  ],
                  "choices": [
                    {
                      "value": "item1",
                      "text": "No physical damage or very minor physical damage (e.g., surface scratches)"
                    },
                    {
                      "value": "item2",
                      "text": "Minor physical damage (e.g., lethargic speech; first-degree burns; mild bleeding; sprains)"
                    },
					{
                      "value": "item3",
                      "text": "Moderate physical damage; medical attention needed (e.g., conscious but sleepy, somewhat responsive; second-degree burns; bleeding of major vessel)"
                    },
					{
                      "value": "item4",
                      "text": "Moderately severe physical damage; medical hospitalization and likely intensive care required (e.g., comatose with reflexes intact; third-degree burns less than 20% of body; extensive blood loss but can recover; major fractures)"
                    },
					{
                      "value": "item5",
                      "text": "Severe physical damage; medical hospitalization with intensive care required (e.g., comatose without reflexes; third-degree burns over 20% of body; extensive blood loss with unstable vital signs; major damage to a vital area)"
                    },
					{
					  "value": "item6",
                      "text": "Death"
					}
                  ]
            },
			{
                  "type": "radiogroup",
                  "name": "question32",
				  "visibleIf": "{question31} = \"item1\"",
                  "title": "Potential Lethality. Likely lethality of actual attempt if no medical damage (the following examples, while having no actual medical damage, had potential for very serious lethality: put gun in mouth and pulled the trigger but gun fails to fire so no medical damage; laying on train tracks with oncoming train but pulled away beforerun over).",
                  "validators": [
                    {
                      "type": "expression",
                      "text": "Please fill the required field",
                      "expression": "{question32} notempty"
                    }
                  ],
                  "choices": [
                    {
                      "value": "item1",
                      "text": "Behaviour not likely to result in injury"
                    },
                    {
                      "value": "item2",
                      "text": "Behaviour likely to result in injury but not likely to cause death"
                    },
					{
                      "value": "item3",
                      "text": "Behaviour likely to result in death despite available medical care"
                    }
                  ]
            }
		  ],
          "title": "Other",
          "state": "collapsed"
        }
      ]
    }
  ],
  "showQuestionNumbers": "on"
}'
WHERE short_name = 'C-SSRS';