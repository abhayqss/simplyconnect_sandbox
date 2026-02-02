update DocumentSignatureTemplateAutoFillFieldType
set json_schema = '{"title":"Client SSN - last 4 digits","type":"string","format":"number"}'
where code = 'CLIENT_SSN_LAST_FOUR_DIGITS'
go
