if col_length('DocumentSignatureTemplate', 'is_manually_created') is not null
    begin
        alter table DocumentSignatureTemplate
            drop column is_manually_created
    end
go

if col_length('DocumentSignatureTemplate', 'created_by_id') is not null
    begin
        alter table DocumentSignatureTemplate
            drop constraint FK_DocumentSignatureTemplate_Employee_enc_created_by
        alter table DocumentSignatureTemplate
            drop column created_by_id
    end
go

if col_length('DocumentSignatureTemplate', 'creation_datetime') is not null
    begin
        alter table DocumentSignatureTemplate
            drop column creation_datetime
    end
go

alter table DocumentSignatureTemplate
    add is_manually_created bit
go

update DocumentSignatureTemplate
set is_manually_created = 0
go

alter table DocumentSignatureTemplate
    alter column is_manually_created bit not null
go

alter table DocumentSignatureTemplate
    add created_by_id bigint
        constraint FK_DocumentSignatureTemplate_Employee_enc_created_by foreign key (created_by_id) references Employee_enc (id)
go

alter table DocumentSignatureTemplate
    add creation_datetime datetime2(7)
go
