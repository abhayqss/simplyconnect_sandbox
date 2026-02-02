if object_id('DocumentSignatureRequestNotSubmittedField') is not null
    begin
        drop table DocumentSignatureRequestNotSubmittedField
    end
go

create table DocumentSignatureRequestNotSubmittedField
(
    id                   bigint identity not null,
    constraint PK_DocumentSignatureRequestNotSubmittedField primary key (id),

    signature_request_id bigint          not null,
    constraint FK_DocumentSignatureRequestNotSubmittedField_DocumentSignatureRequest_signature_request_id
        foreign key (signature_request_id) references DocumentSignatureRequest_enc (id),

    top_left_x           smallint        not null,
    top_left_y           smallint        not null,
    bottom_right_x       smallint        not null,
    bottom_right_y       smallint        not null,
    page_no              smallint        not null,

    name                 varchar(60)     not null,

    pdc_flow_type        varchar(40),

    template_field_id    bigint,
    related_field_id     bigint,
)
go

insert into DocumentSignatureRequestNotSubmittedField(signature_request_id,
                                                      top_left_x,
                                                      top_left_y,
                                                      bottom_right_x,
                                                      bottom_right_y,
                                                      page_no,
                                                      name,
                                                      pdc_flow_type,
                                                      template_field_id,
                                                      related_field_id)
select r.id,
       fl.top_left_x,
       fl.top_left_y,
       fl.bottom_right_x,
       fl.bottom_right_y,
       fl.page_no,
       f.name,
       f.pdc_flow_type,
       f.id,
       f.related_field_id
from DocumentSignatureRequest r
         inner join DocumentSignatureTemplateField f on f.signature_template_id = r.signature_template_id
         inner join DocumentSignatureTemplateFieldLocation fl on f.id = fl.signature_template_field_id
where f.pdc_flow_type is not null
  and f.id not in (select sf.signature_template_field_id
                   from DocumentSignatureRequestSubmittedField_enc sf
                   where sf.signature_request_id = r.id)
  and exists(select 1
       from DocumentSignatureRequestSubmittedField_enc sf
       where sf.signature_request_id = r.id)
go

update field_with_related
set field_with_related.related_field_id = field.id
from DocumentSignatureRequestNotSubmittedField field_with_related
         inner join DocumentSignatureRequestNotSubmittedField field
                    on field_with_related.related_field_id = field.template_field_id
go

alter table DocumentSignatureRequestNotSubmittedField
    drop column template_field_id
go

if object_id('DocumentSignatureRequestSubmittedFieldStyle') is not null
    begin
        drop table DocumentSignatureRequestSubmittedFieldStyle
    end
go

create table DocumentSignatureRequestSubmittedFieldStyle
(
    id                 bigint identity not null,
    constraint PK_DocumentSignatureRequestSubmittedFieldStyle primary key (id),
    submitted_field_id bigint          not null,
    constraint FK_DocumentSignatureRequestSubmittedFieldStyle_DocumentSignatureRequestSubmittedField
        foreign key (submitted_field_id) references DocumentSignatureRequestSubmittedField_enc (id),
    type               varchar(40),
    value              varchar(40)
)
go

insert into DocumentSignatureRequestSubmittedFieldStyle(submitted_field_id, type, value)
select field.id, style.type, style.value
from DocumentSignatureRequestSubmittedField_enc field
         inner join DocumentSignatureTemplateFieldStyle style
                    on field.signature_template_field_id = style.signature_template_field_id
go

alter table DocumentSignatureRequestSubmittedField_enc
    drop constraint FK_DocumentSignatureRequestSubmittedField_DocumentSignatureTemplateField_signature_template_field_id;
go

alter table DocumentSignatureRequestSubmittedField_enc
    drop column signature_template_field_id
go

alter view DocumentSignatureRequestSubmittedField as
    select id,
           signature_request_id,
           top_left_x,
           top_left_y,
           bottom_right_x,
           bottom_right_y,
           page_no,
           convert(varchar(max), DecryptByKey([field_value])) field_value,
           field_type,
           pdcflow_overlay_type
    from DocumentSignatureRequestSubmittedField_enc
go

alter trigger DocumentSignatureRequestSubmittedField_Insert
    on DocumentSignatureRequestSubmittedField
    instead of insert
    as
begin
    insert into DocumentSignatureRequestSubmittedField_enc(signature_request_id,
                                                           top_left_x,
                                                           top_left_y,
                                                           bottom_right_x,
                                                           bottom_right_y,
                                                           page_no,
                                                           field_value,
                                                           field_type,
                                                           pdcflow_overlay_type)
    select i.signature_request_id,
           i.top_left_x,
           i.top_left_y,
           i.bottom_right_x,
           i.bottom_right_y,
           i.page_no,
           EncryptByKey(Key_GUID('SymmetricKey1'), i.field_value),
           i.field_type,
           i.pdcflow_overlay_type
    from inserted i
    select @@IDENTITY;
end
go

alter trigger DocumentSignatureRequestSubmittedField_Update
    on DocumentSignatureRequestSubmittedField
    instead of update
    as
begin
    update DocumentSignatureRequestSubmittedField_enc
    set signature_request_id        = i.signature_request_id,
        top_left_x                  = i.top_left_x,
        top_left_y                  = i.top_left_y,
        bottom_right_x              = i.bottom_right_x,
        bottom_right_y              = i.bottom_right_y,
        page_no                     = i.page_no,
        field_value                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.field_value),
        field_type                  = i.field_type,
        pdcflow_overlay_type        = i.pdcflow_overlay_type
    from inserted i
    where i.id = DocumentSignatureRequestSubmittedField_enc.id
end
go
