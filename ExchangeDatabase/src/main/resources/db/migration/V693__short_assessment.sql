declare @assessment_group_id bigint;
select @assessment_group_id = id from AssessmentGroup where name = 'Behavioral Health'


INSERT INTO [dbo].[Assessment]
           ([assessment_group_id]
           ,[name]
           ,[json_content]
           ,[short_name]
           ,[scoring_enabled]
           ,[severity_column_name]
           ,[management_comment]
           ,[has_numeration]
           ,[type]
           ,[code]
           ,[send_event_enabled])
     VALUES
           (@assessment_group_id
           ,'Short Assessment'
           ,'{
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
"name": "Comment1",
"visibleIf": "{Has the resident had a change in condition in the last 30 days?} = ''item1''",
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
"name": "question4",
"visibleIf": "{Has the resident seen their PCP in the last year?} = ''item1''",
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
"name": "Comment",
"visibleIf": "{Does the resident have 2 chronic conditions?} = ''item1''",
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
"name": "Has the resident been admitted to the hospital or nursing facility in the past 30 days?",
"title": "Has the resident been admitted to the hospital or nursing facility in the past 30 days?",
"hideNumber": true,
"isRequired": true,
"renderAs": "prettycheckbox",
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
"visibleIf": "{Has the resident been admitted to the hospital or nursing facility in the past 30 days?} = ''item1''",
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
"name": "question7",
"visibleIf": "{Has the resident had a med change in the past 30 days?} = ''item1''",
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
"visibleIf": "{Does the resident have access to needed food?} = ''item2''",
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
"name": "question1",
"visibleIf": "{Does the resident have access to safe and affordable housing?} = ''item2''",
"title": "Comment",
"hideNumber": true
}
]
}
]
}'
           ,'Short Assessment'
           ,0
           ,NULL
           ,NULL
           ,0
           ,0
           ,'SHORT_ASSESSMENT'
           ,0)
GO


