if col_length('DocumentSignatureTemplateField', 'title') is not null
    begin
        alter table DocumentSignatureTemplateField
            drop column title
    end
go

alter table DocumentSignatureTemplateField
    add title varchar(256)
go
