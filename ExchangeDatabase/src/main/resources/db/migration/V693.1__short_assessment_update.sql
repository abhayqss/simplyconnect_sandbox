
UPDATE [dbo].[Assessment]
   SET [json_content] = '{
 "pages": [
  {
   "name": "page1",
   "elements": [
    {
     "type": "radiogroup",
     "name": "Has the resident had a change in condition in the last 30 days?",
     "title": "Has the resident had a change in condition in the last 30 days?",
     "hideNumber": true,
     "isRequired": true,
     "requiredErrorText": "Required field",
     "renderAs": "prettycheckbox",
     "choices": [
      {
       "value": "YES",
       "text": "Yes"
      },
      {
       "value": "NO",
       "text": "No"
      }
     ]
    },
    {
     "type": "comment",
     "name": "Comment1",
     "visibleIf": "{Has the resident had a change in condition in the last 30 days?} = ''YES''",
     "title": "Comment",
     "hideNumber": true
    },
    {
     "type": "radiogroup",
     "name": "Has the resident seen their PCP in the last year?",
     "hideNumber": true,
     "isRequired": true,
     "renderAs": "prettycheckbox",
     "choices": [
      {
       "value": "YES",
       "text": "Yes"
      },
      {
       "value": "NO",
       "text": "No"
      }
     ]
    },
    {
     "type": "text",
     "name": "question4",
     "visibleIf": "{Has the resident seen their PCP in the last year?} = ''YES''",
     "title": "When",
     "hideNumber": true,
     "inputType": "text",
     "calendarFlag":"withoutTime",
     "disableFuture":true
    },
    {
     "type": "radiogroup",
     "name": "Does the resident have 2 chronic conditions?",
     "title": "Does the resident have 2 chronic conditions?",
     "hideNumber": true,
     "isRequired": true,
     "renderAs": "prettycheckbox",
     "choices": [
      {
       "value": "YES",
       "text": "Yes"
      },
      {
       "value": "NO",
       "text": "No"
      }
     ]
    },
    {
     "type": "comment",
     "name": "Comment",
     "visibleIf": "{Does the resident have 2 chronic conditions?} = ''YES''",
     "title": "Comment",
     "hideNumber": true
    },
    {
     "type": "radiogroup",
     "name": "Does the resident have specials who treat their chronic conditions?",
     "title": "Does the resident have specialists who treat their chronic conditions?",
     "hideNumber": true,
     "renderAs": "prettycheckbox",
     "choices": [
      {
       "value": "YES",
       "text": "Yes"
      },
      {
       "value": "NO",
       "text": "No"
      }
     ]
    },
    {
     "type": "radiogroup",
     "name": "Has the resident been admitted to the hospital or nursing facility in the past 30 days?",
     "title": "Has the resident been admitted to the hospital or nursing facility in the past 30 days?",
     "hideNumber": true,
     "isRequired": true,
     "renderAs": "prettycheckbox",
     "choices": [
      {
       "value": "YES",
       "text": "Yes"
      },
      {
       "value": "NO",
       "text": "No"
      }
     ]
    },
    {
     "type": "comment",
     "name": "question6",
     "visibleIf": "{Has the resident been admitted to the hospital or nursing facility in the past 30 days?} = ''YES''",
     "title": "Comment",
     "hideNumber": true
    },
    {
     "type": "radiogroup",
     "name": "Has the resident had a med change in the past 30 days?",
     "title": "Has the resident had a med change in the past 30 days?",
     "hideNumber": true,
     "isRequired": true,
     "renderAs": "prettycheckbox",
     "choices": [
      {
       "value": "YES",
       "text": "Yes"
      },
      {
       "value": "NO",
       "text": "No"
      }
     ]
    },
    {
     "type": "comment",
     "name": "question7",
     "visibleIf": "{Has the resident had a med change in the past 30 days?} = ''YES''",
     "title": "Comment",
     "hideNumber": true
    },
    {
     "type": "radiogroup",
     "name": "Does the resident have access to needed food?",
     "title": "Does the resident have access to needed food?",
     "hideNumber": true,
     "isRequired": true,
     "renderAs": "prettycheckbox",
     "choices": [
      {
       "value": "YES",
       "text": "Yes"
      },
      {
       "value": "NO",
       "text": "No"
      }
     ]
    },
    {
     "type": "comment",
     "name": "question8",
     "visibleIf": "{Does the resident have access to needed food?} = ''NO''",
     "title": "Comment",
     "hideNumber": true
    },
    {
     "type": "radiogroup",
     "name": "Does the resident have access to safe and affordable housing?",
     "title": "Does the resident have access to safe and affordable housing? ",
     "hideNumber": true,
     "isRequired": true,
     "renderAs": "prettycheckbox",
     "choices": [
      {
       "value": "YES",
       "text": "Yes"
      },
      {
       "value": "NO",
       "text": "No"
      }
     ]
    },
    {
     "type": "comment",
     "name": "question1",
     "visibleIf": "{Does the resident have access to safe and affordable housing?} = ''NO''",
     "title": "Comment",
     "hideNumber": true
    }
   ]
  }
 ]
}'
 WHERE code='SHORT_ASSESSMENT'
GO


