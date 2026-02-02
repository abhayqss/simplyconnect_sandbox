insert into ValueSet (oid, name, title, description)
values ('2.16.840.1.113883.3.88.12.3221.6.8',
        'ProblemSeverity', 'Problem Severity',
        'This is a description of the level of the severity of the problem.')
GO

insert into ValueSet_CcdCode (value_set_id, ccd_code_id)
select (select id from ValueSet where oid = '2.16.840.1.113883.3.88.12.3221.6.8'), id
from CcdCode
where value_set = '2.16.840.1.113883.3.88.12.3221.6.8'
GO

insert into ValueSet (oid, name, title, description)
values ('2.16.840.1.113883.3.88.12.3221.6.2',
        'AdverseEventType', 'Allergy/Adverse Event Type Value Set',
        'This describes the type of product and intolerance suffered by the patient')
GO

insert into ValueSet_CcdCode (value_set_id, ccd_code_id)
select (select id from ValueSet where oid = '2.16.840.1.113883.3.88.12.3221.6.2'), id
from CcdCode
where value_set = '2.16.840.1.113883.3.88.12.3221.6.2'
GO
