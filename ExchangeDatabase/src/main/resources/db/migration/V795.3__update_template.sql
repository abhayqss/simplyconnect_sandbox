update DocumentSignatureTemplateField
set default_value_type = 'CLIENT_HOME_OR_CELL_PHONE'
from DocumentSignatureTemplateField f
         left join DocumentSignatureTemplate t on f.signature_template_id = t.id
where t.name = 'BACKGROUND_CHECK_CONSENT'
  and f.default_value_type = 'CLIENT_HOME_PHONE'
