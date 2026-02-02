update DocumentSignatureTemplateFieldLocation
set top_left_y     = 486,
    bottom_right_y = 508
from DocumentSignatureTemplateFieldLocation l
         left join DocumentSignatureTemplateField f on f.id = l.signature_template_field_id
         left join DocumentSignatureTemplate t on f.signature_template_id = t.id
where t.name = 'ACKNOWLEDGEMENT'
  and f.name = 'facilityRepresentativeSignature1'

update DocumentSignatureTemplateFieldLocation
set top_left_y     = 651,
    bottom_right_y = 673
from DocumentSignatureTemplateFieldLocation l
         left join DocumentSignatureTemplateField f on f.id = l.signature_template_field_id
         left join DocumentSignatureTemplate t on f.signature_template_id = t.id
where t.name = 'ACKNOWLEDGEMENT'
  and f.name = 'residentSignature2'

update DocumentSignatureTemplateFieldLocation
set top_left_y     = 698,
    bottom_right_y = 720
from DocumentSignatureTemplateFieldLocation l
         left join DocumentSignatureTemplateField f on f.id = l.signature_template_field_id
         left join DocumentSignatureTemplate t on f.signature_template_id = t.id
where t.name = 'ACKNOWLEDGEMENT'
  and f.name = 'facilityRepresentativeSignature2'

