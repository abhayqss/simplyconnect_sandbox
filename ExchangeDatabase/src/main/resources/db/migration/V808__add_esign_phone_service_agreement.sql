insert into DocumentSignatureTemplate(name, title, form_ui_schema, form_schema)
values ('PHONE_SERVICE_AGREEMENT',
        'Phone Service Agreement',
        '{"room":{"ui:options":{"maxLength":128}},"residentPhone":{"ui:field":"PhoneField"},"dateActivated":{"ui:field":"DateField"},"dateDeactivated":{"ui:field":"DateField"},"ui:grid":[{"room":{"md":6},"residentPhone":{"md":6}},{"dateActivated":{"md":6},"dateDeactivated":{"md":6}}]}',
        '{"type":"object","properties":{"room":{"type":"string","title":"Room #"},"residentPhone":{"title":"Resident Phone #"},"dateActivated":{"title":"Date Activated"},"dateDeactivated":{"title":"Date Deactivated"}}}')
go
