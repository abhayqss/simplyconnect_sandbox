insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
select f.id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER'
from DocumentSignatureTemplateField f
left join DocumentSignatureTemplate t on f.signature_template_id = t.id
where t.name = 'BACKGROUND_CHECK_CONSENT' and f.name = 'personalInfo.sex'
