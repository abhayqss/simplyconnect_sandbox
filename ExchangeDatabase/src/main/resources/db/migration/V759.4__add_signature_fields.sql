create table DocumentSignatureTemplateField
(
    id                    bigint identity not null,
    constraint PK_DocumentSignatureTemplateField primary key (id),

    signature_template_id bigint          not null,
    constraint FK_DocumentSignatureTemplateField_DocumentSignatureTemplate_signature_template_id
        foreign key (signature_template_id) references DocumentSignatureTemplate (id),

    name                  varchar(60)     not null,
    pdc_flow_type         varchar(40),
    sc_source_field_type  varchar(40),
    default_value_type    varchar(40),
    related_field_id      bigint,
    related_field_value   varchar(80)
)
go

create table DocumentSignatureTemplateFieldLocation
(
    id                          bigint identity not null,
    constraint PK_DocumentSignatureTemplateFieldLocation primary key (id),
    signature_template_field_id bigint          not null,
    constraint FK_DocumentSignatureTemplateFieldLocation_DocumentSignatureTemplateField
        foreign key (signature_template_field_id) references DocumentSignatureTemplateField (id),
    field_value                 varchar(80),
    top_left_x                  smallint        not null,
    top_left_y                  smallint        not null,
    bottom_right_x              smallint        not null,
    bottom_right_y              smallint        not null,
    page_no                     smallint        not null,
)
go

create table DocumentSignatureRequestSubmittedField_enc
(
    id                          bigint identity not null,
    constraint PK_DocumentSignatureRequestSubmittedField primary key (id),

    signature_request_id        bigint          not null,
    constraint FK_DocumentSignatureRequestSubmittedField_DocumentSignatureRequest_signature_request_id
        foreign key (signature_request_id) references DocumentSignatureRequest_enc (id),

    signature_template_field_id bigint,
    constraint FK_DocumentSignatureRequestSubmittedField_DocumentSignatureTemplateField_signature_template_field_id
        foreign key (signature_template_field_id) references DocumentSignatureTemplateField (id),

    top_left_x                  smallint        not null,
    top_left_y                  smallint        not null,
    bottom_right_x              smallint        not null,
    bottom_right_y              smallint        not null,
    page_no                     smallint        not null,

    field_value                 varbinary(max),
    field_type                  varchar(40),

    pdcflow_overlay_type        tinyint
)
go

create view DocumentSignatureRequestSubmittedField as
select id,
       signature_request_id,
       signature_template_field_id,
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

create trigger DocumentSignatureRequestSubmittedField_Insert
    on DocumentSignatureRequestSubmittedField
    instead of insert
    as
begin
    insert into DocumentSignatureRequestSubmittedField_enc(signature_request_id,
                                                           signature_template_field_id,
                                                           top_left_x,
                                                           top_left_y,
                                                           bottom_right_x,
                                                           bottom_right_y,
                                                           page_no,
                                                           field_value,
                                                           field_type,
                                                           pdcflow_overlay_type)
    select i.signature_request_id,
           i.signature_template_field_id,
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

create trigger DocumentSignatureRequestSubmittedField_Update
    on DocumentSignatureRequestSubmittedField
    instead of update
    as
begin
    update DocumentSignatureRequestSubmittedField_enc
    set signature_request_id        = i.signature_request_id,
        signature_template_field_id = i.signature_template_field_id,
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
