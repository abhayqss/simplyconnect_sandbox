if col_length('Document', 'signature_request_id') is not null
    begin
        alter table Document
            drop constraint FK_Document_DocumentSignatureRequest_signature_request_id
        alter table Document
            drop column signature_request_id;
    end
go

if col_length('Organization', 'license_number') is not null
    begin
        alter table Organization
            drop column license_number;
    end
go

if object_id('DocumentSignatureHistory') is not null
    drop table DocumentSignatureHistory
go

if object_id('DocumentSignatureRequestSubmittedField') is not null
    drop view DocumentSignatureRequestSubmittedField
go

if object_id('DocumentSignatureRequestSubmittedField_enc') is not null
    drop table DocumentSignatureRequestSubmittedField_enc
go

if object_id('DocumentSignatureHistoryView') is not null
    drop view DocumentSignatureHistoryView
go

if object_id('DocumentSignatureTemplateFieldLocation') is not null
    drop table DocumentSignatureTemplateFieldLocation
go

if object_id('DocumentSignatureTemplateField') is not null
    drop table DocumentSignatureTemplateField
go

if object_id('DocumentSignatureRequestPdcFlowCallbackLog') is not null
    drop table DocumentSignatureRequestPdcFlowCallbackLog
go

if object_id('DocumentSignatureRequestNotification') is not null
    drop table DocumentSignatureRequestNotification
go

if object_id('DocumentSignatureRequest_enc') is not null
    drop table DocumentSignatureRequest_enc
go

if object_id('DocumentSignatureRequest') is not null
    drop view DocumentSignatureRequest
go

if object_id('DocumentSignatureTemplate_SourceDatabase') is not null
    drop table DocumentSignatureTemplate_SourceDatabase
go

if object_id('DocumentSignatureTemplate_Organization') is not null
    drop table DocumentSignatureTemplate_Organization
go

if object_id('DocumentSignatureTemplate') is not null
    drop table DocumentSignatureTemplate
go
